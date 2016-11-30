package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.windowstrategy;

import java.util.HashSet;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.AssignedEdge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.EdgeWindow;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class WindowStrategyWindow extends EdgeWindow {

	private AssignedEdge lastAssignedEdge;

	public WindowStrategyWindow(int size) {
		super(size);
	}

	@Override
	public void addEdge(Edge e) {
		this.edges.add(new WindowEdge(e));
	}

	/**
	 * @return edge that was the last one removed out the window and assigned to
	 *         a partition
	 */
	public AssignedEdge getLastAssignedEdge() {
		return this.lastAssignedEdge;
	}

	public void setLastAssignedEdge(AssignedEdge lastAssignedEdge) {
		this.lastAssignedEdge = lastAssignedEdge;
	}
}
