package de.ipvs.fachstudie.graphpartitioning.partitioner.memory.limitedMemoryAgingData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 *         The aging data container contains the aging data entries. It stores
 *         entries until the limit is reached. Once the limit is exceeded the
 *         oldest entries are removed from the container.
 */
public class AgingDataContainer {
	private Set<AgingDataEntry> data;
	private int maxMemorySize;
	private int currentMemorySize;
	private Set<Integer> toBeReturned;
	private boolean useLimit;
	private ArrayList<DeleteListener<Integer>> listeners;

	// Constructor
	public AgingDataContainer(int maxDataAge, boolean on) {
		this.maxMemorySize = maxDataAge;
		this.data = Collections.newSetFromMap(new ConcurrentSkipListMap<AgingDataEntry, Boolean>());
		this.listeners = new ArrayList<DeleteListener<Integer>>();
		this.useLimit = on;
	}

	/**
	 * Tells the listeners that the given entry was removed from the container.
	 * 
	 * @param killedEntry
	 */
	private synchronized void onDataDelete(AgingDataEntry killedEntry) {
		this.listeners.forEach(new Consumer<DeleteListener<Integer>>() {
			@Override
			public void accept(DeleteListener<Integer> listener) {
				listener.onEntryDeleted(killedEntry);
			}
		});
	}

	public void addDeleteListener(DeleteListener<Integer> listener) {
		this.listeners.add(listener);
	}

	public void removeDeleteListener(DeleteListener<Integer> listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Stores new data in the memory and ages the data.
	 * 
	 * @param newData
	 */
	public synchronized void store(AgingData dataToStore, int dataElement) {
		AgingDataEntry newEntry = new AgingDataEntry(dataToStore);

		// add data to contained element or create new data entry
		if (data.contains(newEntry)) {
			data.forEach(new Consumer<AgingDataEntry>() {
				public void accept(AgingDataEntry dataElementInMem) {
					if (dataElementInMem.compareTo(newEntry) == 0) {
						dataElementInMem.addDataElement(dataElement);
					}
				}
			});
		} else {
			newEntry.addDataElement(dataElement);
			newEntry.setMaxLivetime(maxMemorySize);
			data.add(newEntry);
			currentMemorySize++;
		}
		if (useLimit) {
			// increment livetime and remove too old data if maxSize is reached
			do {
				data.forEach(new Consumer<AgingDataEntry>() {
					public void accept(AgingDataEntry dataElementInMem) {
						if (dataElementInMem.incrementLivetime()) {
							if (currentMemorySize >= maxMemorySize) {
								data.remove(dataElementInMem);
								onDataDelete(dataElementInMem);
								currentMemorySize--;
							}
						}
					}
				});
			} while (currentMemorySize >= maxMemorySize);
		}
	}

	/**
	 * Retrieves data from the memory. This leads to a reset of the lifetime of
	 * a data entry.
	 * 
	 * @param id
	 * @return set of data belonging to id or null if not contained
	 */
	public synchronized Set<Integer> accessData(int id) {
		// long execTime = System.currentTimeMillis();
		toBeReturned = null;
		data.forEach(new Consumer<AgingDataEntry>() {
			public void accept(AgingDataEntry dataElementInMem) {
				if (dataElementInMem.getId() == id) {
					toBeReturned = dataElementInMem.getAllData();
					dataElementInMem.resetLivetime();
				}
			}
		});
		// execTime = System.currentTimeMillis() - execTime;
		// if(execTime > 1){
		// System.out.println("\naccessData() slow execution time (>1ms) for
		// this call: "
		// + execTime + " ms");
		// }
		return toBeReturned;
	}

	/**
	 * Prints content of memory to console.
	 */
	public synchronized void printContent() {
		System.out.println("---------------------------------------------------------------------------");
		data.forEach(new Consumer<AgingDataEntry>() {
			public void accept(AgingDataEntry dataElementInMem) {
				System.out.println("[MEMORY INFO] data id: " + dataElementInMem.getId() + "\t(Partitions = "
						+ dataElementInMem.getAllData() + ", livetime = " + dataElementInMem.getCurrentLivetime()
						+ ")");
			}
		});
		System.out.println("---------------------------------------------------------------------------");
	}
}
