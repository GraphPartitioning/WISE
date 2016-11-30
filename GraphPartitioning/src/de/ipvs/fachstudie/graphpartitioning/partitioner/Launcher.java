package de.ipvs.fachstudie.graphpartitioning.partitioner;

import java.time.LocalTime;
import java.util.ArrayList;

import de.ipvs.fachstudie.graphpartitioning.evaluation.Evaluation;
import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.LimitedMemory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.UnlimitedMemory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.HDRFStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.PartitioningStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.adaptiveWindowStrategy.WindowStrategy_LA;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.windowstrategy.WindowStrategy;
import de.ipvs.fachstudie.graphpartitioning.userInput.GUI;
import de.ipvs.fachstudie.graphpartitioning.userInput.ProgramArgumentHandler;
import de.ipvs.fachstudie.graphpartitioning.userInput.UserInputContainer;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.ParametrizedStrategy;
import de.ipvs.fachstudie.graphpartitioning.util.FileReader;
import de.ipvs.fachstudie.graphpartitioning.util.StrategyFactory;

/**
 * Launcher with main method of the graph partitioning project
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 */

public class Launcher {

	private static long startTime;
	private static boolean usePrestreaming = false; // set via user input

	/**
	 * starts the GraphPartitioning. If run without any parameters a command
	 * line interface will appear to determine the configuration. If parameters
	 * were given, every configuration has to be done by parameter (otherwise
	 * default values will be used). List of Parameter is in the documentation
	 * available.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		UserInputContainer userInput;
		startTime = System.currentTimeMillis();

		if (args.length == 0) {
			// parameters will be asked in the UI
			userInput = GUI.start();
		} else {
			userInput = new UserInputContainer();
			userInput = ProgramArgumentHandler.handleParameters(args, userInput);
		}
		long timeStamp = System.currentTimeMillis();
		// execute the chosen algorithms
		ArrayList<ParametrizedStrategy> allStrategies = userInput.getStrategies();
		for (ParametrizedStrategy parametrizedStrategy : allStrategies) {
			Memory memory = null;
			if (userInput.getMemorySize() > 0) {
				memory = new LimitedMemory(userInput.getMemorySize(), userInput.getNumberOfPartitions(),
						userInput.getMemorySize() > 0);
			} else {
				memory = new UnlimitedMemory(userInput.getNumberOfPartitions());
			}

			if (usePrestreaming) {
				System.out.println("Prestream starts...");
				long prestreamtime = System.currentTimeMillis();
				prestream(memory, userInput.getGraphPath());
				prestreamtime = System.currentTimeMillis() - prestreamtime;
				System.out.println("Prestream ended after " + prestreamtime + "ms");
			}

			PartitioningStrategy strategy = StrategyFactory.instantiateStrategy(parametrizedStrategy, memory,
					userInput);
			GraphPartitioner partitioner = new GraphPartitioner(strategy, userInput, memory, timeStamp);

			System.out.println("[" + LocalTime.now() + "] computing: " + parametrizedStrategy.getStrategyName());
			partitioner.computePartitioning();

			System.out.println("\n[" + LocalTime.now() + "] evaluating: " + parametrizedStrategy.getStrategyName());

			Evaluation evaluation = new Evaluation(partitioner.getResults());

			if (parametrizedStrategy.getStrategyName().equals("windowstrategy")) {
				((WindowStrategy) strategy).killThreads();
			}
			if (parametrizedStrategy.getStrategyName()
					.equals("lazyadaptivewindowstrategy")) {
				System.out.println("--------------------");
				System.out.println("Window statistics: ");
				System.out.println(
						((WindowStrategy_LA) strategy)
						.getStat().toString());
				System.out.println("--------------------");
			}

			// test for GBC
			partitioner = null;
			memory = null;
			strategy = null;
			evaluation.evaluatePartitioning(timeStamp);
			evaluation = null;
		}
		System.out.println("\n[" + LocalTime.now() + "] computation finished successfully.");
		long totalRuntime = System.currentTimeMillis() - startTime;
		System.out.println("Total Runtime: " + (totalRuntime / 1000) + "s");
		
	}

	/**
	 * @return startTime of the calculation
	 */
	public static long getTimeStamp() {
		return startTime;
	}

	private static Memory prestream(Memory memory, String inputPath) {
		double hashProbability = 0.03; // set value > 0 (e.g. 0.03) for combined
										// graphs
		double percentage = 0.2;

		HDRFStrategy strategy = new HDRFStrategy(memory, hashProbability);
		FileReader reader = new FileReader(inputPath);
		int pid;

		while (reader.hasNextEdge()) {
			Edge e = reader.getNextEdge();

			if (e != null) {
				pid = strategy.getPartitionForEdge(e);
				memory.store(e, pid);
			}
		}

		memory.removeRandomReplicas(percentage);
		return memory;
	}

}
