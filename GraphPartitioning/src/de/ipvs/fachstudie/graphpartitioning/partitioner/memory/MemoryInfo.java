package de.ipvs.fachstudie.graphpartitioning.partitioner.memory;

import java.util.Date;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger Contains the information about the current memory state.
 */
public class MemoryInfo {
	private long maxNumberEdges;
	private long minNumberEdges;
	private double avgNumberEdges;
	private int maxEdgesPartitionId;
	private int minEdgesPartitionId;
	private Date timeStamp;

	MemoryInfo(long maxNumberEdges, long minNumberEdges, double avgNumberEdges, int maxEdgesPartitionId,
			int minEdgesPartitionId) {

		this.maxNumberEdges = maxNumberEdges;
		this.minNumberEdges = minNumberEdges;
		this.avgNumberEdges = avgNumberEdges;
		this.maxEdgesPartitionId = maxEdgesPartitionId;
		this.minEdgesPartitionId = minEdgesPartitionId;
		this.timeStamp = new Date();
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public long getMaxNumberEdges() {
		return maxNumberEdges;
	}

	public long getMinNumberEdges() {
		return minNumberEdges;
	}

	public double getAvgNumberEdges() {
		return avgNumberEdges;
	}

	public int getMaxEdgesPartitionId() {
		return maxEdgesPartitionId;
	}

	public int getMinEdgesPartitionId() {
		return minEdgesPartitionId;
	}
}
