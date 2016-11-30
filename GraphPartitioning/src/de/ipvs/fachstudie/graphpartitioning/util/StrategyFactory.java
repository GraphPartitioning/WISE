package de.ipvs.fachstudie.graphpartitioning.util;

import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.DBHStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.HDRFStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.HashingStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.PartitioningStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.SPowerGraphDegree;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.StreamingHeuristic;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.adaptiveWindowStrategy.WindowStrategy_LA;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.bestFittingEdge.BestFittingEdgeStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.lazyWindowStrategy.LazyWindowStrategy;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.stabilizedhdrf.StabilizedHDRF;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.windowstrategy.WindowStrategy;
import de.ipvs.fachstudie.graphpartitioning.userInput.UserInputContainer;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.AbstractParameterContainer;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.HDRFParameterContainer;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.ParametrizedStrategy;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.WindowStrategyParameterContainer;

/**
 * FactoryClass which provides strategies
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 *
 */
public class StrategyFactory {

	/**
	 * Factory method for partitioning strategies. Used in the constructor
	 * 
	 * @param strategyName
	 *            Name from NamesOfStrategies Enum
	 * @param memory
	 *            the LimitedMemory used in the strategy
	 * @return object of chosen strategy
	 */
	public static PartitioningStrategy instantiateStrategy(ParametrizedStrategy paramStrategy, Memory memory,
			UserInputContainer userInput) {
		String strategyName = paramStrategy.getStrategyName();
		AbstractParameterContainer parameters = paramStrategy.getParameters();

		if (NamesOfStrategies.HASHING.toString().equals(strategyName)) {
			return new HashingStrategy(memory);
		} else if (NamesOfStrategies.HDRF.toString().equals(strategyName)) {
			if (parameters != null) {
				HDRFParameterContainer params = (HDRFParameterContainer) parameters;
				return new HDRFStrategy(params.getLambda(), params.getEpsilon(), memory);
			}
			return new HDRFStrategy(memory);
		} else if (NamesOfStrategies.DEGREE.toString().equals(strategyName)) {
			return new SPowerGraphDegree(memory);
		} else if (NamesOfStrategies.STREAMINGHEURISTIC.toString().equals(strategyName)) {
			return new StreamingHeuristic(memory);
		} else if (NamesOfStrategies.WINDOWSTRATEGY.toString().equals(strategyName)) {
			if (parameters != null) {
				WindowStrategyParameterContainer params = (WindowStrategyParameterContainer) parameters;
				return new WindowStrategy(memory, userInput.getWindowSize(), params.getAlpha(), params.getBeta(),
						params.getGamma(), params.getThreads());
			}
			return new WindowStrategy(memory, userInput.getWindowSize());
		} else if (NamesOfStrategies.LAZYWINDOWSTRATEGY.toString().equals(strategyName)) {
			return new LazyWindowStrategy(memory);
		} else if (NamesOfStrategies.LAZYADAPTIVEWINDOWSTRATEGY.toString().equals(strategyName)) {
			return new WindowStrategy_LA(memory);
		} else if (NamesOfStrategies.BESTFITTINGEDGE.toString().equals(strategyName)) {
			return new BestFittingEdgeStrategy(memory, userInput.getWindowSize());
		} else if (NamesOfStrategies.STABILIZEDHDRF.toString().equals(strategyName)) {
			return new StabilizedHDRF(memory, userInput.getWindowSize());
		} else if (NamesOfStrategies.DBH.toString().equals(strategyName)) {
			return new DBHStrategy(memory);
		}
		System.err.println("Couldn't find strategy with name " + strategyName + ". Hashing will be used.");
		return new HashingStrategy(memory);
	}
}
