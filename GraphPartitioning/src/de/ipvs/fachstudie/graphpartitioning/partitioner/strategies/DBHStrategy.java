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
 * @author Heiko Geppert
 *
 */
public class DBHStrategy implements SingleEdgePartitioningStrategy {

	private HashMap<Integer, Integer> approximatedNodeDistribution = 
			new HashMap<Integer, Integer>();
	
	private Memory memory;
	private int pid;
	

	public DBHStrategy(Memory memory) {
		this.memory = memory;
	}
	
	/**
	 * Returns a simple hash function for vertex v_id
	 * @param v_id
	 * @return
	 */
	private int vertexHash(int v_id) {
		return v_id % memory.getNumberOfPartitions();
	}

	@Override
	public int getPartitionForEdge(Edge edge) {
		
		updateApproximatedNodeDistribution(edge);
		
		int u = edge.getFirstVertex().getId();
		int v = edge.getSecondVertex().getId();
		
		int deg_u = this.approximatedNodeDistribution.getOrDefault(u, 1);
		int deg_v = this.approximatedNodeDistribution.getOrDefault(v, 1);

		int partitionId;
		
		if (deg_u<deg_v) {
			partitionId = vertexHash(u);
		} else {
			partitionId = vertexHash(v);
		}
		memory.store(edge, partitionId);
		return partitionId;
		
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


	@Override
	public String toString() {
		return NamesOfStrategies.DBH.toString();
	}
	
	@Override
	public HashMap<String, Double> getParameters() {
		HashMap<String, Double> parameters = new HashMap<String, Double>();
		return parameters;
	}
}
