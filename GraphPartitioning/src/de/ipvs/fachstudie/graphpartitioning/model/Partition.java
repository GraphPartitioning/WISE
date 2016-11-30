package de.ipvs.fachstudie.graphpartitioning.model;

import gnu.trove.set.hash.TIntHashSet;

import java.util.HashSet;

import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.limitedMemoryAgingData.AgingDataEntry;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.limitedMemoryAgingData.DeleteListener;

/**
 * Models a partition. Is used in the memory of the algorithm. Can be used in a
 * limited memory.
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 */
public class Partition implements DeleteListener<Integer> {

	private int id;
	private long vertexCount = 0;
	private long edgeCount = 0;
	private HashSet<Integer> vertexIdsToRemove = new HashSet<Integer>();
	private TIntHashSet vertexIds = new TIntHashSet();
	private final int maxUntilDelete = 500;

	// Constructors
	public Partition(int id) {
		this.id = id;
	}

	// getter/setter
	public int getId() {
		return this.id;
	}

	public void addEdge(Edge e) {
		this.edgeCount++;
		this.vertexIds.add(e.getFirstVertex().getId());
		this.vertexIds.add(e.getSecondVertex().getId());
		this.vertexCount = this.vertexIds.size();
	}

	/**
	 * 
	 * @param id
	 * @return true, if the vertex with the id 'id' is part of the partition
	 */
	public boolean containsReplicationOfVertex(int id) {
		return this.vertexIds.contains(id);
	}

	/**
	 * 
	 * @param id
	 * @return true, if the vertex with v is part of the partition
	 */
	public boolean containsReplicatoinOfVertex(Vertex v) {
		return this.vertexIds.contains(v.getId());
	}

	/**
	 * The number can be smaller than the true amount of vertices, if some of
	 * them were deleted to match the memory limit.
	 * 
	 * @return the number of vertices still stored.
	 */
	public long getVertexCount() {
		return this.vertexCount;
	}

	/**
	 * 
	 * @return the number of all edges, even if they aren't stored anymore
	 *         because of the limited memory.
	 */
	public long getEdgeCount() {
		return edgeCount;
	}

	/**
	 * Deletes all appearances of the deletedData
	 * 
	 * used in limitedMemory
	 */
	@Override
	public void onEntryDeleted(AgingDataEntry deletedData) {
		this.vertexIdsToRemove.add(deletedData.getId());
		if (this.vertexIdsToRemove.size() == this.maxUntilDelete) {
			this.vertexIds.removeAll(this.vertexIdsToRemove);
			this.vertexIdsToRemove.clear();
		}

	}
}
