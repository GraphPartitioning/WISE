package de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class ParametrizedStrategy {

	private String strategyName;
	private AbstractParameterContainer parameters;

	public ParametrizedStrategy(String strategyName, AbstractParameterContainer parameters) {
		this.strategyName = strategyName;
		this.parameters = parameters;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public AbstractParameterContainer getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		String output = this.strategyName + "- parameters: ";
		if (parameters != null) {
			if (parameters instanceof HDRFParameterContainer) {
				HDRFParameterContainer params = (HDRFParameterContainer) parameters;
				output += "epsilon=" + params.getEpsilon() + ", lambda=" + params.getLambda();
			} else if (parameters instanceof WindowStrategyParameterContainer) {
				WindowStrategyParameterContainer params2 = (WindowStrategyParameterContainer) parameters;
				output += "alpha=" + params2.getAlpha() + ", beta=" + params2.getBeta() + ", gamma="
						+ params2.getGamma();
			}
		} else {
			output += "none";
		}
		return output;
	}
}
