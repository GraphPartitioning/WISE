package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.adaptiveWindowStrategy;

import java.util.ArrayList;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.model.Vertex;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.PartitionInfo;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.AssignedEdge;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;

public class WindowEdge_LA extends Edge {
	public double[] scores;	// scores for all partitions
	public double maxScore = 0;

	public int[] replicaScores;
	public int maxRepScore;
	
	public WindowEdge_LA(Edge e, int numberOfPartitions) {
		super(e.getFirstVertex(), e.getSecondVertex());
		replicaScores = new int[numberOfPartitions];
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
	 * Initializes the replication scores for each partition
	 * @param memory
	 * @param numberOfPartitions
	 */
	public void updateRepScores(Memory memory, int numberOfPartitions) {
		replicaScores = new int[numberOfPartitions];
		int u = this.getFirstVertex().getId();
		int v = this.getSecondVertex().getId();
		this.maxRepScore = 0;
		for (int p_id = 0; p_id<numberOfPartitions; p_id++) {
			if (replicaScores[p_id]<2) { // otherwise skip new score computation				
				// replication score
				int repScore = 0;
				if (memory.getPartitionInfo(p_id).containsReplicationOfVertex(u)) {
					repScore++;
				}
				if (memory.getPartitionInfo(p_id).containsReplicationOfVertex(v)) {
					repScore++;
				}
				replicaScores[p_id] = repScore;
				if (repScore>maxRepScore) {
					this.maxRepScore = repScore;
				}
			}
		}
	}
	

	public void updateScores(Memory memory, int numberOfPartitions,
			ArrayList<Double> balanceScores, double alpha, double gamma,
			double delta) {
		
		// we assume rep scores of all edges are already up to date
		//updateRepScores(memory, numberOfPartitions);
		
		scores = new double[numberOfPartitions];
		maxScore = -1;
		int bestPartition = 0;

		// specifity score (does not change for each partition)
		double specificityScore = getSpecifity();
		
//		// calculate relative degree
//		double deg_v1 = WindowStrategy_LA.approximatedNodeDistribution.
//				getOrDefault(getFirstVertex().getId(),1);
//		double deg_v2 = WindowStrategy_LA.approximatedNodeDistribution.
//				getOrDefault(getSecondVertex().getId(),1);
//		double rel_deg_v1 = deg_v1 / (deg_v1+deg_v2);
//		double rel_deg_v2 = deg_v2 / (deg_v1+deg_v2);
		
		for (int p_id = 0; p_id<numberOfPartitions; p_id++) {
			
			// replication score
			int repScore = replicaScores[p_id];
			
			// degree score (does not change for each partition
			double degreeScore = getDegreeScore(p_id, memory);
			
//			// calculate HDRF-like repScore
//			double repScore = 0;
//			if (memory.getPartitionInfo(p_id).containsReplicationOfVertex(
//					getFirstVertex().getId())) {
//				repScore += 1 + (1-rel_deg_v1);
//			}
//			if (memory.getPartitionInfo(p_id).containsReplicationOfVertex(
//					getSecondVertex().getId())) {
//				repScore += 1 + (1-rel_deg_v2);
//			}
			
//			// calculate degree-aware optimization -> reinforce placement of low-degree v..s
//			int degreeScoreInner = 0;
//			if (deg_v1<=deg_v2) {
//				if (memory.getPartitionInfo(p_id).containsReplicationOfVertex(
//						getFirstVertex().getId())) {
//					// v1 is low-degree AND replicated on p_id
//					degreeScoreInner += 1;
//				}
//			} 
//			if (deg_v2<=deg_v1) {
//				if (memory.getPartitionInfo(p_id).containsReplicationOfVertex(
//						getSecondVertex().getId())) {
//					// v1 is low-degree AND replicated on p_id
//					degreeScoreInner += 1;
//				}
//			}
			
			// balance score (already multiplied with beta)
			double balanceScore = balanceScores.get(p_id);
			
			// total score
			double score = alpha * repScore + balanceScore 
					+ gamma * specificityScore + delta * degreeScore;
//			System.out.println("Rep score: " + repScore);
//			System.out.println("Degree score: " + degreeScore);
//			System.out.println("Balance score: " + balanceScore);
//			System.out.println("Specificity sc: " + specificityScore);
//			System.out.println();
			scores[p_id] = score;
			if (score>maxScore) {
				maxScore = score;
				bestPartition = p_id;
			}
		}
//		System.out.println(Configuration.WINDOWSTRATEGY_BETA);
//		System.out.println("Best: " + maxScore);
//		System.out.println("Best partition: " + bestPartition);
//		System.out.println("----------------------");
	}

	public double getSpecifity(){
		float sum = 0.0f;
		for(int i = 0; i< replicaScores.length; i++){
			sum += Math.abs(this.maxRepScore - replicaScores[i]);	
		}
		sum = sum/(float) (2*(replicaScores.length-1));
		return sum;
	}
	
	
	public double getDegreeScore(int p_id, Memory memory) {
		double degreeScore = 0;
		int v1 = this.getFirstVertex().getId();
		int v2 = this.getSecondVertex().getId();
		if (memory.getPartitionInfo(p_id).containsReplicationOfVertex(v1)) {
			// vertex v1 is replicated on p_id
			int deg_v1 = Degree.deg(v1,1);
			int deg_v2 = Degree.deg(v2,1);
			degreeScore += 1-(double)deg_v1/(double)(deg_v1+deg_v2);
		}
		if (memory.getPartitionInfo(p_id).containsReplicationOfVertex(v2)) {
			// vertex v2 is replicated on p_id
			int deg_v1 = Degree.deg(v1,1);
			int deg_v2 = Degree.deg(v2,1);
			degreeScore += 1-(double)deg_v2/(double)(deg_v1+deg_v2);
		}
		return degreeScore;
	}
	
//	public double getDegreeScore2() {
//		// calculate degreeScore between 0 and 1
//		int v1 = this.getFirstVertex().getId();
//		int v2 = this.getSecondVertex().getId();
//		int edgeDegree = 0;
//		edgeDegree += WindowStrategy_LA.degreeTable.
//				getOrDefault(v1, 1);
//		edgeDegree += WindowStrategy_LA.degreeTable.
//				getOrDefault(v2, 1);
//		double degreeScore;
//		if (edgeDegree>1) {
//			degreeScore = (double)(WindowStrategy_LA.maxEdgeDegree-edgeDegree) / 
//					(double)(WindowStrategy_LA.maxEdgeDegree+0.00001);
//		} else {
//			// ignore edges with little information
//			degreeScore = 0;
//		}
//		return degreeScore;
//	}
}
