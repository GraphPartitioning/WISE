package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.lazyWindowStrategy;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.EdgeWindow;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;

public class LazyWindow extends EdgeWindow {

	// the list of all edges in the window
	private List<LazyWindowEdge> badEdges = new ArrayList<LazyWindowEdge>();
//	private int numberOfAssignedEdges = 0;
//	private boolean trackScoreDistribution = false;
	private double summedScore;
	private int numberOfAssignedEdges = 0;

	

	// only a few of them are candidates (those with high score)
	private ArrayList<LazyWindowEdge> candidates = new ArrayList<LazyWindowEdge>();
	private double scoreThreshold = Configuration.WINDOWSTRATEGY_ALPHA;	// gets updated
	
	private LazyWindowStrategy strategy;


	
	public LazyWindow(int size, LazyWindowStrategy strategy) {
		super(size);
		this.strategy = strategy;
		
	}
	
	/**
	 * Returns the average score of an edge assignment,
	 * if the edge would have been assigned according to 
	 * single edge streaming approach.
	 * @return
	 */
	public double getAverageScore() {
		return summedScore / (double)numberOfAssignedEdges;
	}
	
	/**
	 * Update the scoreThreshold based on the score
	 * of the last assigned edge.
	 * @param bestScore
	 */
	public void updateScoreThreshold(double bestScore) {

//		scoreThreshold = 0.5 * scoreThreshold + 0.5 * bestScore;
		
		// score is the average over all edges in the candidate set
		double tmp_scr = 0;
		for (LazyWindowEdge we : candidates) {
			tmp_scr += we.maxScore;
		}
		if (candidates.size()>0) {
			tmp_scr = tmp_scr / (double)candidates.size();
		}
		scoreThreshold = tmp_scr + 0.1;
		
//		System.out.println(candidates.size() + "\t" + scoreThreshold);
//		System.out.println("Score threshold: " + scoreThreshold);
//		System.out.println("Best edge: " + bestScore);
//		System.out.println("Candidate size: " + candidates.size());
//		System.out.println("Bad edges size: " + badEdges.size());
//		System.out.println("----------------");
	}
	
	@Override
	public boolean isEmpty() {
		return badEdges.isEmpty() && candidates.isEmpty();
	}

	@Override
	public boolean hasMoreEdges() {
		// TODO Auto-generated method stub
		return super.hasMoreEdges();
	}

	@Override
	public void setSize(int size) {
		// TODO Auto-generated method stub
		super.setSize(size);
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return super.getSize();
	}

	@Override
	public boolean hasCapacity() {
		return candidates.size()+badEdges.size()<this.getSize();
	}

	@Override
	public int getEdgeCount() {
		// TODO Auto-generated method stub
		return super.getEdgeCount();
	}

	@Override
	public ArrayList<Edge> getEdges() {
		// TODO Auto-generated method stub
		return super.getEdges();
	}

	@Override
	public boolean removeEdge(Edge e) {
		// TODO Auto-generated method stub
		return super.removeEdge(e);
	}

	@Override
	public void addEdge(Edge e) {
		numberOfAssignedEdges++;
		
		LazyWindowEdge we = new LazyWindowEdge(e, strategy.numberOfPartitions);
		we.updateRepScores(strategy.memory, strategy.numberOfPartitions);
		
		updateScore(we);
		
//		// update scoreThreshold as the average score for the single edge case
//		updateScoreThreshold(we.maxScore);
		
		// update summed score of potential streaming approach
		summedScore += we.maxScore;
		
		updateDegreeDistribution(e);
		
		if (this.addToCandidates(we)) {
			candidates.add(we);
		} else {
			badEdges.add(we);
		}
	}
	
	/**
	 * Updates the degree for both incident vertices.
	 * @param e
	 */
	private void updateDegreeDistribution(Edge e) {
		
		// update degree distribution
		int v1 = e.getFirstVertex().getId();
		int v2 = e.getSecondVertex().getId();
		int degV1 = LazyWindowStrategy.approximatedNodeDistribution
				.getOrDefault(v1, 0) + 1;
		int degV2 = LazyWindowStrategy.approximatedNodeDistribution
				.getOrDefault(v2, 0) + 1;
		LazyWindowStrategy.approximatedNodeDistribution.put(v1, degV1);
		LazyWindowStrategy.approximatedNodeDistribution.put(v2, degV2);
		
		// update max edge degree
		int edgeDegree = degV1 + degV2;
		if (edgeDegree>LazyWindowStrategy.maxEdgeDegree) {
			LazyWindowStrategy.maxEdgeDegree = edgeDegree;
		}
	}

	public LazyWindowEdge getAndRemoveBestEdge() {
//		numberOfAssignedEdges++;
		if (candidates.size()==0 && badEdges.size()==0) {
			return null;
		}

//		if (trackScoreDistribution && (numberOfAssignedEdges==10000)) {
//			trackWindowScoreDistribution(1);
//		}

		
		// get best edge from candidates
		LazyWindowEdge bestEdge;
		double bestScore = -1;
		if (candidates.size()==0) {
			bestEdge = badEdges.remove(0);
//			System.out.println(Configuration.WINDOW_SIZE);
			updateScore(bestEdge);
		} else {
			bestEdge = candidates.get(0);
			int index = 0;
			int i=0;
//			System.out.println("Number of candidates: " + candidates.size());
			for (LazyWindowEdge edge : candidates) {
				updateScore(edge);
//				System.out.println("Candidate edge score: " + edge.maxScore + " - " + edge.maxRepScore);
				if (edge.maxScore>bestScore) {
					bestScore = edge.maxScore;
					bestEdge = edge;
					index = i;
				}
				i++;
			}
//			System.out.println();
			candidates.remove(index);
		}
		
		return bestEdge;
	}
	
	
	private void updateScore(LazyWindowEdge we) {
		we.updateScores(strategy.memory, strategy.numberOfPartitions,
				strategy.balances, Configuration.WINDOWSTRATEGY_ALPHA, 
				Configuration.WINDOWSTRATEGY_GAMMA, Configuration.WINDOWSTRATEGY_DELTA);
	}
	

	/**
	 * Updates the repScores in all relevant window edges
	 * @param bestAssignment
	 */
	public void updateRepScores(LazyWindowEdge we, int p_id) {
		int u = we.getFirstVertex().getId();
		int v = we.getSecondVertex().getId();
		boolean newReplicaU = !strategy.memory.getPartitionInfo(p_id)
				.containsReplicationOfVertex(u);
		boolean newReplicaV = !strategy.memory.getPartitionInfo(p_id)
				.containsReplicationOfVertex(v);
		if (newReplicaU) {
			increaseRepScore(candidates,true, u, p_id);
			increaseRepScore(badEdges,false,u, p_id);
		}
		if (newReplicaV) {
			increaseRepScore(candidates,true,v, p_id);
			increaseRepScore(badEdges, false,v, p_id);
		}
	}
	
	/**
	 * Increases the repScore of all vertices in the edge collection l
	 * that are equal vertexID.
	 * @param l
	 * @param vertexID
	 */
	private void increaseRepScore(Collection<LazyWindowEdge> l, boolean candidateList, 
			int vertexID, int p_id) {
		Iterator<LazyWindowEdge> iter = l.iterator();
		while (iter.hasNext()) {
			LazyWindowEdge lwe = iter.next();
			int lwe_u = lwe.getFirstVertex().getId();
			int lwe_v = lwe.getSecondVertex().getId();
			if (vertexID==lwe_u || vertexID==lwe_v) {
				lwe.replicaScores[p_id]++;
				if (lwe.replicaScores[p_id]>lwe.maxRepScore) {
					lwe.maxRepScore = lwe.replicaScores[p_id];
				}
				if (!candidateList) {
//					if (candidates.size()<
//							Configuration.LAZYWINDOWSTRATEGY_MAXCANDIDATESSIZE) {
					//TODO: if candidate list is empty
						updateScore(lwe);
//					}
					if (addToCandidates(lwe)) {
						iter.remove();
						candidates.add(lwe);
					}
				}
			}
		}
	}
	
	private boolean addToCandidates(LazyWindowEdge we) {
		if (!Configuration.LAZY) {
			return true;
		} else {
			// lazy window strategy is used
			if (candidates.isEmpty()) {
				return true;
			}
			if (we.maxScore>scoreThreshold-Configuration.LAZYWINDOWSTRATEGY_EPSILON
					&& we.maxRepScore>0
//					&& candidates.size()<Configuration.LAZYWINDOWSTRATEGY_MAXCANDIDATESSIZE
					) {
				return true;
			}
			return false;
		}
	}	
	
//	private void trackWindowScoreDistribution(int numberOfEdgesAssigned) {
//		String filename = Configuration.OUTPUT_PATH 
//				+ "windowScoreDistribution_" + Configuration.WINDOW_SIZE + ".txt";
//		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
//				new FileOutputStream(filename, true), "utf-8"))) {
//			writer.write(Configuration.WINDOW_SIZE + "\t"
//					+ numberOfEdgesAssigned + "\t"
//					+ summedAssignmentScore + "\t"
//					+ minAssignmentScore + "\t"
//					+ maxAssignmentScore + "\t"
//					+ summedLatency + "\t"
//					+ maxLatency + "\n"					
//					);
////			for (LazyWindowEdge we : badEdges) {
////				updateScore(we);
////				writer.write(we.maxScore + "\t" + we.maxRepScore + "\n");
////			}
//		} catch (Exception e) {
//			System.out.println("ERROR: Couldn't write evaluation to file");
//			e.printStackTrace();
//		}
//	}
	
}
