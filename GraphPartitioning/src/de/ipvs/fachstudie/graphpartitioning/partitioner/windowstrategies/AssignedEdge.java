package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class AssignedEdge {
	private Edge edge;
	private int partitionId;

	public AssignedEdge(Edge e, int id) {
		this.edge = e;
		this.partitionId = id;
	}

	public Edge getEdge() {
		return edge;
	}

	public int getPartitionId() {
		return partitionId;
	}
}
