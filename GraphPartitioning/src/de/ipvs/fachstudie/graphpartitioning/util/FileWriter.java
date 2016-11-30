package de.ipvs.fachstudie.graphpartitioning.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger File Writer writes a collection of edges to a given path.
 */
public class FileWriter {

	private String writePath;
	private Writer writer;
	// because "\n" should appear at the beginning of the file
	private boolean firstEdge;
	private List<Edge> edges;

	public FileWriter(String path, List<Edge> edges, boolean alreadyWritten) {
		this.writePath = path;
		this.edges = edges;
		// already written has to be stored outside of the class
		firstEdge = !alreadyWritten;
		try {
			// only create new file if old one doesn't exists
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writePath, true), "utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeToFile(List<Edge> edges) {
		try {
			for (Edge edge : edges) {
				if (this.firstEdge) {
					writer.write(edge.toString());
					this.firstEdge = false;
				} else {
					writer.write("\n");
					writer.write(edge.toString());
				}
			}
			writer.flush();
		} catch (IOException e) {
			System.out.println("ERROR: Couldn't write partitioning to file.");
			e.printStackTrace();
		}
	}

	public void writeAll() {
		this.writeToFile(edges);
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
