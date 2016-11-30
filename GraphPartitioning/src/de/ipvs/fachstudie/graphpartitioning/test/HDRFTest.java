package de.ipvs.fachstudie.graphpartitioning.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;

import de.ipvs.fachstudie.graphpartitioning.evaluation.Evaluation;
import de.ipvs.fachstudie.graphpartitioning.evaluation.EvaluationData;
import de.ipvs.fachstudie.graphpartitioning.partitioner.GraphPartitioner;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.LimitedMemory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.PartitioningStrategy;
import de.ipvs.fachstudie.graphpartitioning.userInput.UserInputContainer;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.HDRFParameterContainer;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.ParametrizedStrategy;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;
import de.ipvs.fachstudie.graphpartitioning.util.NamesOfStrategies;
import de.ipvs.fachstudie.graphpartitioning.util.StrategyFactory;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class HDRFTest {

	private final String filePathOfGraph_simpleGraph = Configuration.FILE_PATH_UNIT + "simpleGraph.txt";
	private final String filePathOfGraph_facebookRandom = Configuration.FILE_PATH_UNIT + "facebook_random.txt";
	private int numberOfPartitions;
	private GraphPartitioner partitioner;
	private String strategyName = NamesOfStrategies.HDRF.toString();
	private long timeStamp = System.currentTimeMillis();

	@Test
	public void testWithoutParameter() {
		ParametrizedStrategy strategy = new ParametrizedStrategy(strategyName, null);
		ArrayList<ParametrizedStrategy> strategies = new ArrayList<ParametrizedStrategy>();

		numberOfPartitions = 4;
		strategies.add(strategy);
		// no memory limit, windowsize 1, don't use the window
		UserInputContainer userInput = new UserInputContainer(numberOfPartitions, 0, 1, filePathOfGraph_simpleGraph,
				strategies);
		Memory memory = new LimitedMemory(userInput.getMemorySize(), userInput.getNumberOfPartitions(),
				userInput.getUseWindow());
		PartitioningStrategy algorithm = StrategyFactory.instantiateStrategy(strategy, memory, userInput);
		partitioner = new GraphPartitioner(algorithm, userInput, memory, timeStamp);
		partitioner.computePartitioning();

		EvaluationData evalData = partitioner.getResults();
		Evaluation eval = new Evaluation(evalData);
		eval.evaluatePartitioning(timeStamp);

		double meanNumberOfEdges = 0.0;

		for (int id = 0; id < partitioner.getNumberOfPartitions(); id++) {
			meanNumberOfEdges += partitioner.getPartitionInfo(id).getTotalEdgeCount();
		}
		meanNumberOfEdges /= numberOfPartitions;

		assertTrue("Replication Degree niedriger als erwartet", evalData.getReplicationDegree() > 1.222);
		assertTrue("Replication Degree zu hoch", evalData.getReplicationDegree() < 1.223);
		assertTrue("balance besser als erwartet", evalData.getBalance() > 47.3);
		assertTrue("balance zu schlecht", evalData.getBalance() < 47.4);

		for (int i = 0; i < partitioner.getNumberOfPartitions(); i++) {
			double deviation = partitioner.getPartitionInfo(i).getTotalEdgeCount() - meanNumberOfEdges;
			assertTrue("Anzahl der Kanten weicht zu sehr vom Durchschnitt ab (Anzahl zu gross)", deviation < 1.3);
			assertTrue("Anzahl der Kanten weicht zu sehr vom Durchschnitt ab (Anzahl zu klein)", deviation > -1.3);
		}

	}

	@Test
	public void testWithParameters() {
		numberOfPartitions = 50;
		ArrayList<ParametrizedStrategy> strategies = new ArrayList<ParametrizedStrategy>();

		// first hdrf: balance optimum, second hdrf: replication degree optimum
		ParametrizedStrategy strategie_bal = new ParametrizedStrategy(strategyName,
				new HDRFParameterContainer(1000000000000.0, 1));
		ParametrizedStrategy strategie_rep = new ParametrizedStrategy(strategyName, new HDRFParameterContainer(0.0, 1));

		strategies.addAll(Arrays.asList(strategie_bal, strategie_rep));

		// no memory limit, windowsize 1, don't use the window
		UserInputContainer userInput = new UserInputContainer(numberOfPartitions, 0, 1, filePathOfGraph_facebookRandom,
				strategies);

		EvaluationData evalData_bal = null;
		EvaluationData evalData_rep = null;

		boolean first = true;

		for (ParametrizedStrategy strategy : userInput.getStrategies()) {
			LimitedMemory memory = new LimitedMemory(userInput.getMemorySize(), userInput.getNumberOfPartitions(),
					userInput.getUseWindow());
			PartitioningStrategy algorithm = StrategyFactory.instantiateStrategy(strategy, memory, userInput);
			GraphPartitioner partitioner = new GraphPartitioner(algorithm, userInput, memory, timeStamp);
			partitioner.computePartitioning();

			if (first) {
				first = false;
				evalData_bal = partitioner.getResults();
			} else {
				evalData_rep = partitioner.getResults();
			}
		}

		Evaluation eval_bal = new Evaluation(evalData_bal);
		eval_bal.evaluatePartitioning(timeStamp);

		Evaluation eval_rep = new Evaluation(evalData_rep);
		eval_rep.evaluatePartitioning(timeStamp);

		assertTrue("Balance-Vergleich schlug fehl", evalData_bal.getBalance() < evalData_rep.getBalance());
		assertTrue("Replicationdegree-Vergleich schlug fehl",
				evalData_bal.getReplicationDegree() > evalData_rep.getReplicationDegree());

	}
}
