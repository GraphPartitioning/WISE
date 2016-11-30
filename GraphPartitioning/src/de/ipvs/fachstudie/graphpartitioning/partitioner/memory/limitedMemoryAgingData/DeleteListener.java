package de.ipvs.fachstudie.graphpartitioning.partitioner.memory.limitedMemoryAgingData;

import java.util.EventListener;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 *         When the aging data container removes an entry, the contained vertex
 *         has to be deleted from all partitions it is contained in. To provide
 *         this functionality the partitions implement this interface.
 */
public interface DeleteListener<T> extends EventListener {
	/**
	 * Specify here what has to be done when the AgingDataContainer removes an
	 * entry.
	 * 
	 * @param deletedData
	 *            the data object the AgingDataContainer has deleted
	 */
	public void onEntryDeleted(AgingDataEntry deletedData);

}
