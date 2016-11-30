package de.ipvs.fachstudie.graphpartitioning.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.adaptiveWindowStrategy.WindowStatistics_LA;

public class AdaptiveWindowStatisticsTest {

	@Test
	public void test() {
		WindowStatistics_LA stat = new WindowStatistics_LA();
		int I = 10000; // the number of iterations
		for (int i=0; i<I; i++) {
			int winSize = i;
			long latency = i;
			int repScore = (int)Math.round(i * 0.000001);
			stat.updateStatistics(winSize, latency, repScore);
		}
		for (int i=0; i<I; i++) {
			int winSize = (int)Math.round(Math.random()*I);
			int latency = I * 2;
			assertTrue(stat.getWindowSize(winSize, latency, 100)==2*winSize);
			latency = (int) Math.round(Math.random()*(I*0.98)+1);
//			System.out.println(stat.getWindowSize(winSize, latency));
//			System.out.println(latency);
//			System.out.println();
			assertTrue(stat.getWindowSize(winSize, latency, 100)==latency);
		}
	}

}
