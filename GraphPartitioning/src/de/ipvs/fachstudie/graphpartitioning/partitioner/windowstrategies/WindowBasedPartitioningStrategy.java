package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies;

import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.PartitioningStrategy;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public interface WindowBasedPartitioningStrategy extends PartitioningStrategy {
	public AssignedEdge getPartitionForEdge();

	public EdgeWindow getWindow();

}
