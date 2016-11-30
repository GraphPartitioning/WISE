package de.ipvs.fachstudie.graphpartitioning.userInput;

import java.util.ArrayList;

import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.ParametrizedStrategy;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;
import de.ipvs.fachstudie.graphpartitioning.util.NamesOfStrategies;

/**
 * Container for all general parameters. Is used from the console ui and from
 * the parameter ui.
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class UserInputContainer {

	int numberOfPartitions;
	int memorySize;
	int windowSize;
	boolean useWindow;
	String graphPath;
	ArrayList<ParametrizedStrategy> strategies;

	/**
	 * provides default values in case the user does not specify the values
	 */
	public UserInputContainer() {
		this.numberOfPartitions = Configuration.NUMBER_OF_PARTITIONS;
		this.memorySize = Configuration.MEMORY_SIZE;
		this.windowSize = Configuration.WINDOW_SIZE;
		this.graphPath = Configuration.DEFAULT_GRAPH_PATH;
		this.useWindow = Configuration.WINDOW_SIZE > 1;
		this.strategies = new ArrayList<ParametrizedStrategy>();
	}

	public UserInputContainer(int numberOfPartitions, int memorySize, int windowSize, String graphPath,
			ArrayList<ParametrizedStrategy> strategiesToRun) {
		this.numberOfPartitions = numberOfPartitions;
		this.memorySize = memorySize;
		this.windowSize = windowSize;
		this.useWindow = windowSize > 1;
		this.graphPath = graphPath;
		this.strategies = strategiesToRun;
	}

	public void addStrategy(ParametrizedStrategy paramStrategy) {
		this.strategies.add(paramStrategy);
	}

	public ArrayList<ParametrizedStrategy> getStrategies() {
		return this.strategies;
	}

	public void setUseWindow(boolean on) {
		this.useWindow = on;
	}

	public boolean getUseWindow() {
		return this.useWindow;
	}

	public int getNumberOfPartitions() {
		return numberOfPartitions;
	}

	public int getMemorySize() {
		return memorySize;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public String getGraphPath() {
		return graphPath;
	}

	public void setNumberOfPartitions(int numberOfPartitions) {
		this.numberOfPartitions = numberOfPartitions;
	}

	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	public void setGraphPath(String graphPath) {
		this.graphPath = graphPath;
	}

	public void addAllStrategies() {
		NamesOfStrategies[] namesOfStrategies = NamesOfStrategies.values();
		for (NamesOfStrategies strategyName : namesOfStrategies) {
			this.strategies.add(new ParametrizedStrategy(strategyName.toString(), null));
		}
	}
}
