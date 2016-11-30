package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.bestFittingEdge;

import java.util.HashMap;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.HDRFStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.AssignedEdge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.EdgeWindow;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.WindowBasedPartitioningStrategy;
import de.ipvs.fachstudie.graphpartitioning.util.NamesOfStrategies;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class BestFittingEdgeStrategy implements WindowBasedPartitioningStrategy {

	private HDRFStrategy componentStrategy;
	private EdgeWindow window;

	public BestFittingEdgeStrategy(Memory memory, int windowSize) {
		this.componentStrategy = new HDRFStrategy(memory);
		this.window = new EdgeWindow(windowSize);
	}

	public String toString() {
		return NamesOfStrategies.BESTFITTINGEDGE.toString();
	}

	@Override
	public AssignedEdge getPartitionForEdge() {
		// System.out.println("Winodowsize: " + window.getEdgeCount());
		int partitionId;
		double currentScore;
		double maximumScore = 0;
		int maximumPartition = 0;
		Edge maximumEdge = null;
		for (Edge currentEdge : window.getEdges()) {
			partitionId = componentStrategy.getPartitionForEdge(currentEdge);
			currentScore = componentStrategy.calculateScore(partitionId, currentEdge);
			// System.out.println("Size: "+ i + "partitionId" + partitionId +
			// "currentScore "+ currentScore + "Edge" + edge.toString());
			if (maximumScore <= currentScore) {
				maximumEdge = currentEdge;
				maximumPartition = partitionId;
				maximumScore = currentScore;
				// System.out.println("update");
			}
		}

		// System.out.println(maximumEdge.toString());
		// counter ++;
		// System.out.println("test"+ counter);
		this.window.removeEdge(maximumEdge);
		return new AssignedEdge(maximumEdge, maximumPartition);

	}

	@Override
	public EdgeWindow getWindow() {
		return this.window;
	}

	@Override
	public HashMap<String, Double> getParameters() {
		return new HashMap<String, Double>();
	}
}
