package de.ipvs.fachstudie.graphpartitioning.model;

import java.util.ArrayList;

/**
 * Models an edge of a graph. The edge can be either directed or undirected.
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 */
public class Edge implements Comparable<Edge> {
	private Vertex u;
	private Vertex v;

	/**
	 * @param u
	 *            source vertex
	 * @param v
	 *            destination vertex
	 */

	public Edge(Vertex u, Vertex v) {
		this.u = u;
		this.v = v;
	}

	/**
	 * 
	 * @return source vertex
	 */
	public Vertex getFirstVertex() {
		return this.u;
	}

	/**
	 * 
	 * @return destination vertex
	 */
	public Vertex getSecondVertex() {
		return this.v;
	}

	public String toString() {
		return this.getFirstVertex().getId() + "    " + this.getSecondVertex().getId();
	}

	/**
	 * compares both vertices. Source and destination are considered
	 */
	@Override
	public int compareTo(Edge o) {
		int differenceFirstVertex = o.getFirstVertex().getId() - this.getFirstVertex().getId();
		if (differenceFirstVertex != 0) {
			return differenceFirstVertex;
		} else {
			return o.getSecondVertex().getId() - this.getSecondVertex().getId();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof Edge))
			return false;
		Edge otherEdge = (Edge) other;
		return otherEdge.compareTo(this) == 0;
	}

	@Override
	public int hashCode() {
		return this.u.getId() + this.v.getId();
	}

	/**
	 * @param edge
	 * @return ArrayList containing the vertices that this edge and the given
	 *         edge have in common.
	 */
	public ArrayList<Vertex> commonVertices(Edge edge) {
		ArrayList<Vertex> commonVertices = new ArrayList<Vertex>();
		if (this.u.equals(edge.v) || this.u.equals(edge.u)) {
			commonVertices.add(this.u);
		}
		if (this.v.equals(edge.v) || this.v.equals(edge.u)) {
			commonVertices.add(this.v);
		}
		return commonVertices;
	}
}
