package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.windowstrategy;

import java.util.ArrayList;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class WindowEdgeUpdater extends Thread {

	private Object synchObject = new Object();
	private boolean execute = false;

	WindowStrategy windowStrategy;
	WindowStrategyWindow window;
	Memory memory;
	int numberOfPartitions;
	private double alpha;
	// private double beta;
	private double gamma;
	Edge edge = null;
	int assignToId = 0;
	double maxResult = -1.0;
	ArrayList<Double> balances;

	int startIndex = 0;
	int numberOfUpdates = 0;

	/*
	 * Delta computation
	 */
	private int maxEdgeDegree;

	/*
	 * Lazy computation
	 */
	// private double maxPossibleScore;

	public WindowEdgeUpdater(WindowStrategyWindow window, WindowStrategy windowStrategy, int numberOfPartitions,
			ArrayList<Double> balances, Memory memory, double alpha, double beta, double gamma) {
		super();
		this.window = window;
		this.windowStrategy = windowStrategy;
		this.numberOfPartitions = numberOfPartitions;
		this.memory = memory;
		this.alpha = alpha;
		// this.beta = beta;
		this.gamma = gamma;
		this.balances = balances;
		// this.updateMaxPossibleScore();
	}

	public void setInformation(int startIndex, int numberOfUpdates, int maxEdgeDegree) {
		synchronized (synchObject) {
			this.startIndex = startIndex;
			this.numberOfUpdates = numberOfUpdates;
			edge = null;
			assignToId = 0;
			maxResult = -1.0;
			this.maxEdgeDegree = maxEdgeDegree;

			// notify thread that it can start processing
			execute = true;
			synchObject.notify();

		}
	}

	public void calculateEdges() {
		for (int i = startIndex; i < startIndex + numberOfUpdates; i++) {
			WindowEdge windowEdge = (WindowEdge) window.getEdges().get(i);
			windowEdge.calculateReplication(this.memory, this.numberOfPartitions, window.getLastAssignedEdge());

			// abort if edge is not active (replication degree is 0 for whole
			// row)
			if (!windowEdge.isActive() && i > startIndex) {
				// System.out.println("Edge " + windowEdge + " is not active" +
				// windowEdge.getReplications()[0][0]);
				continue;
			}

			// windowEdge.initValues(this.memory, this.numberOfPartitions);
			int repScore = 0;
			double score;
			// System.out.print(windowEdge.getFirstVertex().getId() + "-" +
			// windowEdge.getSecondVertex().getId() + "\t");
			for (int partitionIdx = 0; partitionIdx < numberOfPartitions; partitionIdx++) {
				repScore = windowEdge.getReplications()[partitionIdx][0];

				// calculate degreeScore between 0 and 1
				int edgeDegree = 0;
				edgeDegree += windowStrategy.approximatedNodeDistribution
						.getOrDefault(windowEdge.getFirstVertex().getId(), 1);
				edgeDegree += windowStrategy.approximatedNodeDistribution
						.getOrDefault(windowEdge.getSecondVertex().getId(), 1);
				double degreeScore;
				if (edgeDegree > 1) {
					degreeScore = (double) (this.maxEdgeDegree - edgeDegree) / (double) (this.maxEdgeDegree + 0.00001);
				} else {
					// ignore edges with little information
					degreeScore = 0;
				}

				// for performance reasons balances are already multiplied with
				// beta
				score = this.alpha * repScore + this.balances.get(partitionIdx) + this.gamma * windowEdge.getSpecifity()
						+ WindowStrategy.delta * degreeScore;
				// System.out.print(score + " (" + repScore + ", "
				// + this.balances.get(partitionIdx) + ", "
				// + windowEdge.getSpecifity() + ", " +
				// degreeScore + ")\t"
				// );
				if (score > maxResult) {
					edge = windowEdge;
					assignToId = partitionIdx;
					maxResult = score;
					// System.out.println(score + "(" + repScore + "," +
					// this.balances.get(partitionIdx)
					// + "," + windowEdge.getSpecifity() + "," + degreeScore +
					// ")\t");
				}

			}
			// System.out.println();

		}
		// System.out.println("Best: " + edge + "\t " + assignToId + "\t" +
		// maxResult);
		// System.out.println("----------------------");
	}

	public Edge getEdge() {
		return edge;
	}

	public int getPartition() {
		return assignToId;
	}

	public double getMaxResult() {
		// return result
		return maxResult;
	}

	public void run() {
		synchronized (synchObject) {
			while (true) {
				// wait for notification
				while (!execute) {
					try {
						synchObject.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// this thread was notified and should execute
				// (setInformation(...) was called)
				// this.updateMaxPossibleScore();
				this.calculateEdges();
				execute = false;

				// thread is ready with this round -> notify main function
				synchronized (WindowStrategy.lock) {
					WindowStrategy.counter--;
					WindowStrategy.lock.notify();
				}
			}
		}
	}

	// private void updateMaxPossibleScore() {
	// this.maxPossibleScore = alpha * 2 + windowStrategy.beta * 1
	// + gamma * 1 + WindowStrategy.delta * 1;
	// System.out.println("Max possible score: " + this.maxPossibleScore);
	// }
}
