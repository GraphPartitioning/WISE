package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.stabilizedhdrf;

import java.util.ArrayList;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.EdgeWindow;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class StabilizationWindow extends EdgeWindow {

	public StabilizationWindow(int size) {
		super(size);
	}

	public Edge getRandomEdge() {
		int edgeCount = this.getEdgeCount();
		// some big prime number. Its important not to depend on the window size
		// here.
		// since we would get bad results for small windows.
		int r = (int) ((Math.random() * 274139));
		r = r % edgeCount;
		ArrayList<Edge> edges = new ArrayList<Edge>(this.edges);

		return edges.get(r);
	}

}
