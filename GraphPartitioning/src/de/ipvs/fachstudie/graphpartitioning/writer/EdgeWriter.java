package de.ipvs.fachstudie.graphpartitioning.writer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.partitioner.strategies.PartitioningStrategy;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;
import de.ipvs.fachstudie.graphpartitioning.util.FileWriter;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 *         EdgeWriter devides the incoming edges to its Partition Writers which
 *         write the edges on the file for the specific partition
 */

public class EdgeWriter {

	Map<Integer, PartitionWriter> map = new HashMap<Integer, PartitionWriter>();
	private PartitioningStrategy strategy;
	private long timeStamp;
	private String filePath = "";

	public EdgeWriter(PartitioningStrategy strategy, long timestamp, int numberOfPartitions) {
		this.strategy = strategy;
		this.timeStamp = timestamp;
		String fileName;
		filePath = Configuration.OUTPUT_PATH + timeStamp + "_" + this.strategy.toString() + "_";
		for (Entry<String, Double> entry : this.strategy.getParameters().entrySet()) {
			filePath += entry.getKey() + "=" + entry.getValue() + "_";
		}

		for (int i = 0; i < numberOfPartitions; i++) {
			fileName = filePath + i + ".txt";
			// Use relative path for Unix systems
			File f = new File(fileName);
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * create a new Partition Writer if we had no writer for that partition
	 * before. Transfer edge to the partition writer
	 */
	public void setEdgeAssignment(Edge edge, int partitionId) {
		PartitionWriter partitionWriter = map.get(partitionId);

		if (partitionWriter == null) {
			partitionWriter = new PartitionWriter(partitionId, filePath, timeStamp);
			map.put(partitionId, partitionWriter);
		}
		partitionWriter.writeEdge(edge);
	}

	/*
	 * write all edges that are store in the buffers within partition writer
	 * even if the buffer is not full.
	 */
	public void writeAllBuffersToFile() {
		for (PartitionWriter partitionWriter : map.values()) {
			partitionWriter.flush();
		}
	}

}
