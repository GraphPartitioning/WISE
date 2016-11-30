package de.ipvs.fachstudie.graphpartitioning.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.ipvs.fachstudie.graphpartitioning.evaluation.Evaluation;
import de.ipvs.fachstudie.graphpartitioning.evaluation.EvaluationData;
import de.ipvs.fachstudie.graphpartitioning.partitioner.GraphPartitioner;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.LimitedMemory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.PartitioningStrategy;
import de.ipvs.fachstudie.graphpartitioning.userInput.UserInputContainer;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.ParametrizedStrategy;
import de.ipvs.fachstudie.graphpartitioning.userInput.strategyParameterContainers.WindowStrategyParameterContainer;
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
public class WindowStrategyTest {

	private final static String filePathOfGraph = Configuration.FILE_PATH_UNIT + "facebook_random.txt";
	private final static String strategyName = NamesOfStrategies.WINDOWSTRATEGY.toString();
	private final static int numberOfPartitions = 4;
	static GraphPartitioner partitioner;
	private static long timeStamp;
	static boolean firstCalled = true;
	private ParametrizedStrategy strategy = new ParametrizedStrategy(strategyName,
			new WindowStrategyParameterContainer(1, 8, 4, 1));
	private ArrayList<ParametrizedStrategy> strategies = new ArrayList<ParametrizedStrategy>();

	@Before
	public void setUp() throws Exception {
		if (firstCalled) {
			strategies.add(strategy);
			timeStamp = System.currentTimeMillis();
			UserInputContainer userInput = new UserInputContainer(numberOfPartitions, 10000, 1000, filePathOfGraph,
					strategies);
			Memory memory = new LimitedMemory(userInput.getMemorySize(), userInput.getNumberOfPartitions(),
					userInput.getUseWindow());
			PartitioningStrategy algorithm = StrategyFactory.instantiateStrategy(strategy, memory, userInput);
			partitioner = new GraphPartitioner(algorithm, userInput, memory, timeStamp);
			partitioner.computePartitioning();
			firstCalled = false;
		}
	}

	@Test
	public void test() {
		EvaluationData data = partitioner.getResults();
		Evaluation evaluation = new Evaluation(data);
		evaluation.evaluatePartitioning(timeStamp);

		for (int i = 0; i < 4; i++) {
			System.out.println(i + ") " + partitioner.getPartitionInfo(i).getTotalEdgeCount());
		}
		assertTrue(partitioner.getPartitionInfo(0).getTotalEdgeCount() <= 22059);
		assertTrue(partitioner.getPartitionInfo(1).getTotalEdgeCount() <= 22059);
		assertTrue(partitioner.getPartitionInfo(2).getTotalEdgeCount() <= 22059);
		assertTrue(partitioner.getPartitionInfo(3).getTotalEdgeCount() <= 22059);

		assertTrue(data.getReplicationDegree() < 1.5);
		assertTrue(data.getReplicationDegree() > 1.4);
		assertTrue(data.getBalance() < 0.01);
	}

}
