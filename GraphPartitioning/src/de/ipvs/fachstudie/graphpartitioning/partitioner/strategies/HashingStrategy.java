package de.ipvs.fachstudie.graphpartitioning.partitioner.strategies;

import java.util.HashMap;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.util.NamesOfStrategies;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 *         Primitive hash strategy for testing purposes.
 */
public class HashingStrategy implements SingleEdgePartitioningStrategy {
	private int counter = 0;
	private int numberOfPartitions;

	public HashingStrategy(Memory memory) {
		this.numberOfPartitions = memory.getNumberOfPartitions();
	}

	/**
	 * Distribute edges over partitions evenly.
	 */
	@Override
	public int getPartitionForEdge(Edge edge) {
		this.counter++;
		this.counter = this.counter % numberOfPartitions;
		return this.counter;

	}

	@Override
	public String toString() {
		return NamesOfStrategies.HASHING.toString();
	}

	@Override
	public HashMap<String, Double> getParameters() {
		return new HashMap<String, Double>();
	}
}
