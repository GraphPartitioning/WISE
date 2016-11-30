package de.ipvs.fachstudie.graphpartitioning.partitioner;

import java.util.List;

import de.ipvs.fachstudie.graphpartitioning.evaluation.EvaluationData;
import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.PartitionInfo;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.HDRFStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.PartitioningStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.SingleEdgePartitioningStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.AssignedEdge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.EdgeWindow;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.WindowBasedPartitioningStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.windowstrategy.WindowStrategy;
import de.ipvs.fachstudie.graphpartitioning.userInput.UserInputContainer;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;
import de.ipvs.fachstudie.graphpartitioning.util.FileReader;
import de.ipvs.fachstudie.graphpartitioning.writer.EdgeWriter;

/**
 * This class runs the partitioning strategy with all the constraints concerning
 * memory and number of partitions.
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 */

public class GraphPartitioner {

	private Memory memory;
	private PartitioningStrategy strategy;
	private EdgeWriter edgeWriter;
	private FileReader inputReader;
	private EvaluationData results;
	private UserInputContainer userInput;
	private WindowBasedPartitioningStrategy windowStrategy = null;
	private SingleEdgePartitioningStrategy singleEdgeStrategy = null;
	private EdgeWindow window = null;
	private boolean isWindowBasedStrategy = false;
	private long progressStep;
	private long progressCounter = 0;

	/**
	 * Partitioner constructor
	 * 
	 * @param algorithm
	 * @param inputPath
	 * @param partitionsCount
	 * @param memSize
	 * @param edgeWriter
	 */
	public GraphPartitioner(PartitioningStrategy strategy, UserInputContainer userInput, Memory memory,
			long timestamp) {

		this.memory = memory;
		this.strategy = strategy;
		this.edgeWriter = new EdgeWriter(strategy, timestamp, userInput.getNumberOfPartitions());
		this.inputReader = new FileReader(userInput.getGraphPath());
		this.userInput = userInput;
		this.progressStep = this.inputReader.getFileSize() / 600;

		if (this.strategy instanceof WindowBasedPartitioningStrategy) {
			windowStrategy = (WindowBasedPartitioningStrategy) this.strategy;
			window = windowStrategy.getWindow();
			isWindowBasedStrategy = true;
			userInput.setUseWindow(true);
		} else if (this.strategy instanceof SingleEdgePartitioningStrategy) {
			singleEdgeStrategy = (SingleEdgePartitioningStrategy) this.strategy;
			isWindowBasedStrategy = false;
			userInput.setUseWindow(false);
		}
	}

	public void setWindowSize(int size) {
		if (size > 0) {
			this.userInput.setWindowSize(size);
		}
	}

	public void setInputGraphPath(String input) {
		this.userInput.setGraphPath(input);
		this.inputReader = new FileReader(input);
	}

	public int getNumberOfPartitions() {
		return userInput.getNumberOfPartitions();
	}

	/**
	 * Returns a the indicated partition in a wrapper class that provides only
	 * getters so that data encapsulation is ensured.
	 * 
	 * @param id
	 * @return PartitionInfo
	 */
	public PartitionInfo getPartitionInfo(int id) {
		return memory.getPartitionInfo(id);
	}

	/**
	 * Runs the partitioning strategy on the given input graph. Sends every edge
	 * to the processing environment and starts the evaluation on the processing
	 * environment after finishing the partitioning.
	 */
	public void computePartitioning() {
		Edge edge;
		AssignedEdge assignedEdge;
		long execTime = System.currentTimeMillis();
		int partitionId;

		initProgressBar();

		if (isWindowBasedStrategy) {
			// window handling
			while (inputReader.hasNextEdge() || !window.isEmpty()) {
				List<Edge> newEdges = inputReader.fillWindow(window);

				// for window flushing
				// double r = Math.random();
				// if(r >
				// (double)(window.getSize()-1)/(double)window.getSize()){
				//// while(window.hasMoreEdges()){
				// for(int k=0; k<window.getEdgeCount()/2; k++) {
				//// System.out.println("Flush window with " +
				// window.getSize());
				// assignedEdge = windowStrategy.getPartitionForEdge();
				// partitionId = assignedEdge.getPartitionId();
				// edge = assignedEdge.getEdge();
				// memory.store(edge, partitionId);
				// edgeWriter.setEdgeAssignment(edge, partitionId);
				// // life sign
				// progress();
				// }
				// } else {
				assignedEdge = windowStrategy.getPartitionForEdge();
				if (assignedEdge!=null) {
					partitionId = assignedEdge.getPartitionId();
					edge = assignedEdge.getEdge();
					memory.store(edge, partitionId);
					edgeWriter.setEdgeAssignment(edge, partitionId);
					// life sign
					progress();
					// }
				} else {
					System.err.println("Problem: there was a corrupt edge assignment...");
				}
			}
		} else {
			// single edge handling
			while (inputReader.hasNextEdge()) {
				edge = inputReader.getNextEdge();
				if (edge == null) {
					continue;
				}
				// determine the partition for this edge with strategy
				partitionId = singleEdgeStrategy.getPartitionForEdge(edge);
				// this is the limited local memory, so we won't store the whole
				// graph
				memory.store(edge, partitionId);
				// memory.printMemoryContent(); // FOR DEBUGGING
				// this models the exterior system with its content
				edgeWriter.setEdgeAssignment(edge, partitionId);
				// life sign
				progress();
			}
		}
		edgeWriter.writeAllBuffersToFile();
		// statistics
		execTime = System.currentTimeMillis() - execTime;
		this.setResults(
				new EvaluationData(this.strategy.toString(), this.strategy.getParameters(), this.userInput, execTime));
	}

	/**
	 * Results are the parameters of the algorithm and the runtime
	 * 
	 * @return
	 */
	public EvaluationData getResults() {
		return results;
	}

	private void setResults(EvaluationData results) {
		this.results = results;
	}

	/**
	 * Calculates when the next step in the progressbar has to be set
	 */
	public void progress() {
		progressCounter++;
		if (progressCounter % progressStep == 0) {
			System.out.print("|");
			// for (int i=0; i<memory.getNumberOfPartitions(); i++) {
			// System.out.print(memory.getPartitionInfo(i).getTotalEdgeCount() +
			// "\t");
			// }
			// System.out.println();
		}
	}

	/**
	 * Estimates step of progress bar
	 */
	public void initProgressBar() {
		long fileSize = this.inputReader.getFileSize();
		long estimatedNumberOfEdges;
		double divisor = 6.1;
		long b = 0;

		// longer files mean more edges, more edges means longer node ids
		if (fileSize > 5000) {
			divisor = 7.5;
		}
		if (fileSize > 10000) {
			divisor = 7.7;
		}
		if (fileSize > 50000) {
			divisor = 8;
		}
		if (fileSize > 100000) {

		}
		if (fileSize > 200000) {
			divisor = 9;
		}
		if (fileSize > 500000) {
			divisor = 9.5;
		}
		if (fileSize > 700000) {
			divisor = 10;
		}
		if (fileSize > 1000000) {
			divisor = 1 / 0.0679;
			b = -16326;
		}

		estimatedNumberOfEdges = (long) (fileSize / divisor) + b;
		System.out.println("There are approx. " + estimatedNumberOfEdges + " edges in this graph.");
		Configuration.MINIMAL_GRAPH_SIZE = estimatedNumberOfEdges; // puffer of 20%
		this.progressStep = estimatedNumberOfEdges / 35;
		if (this.progressStep == 0) {
			this.progressStep = 1;
		}

		System.out.print("       0% |");
		for (int i = 0; i < 35; i++) {
			System.out.print(" ");
		}
		System.out.print("| 100% estimated\nProgress: ");
	}
}
