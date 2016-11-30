package de.ipvs.fachstudie.graphpartitioning.evaluation;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import de.ipvs.fachstudie.graphpartitioning.evaluation.PartitionEvaluation.SetsAlreadyFreedException;
import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.Launcher;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;
import de.ipvs.fachstudie.graphpartitioning.util.FileReader;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 *         Evaluation contains all method to evaluate data of one partitioning
 *         algorithm.
 */

public class Evaluation {

	private EvaluationData data;
	private boolean emptyPartition = false;

	public Evaluation(EvaluationData data) {
		this.data = data;
	}

	/**
	 * evaluates the partitioning strategy, prints result on console and on file
	 * 
	 * @param timeStamp
	 */
	public void evaluatePartitioning(long timeStamp) {

		Set<Integer> allVertices = new HashSet<Integer>();
		Set<PartitionEvaluation> allPartitionEvaluation = new HashSet<PartitionEvaluation>();
		long totalNumberOfEdges = 0;
		String parameters = "";
		for (Entry<String, Double> entry : this.data.getParameters().entrySet()) {
			parameters += entry.getKey() + "=" + entry.getValue() + "_";
		}
		for (int i = 0; i < data.getNumberOfPartitions(); i++) {

			PartitionEvaluation partitionEvaluation = readPartition(Configuration.OUTPUT_PATH + timeStamp + "_"
					+ this.data.getStrategyName() + "_" + parameters + i + ".txt", i);
			try {
				allVertices.addAll(partitionEvaluation.getVertexIds());
			} catch (SetsAlreadyFreedException e) {
				e.printStackTrace();
			}

			// to free memory, delete vertex sets and edge sets
			partitionEvaluation.deleteSetsAndStoreTotalSizes();
			totalNumberOfEdges += partitionEvaluation.getTotalEdgeCount();
			allPartitionEvaluation.add(partitionEvaluation);
		}
		data.setNumberOfDifferentVertices(allVertices.size());
		data.setNumberOfEdges(totalNumberOfEdges);

		double replicationDegree = this.calculateReplicationDegree(allPartitionEvaluation, (float) allVertices.size());
		data.setReplicationDegree(replicationDegree);

		double balance = this.calculateBalance(allPartitionEvaluation);
		data.setBalance(balance);

		this.printOnConsole();
		this.printEvaluationToFile();
	}

	/**
	 * helper method to read the partition files and generate the output data
	 * 
	 * @param filename
	 * @param partitionId
	 * @return
	 */
	private PartitionEvaluation readPartition(String filename, int partitionId) {
		PartitionEvaluation partition = new PartitionEvaluation(partitionId);
		FileReader reader = new FileReader(filename);
		Edge edge;
		while ((edge = reader.getNextEdge()) != null) {
			partition.addEdge(edge);
		}

		if (partition.getTotalEdgeCount() == 0) {
			this.emptyPartition = true;
		}
		return partition;
	}

	/**
	 * calculates the replication degree for a specific partitioning strategy
	 * 
	 * @param allPartitionEvaluation
	 * @param numberOfDifferentVertices
	 * @return replication degree
	 */
	private double calculateReplicationDegree(Set<PartitionEvaluation> allPartitionEvaluation,
			float numberOfDifferentVertices) {
		double replicationDegree = 0.0f;
		for (PartitionEvaluation partition : allPartitionEvaluation) {
			replicationDegree += partition.getTotalVertexCount();
		}

		return replicationDegree / numberOfDifferentVertices;
	}

	/**
	 * calculates the balance measured in the relative standard deviation in
	 * percent
	 * 
	 * @param allPartitionEvaluation
	 * @return balance
	 */
	private double calculateBalance(Set<PartitionEvaluation> allPartitionEvaluation) {
		double numberOfPartitions = allPartitionEvaluation.size();
		double numberOfEdges = 0.0f;

		for (PartitionEvaluation partition : allPartitionEvaluation) {
			numberOfEdges += partition.getTotalEdgeCount();
		}

		double avgPartitionSize = numberOfEdges / numberOfPartitions;

		double sum = 0.0;

		for (PartitionEvaluation partition : allPartitionEvaluation) {
			sum += Math.pow(partition.getTotalEdgeCount() - avgPartitionSize, 2);
		}
		double result = (Math.sqrt(sum / numberOfPartitions) / avgPartitionSize) * 100;

		return result;
	}

	/**
	 * Print evaluation on command line
	 */
	private void printOnConsole() {
		String memorySize;
		if (data.getMemorySize() <= 0) {
			memorySize = "unlimited";
		} else {
			memorySize = "" + data.getMemorySize();
		}
		String parameters = "";
		for (Entry<String, Double> entry : this.data.getParameters().entrySet()) {
			parameters += "_" + entry.getKey() + "=" + entry.getValue();
		}
		String text = "\n******************************start********************************\n" + "* Run on: "
				+ Launcher.getTimeStamp() + "\n" + "* Date: " + new Date(Launcher.getTimeStamp()) + "\n"
				+ "* Evaluation results:\n" + "*   graph: " + data.getGraphName() + "\n" + "*   strategy: "
				+ data.getStrategyName() + parameters + "\n" + "*   number of partitions: "
				+ data.getNumberOfPartitions() + "\n" + "*   memory size: " + memorySize + "\n" + "*   used Window: "
				+ data.getUseWindow() + "\n" + "*   windowSize: " + Configuration.WINDOW_SIZE + "\n"
				+ "*   total number of vertices in graph: " + data.getNumberOfDifferentVertices() + "\n"
				+ "*   total number of edges in graph: " + data.getNumberOfEdges() + "\n" + "*   balance: "
				+ data.getBalance() + "\n" + "*   replication degree: " + data.getReplicationDegree() + "\n"
				+ "*   execution time: " + data.getExecutionTime() + "ms\n";
		if (this.emptyPartition) {
			text = text + "*   INVALID RESULT: There are empty partitions!";
			text += "\n";
		}

		text += "******************************end**********************************\n";
		System.out.println(text);
	}

	/**
	 * print evaluation into file
	 */
	private void printEvaluationToFile() {

		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(Configuration.OUTPUT_PATH + "Results.txt", true), "utf-8"))) {
			writer.write(this.getEvaluationTextForFile());
		} catch (Exception e) {
			System.out.println("ERROR: Couldn't write evaluation to file");
			e.printStackTrace();
		}

		/*
		 * Christian:
		 * 
		 */
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Configuration.OUTPUT_PATH
				+ "Evals", true), "utf-8"))) {
			writer.write(data.getWindowSize() + "\t" 
					+ data.getNumberOfPartitions() + "\t"
//					+ Configuration.WINDOWSTRATEGY_ALPHA + "\t"
//					+ Configuration.WINDOWSTRATEGY_BETA + "\t"
//					+ Configuration.WINDOWSTRATEGY_GAMMA + "\t"
//					+ Configuration.WINDOWSTRATEGY_DELTA + "\t"
					+ data.getReplicationDegree() + "\t"
					+ data.getBalance() + "\t" + data.getExecutionTime() + "\n");
		} catch (Exception e) {
			System.out.println("ERROR: Couldn't write evaluation to file");
			e.printStackTrace();
		}

	}

	private String getEvaluationTextForFile() {
		String memorySize;
		if (data.getMemorySize() <= 0) {
			memorySize = "unlimited";
		} else {
			memorySize = "" + data.getMemorySize();
		}
		String windowSize;
		if (data.getUseWindow()) {
			windowSize = "" + data.getWindowSize();
		} else {
			windowSize = "1";
		}
		String parameters = "";
		for (Entry<String, Double> entry : this.data.getParameters().entrySet()) {
			parameters += "," + entry.getKey() + "=" + entry.getValue();
		}
		String lineEnd = ";\n";
		String text = "RunOn=" + Launcher.getTimeStamp() + lineEnd;
		text += "Graph=" + data.getGraphName() + lineEnd;
		text += "Vertices=" + data.getNumberOfDifferentVertices() + lineEnd;
		text += "Edges=" + data.getNumberOfEdges() + lineEnd;
		text += "WinSize=" + windowSize + lineEnd;
		text += "MemSize=" + memorySize + lineEnd;
		text += "NoPartitions=" + data.getNumberOfPartitions() + lineEnd;
		text += "Rep=" + data.getReplicationDegree() + lineEnd;
		text += "Bal=" + data.getBalance() + lineEnd;
		text += "ExeTime=" + data.getExecutionTime() + lineEnd;
		text += "Algorithm=" + data.getStrategyName() + parameters + lineEnd;
		text += "EndOfEntry\n";

		return text;
	}
}
