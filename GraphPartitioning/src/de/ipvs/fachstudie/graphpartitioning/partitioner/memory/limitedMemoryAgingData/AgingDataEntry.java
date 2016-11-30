package de.ipvs.fachstudie.graphpartitioning.partitioner.memory.limitedMemoryAgingData;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 *         Aging data entries hold one piece of data. The aging data container
 *         works with these entries. They offer the aging methods.
 */
public class AgingDataEntry implements Comparable<AgingDataEntry> {

	private int id;
	private Set<Integer> data = new HashSet<Integer>();
	private int maxLivetime;
	private int currentLivetime;

	public AgingDataEntry(AgingData data) {
		this.id = data.getId();
		this.maxLivetime = 0;
	}

	public boolean contains(int data) {
		return this.data.contains(data);
	}

	public void addDataElement(int data) {
		this.data.add(data);
	}

	public Set<Integer> getAllData() {
		return this.data;
	}

	// aging
	// ================================================== //
	public int getCurrentLivetime() {
		return this.currentLivetime;
	}

	public int getId() {
		return this.id;
	}

	public void setMaxLivetime(int maxLivetime) {
		this.maxLivetime = maxLivetime;
	}

	public void resetLivetime() {
		this.currentLivetime = 0;
	}

	public boolean incrementLivetime() {
		this.currentLivetime++;
		if (currentLivetime >= maxLivetime) {
			return true;
		}
		return false;
	}
	// ================================================== //

	@Override
	public int compareTo(AgingDataEntry other) {
		return this.id - other.getId();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof AgingDataEntry)) {
			return false;
		} else {
			AgingDataEntry other = (AgingDataEntry) o;
			return other.getId() == this.id;
		}
	}

}
