package de.ipvs.fachstudie.graphpartitioning.util;

/**
 * Enum containing the strategy names of all available strategies.
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 *
 */
public enum NamesOfStrategies {

	HASHING("hashing"), HDRF("hdrf"), DEGREE("degree"), DBH("DBH"), STREAMINGHEURISTIC(
			"streamingheuristic"), WINDOWSTRATEGY("windowstrategy"), BESTFITTINGEDGE(
					"BestFittingEdge"), STABILIZEDHDRF("stabilizedhdrf"), 
					LAZYWINDOWSTRATEGY("lazywindowstrategy"), 
					LAZYADAPTIVEWINDOWSTRATEGY("lazyadaptivewindowstrategy");

	private final String stringValue;

	private NamesOfStrategies(String stringValue) {
		this.stringValue = stringValue;
	}

	public String toString() {
		return stringValue;
	}
}
