package de.ipvs.fachstudie.graphpartitioning.partitioner.strategies;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.MemoryInfo;
import de.ipvs.fachstudie.graphpartitioning.util.NamesOfStrategies;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 *         The Degree algorithm as described in the S-PowerGraph Paper
 *
 */
public class SPowerGraphDegree implements SingleEdgePartitioningStrategy {
	private Edge e;
	private double maxEdgeCount;
	private double minEdgeCount;
	private long currentPartitionsEdgeCount;
	private double averageEdgesPerPartition;
	// allowed imbalance between max/average nodes on partitions
	private final double epsilon = 0.1;
	private Map<Integer, Double> degreeDistribution = new HashMap<Integer, Double>();
	private Memory memory;

	public SPowerGraphDegree(Memory memory) {
		this.memory = memory;
	}

	/**
	 * Main method of the algorithm. This method implements the algorithm DEGREE
	 * as it is described in the S-PowerGraph paper. It returns for a given edge
	 * the optimal partition where to add the current edge in order to achieve
	 * optimal balance and replication.
	 */
	@Override
	public int getPartitionForEdge(Edge edge) {
		this.e = edge;
		double maxScore = 0.0;
		double currentScore = 0.0;
		double ratio;
		int bestPartitionId = 0;
		Set<Integer> bestOptions = new TreeSet<Integer>();
		boolean onlyBalance = false;

		initializeIterationStep();

		ratio = ((double) maxEdgeCount / (double) averageEdgesPerPartition);
		onlyBalance = (ratio >= (1.0 + epsilon)) ? true : false;
		int partitionsCount = memory.getNumberOfPartitions();

		for (int partitionId = 0; partitionId < partitionsCount; partitionId++) {
			if (onlyBalance) {
				// only optimize balance
				currentScore = getBalance(partitionId);
			} else {
				// optimize overall score
				currentScore = getScoreOfPartition(partitionId);
			}

			if (currentScore > maxScore) {
				bestPartitionId = partitionId;
				maxScore = currentScore;
				bestOptions.clear();
				bestOptions.add(partitionId);
			} else if (currentScore == maxScore) {
				bestOptions.add(partitionId);
			}
		}
		// tie breaker necessary => choose randomly from the best partitions.
		if (bestOptions.size() > 1) {
			Object[] options = bestOptions.toArray();
			bestPartitionId = (int) options[options.length / 2];
		}
		return bestPartitionId;
	}

	/**
	 * This method returns the score of the given partition in the current state
	 * of the iteration.
	 * 
	 * @param partitionId
	 * @return
	 */
	private double getScoreOfPartition(int partitionId) {
		double score = 0.0;
		int u = e.getFirstVertex().getId();
		int v = e.getSecondVertex().getId();
		double degreeU;
		double degreeV;

		Set<Integer> replicationsU = new TreeSet<Integer>();
		Set<Integer> replicationsV = new TreeSet<Integer>();

		replicationsU = memory.getPartitionIdsOfVertex(u);
		replicationsV = memory.getPartitionIdsOfVertex(v);

		degreeU = getDegreeEstimation(u);
		degreeV = getDegreeEstimation(v);

		if (replicationsU.contains(partitionId)) {
			// partition already contains a replication of vertex u
			score += 1.0;
		}
		if (replicationsV.contains(partitionId)) {
			// partition already contains a replication of vertex v
			score += 1.0;
		}
		if (degreeU <= degreeV) {
			if (replicationsU.contains(u)) {
				// smaller degree
				score += 1.0;
			}
		}
		if (degreeV <= degreeU) {
			if (replicationsV.contains(v)) {
				// smaller degree
				score += 1.0;
			}
		}
		score += getBalance(partitionId);
		return score;
	}

	/**
	 * Calculates the balance of the given partition. For the balance value we
	 * use the algorithm from the paper.
	 * 
	 * @param partitionId
	 * @return balance
	 */
	private double getBalance(int partitionId) {
		currentPartitionsEdgeCount = memory.getPartitionInfo(partitionId).getTotalEdgeCount();

		return ((double) (maxEdgeCount - currentPartitionsEdgeCount)) / ((double) (maxEdgeCount - minEdgeCount + 1.0));
	}

	/**
	 * Sets the global variables minEdgeCount, maxEdgeCount and
	 * averageEdgesPerPartition. These values have to be actualized after each
	 * assignment of an edge.
	 */
	private void initializeIterationStep() {
		MemoryInfo info = memory.getMemoryState();

		minEdgeCount = info.getMinNumberEdges();
		maxEdgeCount = info.getMaxNumberEdges();
		averageEdgesPerPartition = info.getAvgNumberEdges();
	}

	/**
	 * Estimates the degree of a given node. The degree is for indirected edges.
	 * The method models a power-law distribution of vertex degrees.
	 * 
	 * @param vertex
	 * @return estimated degree of vertex
	 */
	private double getDegreeEstimation(int vertex) {
		double degree;

		if (degreeDistribution.containsKey(vertex)) {
			degree = degreeDistribution.get(vertex);
			degree++;
			degreeDistribution.put(vertex, degree);
		} else {
			degreeDistribution.put(vertex, 1.0);
			degree = 1.0;
		}
		return degree;
	}

	@Override
	public String toString() {
		return NamesOfStrategies.DEGREE.toString();
	}

	@Override
	public HashMap<String, Double> getParameters() {
		return new HashMap<String, Double>();

	}
}