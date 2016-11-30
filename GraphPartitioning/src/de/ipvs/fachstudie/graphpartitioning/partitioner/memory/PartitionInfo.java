package de.ipvs.fachstudie.graphpartitioning.partitioner.memory;

import de.ipvs.fachstudie.graphpartitioning.model.Partition;
import de.ipvs.fachstudie.graphpartitioning.model.Vertex;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 *         Wrapper class for partition objects. Is necessary to ensure data
 *         encapsulation and memory consistency. This class provides only
 *         getters and no setters.
 * 
 */
public class PartitionInfo {
	private Partition partition;

	public PartitionInfo(Partition p) {
		this.partition = p;
	}

	// getter/setter
	public int getId() {
		return this.partition.getId();
	}

	/**
	 * 
	 * @param id
	 * @return true, if the vertex with the id 'id' is part of the partition
	 */
	public boolean containsReplicationOfVertex(int id) {
		return this.partition.containsReplicationOfVertex(id);
	}

	/**
	 * 
	 * @param id
	 * @return true, if the vertex with v is part of the partition
	 */
	public boolean containsReplicatoinOfVertex(Vertex v) {
		return this.partition.containsReplicatoinOfVertex(v);
	}

	/**
	 * The number can be smaller than the true amount of vertices, if some of
	 * them were deleted to match the memory limit.
	 * 
	 * @return the number of vertices still stored.
	 */
	public long getCurrentVertexCount() {
		return this.partition.getVertexCount();
	}

	/**
	 * 
	 * @return the number of all edges, even if they aren't stored anymore
	 *         because of the limited memory.
	 */
	public long getTotalEdgeCount() {
		return this.partition.getEdgeCount();
	}

}
