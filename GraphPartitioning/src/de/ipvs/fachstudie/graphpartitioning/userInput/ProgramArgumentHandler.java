package de.ipvs.fachstudie.graphpartitioning.userInput;

import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.AbstractParameterContainer;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.HDRFParameterContainer;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.ParametrizedStrategy;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.WindowStrategyParameterContainer;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;
import de.ipvs.fachstudie.graphpartitioning.util.NamesOfStrategies;
import de.ipvs.fachstudie.graphpartitioning.util.Validation;

/**
 * function to create a UserInputContainer by using the program arguments.
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */

public class ProgramArgumentHandler {

	/**
	 * adds all valid parameters in args to the UserInputContainer 'userInput'
	 * 
	 * @param args
	 * @return
	 */
	public static UserInputContainer handleParameters(String[] args, UserInputContainer userInput) {

		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-winsize":
				try {
					int winsize = Integer.parseInt(args[i + 1]);
					i++;
					userInput.setWindowSize(winsize);
				} catch (NumberFormatException e) {
					System.err.println(
							"A problem accured while reading the winsize. Input will be ignored (no. " + i + ")");
				}
				break;
			case "-memsize":
				try {
					int memsize = Integer.parseInt(args[i + 1]);
					i++;
					userInput.setMemorySize(memsize);
				} catch (NumberFormatException e) {
					System.err.println(
							"A problem accured while reading the memsize. Input will be ignored (no. " + i + ")");
				}
				break;
			case "-in":

				String inputPath = args[i + 1];
				if (Validation.validatePath(inputPath)) {
					userInput.setGraphPath(inputPath);
					i++;
				} else {
					System.err.println("Invalid path. Default will be used.");
				}
				break;
			case "-p":
				try {
					int numberOfPartitions = Integer.parseInt(args[i + 1]);
					i++;
					userInput.setNumberOfPartitions(numberOfPartitions);
				} catch (NumberFormatException e) {
					System.err.println(
							"A problem accured while reading the number of partitions. Input will be ignored (no. " + i
									+ ")");
				}
				break;
			case "-hashing":
				userInput.addStrategy(new ParametrizedStrategy(NamesOfStrategies.HASHING.toString(), null));
				break;
			case "-hdrf":
				double hdrfLambda = Configuration.HDRF_LAMBDA;
				double hdrfEpsilon = Configuration.HDRF_EPSILON;
				try {
					hdrfLambda = Double.parseDouble(args[i + 1]);
					i++;

					hdrfEpsilon = Double.parseDouble(args[i + 1]);
					i++;

				} catch (Exception e) {
				} finally {
					AbstractParameterContainer parameters = new HDRFParameterContainer(hdrfLambda, hdrfEpsilon);
					userInput.addStrategy(new ParametrizedStrategy(NamesOfStrategies.HDRF.toString(), parameters));
				}
				break;
			case "-degree":
				userInput.addStrategy(new ParametrizedStrategy(NamesOfStrategies.DEGREE.toString(), null));
				break;
			case "-streamingheuristic":
				userInput.addStrategy(new ParametrizedStrategy(NamesOfStrategies.STREAMINGHEURISTIC.toString(), null));
				break;
			case "-bestfittingedge":
				userInput.addStrategy(new ParametrizedStrategy(NamesOfStrategies.BESTFITTINGEDGE.toString(), null));
				break;
			case "-windowstrategy":
				double wsAlpha = Configuration.WINDOWSTRATEGY_ALPHA;
				double wsBeta = Configuration.WINDOWSTRATEGY_BETA;
				double wsGamma = Configuration.WINDOWSTRATEGY_GAMMA;
				int wsThreads = Configuration.WINDOWSTRATEGY_THREADS;

				try {
					wsAlpha = Float.parseFloat(args[i + 1]);
					i++;

					wsBeta = Float.parseFloat(args[i + 1]);
					i++;

					wsGamma = Float.parseFloat(args[i + 1]);
					i++;

					wsThreads = Integer.parseInt(args[i + 1]);
					i++;

				} catch (Exception e) {
				} finally {
					AbstractParameterContainer parameters = new WindowStrategyParameterContainer(wsAlpha, wsBeta,
							wsGamma, wsThreads);
					userInput.addStrategy(
							new ParametrizedStrategy(NamesOfStrategies.WINDOWSTRATEGY.toString(), parameters));
				}
				break;
			case "-lazywindowstrategy":
				AbstractParameterContainer parameters = new WindowStrategyParameterContainer(0, 0, 0, 0);
				userInput.addStrategy(
						new ParametrizedStrategy(NamesOfStrategies.LAZYWINDOWSTRATEGY.toString(), parameters));
				break;
			case "-lazyadaptivewindowstrategy":
				AbstractParameterContainer parameters2 = new WindowStrategyParameterContainer(0, 0, 0, 0);
				userInput.addStrategy(
						new ParametrizedStrategy(
								NamesOfStrategies.LAZYADAPTIVEWINDOWSTRATEGY.toString(), 
								parameters2));
				break;
			case "-stabelizedhdrf":
				userInput.addStrategy(new ParametrizedStrategy(NamesOfStrategies.STABILIZEDHDRF.toString(), null));
				break;
			case "-DBH":
				userInput.addStrategy(new ParametrizedStrategy(NamesOfStrategies.DBH.toString(), null));
				break;
			case "-all":
				userInput.addAllStrategies();
				break;
			default:
				System.err.println(
						"Parameter: '" + args[i] + "' cann't be evaluated. Are you sure the parameter is right?");
				break;
			}
		}
		if (userInput.getStrategies().size() <= 0) {
			userInput.addAllStrategies();
			System.err.println("No valid strategy choosen. All strategies will be run. This may take a while.");
		}
		return userInput;
	}
}
