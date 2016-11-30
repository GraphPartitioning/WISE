package de.ipvs.fachstudie.graphpartitioning.evaluation;

import java.util.Set;
import java.util.TreeSet;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 *         stores all data about a partition needed when evaluate a graph
 *         partitioning algorithm
 */
public class PartitionEvaluation {

	private int id;
	private Set<Integer> vertices = new TreeSet<Integer>();
	private Set<Edge> edges = new TreeSet<Edge>();
	long totalVertexCount = 0;
	long totalEdgeCount = 0;

	public PartitionEvaluation(int id) {
		this.id = id;
	}

	/**
	 * @return id (partition number)
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * add an edge to the partition (also adds the edges' vertices)
	 * 
	 * @param e
	 */
	public void addEdge(Edge e) {
		totalEdgeCount++;
		this.edges.add(e);
		this.vertices.add(e.getFirstVertex().getId());
		this.vertices.add(e.getSecondVertex().getId());
	}

	/**
	 * @return how many different vertices are stored on this partition in total
	 */
	public long getTotalVertexCount() {
		if (vertices != null) {
			return vertices.size();
		}
		return totalVertexCount;
	}

	/**
	 * @return how many edges are stored on this partition in total
	 */
	public long getTotalEdgeCount() {
		return totalEdgeCount;
	}

	/**
	 * @return ids of all vertices stored on this partition
	 * @throws SetsAlreadyFreedException
	 *             if the vertex set is already freed (because of GBC and memory
	 *             issues)
	 */
	public Set<Integer> getVertexIds() throws SetsAlreadyFreedException {
		if (this.vertices == null) {
			throw new SetsAlreadyFreedException("You already feed the vertex id set");
		}
		return this.vertices;
	}

	/**
	 * set edge and vertex set pointers to null (for memory reasons), only store
	 * their sizes
	 */
	public void deleteSetsAndStoreTotalSizes() {
		totalVertexCount = this.vertices.size();
		this.vertices = null;
		this.edges = null;

	}

	/**
	 * /**
	 * 
	 * @author Christian Mayer
	 * @author Heiko Geppert
	 * @author Larissa Laich
	 * @author Lukas Rieger
	 * 
	 *         Exception that is indicates that vertex and edge set are already
	 *         freed although there is an access on it.
	 */
	public class SetsAlreadyFreedException extends Exception {
		private static final long serialVersionUID = 1L;

		public SetsAlreadyFreedException(String message) {
			super(message);
		}
	}

}
