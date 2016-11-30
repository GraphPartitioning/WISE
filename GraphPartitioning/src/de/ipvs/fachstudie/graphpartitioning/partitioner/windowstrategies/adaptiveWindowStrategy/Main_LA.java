package de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.adaptiveWindowStrategy;

import de.ipvs.fachstudie.graphpartitioning.partitioner.Launcher;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;

public class Main_LA {

	
	
	
	/**
	 * Executes the lazy adaptive window based partitioning algorithm
	 * @param 	args[0] = graph path
	 * 			args[1] = winSize (initial)
	 * 			args[2] = number of partitions
	 * 			args[3] = strategy (Hash, DBH, HDRF, Degree, WISE)
	 * 			args[4] = maximal latency in ms
	 * 			args[5] = maximal graph size (#edges)
	 */
	public static void main(String[] args) {
		String graph;
		if (args.length>0) {
			executeStrategy(args);
			return;
		} else {
//			graph = "..\\graphs\\com-youtube_random.txt";
//			graph = "Z:\\Datasets\\TwitterBillion\\twitter.twitter";
//						graph = "..\\graphs\\www_combined.txt";
//						graph = "..\\graphs\\wiki-Vote.txt";
//			graph = "..\\graphs\\twitter_random.txt";
//			graph = "..\\graphs\\facebook_combined.txt";
//			graph = "..\\graphs\\web-Google.txt";
//			graph = "..\\graphs\\amazon_random.txt";
//			graph = "..\\graphs\\roadNet-CA.txt";
			graph = "..\\graphs\\wiki-Vote.txt";
//			graph = "..\\graphs\\movielens100k_random.txt";
//			graph = "..\\graphs\\email-EuAll_combined.txt";
			
//			Configuration.MAXIMAL_GRAPH_SIZE = 1468365182; // Twitter
//			Configuration.MAXIMAL_GRAPH_SIZE = 88234; // Facebook
//			Configuration.MAXIMAL_GRAPH_SIZE = 1497135; // WWW
//			Configuration.MAXIMAL_GRAPH_SIZE = 2420766; // Twitter
//			Configuration.MAXIMAL_GRAPH_SIZE = 5105039; // Web-Google
//			Configuration.MAXIMAL_GRAPH_SIZE = 3387393; // Amazon
//			Configuration.MAXIMAL_GRAPH_SIZE = 5533214; // Road-Net
			Configuration.MAXIMAL_GRAPH_SIZE = 103689; // Wiki-Vote
//			Configuration.MAXIMAL_GRAPH_SIZE = 100000; // Movielens100k
//			Configuration.MAXIMAL_GRAPH_SIZE = 420045; // Email
		}
//		int[] winSizes = {32*1024};
//		int[] winSizes = {2,2,2,4,8,16,32,64,128,256,512,1024,1024*2,1024*4,1024*8};
		int[] winSizes = {1};
//		int[] winSizes = {1};
//		if (args.length>0) {
//			winSizes[0] = Integer.valueOf(args[0]);
//		}
//		int[] partitions = {64};
//		int[] partitions = {2,4,8,16,32,64,128};
		int[] partitions = {8};
		int mem_size = 1000; // the number of vertices in the memory

		
//		/*
//		 * Window: parameters
//		 */
//		String[] params = new String[3];
//		params[0] = "-in";
//		params[1] = graph;
//		params[2] = "-lazyadaptivewindowstrategy";
//		Configuration.NUMBER_OF_PARTITIONS = 8;
////		Configuration.WINDOW_SIZE = 2000;
//		double[][] paramCombo = new double[][]{{1,0,0},{0.7,0.2,0.1},{0.7,0.1,0.2},
//				{0.4,0.3,0.3},{0.33,0.33,0.33}};
//		for (double[] p : paramCombo) {
//			Configuration.WINDOW_SIZE = 1;
//			Configuration.MAXIMAL_LATENCY = 10 * 1000; // ms
//			Configuration.WINDOWSTRATEGY_ALPHA = p[0];
//			Configuration.WINDOWSTRATEGY_GAMMA = p[1];
//			Configuration.WINDOWSTRATEGY_DELTA = p[2];
//			Launcher.main(params);
//		}
		
		
//		/*
//		 * Window: partitions
//		 */
//		String[] params = new String[3];
//		params[0] = "-in";
//		params[1] = graph;
//		params[2] = "-lazyadaptivewindowstrategy";
//		
//		for (int i=0; i<partitions.length; i++) {
//			Configuration.WINDOW_SIZE = 1;
//			Configuration.MAXIMAL_LATENCY = 15 * 1000; // ms
//			Configuration.NUMBER_OF_PARTITIONS = partitions[i];
//			Configuration.WINDOWSTRATEGY_ALPHA = 0.5;
//			Configuration.WINDOWSTRATEGY_BETA = 0.2;	// beta is selected automatically
//			Configuration.WINDOWSTRATEGY_GAMMA = 0.1;
//			Configuration.WINDOWSTRATEGY_DELTA = 0.2;
//			Launcher.main(params);
//		}
		
		/*
		 * Window: latency bounds
		 */
		String[] params = new String[3];
		params[0] = "-in";
		params[1] = graph;
		params[2] = "-lazyadaptivewindowstrategy";
		int[] bounds = new int[]{10, 20, 40, 80, 160, 320 }; // sec
		
		for (int i=0; i<bounds.length; i++) {
			Configuration.WINDOW_SIZE = 1;
			Configuration.MAXIMAL_LATENCY = bounds[i] * 1000; // ms
			Configuration.NUMBER_OF_PARTITIONS = 8;
			Configuration.WINDOWSTRATEGY_ALPHA = 0.5;
			Configuration.WINDOWSTRATEGY_BETA = 0.2;	// beta is selected automatically
			Configuration.WINDOWSTRATEGY_GAMMA = 0.1;
			Configuration.WINDOWSTRATEGY_DELTA = 0.2;
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
	
	private static void executeStrategy(String[] args) {
		String graph = args[0];
		int winSize = Integer.valueOf(args[1]);
		int partitions = Integer.valueOf(args[2]);
		String strategy = args[3];
		int maxLatency = Integer.valueOf(args[4]);
		String[] params;
		Configuration.OUTPUT_PATH = strategy + "/output/";
		Configuration.MAXIMAL_GRAPH_SIZE = Integer.valueOf(args[5]);
		switch (strategy) {
		case "Hash":
			params = new String[5];
			params[0] = "-in";
			params[1] = graph;
			params[2] = "-p";
			params[3] = "" + partitions;
			params[4] = "-hashing";
			Launcher.main(params);
			break;
		case "DBH":
			params = new String[5];
			params[0] = "-in";
			params[1] = graph;
			params[2] = "-p";
			params[3] = "" + partitions;
			params[4] = "-DBH";
			Launcher.main(params);
			break;
		case "HDRF":
			double lambda = 1.1;
			params = new String[7];
			params[0] = "-in";
			params[1] = graph;
			params[2] = "-p";
			params[3] = "" + partitions;
			params[4] = "-hdrf";
			params[5] = "" + lambda;
			params[6] = "0.0001";
			Launcher.main(params);
			break;
		case "Degree":
			params = new String[5];
			params[0] = "-in";
			params[1] = graph;
			params[2] = "-p";
			params[3] = "" + partitions;
			params[4] = "-degree";
			Launcher.main(params);
			break;
		case "WISE":
			params = new String[3];
			params[0] = "-in";
			params[1] = graph;
			params[2] = "-lazyadaptivewindowstrategy";
			
			Configuration.WINDOW_SIZE = winSize;
			Configuration.MAXIMAL_LATENCY = maxLatency; // ms
			Configuration.NUMBER_OF_PARTITIONS = partitions;
			Configuration.WINDOWSTRATEGY_ALPHA = 0.7;
			Configuration.WINDOWSTRATEGY_BETA = 0.0;	// beta is selected automatically
			Configuration.WINDOWSTRATEGY_GAMMA = 0.2;
			Configuration.WINDOWSTRATEGY_DELTA = 0.1;
			Launcher.main(params);
			
			break;
		}
	}

}
