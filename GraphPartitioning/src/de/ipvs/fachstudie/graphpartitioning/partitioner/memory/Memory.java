package de.ipvs.fachstudie.graphpartitioning.partitioner.memory;

import java.util.HashSet;
import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.model.Vertex;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 */
public interface Memory {
	/**
	 * Returns the number of partitions in the memory
	 * 
	 * @return
	 */
	public int getNumberOfPartitions();

	/**
	 * Stores the edge on the indicated partition.
	 * 
	 * @param e
	 * @param partitionId
	 */
	public void store(Edge e, int partitionId);

	/**
	 * Returns all the partition ids of the partitions where the given vertex is
	 * contained in.
	 * 
	 * @param id
	 * @return Set of ids.
	 */
	public HashSet<Integer> getPartitionIdsOfVertex(int id);

	/**
	 * Convenience method overloading getPartitionIdsOfVertex(int id)
	 * 
	 * @param v
	 * @return
	 */
	public HashSet<Integer> getPartitionIdsOfVertex(Vertex v);

	/**
	 * Prints the current content to the console.
	 */
	public void printMemoryContent();

	/**
	 * Returns a MemoryInfo object containing the following information about
	 * the current state of the partitions: - partition containing the most
	 * edges (id and edge count) - partition containing the least edges (id and
	 * edge count) - average amount of edges on the partitions
	 * 
	 * @return MemoryInfo object
	 */
	public MemoryInfo getMemoryState();

	/**
	 * Returns a MemoryInfo object containing the following information about
	 * the current state of the partitions whose ids are contained in the Set
	 * <Integer> partitionIds: - partition containing the most edges (id and
	 * edge count) - partition containing the least edges (id and edge count) -
	 * average amount of edges on the partitions
	 * 
	 * 
	 * @return MemoryInfo object
	 */
	public MemoryInfo getMemoryState(HashSet<Integer> partitionIds);

	/**
	 * Returns the indicated partition in a wrapper class that provides only
	 * getters so that data encapsulation is ensured.
	 * 
	 * @param id
	 * @return PartitionInfo
	 */
	public PartitionInfo getPartitionInfo(int id);

	/**
	 * Randomly removes replicas from partitions. This might have the effect of
	 * constrained partitioning with lower replication degree.
	 * 
	 * @param percentage
	 *            in [0,1]
	 */
	public void removeRandomReplicas(double percentage);

}
