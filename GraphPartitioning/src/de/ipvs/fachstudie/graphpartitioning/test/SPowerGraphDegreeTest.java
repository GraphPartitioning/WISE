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
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;
import de.ipvs.fachstudie.graphpartitioning.util.StrategyFactory;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 * 
 *         Test class for SPowerGraphDegree class
 *
 */
public class SPowerGraphDegreeTest {

	private final static String filePathOfGraph = Configuration.FILE_PATH_UNIT + "simpleGraph1.txt";
	private final static String strategyName = "degree";
	private final static int numberOfPartitions = 4;
	static GraphPartitioner partitioner;
	private static long timeStamp;
	static boolean firstCalled = true;
	private ParametrizedStrategy strategy = new ParametrizedStrategy(strategyName, null);
	private ArrayList<ParametrizedStrategy> strategies = new ArrayList<ParametrizedStrategy>();

	@Before
	public void setUp() throws Exception {
		if (firstCalled) {
			strategies.add(strategy);
			timeStamp = System.currentTimeMillis();
			UserInputContainer userInput = new UserInputContainer(numberOfPartitions, 0, 1, filePathOfGraph,
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
	public void testStrategyEdgeDistribution() {
		long totalEdgeCount = 0;
		long currentEdgeCount = partitioner.getPartitionInfo(0).getTotalEdgeCount();
		assertTrue(currentEdgeCount < 3);
		totalEdgeCount += currentEdgeCount;
		currentEdgeCount = partitioner.getPartitionInfo(1).getTotalEdgeCount();
		assertTrue(currentEdgeCount < 3);
		totalEdgeCount += currentEdgeCount;
		currentEdgeCount = partitioner.getPartitionInfo(2).getTotalEdgeCount();
		assertTrue(currentEdgeCount < 4);
		totalEdgeCount += currentEdgeCount;
		currentEdgeCount = partitioner.getPartitionInfo(3).getTotalEdgeCount();
		assertTrue(partitioner.getPartitionInfo(3).getTotalEdgeCount() < 3);
		totalEdgeCount += currentEdgeCount;
		assertTrue(totalEdgeCount == 9);
	}

	@Test
	public void testStrategyReplicationDegree() {

		EvaluationData data = partitioner.getResults();
		Evaluation evaluation = new Evaluation(data);
		evaluation.evaluatePartitioning(timeStamp);

		assertTrue(data.getReplicationDegree() < 1.8);
		assertTrue(data.getReplicationDegree() > 1.7);

	}

}
