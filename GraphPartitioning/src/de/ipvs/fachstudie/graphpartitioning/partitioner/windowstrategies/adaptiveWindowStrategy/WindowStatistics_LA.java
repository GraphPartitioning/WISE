package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.adaptiveWindowStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.ipvs.fachstudie.graphpartitioning.util.Configuration;

public class WindowStatistics_LA {

	// sorted window sizes
	List<Integer> w_sizes = new ArrayList<Integer>();
	
	// Window size -> summed latency over all observations in ms
	HashMap<Integer, Long> summedLatency = new HashMap<Integer, Long>();
	
	// Window size -> exponential average latency
	double alpha = 0.9; // importance of last observation
	HashMap<Integer, Long> exponentialAverageLatency = 
			new HashMap<Integer, Long>();
	
	// Window size -> number of observations
	HashMap<Integer, Integer> numberObservations 
		= new HashMap<Integer, Integer>();
	
	/**
	 * One sample from assignment of a single edge:
	 * @param winSize - the current window size
	 * @param latency - the current latency using winSize edges
	 * @param repScore - the repScore from the assigned edge
	 */
	public void updateStatistics(int winSize, long latency, double repScore) {
		
		// update number of observations
		int tmp_o = numberObservations.getOrDefault(winSize, 0);
		numberObservations.put(winSize, tmp_o + 1);
		
		// update summed latency
		long tmp = summedLatency.getOrDefault(winSize, 0l);
		summedLatency.put(winSize, tmp+latency);
		
		// update exponential average latency
		long tmp_ea_lat = exponentialAverageLatency.getOrDefault(winSize, 0l);
		exponentialAverageLatency.put(winSize, 
				Math.round(alpha*tmp_ea_lat + (1-alpha)*latency));
		
		// sorted insert window sizes
		int insertIndex = w_sizes.size();
		for (int i=0; i<w_sizes.size(); i++) {
			if (w_sizes.get(i)>winSize) {
				insertIndex = i;
				break;
			} else if (w_sizes.get(i)==winSize) {
				insertIndex = -1;
				break;
			}
		}
		if (insertIndex>=0) {
			w_sizes.add(insertIndex, winSize);
//			for (int i=0; i<w_sizes.size(); i++) {
//				System.out.println("w=" + w_sizes.get(i) 
//						+ " " + getAverageLatency(w_sizes.get(i))
//						+ " " + numberObservations.get(w_sizes.get(i)));
//			}
		}

		// debug
//		System.out.println("Number of observations: " + (tmp_o+1));
//		System.out.println("For window " + winSize);
//		System.out.println("Average latency " + getAverageLatency(winSize));
//		System.out.println("Latency " + latency);
//		System.out.println();
	}
	
	/**
	 * Returns the best window size, if we have to keep
	 * the latency bound maxLatency based on our observations.
	 * @param oldWindowSize
	 * @param maxLatency
	 * @return
	 */
	public int getWindowSize(int oldWindowSize, long maxLatency,
			int edgesProcessed) {
		
//		// find candidate window sizes that have low latency
//		Integer largestWindow = 2;
//		for (Integer w : w_sizes) {
//			double lat = getAverageLatency(w);
//			if (lat<maxLatency) {
//				if (w>largestWindow) {
//					largestWindow = w;
//				}
//			}
//		}
		
		// determine maximal latency - initially allow larger deviations
//		long maxLat = Math.round(maxLatency * 
//				(1.5-(double)edgesProcessed
//						/(double)Configuration.MINIMAL_GRAPH_SIZE));
		long maxLat = Math.round(maxLatency * 
				(1+(Math.random()-0.5)/5)); // randomize max latency a bit
		
//		if (getAverageLatency(oldWindowSize)<maxLatency) {
		if (getAverageLatency(oldWindowSize)<maxLat) {
			// increase window size, if we still have latency capacity
			return oldWindowSize * 2;
		} else {
			// reduce window size as we don't have capacity
			return Math.max(Configuration.MINIMAL_WINDOW_SIZE,oldWindowSize / 2);
		}
//		
//		// find larger and smaller win sizes
//		int indexLarger = -1;
//		int indexSmaller = -1;
//		for (int i=0; i<w_sizes.size(); i++) {
//			int w = w_sizes.get(i);
//			double lat = getAverageLatency(w);
//			if (lat>maxLatency) {
//				indexLarger = i;
//				indexSmaller = i-1;
//				break;
//			}
//		}
//		if (indexLarger==-1) {
//			// we have too less observations
//			return oldWindowSize * 2;
//		} else if (indexLarger==0) {
//			return Math.max(1, oldWindowSize / 2);
//		} else {
//			// TODO: implement more sophisticated methods
//			return w_sizes.get(indexSmaller);
//		}
		
	}
	
	
//	/**
//	 * Returns the best window size, if we have to keep
//	 * the latency bound maxLatency based on our observations.
//	 * @param oldWindowSize
//	 * @param maxLatency
//	 * @return
//	 */
//	public int getWindowSize2(int oldWindowSize, long maxLatency) {
//		
//		// find larger and smaller win sizes
//		int indexLarger = -1;
//		int indexSmaller = -1;
//		for (int i=0; i<w_sizes.size(); i++) {
//			int w = w_sizes.get(i);
//			double lat = getAverageLatency(w);
//			if (lat>maxLatency) {
//				indexLarger = i;
//				indexSmaller = i-1;
//				break;
//			}
//		}
//		if (indexLarger==-1) {
//			// we have too less observations
//			return oldWindowSize * 2;
//		} else if (indexLarger==0) {
//			return Math.max(1, oldWindowSize / 2);
//		} else {
//			// TODO: implement more sophisticated methods
//			return w_sizes.get(indexSmaller);
//		}
//		
//	}
	
//	public double getAverageLatency(int winSize) {
//		long tmp = summedLatency.get(winSize);
//		int n = numberObservations.get(winSize);
//		return (double)tmp/(double)n;
//	}
	
	/**
	 * Return estimation of latency needed for a certain
	 * window size. Be defensive -> 30% Puffer
	 * @param winSize
	 * @return
	 */
	public double getAverageLatency(int winSize) {
		return exponentialAverageLatency.get(winSize) * 1.5;
	}
	
	public String toString() {
		String s = "";
		s += "Average Window latencies (EA): \n";
		for (Integer w : w_sizes) {
			s += w + "	" + exponentialAverageLatency.get(w) + "ms \n";
		}
		s += "Number of Observations: \n";
		for (Integer w : w_sizes) {
			s += w + "	" + numberObservations.get(w) + "\n";
		}
		
		return s;
	}
	
}
