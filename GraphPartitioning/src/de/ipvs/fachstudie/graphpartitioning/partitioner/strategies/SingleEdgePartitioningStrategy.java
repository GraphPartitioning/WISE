package de.ipvs.fachstudie.graphpartitioning.partitioner.strategies;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 */
public interface SingleEdgePartitioningStrategy extends PartitioningStrategy {
	/**
	 * Assign the given edge to a partition in this method.
	 * 
	 * @param edge
	 * @return partitionid
	 */
	public int getPartitionForEdge(Edge edge);

}
