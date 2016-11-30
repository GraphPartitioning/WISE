package de.ipvs.fachstudie.graphpartitioning.writer;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.util.Configuration;
import de.ipvs.fachstudie.graphpartitioning.util.FileWriter;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 * PartitionWriter contains the buffer which edges to be written in one
 * step asynchronously.
 */
public class PartitionWriter {

	// edge buffer for writing
	int partitionId;
	String fileName;
	private BufferedWriter out;

	public PartitionWriter(int partitionId, String filePath, long timeStamp) {
		this.partitionId = partitionId;
		fileName = filePath + partitionId + ".txt";
		try {
			this.out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName, false), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeEdge(Edge e) {
		try {
			out.write(e.toString() + "\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void flush() {
		try {
			this.out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
