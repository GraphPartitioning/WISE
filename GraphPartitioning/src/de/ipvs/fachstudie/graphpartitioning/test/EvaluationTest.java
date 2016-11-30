package de.ipvs.fachstudie.graphpartitioning.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import de.ipvs.fachstudie.graphpartitioning.evaluation.Evaluation;
import de.ipvs.fachstudie.graphpartitioning.evaluation.EvaluationData;
import de.ipvs.fachstudie.graphpartitioning.userInput.UserInputContainer;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.ParametrizedStrategy;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class EvaluationTest {

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testBalanceAndReplicationDegree() {
		ArrayList<ParametrizedStrategy> algorithms = new ArrayList<ParametrizedStrategy>();
		algorithms.add(new ParametrizedStrategy("UnitBalanceTest", null));
		UserInputContainer userInput = new UserInputContainer(4, 0, 100, "../graphs/unitTestGraphs/facebook_random.txt",
				algorithms);

		HashMap<String, Double> parameters = new HashMap<String, Double>();
		parameters.put("testNumber", 0.0);

		Evaluation evaluation = new Evaluation(new EvaluationData("UnitBalanceTest", parameters, userInput, 230));
		evaluation.evaluatePartitioning(230);
		// manual test
	}

	@Test
	public void testBalanceAndReplicationDegree1() {
		// perfect balance, balance 0,0, replication degree: 1,08 (40/37): 2,3
		// and 21 are replications
		ArrayList<ParametrizedStrategy> algorithms = new ArrayList<ParametrizedStrategy>();
		algorithms.add(new ParametrizedStrategy("1UnitBalanceTest", null));
		UserInputContainer userInput = new UserInputContainer(4, 0, 100, "../graphs/unitTestGraphs/facebook_random.txt",
				algorithms);
		HashMap<String, Double> parameters = new HashMap<String, Double>();
		parameters.put("testNumber", 1.0);
		Evaluation evaluation = new Evaluation(new EvaluationData("UnitBalanceTest", parameters, userInput, 230));
		evaluation.evaluatePartitioning(230);
		// manual test
	}

	@Test
	public void testBalanceAndReplicationDegree2() {
		// perfect balance, balance 100
		ArrayList<ParametrizedStrategy> algorithms = new ArrayList<ParametrizedStrategy>();
		algorithms.add(new ParametrizedStrategy("2UnitBalanceTest", null));
		UserInputContainer userInput = new UserInputContainer(4, 0, 100, "../graphs/unitTestGraphs/facebook_random.txt",
				algorithms);
		HashMap<String, Double> parameters = new HashMap<String, Double>();
		parameters.put("testNumber", 2.0);
		Evaluation evaluation = new Evaluation(new EvaluationData("UnitBalanceTest", parameters, userInput, 230));
		evaluation.evaluatePartitioning(230);
		// manual test
	}

}
