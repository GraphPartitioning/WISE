package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.adaptiveWindowStrategy;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import de.ipvs.fachstudie.graphpartitioning.evaluation.PartitionEvaluation;
import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.Main;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.MemoryInfo;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.PartitionInfo;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.AssignedEdge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.EdgeWindow;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.WindowBasedPartitioningStrategy;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;
import de.ipvs.fachstudie.graphpartitioning.util.NamesOfStrategies;

/**
 * 
 * 
 * @author Rieger, Lukas
 * @author Laich, Larissa
 * @author Geppert, Heiko
 * @author Mayer, Christian
 * 
 */
public class WindowStrategy_LA implements WindowBasedPartitioningStrategy {


	public Memory memory;
	public int numberOfPartitions;
	private Window_LA window;

	public HashMap<String, Double> parameters = new HashMap<String, Double>();
	public ArrayList<Double> balances = new ArrayList<Double>();
	
	// Degree-Awareness (Maps vertex ids to vertex degrees)
	public static int maxEdgeDegree;
	
	public int assignmentCounter;
	long latency;
	
	// Adaptive Window
	private int counter;
	private WindowStatistics_LA stat;
	private long deadline; // in ms

	public WindowStrategy_LA(Memory memory) {
		// this(memory,windowSize);
		this.memory = memory;
		this.window = new Window_LA(Configuration.WINDOW_SIZE + 1, this);
		this.numberOfPartitions = memory.getNumberOfPartitions();

		for (int i = 0; i < this.numberOfPartitions; i++) {
			this.balances.add(0.0d);
		}
		this.assignmentCounter = 0;
		this.counter = 0;
		this.stat = new WindowStatistics_LA();
		this.deadline = System.currentTimeMillis() 
				+ Configuration.MAXIMAL_LATENCY - 1000; // 1 second puffer
//		System.out.println("Now: " + System.currentTimeMillis());
//		System.out.println("Deadline: " + deadline);
		
	}

	@Override
	public AssignedEdge getPartitionForEdge() {

		// measure latency
		if (assignmentCounter==0) {
			latency = System.nanoTime();
		}
		
		// update dynamic beta parameter
		dynamicBeta();

		// calculate new balancing scores
		MemoryInfo memInfo = this.memory.getMemoryState();
		this.calculateBalances(memInfo);

		
		// get best edge in window
		WindowEdge_LA bestEdge = window.getAndRemoveBestEdge();
		if (bestEdge==null) {
			return null;
		}
		double bestScore = 0;
		double bestScore_replicaPart = 0;
		int bestPartition = 0;
		for (int p_id=0; p_id<numberOfPartitions; p_id++) {
			if (bestEdge.scores[p_id]>=bestScore) {
				bestScore = bestEdge.scores[p_id];
				bestPartition = p_id;
				bestScore_replicaPart =
//					Configuration.WINDOWSTRATEGY_ALPHA * 
						bestEdge.replicaScores[p_id];
//					+ Configuration.WINDOWSTRATEGY_GAMMA * bestEdge.getSpecifity()
//					+ Configuration.WINDOWSTRATEGY_DELTA * bestEdge.getDegreeScore();
			}
		}

		// update scoreThreshold
		window.updateScoreThreshold(bestScore);
		
		AssignedEdge bestAssignment = new AssignedEdge(bestEdge, bestPartition);
		
		
		// update repScores of all edges that have changed && update candidates
		window.updateRepScores(bestEdge,bestPartition);

		// track variables for adaptive window
		this.assignmentCounter++;
		this.counter++;
//		this.summedAssignmentScore += bestScore;
//		this.summedAssignmentScore += bestScore_replicaPart;
//		this.minAssignmentScore = window.getAverageScore();
//		this.maxAssignmentScore = Configuration.WINDOWSTRATEGY_ALPHA * 2
//				+ Configuration.WINDOWSTRATEGY_BETA
//				+ Configuration.WINDOWSTRATEGY_GAMMA
//				+ Configuration.WINDOWSTRATEGY_DELTA;
		
		// if one pass over the window ready -> update scores
		long tmpLatency = System.nanoTime() - latency;
		stat.updateStatistics(Configuration.WINDOW_SIZE, 
				tmpLatency, bestScore_replicaPart);
		this.latency = System.nanoTime();
		
		if (counter==Configuration.WINDOW_SIZE) {
			long maxLatency = calculateMaxLatencyPerEdgeAssignment();
//			System.out.println("Max latency (ns): " + maxLatency);
//			System.out.println("Latency needed (ns): " + tmpLatency);
//			System.out.println("Latency estimated (ns): " + stat.getAverageLatency(Configuration.WINDOW_SIZE));
//			System.out.println("Old window size: " + Configuration.WINDOW_SIZE);
			if (maxLatency>0) {
				updateWindowSize(stat.getWindowSize(
						Configuration.WINDOW_SIZE, maxLatency,
						assignmentCounter));
			} else {
				// deadline already missed: go go go!
				updateWindowSize(Configuration.MINIMAL_WINDOW_SIZE);
			}
//			System.out.println("New window size: " + Configuration.WINDOW_SIZE);
			counter = 0;
		}
//		int samplingPeriod = 10000; // edges
//		if (assignmentCounter>0 
//				&& assignmentCounter%Configuration.WINDOW_SIZE==0) {
//				&& assignmentCounter%samplingPeriod==0
//				) {
//			this.summedLatency = System.nanoTime() - latency;
//			double tmpLatency = summedLatency 
//					/ (double) samplingPeriod;
//			if (tmpLatency>this.maxLatency) {
//				this.maxLatency = tmpLatency;
//			}
//			this.trackScores();
//			this.summedAssignmentScore = 0;
//		}
		
		return bestAssignment;
	}
	
	/**
	 * Sets the new window size
	 * @param newWindowSize
	 */
	private void updateWindowSize(int newWindowSize) {
		Configuration.WINDOW_SIZE = newWindowSize;
		window.setSize(newWindowSize);
	}
	
//	private void trackScores() {
//		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
//				new FileOutputStream(Configuration.OUTPUT_PATH
//						+ "Score" + Configuration.WINDOW_SIZE + ".txt", true), "utf-8"))) {
//				writer.write(Configuration.WINDOW_SIZE + "\t"
//						+ assignmentCounter + "\t"
//						+ summedAssignmentScore + "\t"
//						+ minAssignmentScore + "\t"
//						+ maxAssignmentScore + "\t"
//						+ summedLatency + "\t"
//						+ maxLatency + "\n"					
//						);
//		} catch (Exception e) {
//			System.out.println("ERROR: Couldn't write evaluation to file");
//			e.printStackTrace();
//		}
//	}

	/**
	 * Returns the maximal latency allowed for assigning
	 * a single edge in nanoseconds.
	 * @return
	 */
	private long calculateMaxLatencyPerEdgeAssignment() {
		long edgesLeft = Configuration.MAXIMAL_GRAPH_SIZE - assignmentCounter;
		if (edgesLeft<1) {
			edgesLeft = 1;
		}
		double timeLeft = (double)(deadline-System.currentTimeMillis()) 
				/ (double)edgesLeft * 1000000; // nanosecs
//		System.out.println(timeLeft * edgesLeft / 1000 + " sec");
//		System.out.println(timeLeft);
//		System.out.println(Math.round(timeLeft));
		return Math.round(timeLeft);
	}

	private void calculateBalances(MemoryInfo memInfo) {

		PartitionInfo info;
		double balScore;

		for (int partitionIdx = 0; partitionIdx < this.numberOfPartitions; partitionIdx++) {
			balScore = 0.0;
			info = this.memory.getPartitionInfo(partitionIdx);
			// bal Score is between 0 and 1 (if beta is 1)--- + 0.05 avoid division by 0 
			// value is higher if partition should get edges (greater distance to maximum)
			balScore = (float) (Configuration.WINDOWSTRATEGY_BETA*(memInfo.getMaxNumberEdges() - info.getTotalEdgeCount()) 
					/ (memInfo.getMaxNumberEdges() - memInfo.getMinNumberEdges() + 0.05)); 
			this.balances.set(partitionIdx, balScore);
		}
	}


	@Override
	public EdgeWindow getWindow() {
		return this.window;
	}

	@Override
	public String toString() {
		return NamesOfStrategies.WINDOWSTRATEGY.toString();
	}

	@Override
	public HashMap<String, Double> getParameters() {
		return parameters;
	}

	private void dynamicBeta2() {
		
	}

		/**
		 * Updates the beta parameter based on the imbalance of the partitions
		 */
		private void dynamicBeta() {
			
			// imbalance
			double min = memory.getMemoryState().getMinNumberEdges();
			double max = memory.getMemoryState().getMaxNumberEdges();
			double imbalance;
			if (max>0) {
				imbalance = (max-min)/max;
			} else {
				imbalance = 0;
			}
//			System.out.println("Imbalance" + " " + imbalance);
			
			// tolerance as a function of the algorithm phase
			double tolerance = 1-(assignmentCounter
					/ (double) (Configuration.MINIMAL_GRAPH_SIZE));
			tolerance = Math.max(tolerance,0.04);
			
//			tolerance = 0.05; // fraction 
			
			// adapt beta within interval [0.1,0.5]
			Configuration.WINDOWSTRATEGY_BETA += (imbalance - tolerance);
			if (Configuration.WINDOWSTRATEGY_BETA<0.1) {
				Configuration.WINDOWSTRATEGY_BETA = 0.1;
			}
			if (Configuration.WINDOWSTRATEGY_BETA>0.7) {
				Configuration.WINDOWSTRATEGY_BETA = 0.7;
			}
			
			// normalize all parameters, so that the sum is 1
			double sum = Configuration.WINDOWSTRATEGY_ALPHA
					+ Configuration.WINDOWSTRATEGY_BETA
					+ Configuration.WINDOWSTRATEGY_GAMMA
					+ Configuration.WINDOWSTRATEGY_DELTA;
			Configuration.WINDOWSTRATEGY_ALPHA /= sum;
			Configuration.WINDOWSTRATEGY_BETA /= sum;
			Configuration.WINDOWSTRATEGY_GAMMA /= sum;
			Configuration.WINDOWSTRATEGY_DELTA /= sum;
			
//			System.out.println(Configuration.WINDOWSTRATEGY_BETA);
//			System.out.println();
//			Configuration.WINDOWSTRATEGY_BETA = 3* imbalance;

		}

	public WindowStatistics_LA getStat() {
		return stat;
	}
}
