package de.ipvs.fachstudie.graphpartitioning.partitioner.strategies;

import java.util.HashMap;
import java.util.HashSet;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.MemoryInfo;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;
import de.ipvs.fachstudie.graphpartitioning.util.NamesOfStrategies;

/**
 * HighDegree (are) Replicated First Algorithm
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class HDRFStrategy implements SingleEdgePartitioningStrategy {

	private HashMap<Integer, Integer> approximatedNodeDistribution = new HashMap<Integer, Integer>();
	private double lambda;
	private double epsilon;
	private long minSize;
	private long maxSize;
	private Memory memory;
	private int pid;
	private double hashProbability;
	private HashSet<Integer> allPartitions;

	public HDRFStrategy(double lambda, double epsilon, Memory memory) {
		this.lambda = lambda;
		this.epsilon = epsilon;
		this.memory = memory;
		this.hashProbability = 0;
		this.allPartitions = new HashSet<Integer>();
		for (int i = 0; i < memory.getNumberOfPartitions(); i++) {
			this.allPartitions.add(i);
		}
	}

	/**
	 * Lambda = 1.0001 Epsilon = 1
	 */
	public HDRFStrategy(Memory memory, double p) {
		this.lambda = Configuration.HDRF_LAMBDA;
		this.epsilon = Configuration.HDRF_EPSILON;
		this.memory = memory;
		this.hashProbability = p;
		this.allPartitions = new HashSet<Integer>();
		for (int i = 0; i < memory.getNumberOfPartitions(); i++) {
			this.allPartitions.add(i);
		}
	}

	/**
	 * Lambda = 1.0001 Epsilon = 1
	 */
	public HDRFStrategy(Memory memory) {
		this.lambda = Configuration.HDRF_LAMBDA;
		this.epsilon = Configuration.HDRF_EPSILON;
		this.memory = memory;
		this.hashProbability = 0;
		this.allPartitions = new HashSet<Integer>();
		for (int i = 0; i < memory.getNumberOfPartitions(); i++) {
			this.allPartitions.add(i);
		}
	}

	@Override
	public int getPartitionForEdge(Edge edge) {
		double r = Math.random();
		int partitionId;
		// hash next edge to smallest partition (lowest number of edges)
		if (r <= this.hashProbability) {
			partitionId = this.memory.getMemoryState().getMinEdgesPartitionId();
			memory.store(edge, partitionId);
			return partitionId;
		} else {

			updateApproximatedNodeDistribution(edge);
			updateMinMax();

			// Find partition with highest score
			partitionId = calculatePartitionWithBestScore(edge, this.allPartitions);
			memory.store(edge, partitionId);
			return partitionId;
		}
		// double r = Math.random();
		//
		// if (r < this.hashProbability) {
		// pid = memory.getMemoryState().getMinEdgesPartitionId();
		// return pid;
		// } else {
		// updateApproximatedNodeDistribution(edge);
		// updateMinMax();
		//
		// HashSet<Integer> aOf1 =
		// memory.getPartitionIdsOfVertex(edge.getFirstVertex().getId());
		// HashSet<Integer> aOf2 =
		// memory.getPartitionIdsOfVertex(edge.getSecondVertex().getId());
		//
		// @SuppressWarnings("unchecked")
		// HashSet<Integer> intersection = (HashSet<Integer>) aOf1.clone();
		// intersection.retainAll(aOf2);
		//
		// if (aOf1.isEmpty() && aOf2.isEmpty()) {
		// // case 1, none of the nodes exists yet
		// // use smallest partition
		// return memory.getMemoryState().getMinEdgesPartitionId();
		// } else if ((aOf1.isEmpty() && !aOf2.isEmpty()) || (!aOf1.isEmpty() &&
		// aOf2.isEmpty())) {
		// // case 2, only one node exists yet
		// // -> using smallest partition with this one node
		// if (!aOf1.isEmpty()) {
		// return memory.getMemoryState(aOf1).getMinEdgesPartitionId();
		// } else {
		// return memory.getMemoryState(aOf2).getMinEdgesPartitionId();
		// }
		// } else if (!intersection.isEmpty()) {
		// // case 3, both existing, have partitions together
		// // -> using smallest partition with both nodes
		// return memory.getMemoryState(intersection).getMinEdgesPartitionId();
		// } else {
		// // case 4, both existing, no partitions together
		// // -> replicate node with higher degree
		// if (this.approximatedNodeDistribution
		// .get(edge.getFirstVertex().getId()) <
		// this.approximatedNodeDistribution
		// .get(edge.getSecondVertex().getId())) {
		// return calculatePartitionWithBestScore(edge, aOf1);
		// } else {
		// return calculatePartitionWithBestScore(edge, aOf2);
		// }
		// }
		// }
	}

	/**
	 * Adds the Nodes from the edge to the list of approximated node-degrees or
	 * updates the degree if the node already has a value
	 * 
	 * @param edge
	 */
	private void updateApproximatedNodeDistribution(Edge edge) {
		if (approximatedNodeDistribution.containsKey(edge.getFirstVertex().getId())) {
			approximatedNodeDistribution.put(edge.getFirstVertex().getId(),
					approximatedNodeDistribution.get(edge.getFirstVertex().getId()) + 1);
		} else {
			approximatedNodeDistribution.put(edge.getFirstVertex().getId(), 1);
		}

		if (approximatedNodeDistribution.containsKey(edge.getSecondVertex().getId())) {
			approximatedNodeDistribution.put(edge.getSecondVertex().getId(),
					approximatedNodeDistribution.get(edge.getSecondVertex().getId()) + 1);
		} else {
			approximatedNodeDistribution.put(edge.getSecondVertex().getId(), 1);
		}
	}

	/**
	 * calculates the HDRF Score for all partitions and returns the one with the
	 * best score
	 * 
	 * @param edge
	 * @param partitionSet
	 * @return
	 */
	private int calculatePartitionWithBestScore(Edge edge, HashSet<Integer> partitionSet) {

		double maxScore = -1;
		double actualScore = -1;
		int maxPartition = -1;
		for (Integer partitionId : partitionSet) {
			actualScore = calculateScore(partitionId, edge);
			if (actualScore > maxScore) {
				maxScore = actualScore;
				maxPartition = partitionId;
			}
		}
		// System.out.println("Max: " + maxScore);
		// System.out.println("---------");
		return maxPartition;
	}

	/**
	 * calculates teh matching score of partition 'partitionId' and edge 'e'
	 * 
	 * @param partitionId
	 * @param e
	 * @return
	 */
	public double calculateScore(int partitionId, Edge e) {
		double scoreREP = calculateScoreREP(e, partitionId);
		double scoreBAL = calculateScoreBAL(partitionId);
		// System.out.println("Score edge " + e + " for partition " +
		// partitionId + ": " + scoreREP + "\t" + scoreBAL);
		return scoreREP + scoreBAL;
	}

	/**
	 * calculates the Balance Score of HDRF.
	 * 
	 * Formula: lambda * (maxSize-partitionSize)/(epsilon + maxSize - minSize)
	 * 
	 * @param partitionId
	 * @return
	 */
	private double calculateScoreBAL(int partitionId) {
		double temp1 = maxSize - memory.getPartitionInfo(partitionId).getTotalEdgeCount();
		double temp2 = epsilon + maxSize - minSize;
		double temp3 = lambda * temp1 / temp2;
		return temp3;
	}

	/**
	 * calculates the Replication Score of HDRF
	 * 
	 * @param e
	 * @param partitionId
	 * @return
	 */
	private double calculateScoreREP(Edge e, int partitionId) {
		return g(e.getFirstVertex().getId(), partitionId, e) + g(e.getSecondVertex().getId(), partitionId, e);
	}

	/**
	 * g(v,p) used to calculate C-REP
	 * 
	 * @param v
	 *            Vertex ID
	 * @param p
	 *            Partition
	 * @param e
	 *            Edge where v is in
	 * @return
	 */
	private double g(int v, int partitionId, Edge e) {

		if (!memory.getPartitionIdsOfVertex(v).isEmpty() && memory.getPartitionIdsOfVertex(v).contains(partitionId)) {
			return 1 + (1 - calculateTheta(e, v));
		} else {
			return 0;
		}
	}

	/**
	 * Theta of v
	 * 
	 * @param e
	 * @param v
	 *            the vertex Theta shall be calcuated
	 * @return
	 */
	private double calculateTheta(Edge e, int v) {
		int degreeV;
		int degreeOther;
		if (e.getFirstVertex().getId() == v) {
			degreeV = approximatedNodeDistribution.get(e.getFirstVertex().getId());
			degreeOther = approximatedNodeDistribution.get(e.getSecondVertex().getId());
		} else {
			degreeV = approximatedNodeDistribution.get(e.getSecondVertex().getId());
			degreeOther = approximatedNodeDistribution.get(e.getFirstVertex().getId());
		}
		return degreeV / (degreeV + degreeOther);
	}

	/**
	 * minsize & maxsize will be updated. Should be called before using min-
	 * /maxsize
	 */
	private void updateMinMax() {
		MemoryInfo info = memory.getMemoryState();
		minSize = info.getMinNumberEdges();
		maxSize = info.getMaxNumberEdges();
	}

	@Override
	public String toString() {
		return NamesOfStrategies.HDRF.toString();
	}

	@Override
	public HashMap<String, Double> getParameters() {
		HashMap<String, Double> parameters = new HashMap<String, Double>();
		parameters.put("lamda", this.lambda);
		parameters.put("epsilon", this.epsilon);
		return parameters;
	}
}
