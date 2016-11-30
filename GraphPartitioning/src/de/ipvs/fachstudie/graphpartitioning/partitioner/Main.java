package de.ipvs.fachstudie.graphpartitioning.partitioner;

import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.windowstrategy.WindowStrategy;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 */

public class Main {

	public static void main(String[] args) {

		String graph;
		if (args.length == 0) {
			graph = "..\\graphs\\movielens100k_random.txt";
		} else {
			graph = args[0];
		}
		int[] partitions = { 20 };
		// int[] partitions = {2,4,8,16,32,64,128, 256};
		int[] winSizes = { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768 };
		// int[] winSizes = {10,100,1000};

		double alpha = 0.4;
		int beta = 0; // beta is selected automatically
		double gamma = 0.2;
		double delta = 0.2;
		WindowStrategy.delta = delta;

		/*
		 * Window
		 */
		String[] params = new String[10];
		params[0] = "-in";
		params[1] = graph;
		params[2] = "-p";
		params[3] = "" + partitions[0];
		params[4] = "-winsize";
		params[5] = winSizes[0] + "";
		params[6] = "-windowstrategy";
		params[7] = "" + alpha;
		params[8] = "" + beta;
		params[9] = "" + gamma;

		for (int i = 0; i < winSizes.length; i++) {
			params[5] = "" + winSizes[i];
			// params[3] = partitions[i] + "";
			Launcher.main(params);
		}

		/*
		 * HDRF
		 */
		// double lambda = 3;
		// String[] params = new String[7];
		// params[0] = "-in";
		// params[1] = graph;
		// params[2] = "-p";
		// params[3] = "" + partitions;
		// params[4] = "-hdrf";
		// params[5] = "" + lambda;
		// params[6] = "0.0001";
		//
		// for (int i=0; i<partitions.length; i++) {
		// //params[5] = "" + window;
		// params[3] = partitions[i] + "";
		// Launcher.main(params);
		// }

		/*
		 * Degree
		 */
		// String[] params = new String[5];
		// params[0] = "-in";
		// params[1] = graph;
		// params[2] = "-p";
		// params[3] = "" + partitions;
		// params[4] = "-degree";
		//
		// for (int i=0; i<partitions.length; i++) {
		// //params[5] = "" + window;
		// params[3] = partitions[i] + "";
		// Launcher.main(params);
		// }

		// /*
		// * Hashing
		// */
		// String[] params = new String[5];
		// params[0] = "-in";
		// params[1] = graph;
		// params[2] = "-p";
		// params[3] = "" + partitions;
		// params[4] = "-hashing";
		//
		// for (int i=0; i<partitions.length; i++) {
		// //params[5] = "" + window;
		// params[3] = partitions[i] + "";
		// Launcher.main(params);
		// }

		/*
		 * Number of Threads
		 */

	}

}
