package de.ipvs.fachstudie.graphpartitioning.model;

import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.limitedMemoryAgingData.AgingData;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 *         Models a vertex of a graph. Contains all the partitions it was
 *         assigned to.
 */
public class Vertex implements AgingData, Comparable<Vertex> {
	private int id;

	public Vertex(int id) {
		this.id = id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof Vertex))
			return false;
		Vertex otherMyClass = (Vertex) other;
		return otherMyClass.getId() == this.id;
	}

	@Override
	public int compareTo(Vertex other) {
		return this.id - other.getId();
	}

}
