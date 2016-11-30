package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.stabilizedhdrf;

import java.util.HashMap;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.HDRFStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.SingleEdgePartitioningStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.AssignedEdge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.EdgeWindow;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.WindowBasedPartitioningStrategy;
import de.ipvs.fachstudie.graphpartitioning.util.NamesOfStrategies;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger HDRF with a window that returns a random edge. The
 *         purpose of this is to stabilize the HDRFStrategy in case the edges
 *         come in BFS order by adding randomness to the order of edges.
 */
public class StabilizedHDRF implements WindowBasedPartitioningStrategy {

	private SingleEdgePartitioningStrategy strategy;
	private StabilizationWindow window;

	public StabilizedHDRF(Memory memory, int windowSize) {
		this.strategy = new HDRFStrategy(memory);
		this.window = new StabilizationWindow(windowSize);
	}

	@Override
	public AssignedEdge getPartitionForEdge() {
		Edge edge = window.getRandomEdge();
		int partitionId = strategy.getPartitionForEdge(edge);
		this.window.removeEdge(edge);

		return new AssignedEdge(edge, partitionId);
	}

	@Override
	public EdgeWindow getWindow() {
		return window;
	}

	@Override
	public String toString() {
		return NamesOfStrategies.STABILIZEDHDRF.toString();
	}

	@Override
	public HashMap<String, Double> getParameters() {
		// TODO Auto-generated method stub
		return new HashMap<String, Double>();
	}

}
