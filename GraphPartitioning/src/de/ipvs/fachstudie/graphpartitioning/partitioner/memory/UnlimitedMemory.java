package de.ipvs.fachstudie.graphpartitioning.partitioner.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.model.Partition;
import de.ipvs.fachstudie.graphpartitioning.model.Vertex;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 */
public class UnlimitedMemory implements Memory {
	private HashMap<Integer, Set<Integer>> dataContainer;
	private ArrayList<Partition> partitions;

	public UnlimitedMemory(int partitionsCount) {
		this.dataContainer = new HashMap<Integer, Set<Integer>>();
		this.partitions = new ArrayList<Partition>();

		// init partitions
		Partition newPartition;
		for (int i = 0; i < partitionsCount; i++) {
			newPartition = new Partition(i);
			partitions.add(newPartition);
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
	@Override
	public void store(Edge e, int partitionId) {
		// defensive programming: check range of partitions first
		if (partitionId >= 0 && partitionId < this.partitions.size()) {
			Vertex first = e.getFirstVertex();
			Vertex second = e.getSecondVertex();
			int firstId = first.getId();
			int secondId = second.getId();

			// store edge on the partition it was assigned to
			this.partitions.get(partitionId).addEdge(e);

			// update partitions where vertex is contained
			Set<Integer> vertexData = null;
			if (dataContainer.containsKey(firstId)) {
				vertexData = dataContainer.get(firstId);
			} else {
				vertexData = new HashSet<Integer>();
			}

			vertexData.add(partitionId);
			dataContainer.put(firstId, vertexData);
			vertexData = null;

			if (dataContainer.containsKey(secondId)) {
				vertexData = dataContainer.get(secondId);
			} else {
				vertexData = new HashSet<Integer>();
			}
			vertexData.add(partitionId);
			dataContainer.put(secondId, vertexData);
			vertexData = null;

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
	@Override
	public HashSet<Integer> getPartitionIdsOfVertex(int id) {
		HashSet<Integer> partitionIdsOfVertex = (HashSet<Integer>) dataContainer.get(id);
		if (partitionIdsOfVertex == null) {
			return new HashSet<Integer>();
		}

		return partitionIdsOfVertex;
	}

	// convinience method overloading getPartitionIdsOfVertex(int id)
	@Override
	public HashSet<Integer> getPartitionIdsOfVertex(Vertex v) {
		return getPartitionIdsOfVertex(v.getId());
	}

	/**
	 * Prints the current content of AgingDataContainer to the console.
	 */
	@Override
	public void printMemoryContent() {
		for (Entry<Integer, Set<Integer>> entry : this.dataContainer.entrySet()) {
			System.out.println(entry.getKey() + "\tis on partition(s):\t" + entry.getValue().toString());
		}
	}

	/**
	 * Returns a MemoryInfo object containing the following information about
	 * the current state of the partitions: - partition containing the most
	 * edges (id and edge count) - partition containing the least edges (id and
	 * edge count) - average amount of edges on the partitions
	 * 
	 * @return MemoryInfo object
	 */
	@Override
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
	@Override
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
	@Override
	public PartitionInfo getPartitionInfo(int id) {
		return new PartitionInfo(this.partitions.get(id));
	}

	/**
	 * Randomly removes replicas from partitions. This might have the effect of
	 * constrained partitioning with lower replication degree.
	 * 
	 * @param percentage
	 *            in [0,1]
	 */
	@Override
	public void removeRandomReplicas(double percentage) {
		Random random = new Random();
		int count = 0;
		for (Integer v : dataContainer.keySet()) {
			List<Integer> replicas = new ArrayList<Integer>(dataContainer.get(v));
			int numberOfVerticesToRemove = (int) Math.round(replicas.size() * percentage);
			for (int i = 0; i < numberOfVerticesToRemove; i++) {
				int randIndex = random.nextInt(replicas.size());
				replicas.remove(randIndex);
				count++;
			}
			Set<Integer> newReplicas = new HashSet<Integer>(replicas);
			dataContainer.put(v, newReplicas);
		}
		// System.out.println(count + " vertices removed!");
	}

}
