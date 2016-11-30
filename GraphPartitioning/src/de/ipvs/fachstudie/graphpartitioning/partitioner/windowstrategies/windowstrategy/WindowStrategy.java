package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.windowstrategy;

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
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class WindowStrategy implements WindowBasedPartitioningStrategy {

	private WindowStrategyWindow window;
	private Memory memory;
	private int numberOfPartitions;
	private double alpha = Configuration.WINDOWSTRATEGY_ALPHA;
	public double beta = Configuration.WINDOWSTRATEGY_BETA;
	private double gamma = Configuration.WINDOWSTRATEGY_GAMMA;

	HashMap<String, Double> parameters = new HashMap<String, Double>();
	int numberOfThreadsToUpdateWindow = Configuration.WINDOWSTRATEGY_THREADS;
	ArrayList<WindowEdgeUpdater> updaterThreads = new ArrayList<WindowEdgeUpdater>();
	ArrayList<Double> balances = new ArrayList<Double>();

	// Thread synch
	public static Object lock = new Object();
	public static int counter;

	// Degree-Awareness
	public static double delta = 0.2;
	public HashMap<Integer, Integer> approximatedNodeDistribution = new HashMap<Integer, Integer>();

	public WindowStrategy(Memory memory, int windowSize) {
		this.memory = memory;
		this.window = new WindowStrategyWindow(windowSize + 1);
		this.numberOfPartitions = memory.getNumberOfPartitions();
		parameters.put("alpha", this.alpha);
		parameters.put("beta", this.beta);
		parameters.put("gamma", this.gamma);
		parameters.put("threads", (double) this.numberOfThreadsToUpdateWindow);

		for (int i = 0; i < numberOfThreadsToUpdateWindow; i++) {
			updaterThreads.add(new WindowEdgeUpdater(this.window, this, this.numberOfPartitions, this.balances,
					this.memory, alpha, beta, gamma));
			updaterThreads.get(i).start();
		}

		for (int i = 0; i < this.numberOfPartitions; i++) {
			this.balances.add(0.0d);
		}
	}

	public WindowStrategy(Memory memory, int windowSize, double alpha, double beta, double gamma, int threads) {
		// this(memory,windowSize);
		this.memory = memory;
		this.window = new WindowStrategyWindow(windowSize + 1);
		this.numberOfPartitions = memory.getNumberOfPartitions();
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		this.numberOfThreadsToUpdateWindow = threads;
		// replace former values
		parameters.put("alpha", this.alpha);
		parameters.put("beta", this.beta);
		parameters.put("gamma", this.gamma);
		parameters.put("threads", (double) this.numberOfThreadsToUpdateWindow);

		for (int i = 0; i < numberOfThreadsToUpdateWindow; i++) {
			updaterThreads.add(new WindowEdgeUpdater(this.window, this, this.numberOfPartitions, this.balances,
					this.memory, alpha, beta, gamma));
			updaterThreads.get(i).start();
		}

		for (int i = 0; i < this.numberOfPartitions; i++) {
			this.balances.add(0.0d);
		}
	}

	@Override
	public AssignedEdge getPartitionForEdge() {

		/*
		 * Dynamic Beta
		 */
		double min = memory.getMemoryState().getMinNumberEdges();
		double max = memory.getMemoryState().getMaxNumberEdges();
		// double totalEdges = memory.getMemoryState().getAvgNumberEdges()
		// *memory.getNumberOfPartitions();
		// double imbalance = (max-min) / total;
		// double imbalance =
		// (max-min)/(double)memory.getMemoryState().getAvgNumberEdges();
		double imbalance = (max - min) / max;
		// System.out.println(imbalance + " " + max + " " + min);
		if (imbalance <= 0.03) {
			beta = 0;
		}
		if (imbalance > 0.03) {
			beta = 0.2;
		}
		if (imbalance > 0.04) {
			beta = 0.4;
		}
		if (imbalance > 0.05) {
			beta = 0.6;
		}
		if (imbalance > 0.06) {
			beta = 0.8;
		}
		// System.out.println("Beta: " + beta);
		// System.out.println("Imbalance: " + imbalance);

		/*
		 * Delta - Degree-awareness
		 */
		int maxDegree = 0;
		for (Edge e : window.getEdges()) {
			int deg = 0;
			deg += this.approximatedNodeDistribution.getOrDefault(e.getFirstVertex().getId(), 1);
			deg += this.approximatedNodeDistribution.getOrDefault(e.getSecondVertex().getId(), 1);
			if (deg > maxDegree) {
				maxDegree = deg;
			}
		}

		counter = numberOfThreadsToUpdateWindow;

		Edge bestEdge = null;
		MemoryInfo memInfo = this.memory.getMemoryState();
		this.calculateBalances(memInfo);
		AssignedEdge bestAssignment = null;
		int windowSize = this.window.getEdges().size();
		int numberOfUpdatesPerThread = windowSize / numberOfThreadsToUpdateWindow;
		int rest = windowSize % numberOfThreadsToUpdateWindow;
		// ArrayList<WindowEdgeUpdater> updaterThreads= new
		// ArrayList<WindowEdgeUpdater>();;

		for (int i = 0; i < numberOfThreadsToUpdateWindow; i++) {
			// updaterThreads.add(new WindowEdgeUpdater(this.window,
			// this.numberOfPartitions, this.balances, this.memory, alpha, beta,
			// gamma));
			// set values and start
			if (i == numberOfThreadsToUpdateWindow - 1) {
				updaterThreads.get(i).setInformation(numberOfUpdatesPerThread * i, numberOfUpdatesPerThread + rest,
						maxDegree);
			} else {
				updaterThreads.get(i).setInformation(numberOfUpdatesPerThread * i, numberOfUpdatesPerThread, maxDegree);
			}
			// updaterThreads.get(i).start();
		}

		Edge edge = null;
		int assignToId = 0;
		double maxResult = -1.0;

		// Synchronization barrier --> wait for all threads to finish
		synchronized (lock) {
			while (counter != 0) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// Now we can get all results as threads are ready
		for (int i = 0; i < numberOfThreadsToUpdateWindow; i++) {
			// //join and get results
			// try {
			// updaterThreads.get(i).join();
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			double tempResult = updaterThreads.get(i).getMaxResult();
			if (tempResult > maxResult) {
				edge = updaterThreads.get(i).getEdge();
				assignToId = updaterThreads.get(i).getPartition();
				maxResult = tempResult;
			}
		}

		bestAssignment = new AssignedEdge(edge, assignToId);
		bestEdge = bestAssignment.getEdge();
		// System.out.println("Best edge: " + edge + "-> " + assignToId
		// + " (score: " + maxResult + ")");
		// System.out.println("--------------------");
		// System.out.println(this.approximatedNodeDistribution);

		this.window.getEdges().remove(bestEdge);
		this.window.setLastAssignedEdge(bestAssignment);

		return bestAssignment;
	}

	private void calculateBalances(MemoryInfo memInfo) {

		PartitionInfo info;
		double balScore;

		for (int partitionIdx = 0; partitionIdx < this.numberOfPartitions; partitionIdx++) {
			balScore = 0.0;
			info = this.memory.getPartitionInfo(partitionIdx);
			// bal Score is between 0 and 1 (if beta is 1)--- + 0.05 avoid
			// division by 0
			// value is higher if partition should get edges (greater distance
			// to maximum)
			balScore = (float) (beta * (memInfo.getMaxNumberEdges() - info.getTotalEdgeCount())
					/ (memInfo.getMaxNumberEdges() - memInfo.getMinNumberEdges() + 0.05));
			/*
			 * difference =
			 * Math.abs(info.getTotalEdgeCount()-memInfo.getAvgNumberEdges());
			 * if(difference > maximumDifference){ maximumDifference =
			 * difference; }
			 */
			this.balances.set(partitionIdx, balScore);
		}

		/*
		 * if(maximumDifference/memInfo.getAvgNumberEdges() > 0.1){ for (int
		 * partitionIdx = 0; partitionIdx < this.numberOfPartitions;
		 * partitionIdx++) { balances[partitionIdx] = balances[partitionIdx]*1;
		 * } }
		 */

	}

	/**
	 * Adds the Nodes from the edge to the list of approximated node-degrees or
	 * updates the degree if the node already has a value
	 * 
	 * @param edge
	 */
	public void updateApproximatedNodeDistribution(Edge edge) {
		if (approximatedNodeDistribution.containsKey(edge.getFirstVertex().getId())) {
			approximatedNodeDistribution.put(edge.getFirstVertex().getId(),
					approximatedNodeDistribution.get(edge.getFirstVertex().getId()) + 1);
		} else {
			approximatedNodeDistribution.put(edge.getFirstVertex().getId(), 1);
		}

		if (approximatedNodeDistribution.containsKey(edge.getSecondVertex().getId())) {
			approximatedNodeDistribution.put(edge.getSecondVertex().getId(),
					approximatedNodeDistribution.get(edge.getSecondVertex().getId()) + 1);
		} else {
			approximatedNodeDistribution.put(edge.getSecondVertex().getId(), 1);
		}
		// System.out.println(approximatedNodeDistribution);
	}

	/**
	 * Returns the highest degree of all vertices in the window.
	 * 
	 * @return
	 */
	public int getHighestDegreeInWindow() {
		int maxDegree = 0;
		for (Edge e : window.getEdges()) {
			Integer v1 = e.getFirstVertex().getId();
			Integer v2 = e.getSecondVertex().getId();
			Integer d1 = this.approximatedNodeDistribution.get(v1);
			Integer d2 = this.approximatedNodeDistribution.get(v2);
			if (d1 != null && d1 > maxDegree) {
				maxDegree = d1;
			}
			if (d2 != null && d2 > maxDegree) {
				maxDegree = d2;
			}
		}
		return maxDegree;
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

	public void killThreads() {
		for (WindowEdgeUpdater thread : updaterThreads) {
			thread.stop();
		}
	}
}
