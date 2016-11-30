package de.ipvs.fachstudie.graphpartitioning.evaluation;

import java.util.HashMap;
import de.ipvs.fachstudie.graphpartitioning.userInput.UserInputContainer;

/**
 * /**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 * 
 *         EvaluationData contains all data that can be evaluated when
 *         partitioning
 */
public class EvaluationData {
	private String strategyName;
	HashMap<String, Double> parameters;
	private String graphName;
	private long executionTime;
	private int numberOfPartitions;
	private int memorySize;
	private double replicationDegree;
	private double balance;
	private long numberOfDifferentVertices;
	private long numberOfEdges;
	private int windowSize;
	private boolean useWindow;

	public EvaluationData(String strategyName, HashMap<String, Double> parameters, UserInputContainer userInput,
			long executionTime) {
		this.strategyName = strategyName;
		this.parameters = parameters;
		this.graphName = userInput.getGraphPath();
		this.executionTime = executionTime;
		this.numberOfPartitions = userInput.getNumberOfPartitions();
		this.memorySize = userInput.getMemorySize();
		this.windowSize = userInput.getWindowSize();
		this.useWindow = userInput.getUseWindow();
	}

	public boolean getUseWindow() {
		return this.useWindow;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public int getNumberOfPartitions() {
		return this.numberOfPartitions;
	}

	public int getMemorySize() {
		return this.memorySize;
	}

	public String getStrategyName() {
		return this.strategyName;
	}

	public String getGraphName() {
		return this.graphName;
	}

	public long getExecutionTime() {
		return this.executionTime;
	}

	public double getReplicationDegree() {
		return replicationDegree;
	}

	public void setReplicationDegree(double replicationDegree) {
		this.replicationDegree = replicationDegree;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public long getNumberOfDifferentVertices() {
		return numberOfDifferentVertices;
	}

	public void setNumberOfDifferentVertices(long numberOfDifferentVertices) {
		this.numberOfDifferentVertices = numberOfDifferentVertices;
	}

	public long getNumberOfEdges() {
		return numberOfEdges;
	}

	public void setNumberOfEdges(long numberOfEdges) {
		this.numberOfEdges = numberOfEdges;
	}

	HashMap<String, Double> getParameters() {
		return parameters;
	}

}
