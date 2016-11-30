package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.lazyWindowStrategy;

import de.ipvs.fachstudie.graphpartitioning.partitioner.Launcher;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;

public class MainLazyWindow {

	/**
	 * Executes the latzy window based partitioning algorithm
	 * @param args[0] = window size, if empty, we assume the default value
	 */
	public static void main(String[] args) {
		
	
		String graph = "..\\graphs\\wiki-Vote.txt";
//			graph = "..\\graphs\\www_combined.txt";
//			graph = "..\\graphs\\wiki-Vote.txt";
//		graph = "../graphs/www_combined.txt";
//			graph = "..\\graphs\\facebook_random.txt";
//		int[] winSizes = {32*1024};
		int[] winSizes = {2,2,2,4,8,16,32,64,128,256,512,1024,1024*2,1024*4,1024*8};
//		int[] winSizes = {1,2,10,100,1000,10000,40000,80000};
//		int[] winSizes = {1};
		if (args.length>0) {
			winSizes[0] = Integer.valueOf(args[0]);
		}
		int[] partitions = {16};
//		int[] partitions = {2,4,8,16,32,64};
//		int[] partitions = {2,8};
		Configuration.NUMBER_OF_PARTITIONS = partitions[0];
//		Configuration.WINDOW_SIZE = 1000;
		int mem_size = 1000; // the number of vertices in the memory

		Configuration.WINDOWSTRATEGY_ALPHA = 0.7;
		Configuration.WINDOWSTRATEGY_BETA = 0.0;	// beta is selected automatically
		Configuration.WINDOWSTRATEGY_GAMMA = 0.2;
		Configuration.WINDOWSTRATEGY_DELTA = 0.1;
		
//		/*
//		 * Window: parameters
//		 */
//		String[] params = new String[3];
//		params[0] = "-in";
//		params[1] = graph;
//		params[2] = "-lazywindowstrategy";
//		Configuration.NUMBER_OF_PARTITIONS = 8;
//		Configuration.WINDOW_SIZE = 2000;
//		double[][] paramCombo = new double[][]{{1,0,0},{0.7,0.2,0.1},{0.7,0.1,0.2},
//				{0.4,0.3,0.3},{0.33,0.33,0.33}};
//		for (double[] p : paramCombo) {
//			Configuration.WINDOWSTRATEGY_ALPHA = p[0];
//			Configuration.WINDOWSTRATEGY_GAMMA = p[1];
//			Configuration.WINDOWSTRATEGY_DELTA = p[2];
//			Launcher.main(params);
//		}
		
		
		/*
		 * Window: partitions
		 */
		String[] params = new String[3];
		params[0] = "-in";
		params[1] = graph;
		params[2] = "-lazywindowstrategy";
		Configuration.WINDOW_SIZE = 2;
//		Configuration.LAZYWINDOWSTRATEGY_MAXCANDIDATESSIZE = Configuration.WINDOW_SIZE;
//		Configuration.LAZYWINDOWSTRATEGY_MAXCANDIDATESSIZE = 1;
		
//		for (int i=0; i<partitions.length; i++) {
		for (int i : winSizes) {
//			Configuration.NUMBER_OF_PARTITIONS = partitions[i];
			Configuration.WINDOW_SIZE = i;
//			Configuration.LAZYWINDOWSTRATEGY_MAXCANDIDATESSIZE = Configuration.WINDOW_SIZE;
//			Configuration.WINDOW_SIZE = Math.max(4000, 10 * partitions[i]);
			Launcher.main(params);
		}
		
//		/*
//		 * DBH
//		 */
//		String[] params = new String[5];
//		params[0] = "-in";
//		params[1] = graph;
//		params[2] = "-p";
//		params[3] = "" + partitions;
//		params[4] = "-DBH";
//		
//		for (int i=0; i<partitions.length; i++) {
//			params[3] = partitions[i] + "";
//			Launcher.main(params);
//		}
		
//		/*
//		 * HDRF
//		 */
//		double lambda = 3.1;
//		String[] params = new String[7];
//		params[0] = "-in";
//		params[1] = graph;
//		params[2] = "-p";
//		params[3] = "" + partitions;
//		params[4] = "-hdrf";
//		params[5] = "" + lambda;
//		params[6] = "0.0001";
//		
//		for (int i=0; i<partitions.length; i++) {
//			//params[5] = "" + window;
//			params[3] = partitions[i] + "";
//			Launcher.main(params);
//		}
		
//		/*
//		 * Degree
//		 */
//		String[] params = new String[5];
//		params[0] = "-in";
//		params[1] = graph;
//		params[2] = "-p";
//		params[3] = "" + partitions;
//		params[4] = "-degree";
//		
//		for (int i=0; i<partitions.length; i++) {
//			//params[5] = "" + window;
//			params[3] = partitions[i] + "";
//			Launcher.main(params);
//		}
		
		
		
//		/*
//		 * Hashing
//		 */
//		String[] params = new String[5];
//		params[0] = "-in";
//		params[1] = graph;
//		params[2] = "-p";
//		params[3] = "" + partitions;
//		params[4] = "-hashing";
//		
//		for (int i=0; i<partitions.length; i++) {
//			//params[5] = "" + window;
//			params[3] = partitions[i] + "";
//			Launcher.main(params);
//		}
		
//		/*
//		 * Window: unlimited memory
//		 */
//		String[] params = new String[5];
//		params[0] = "-in";
//		params[1] = graph;
//		params[2] = "-windowstrategy";
//		params[3] = "-memsize";
//		params[4] = "" + 2000;
//		Configuration.NUMBER_OF_PARTITIONS = 2;
//		Configuration.WINDOW_SIZE = 500;
//		
//		for (int i=0; i<partitions.length; i++) {
//			Configuration.NUMBER_OF_PARTITIONS = partitions[i];
//			Launcher.main(params);
//		}

  }

}
