package de.ipvs.fachstudie.graphpartitioning.partitioner.memory;

import java.util.ArrayList;
import java.util.HashSet;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.model.Partition;
import de.ipvs.fachstudie.graphpartitioning.model.Vertex;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.limitedMemoryAgingData.AgingDataContainer;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 *         Limited memory prevents the memory from overflow if the graph is too
 *         big. It uses the AgingDataContainer for this functionality. The limit
 *         can be turned of with 0.
 */
public class LimitedMemory implements Memory {
	private AgingDataContainer dataContainer;
	private ArrayList<Partition> partitions;

	// Constructor
	public LimitedMemory(int maxMemorySize, int partitionsCount, boolean on) {
		dataContainer = new AgingDataContainer(maxMemorySize + 1, on);
		partitions = new ArrayList<Partition>();

		// init partitions
		Partition newPartition;
		for (int i = 0; i < partitionsCount; i++) {
			newPartition = new Partition(i);
			partitions.add(newPartition);
			dataContainer.addDeleteListener(newPartition);
		}
	}

	public int getNumberOfPartitions() {
		return this.partitions.size();
	}

	/**
	 * Stores the edge on the indicated partition.
	 * 
	 * @param e
	 * @param partitionId
	 */
	public void store(Edge e, int partitionId) {
		// defensive programming: check range of partitions first
		if (partitionId >= 0 && partitionId < this.partitions.size()) {
			Vertex first = e.getFirstVertex();
			Vertex second = e.getSecondVertex();

			// store edge on the partition it was assigned to
			this.partitions.get(partitionId).addEdge(e);
			// store or update vertices of edge in the AgingDataContainer
			dataContainer.store(first, partitionId);
			dataContainer.store(second, partitionId);

		} else {
			// partition id out of range
			System.err.println("[MEMORY ERROR] Partition id doesn't exist! id = " + partitionId
					+ ", partitions.size() = " + partitions.size());
		}
	}

	/**
	 * Returns all the partition ids of the partitions where the given vertex is
	 * contained in.
	 * 
	 * @param id
	 * @return Set of ids.
	 */
	public HashSet<Integer> getPartitionIdsOfVertex(int id) {
		HashSet<Integer> partitionIdsOfVertex = (HashSet<Integer>) dataContainer.accessData(id);
		if (partitionIdsOfVertex == null) {
			return new HashSet<Integer>();
		}

		return partitionIdsOfVertex;
	}

	// convinience method overloading getPartitionIdsOfVertex(int id)
	public HashSet<Integer> getPartitionIdsOfVertex(Vertex v) {
		System.out.println("getPartitionsOfVertex: " + getPartitionIdsOfVertex(v.getId()));
		return getPartitionIdsOfVertex(v.getId());
	}

	/**
	 * Prints the current content of AgingDataContainer to the console.
	 */
	public void printMemoryContent() {
		dataContainer.printContent();
	}

	/**
	 * Returns a MemoryInfo object containing the following information about
	 * the current state of the partitions: - partition containing the most
	 * edges (id and edge count) - partition containing the least edges (id and
	 * edge count) - average amount of edges on the partitions
	 * 
	 * Of course our knowledge is limited since we can't keep all edges in
	 * memory. So depending on the LimitedMemory's size the data is more or less
	 * precise.
	 * 
	 * @return MemoryInfo object
	 */
	public MemoryInfo getMemoryState() {
		HashSet<Integer> partitionIds = new HashSet<Integer>();
		for (Partition p : partitions) {
			partitionIds.add(p.getId());
		}

		return getMemoryState(partitionIds);
	}

	/**
	 * Returns a MemoryInfo object containing the following information about
	 * the current state of the partitions whose ids are contained in the Set
	 * <Integer> partitionIds: - partition containing the most edges (id and
	 * edge count) - partition containing the least edges (id and edge count) -
	 * average amount of edges on the partitions
	 * 
	 * Of course our knowledge is limited since we can't keep all edges in
	 * memory. So depending on the LimitedMemory's size the data is more or less
	 * precise.
	 * 
	 * @return MemoryInfo object
	 */
	public MemoryInfo getMemoryState(HashSet<Integer> partitionIds) {
		Partition pMax = new Partition(Integer.MAX_VALUE);
		long max = Long.MIN_VALUE;
		Partition pMin = new Partition(Integer.MAX_VALUE);
		long min = Long.MAX_VALUE;
		long tempSize;
		long totalEdges = 0;
		double avgEdgeCount = 0.0;

		// get all the partitions from by id
		ArrayList<Partition> partitions = new ArrayList<Partition>();
		for (int pID : partitionIds) {
			partitions.add(this.partitions.get(pID));
		}

		// compute min/max/avg
		for (Partition p : partitions) {
			tempSize = p.getEdgeCount();
			totalEdges += tempSize;
			if (tempSize >= max) {
				if (tempSize == max) {
					if (p.getId() < pMax.getId()) {
						max = tempSize;
						pMax = p;
					}
				} else {
					max = tempSize;
					pMax = p;
				}
			}
			if (tempSize <= min) {
				if (tempSize == min) {
					if (p.getId() < pMin.getId()) {
						min = tempSize;
						pMin = p;
					}
				} else {
					min = tempSize;
					pMin = p;
				}
			}
		}
		avgEdgeCount = (double) totalEdges / (double) partitions.size();
		return new MemoryInfo(max, min, avgEdgeCount, pMax.getId(), pMin.getId());
	}

	/**
	 * Returns a the indicated partition in a wrapper class that provides only
	 * getters so that data encapsulation is ensured.
	 * 
	 * @param id
	 * @return PartitionInfo
	 */
	public PartitionInfo getPartitionInfo(int id) {
		return new PartitionInfo(this.partitions.get(id));
	}

	@Override
	public void removeRandomReplicas(double percentage) {
		System.out.println("[ERROR] removeRandomReplicas() not implemented in LimitedMemory");

	}

}
