package de.ipvs.fachstudie.graphpartitioning.userInput;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Scanner;

import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.HDRFParameterContainer;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.ParametrizedStrategy;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.WindowStrategyParameterContainer;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;
import de.ipvs.fachstudie.graphpartitioning.util.NamesOfStrategies;
import de.ipvs.fachstudie.graphpartitioning.util.Validation;

/**
 * provides user dialog on the command line
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class GUI {
	private static Scanner scanner = new Scanner(System.in);
	private static UserInputContainer userInput = null;

	/**
	 * starts the dialog with the main menu
	 * 
	 * @return a UserInputContainer with the users configuration
	 */
	public static UserInputContainer start() {
		int input = 0;

		printSeparationLine();
		System.out.println("*       WELCOME TO THE GRAPHPARTITIONING ALGORITHM TESTER V1.0      *");
		printSeparationLine();
		System.out.println("  YOU CAN CHOOSE THE FOLLOWING OPTIONS:");
		System.out.println("    [1] DISPLAY MANUAL");
		System.out.println("    [2] CONFIGURATE SETTINGS MANUALLY AND RUN ALGORITHMS");
		System.out.println("    [3] QUIT");
		printSeparationLine();
		System.out.print("*   YOUR CHOICE: ");

		input = getInput(1, 3);

		switch (input) {
		case 1:
			printManualScreen();
			break;
		case 2:
			configurateScreen();
			break;
		case 3:
			quit();
			break;
		}
		return userInput;
	}

	/**
	 * starts the dialog to configurate the environment and choose the
	 * algorithms executed
	 * 
	 * @return the users configuration
	 */
	public static void configurateScreen() {
		int numberOfPartitions = Configuration.NUMBER_OF_PARTITIONS;
		int memorySize = Configuration.MEMORY_SIZE;
//		int windowSize = Configuration.WINDOW_SIZE;
		int windowSize;
		String graphPath = "";
		ArrayList<ParametrizedStrategy> strategiesToRun = new ArrayList<ParametrizedStrategy>();

		System.out.print("*    --> TEST ALGORITHMS \n");
		printSeparationLine();
		System.out.println("    + DEFINE ENVIRONMENT: ");
		System.out.print("         Path of input graph: ");
		graphPath = getPath();
		System.out.print("         Number of partitions: ");
		numberOfPartitions = getInput(2, Integer.MAX_VALUE);
		System.out.print("         Limited memory size (0 = off): ");
		memorySize = getInput(0, Integer.MAX_VALUE);
		System.out.print("         Define window size for window based algorithms (value > 0): ");
		windowSize = getInput(1, Integer.MAX_VALUE);
		System.out.println("");
		System.out.println("    + CHOOSE STRATEGIES FOR PARTITIONING: ");
		System.out.println("         [1] Hashing Strategy\t\t\t(single edge)");
		System.out.println("         [2] HDRF Strategy\t\t\t(single edge)");
		System.out.println("         [3] SPowerGraph Degree Algorithm\t(single edge)");
		System.out.println("         [4] Streaming Heuristic Strategy\t(single edge)");
		System.out.println("         [5] Window Strategy\t\t\t(window based)");
		System.out.println("         [6] Best Fitting Edge Strategy\t\t(window based)");
		System.out.println("         [7] Stabilized HDRF Strategy\t\t(window based)");
		System.out.println("         [8] use all");

		strategiesToRun = getStrategies();
		printSeparationLine();
		System.out.println(" PROCESSING DATA...");

		userInput = new UserInputContainer(numberOfPartitions, memorySize, windowSize, graphPath, strategiesToRun);
	}

	private static void quit() {
		System.out.print("*    --> QUIT \n");
		System.out.println("* Bye bye...!");
		printSeparationLine();
		System.exit(0);
	}

	/**
	 * Dialog for adding algorithms
	 * 
	 * @return list of algorithms to be executed
	 */
	private static ArrayList<ParametrizedStrategy> getStrategies() {
		ArrayList<ParametrizedStrategy> strategies = new ArrayList<ParametrizedStrategy>();
		do {
			System.out.print("     YOUR CHOICE: ");
			int input = getInput(1, 8);
			System.out.println("");
			switch (input) {
			case 1:
				strategies.add(new ParametrizedStrategy(NamesOfStrategies.HASHING.toString(), null));
				break;
			case 2:
				System.out.println("    + SET PARAMETERS FOR HDRF");
				System.out.print("        Enter lambda parameter (balance weight): ");
				double lambda = getDoubleValue(0, Double.MAX_VALUE);
				System.out.print("        Enter epsilon parameter: ");
				double epsilon = getDoubleValue(0, Double.MAX_VALUE);
				System.out.println();
				HDRFParameterContainer params = new HDRFParameterContainer(lambda, epsilon);
				strategies.add(new ParametrizedStrategy(NamesOfStrategies.HDRF.toString(), params));
				break;
			case 3:
				strategies.add(new ParametrizedStrategy(NamesOfStrategies.DEGREE.toString(), null));
				break;
			case 4:
				strategies.add(new ParametrizedStrategy(NamesOfStrategies.STREAMINGHEURISTIC.toString(), null));
				break;
			case 5:
				System.out.println("    + SET PARAMETERS FOR WINDOWSTRATEGY");
				System.out.print("        Enter alpha parameter (replication score weight): ");
				double alpha = getDoubleValue(0, Double.MAX_VALUE);
				System.out.print("        Enter beta parameter (balance score weight): ");
				double beta = getDoubleValue(0, Double.MAX_VALUE);
				System.out.print("        Enter gamma (entropy weight): ");
				double gamma = getDoubleValue(0, Double.MAX_VALUE);
				System.out.println();
				System.out.print("        Enter number of threads: ");
				int threads = getInput(1, 1000);
				System.out.println();
				WindowStrategyParameterContainer params2 = new WindowStrategyParameterContainer(alpha, beta, gamma,
						threads);
				strategies.add(new ParametrizedStrategy(NamesOfStrategies.WINDOWSTRATEGY.toString(), params2));
				break;
			case 6:
				strategies.add(new ParametrizedStrategy(NamesOfStrategies.BESTFITTINGEDGE.toString(), null));
				break;
			case 7:
				strategies.add(new ParametrizedStrategy(NamesOfStrategies.STABILIZEDHDRF.toString(), null));
				break;
			case 8:
				for (NamesOfStrategies name : NamesOfStrategies.values()) {
					strategies.add(new ParametrizedStrategy(name.toString(), null));
				}
				break;
			default:
				System.err.println("[CONSOLE ERROR] Invalid input:(. Nothing added.");
			}
			System.out.print("     Do you want to run another algorithm in this environment? (no = start computation)");
		} while (getYesNo());
		return strategies;
	}

	public static void printManualScreen() {
		System.out.print("*    --> DISPLAY MANUAL \n");
		printSeparationLine();
		System.out.println("* GRAPHPARTITIONING ALGORITHM TESTER V1.0 is a software that\n"
				+ "* can be used to evalute graph partitioning algorithms.\n"
				+ "* In the testing area You can configurate the setting in which to run\n" + "* the algorithms.\n"
				+ "* Next you can choose one or more algorithms to run with the settings\n" + "* You provided before.\n"
				+ "* In the end the partitioning results will be shown.\n"
				+ "* The software was implemented for the Fachstudie at Stuttgart University in SS16.\n" + "* \n"
				+ "* All rights reserved. (C) 2016 by Geppert, Laich, Rieger\n" + "* Press ENTER to return.");
		printSeparationLine();
		scanner.nextLine();
		start();
	}

	private static void printSeparationLine() {
		System.out.println("*********************************************************************");
	}

	private static String getPath() {
		do {
			String in = scanner.nextLine();
			if (Validation.validatePath(in)) {
				return in;
			} else {
				System.err.println("[CONSOLE ERROR] Invalid path:(. Try again...");

			}
		} while (true);
	}

	/**
	 * generic dialog for yes/no choices
	 * 
	 * @return
	 */
	private static boolean getYesNo() {
		do {
			System.out.print(" Enter yes/y or no/n: ");
			String input = scanner.nextLine();
			if (input.equals("y") || input.equals("yes")) {
				return true;
			} else if (input.equals("n") || input.equals("no")) {
				return false;
			}
			System.out.println("You can do better...try again, please:)");
		} while (true);
	}

	/**
	 * generic dialog to get an integer [minRange, maxRange]
	 * 
	 * @param minRange
	 * @param maxRange
	 * @return
	 */
	private static int getInput(int minRange, int maxRange) {
		int input = 0;
		boolean correct = true;
		do {
			try {
				input = Integer.parseInt(scanner.nextLine());
				if (input < minRange || input > maxRange) {
					correct = false;
					throw new InvalidParameterException();
				}
			} catch (Exception e1) {
				System.err.println("[CONSOLE ERROR] Invalid input. Choose one of the options above.");
				input = 0;
			}
		} while (!correct);

		return input;
	}

	/**
	 * generic dialog to get a double [minRange, maxRange]
	 * 
	 * @param minRange
	 * @param maxRange
	 * @return
	 */
	private static double getDoubleValue(double minRange, double maxRange) {
		String inputString;
		double input;

		do {
			inputString = scanner.nextLine();
			try {
				input = Double.parseDouble(inputString);
				if (input >= minRange && input <= maxRange) {
					return input;
				}
			} catch (Exception e) {
				System.out.println("Invalid input: Enter double value.");
			}
			System.out.println("    ERROR: enter a value in range: [" + minRange + " ; " + maxRange + "]");
		} while (true);
	}

}
