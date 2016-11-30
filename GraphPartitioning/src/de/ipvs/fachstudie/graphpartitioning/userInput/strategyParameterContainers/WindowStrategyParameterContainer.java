package de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers;

import de.ipvs.fachstudie.graphpartitioning.util.Configuration;
import de.ipvs.fachstudie.graphpartitioning.util.NamesOfStrategies;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class WindowStrategyParameterContainer extends AbstractParameterContainer {

	private double alpha;
	private double beta;
	private double gamma;
	private int threads;

	public WindowStrategyParameterContainer(double alpha, double beta, double gamma, int threads) {
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		this.threads = threads;
	}

	@Override
	public String getAlgorithmName() {
		return NamesOfStrategies.WINDOWSTRATEGY.toString();
	}

	public double getAlpha() {
		return alpha;
	}

	public double getBeta() {
		return beta;
	}

	public double getGamma() {
		return gamma;
	}

	public int getThreads() {
		if (threads > 0) {
			return threads;
		}
		return Configuration.WINDOWSTRATEGY_THREADS;
	}
}
