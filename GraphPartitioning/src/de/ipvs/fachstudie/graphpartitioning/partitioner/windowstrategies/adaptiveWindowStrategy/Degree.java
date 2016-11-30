package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.adaptiveWindowStrategy;

import java.util.HashMap;

/**
 * Class to store and maintain the degree table during processing
 * @author mayercn
 *
 */
public class Degree {

	private static HashMap<Integer, Integer> degreeTable =
			new HashMap<Integer, Integer>();
	
	/**
	 * Returns the estimated degree of vertex with v_id
	 * @param v_id
	 */
	public static int deg(int v_id, int defaultDegree) {
		return degreeTable.getOrDefault(v_id, defaultDegree);
	}
	
	/**
	 * Overwrites the degree of vertex v_id with the value degree
	 * @param v_id
	 * @param degree
	 */
	public static void updateDegree(int v_id, int degree) {
		degreeTable.put(v_id, degree);
	}
}
