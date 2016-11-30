package de.ipvs.fachstudie.graphpartitioning.util;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 * 
 *         Configuration contains all default values
 */
public class Configuration {


	// default directory of txt files
	public static final String FILE_PATH = "../graphs/";
	public static final String FILE_PATH_UNIT = "../graphs/unitTestGraphs/";
	public static String OUTPUT_PATH = "../graphs/output/";

	// ConsoleUserInput dafault values, ProcessingEnvironment
	// user input default values
	public static int NUMBER_OF_PARTITIONS = 8;
	public static final int MEMORY_SIZE = 0;
	public static final boolean USE_WINDOW = false;
	public static final String DEFAULT_GRAPH_PATH = Configuration.FILE_PATH_UNIT + "facebook_random.txt";

	public static final double HDRF_LAMBDA = 1.01;
	public static final double HDRF_EPSILON = 0.0001;

	// Window related parameters
	public static int WINDOW_SIZE = 1000;
	public static double WINDOWSTRATEGY_GAMMA = 0.2;
	public static double WINDOWSTRATEGY_BETA = 0.0;
	public static double WINDOWSTRATEGY_ALPHA = 0.7;
	public static double WINDOWSTRATEGY_DELTA = 0.1;

	public static final int WINDOWSTRATEGY_THREADS = 1;

	// use the lazy window strategy? -> candidate set
	public static final boolean LAZY = true;
	public static final boolean ADAPTIVE = true;
	public static int MAXIMAL_LATENCY = 1 * 1000; // ms

	
	// public static final int WINDOWSTRATEGY_THREADS = 1;
//	public static int LAZYWINDOWSTRATEGY_MAXCANDIDATESSIZE = 100;
	public static double LAZYWINDOWSTRATEGY_EPSILON = 0.1;
	public static long MINIMAL_GRAPH_SIZE = 1000; // estimated number of edges.
	public static long MAXIMAL_GRAPH_SIZE = -1;
	public static int MINIMAL_WINDOW_SIZE = 1;
}
