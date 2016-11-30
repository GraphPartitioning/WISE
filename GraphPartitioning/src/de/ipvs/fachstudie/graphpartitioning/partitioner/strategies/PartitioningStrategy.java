package de.ipvs.fachstudie.graphpartitioning.partitioner.strategies;

import java.util.HashMap;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public interface PartitioningStrategy {
	/**
	 * Returns the name of the strategy as string.
	 * 
	 * @return String
	 */
	public String toString();

	public HashMap<String, Double> getParameters();

}
