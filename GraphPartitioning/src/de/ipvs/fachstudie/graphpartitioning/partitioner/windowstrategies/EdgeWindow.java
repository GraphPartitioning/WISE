package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies;

import java.util.ArrayList;
import java.util.HashSet;
import de.ipvs.fachstudie.graphpartitioning.model.Edge;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 *         Contains at most the given limit of edges from the graph input.
 *         Provides functions to take out components contained in the window.
 *
 */
public class EdgeWindow {

	protected ArrayList<Edge> edges = new ArrayList<Edge>();
	private int size;

	// Constructor
	public EdgeWindow(int size) {
		if (size > 0) {
			this.size = size;
		} else {
			this.size = 1;
		}
	}

	// getter/setter
	public boolean isEmpty() {
		return edges.isEmpty();
	}

	public boolean hasMoreEdges() {
		return !edges.isEmpty();
	}

	public void setSize(int size) {
		if (size > 0) {
			this.size = size;
		}
	}

	public int getSize() {
		return this.size;
	}

	public boolean hasCapacity() {
		return size - edges.size() > 0;
	}

	public int getEdgeCount() {
		return this.edges.size();
	}

	/**
	 * Adds the given edge to the window.
	 * 
	 * @param e
	 */
	public void addEdge(Edge e) {
		this.edges.add(e);
	}

	/**
	 * @return edge
	 */
	public ArrayList<Edge> getEdges() {
		return this.edges;
	}

	/**
	 * Removes the given edge from the component.
	 * 
	 * @param e
	 * @return boolean success
	 */
	public boolean removeEdge(Edge e) {
		return this.edges.remove(e);
	}
}
