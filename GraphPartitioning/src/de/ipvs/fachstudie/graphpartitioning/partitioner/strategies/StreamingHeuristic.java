package de.ipvs.fachstudie.graphpartitioning.partitioner.strategies;

import java.util.HashMap;
import java.util.HashSet;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.util.NamesOfStrategies;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class StreamingHeuristic implements SingleEdgePartitioningStrategy {

	private Memory memory;

	public StreamingHeuristic(Memory memory) {
		this.memory = memory;
	}

	@Override
	public int getPartitionForEdge(Edge edge) {
		// get a set containing all partitions the nodes are a member of
		HashSet<Integer> partitionSetNodeU = memory.getPartitionIdsOfVertex(edge.getFirstVertex().getId());
		HashSet<Integer> partitionSetNodeV = memory.getPartitionIdsOfVertex(edge.getSecondVertex().getId());

		@SuppressWarnings("unchecked")
		HashSet<Integer> intersection = (HashSet<Integer>) partitionSetNodeU.clone();
		// new HashSet<Partition>(partitionSetNodeU);
		intersection.retainAll(partitionSetNodeV);
		// Case 1
		if (intersection.size() != 0) {
			return memory.getMemoryState(intersection).getMinEdgesPartitionId();
		} else {
			HashSet<Integer> union = new HashSet<Integer>();
			union.addAll(partitionSetNodeU);
			union.addAll(partitionSetNodeV);
			// Case 2
			if (union.size() != 0) {
				return memory.getMemoryState(union).getMinEdgesPartitionId();
			} else {
				// Case 3
				return memory.getMemoryState().getMinEdgesPartitionId();
			}
		}
	}

	@Override
	public String toString() {
		return NamesOfStrategies.STREAMINGHEURISTIC.toString();
	}

	@Override
	public HashMap<String, Double> getParameters() {
		return new HashMap<String, Double>();

	}
}
