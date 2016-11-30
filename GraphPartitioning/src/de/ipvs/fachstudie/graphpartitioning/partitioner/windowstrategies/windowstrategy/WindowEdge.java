package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.windowstrategy;

import java.util.ArrayList;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.model.Vertex;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.PartitionInfo;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.AssignedEdge;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class WindowEdge extends Edge {
	private int[][] replications;
	int maxValue = 0;
	float specifity = 0;

	public WindowEdge(Edge e) {
		super(e.getFirstVertex(), e.getSecondVertex());
	}

	public int[][] getReplications() {
		return replications;
	}

	public float getSpecifity() {
		return this.specifity;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		// if (!(other instanceof WindowEdge))return false; //should also accept
		// edges
		Edge otherEdge = (Edge) other;
		return otherEdge.compareTo(this) == 0;
	}

	/**
	 * init or update replication scores for this edge
	 * 
	 * @param memory
	 * @param numberOfPartitions
	 * @param lastAssignedEdge
	 */
	public void calculateReplication(Memory memory, int numberOfPartitions, AssignedEdge lastAssignedEdge) {
		// replication was not calculated
		if (this.getReplications() == null) {
			this.initValues(memory, numberOfPartitions);
		} else {
			this.updateValues(lastAssignedEdge);
		}
	}

	/**
	 * Edge is active if one of incident vertices is replicated on at least one
	 * partition
	 * 
	 * @return
	 */
	public boolean isActive() {
		return maxValue > 0;
	}

	private void initValues(Memory memory, int numberOfPartitions) {
		PartitionInfo info;
		int u = this.getFirstVertex().getId();
		int v = this.getSecondVertex().getId();
		// each array has length 3 and is telling the following for each
		// partition:
		// [replicationDegree][id of vertex contributing to
		// replicationDegree][id of vertex contributing to replicationDegree]
		int[][] replications = new int[numberOfPartitions][3];

		for (int partitionIdx = 0; partitionIdx < numberOfPartitions; partitionIdx++) {
			info = memory.getPartitionInfo(partitionIdx);
			// initialize with -1 as this vertex id does not exist ->
			// uninitialized
			replications[partitionIdx][1] = -1;
			replications[partitionIdx][2] = -1;

			if (info.containsReplicationOfVertex(u)) {
				replications[partitionIdx][1] = u;
				replications[partitionIdx][0]++;
			}
			if (info.containsReplicationOfVertex(v)) {
				replications[partitionIdx][2] = v;
				replications[partitionIdx][0]++;
			}
			if (replications[partitionIdx][0] > maxValue) {
				maxValue = replications[partitionIdx][0];
			}
		}
		this.replications = replications;
		updateSpecifity();
	}

	private void updateSpecifity() {
		float sum = 0.0f;
		for (int i = 0; i < replications.length; i++) {
			sum += Math.abs(maxValue - replications[i][0]);
		}
		sum = sum / (float) (2 * (replications.length - 1));
		this.specifity = sum;

	}

	private void updateValues(AssignedEdge lastAssignedEdge) {
		if (lastAssignedEdge != null) {
			ArrayList<Vertex> commonVertices = this.commonVertices(lastAssignedEdge.getEdge());

			if (commonVertices.size() != 0) {
				boolean shouldUpdateSpecitiy = false;
				int assignedId = lastAssignedEdge.getPartitionId();

				for (Vertex vertex : commonVertices) {
					switch (this.replications[assignedId][0]) {
					// no replications on this partition
					case 0:
						this.replications[assignedId][1] = vertex.getId();
						this.replications[assignedId][0]++;
						if (replications[assignedId][0] > maxValue) {
							maxValue = replications[assignedId][0];
						}
						shouldUpdateSpecitiy = true;
						break;
					// one replication on this partition
					case 1:
						// only update if this commonVertex is not already known
						if (!(this.replications[assignedId][1] == vertex.getId()
								|| this.replications[assignedId][2] == vertex.getId())) {
							// find "unused" space for new replication value
							if (this.replications[assignedId][1] == -1) {
								this.replications[assignedId][1] = vertex.getId();
							} else {
								this.replications[assignedId][2] = vertex.getId();
							}
							this.replications[assignedId][0]++;
							if (replications[assignedId][0] > maxValue) {
								maxValue = replications[assignedId][0];
							}

							shouldUpdateSpecitiy = true;
						}
						break;
					// two replications on this partition
					case 2:
						break; // nothing to update
					}
				}
				if (shouldUpdateSpecitiy) {
					this.updateSpecifity();
				}
			}
		}
	}
}
