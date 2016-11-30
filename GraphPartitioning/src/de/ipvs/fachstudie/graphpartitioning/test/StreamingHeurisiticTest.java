package de.ipvs.fachstudie.graphpartitioning.test;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import de.ipvs.fachstudie.graphpartitioning.evaluation.Evaluation;
import de.ipvs.fachstudie.graphpartitioning.evaluation.EvaluationData;
import de.ipvs.fachstudie.graphpartitioning.partitioner.GraphPartitioner;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.LimitedMemory;
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
 */
public class StreamingHeurisiticTest {

	private final static String filePathOfGraph = Configuration.FILE_PATH_UNIT + "simpleGraph1.txt";
	private final static String strategyName = "streamingheuristic";
	private static ParametrizedStrategy strategy = new ParametrizedStrategy(strategyName, null);
	private final static int numberOfPartitions = 4;
	static GraphPartitioner partitioner;
	private static long timeStamp;
	static boolean firstCalled = true;

	@Before
	public void setUp() {
		if (firstCalled) {
			timeStamp = System.currentTimeMillis();
			// memory size 0 (no memory limit) //3
			UserInputContainer userInput = new UserInputContainer(numberOfPartitions, 0, 1, filePathOfGraph, null);
			LimitedMemory memory = new LimitedMemory(userInput.getMemorySize(), userInput.getNumberOfPartitions(),
					userInput.getUseWindow());
			PartitioningStrategy algorithm = StrategyFactory.instantiateStrategy(strategy, memory, userInput);
			partitioner = new GraphPartitioner(algorithm, userInput, memory, timeStamp);
			partitioner.computePartitioning();
			firstCalled = false;
		}
		/*
		 * for(int i=0; i<4;i++){ System.out.println("Number of Partition:" +
		 * i); for(Edge edge: partitioner.getCopyOfPartition(i).getEdges()){
		 * System.out.println(edge.toString()); } }
		 */

	}

	@Test
	public void testStrategy() {
		Evaluation evaluation = new Evaluation(partitioner.getResults());
		evaluation.evaluatePartitioning(timeStamp);
		assertTrue("TotalEdgeCount for Partition 0", partitioner.getPartitionInfo(0).getTotalEdgeCount() == 3);
		assertTrue("TotalEdgeCount for Partition 1", partitioner.getPartitionInfo(1).getTotalEdgeCount() == 3);
		assertTrue("TotalEdgeCount for Partition 2", partitioner.getPartitionInfo(2).getTotalEdgeCount() == 2);
		assertTrue("TotalEdgeCount for Partition 3", partitioner.getPartitionInfo(3).getTotalEdgeCount() == 1);
	}

	@Test
	public void testEvaluation() {
		EvaluationData data = partitioner.getResults();
		Evaluation evaluation = new Evaluation(data);
		evaluation.evaluatePartitioning(timeStamp);
		assertTrue(data.getReplicationDegree() < 1.3);
		assertTrue(data.getReplicationDegree() > 1.2);
	}
}
