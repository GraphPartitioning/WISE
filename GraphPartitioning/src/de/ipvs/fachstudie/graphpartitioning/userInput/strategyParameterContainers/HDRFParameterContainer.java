package de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers;

import de.ipvs.fachstudie.graphpartitioning.util.NamesOfStrategies;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class HDRFParameterContainer extends AbstractParameterContainer {

	private double lambda;
	private double epsilon;

	public HDRFParameterContainer(double lambda, double epsilon) {
		this.lambda = lambda;
		this.epsilon = epsilon;
	}

	@Override
	public String getAlgorithmName() {
		return NamesOfStrategies.HDRF.toString();
	}

	public double getLambda() {
		return lambda;
	}

	public double getEpsilon() {
		return epsilon;
	}
}
