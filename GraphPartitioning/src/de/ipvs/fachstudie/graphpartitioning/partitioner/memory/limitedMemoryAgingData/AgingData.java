package de.ipvs.fachstudie.graphpartitioning.partitioner.memory.limitedMemoryAgingData;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 *         Data that needs to be stored in the aging data container needs to
 *         implement this interface. It provides an abstract view on the
 *         vertices so that the data container doesn't know what is stored in it
 *         neither does it have to know the class Vertex in this specific
 *         context.
 */
public interface AgingData {
	/**
	 * The data with the same id (Integer ==) is considered as the same.
	 * 
	 * @param id
	 */
	public void setId(int id);

	/**
	 * Return the id of this object
	 * 
	 * @return id
	 */
	public int getId();

}
