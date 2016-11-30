package de.ipvs.fachstudie.graphpartitioning.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.model.Vertex;
import de.ipvs.fachstudie.graphpartitioning.partitioner.windowstrategies.EdgeWindow;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 * 
 *         The FileReader reads the edges of a graph from a file. Edges must be
 *         given in the following notation: 2 4 for the edge (2,4)
 */
public class FileReader {
	private BufferedInputStream inputStream;
	Scanner scanner;
	private long fileSize = 0;

	public FileReader(String inputFile) {
		try {
			File f = new File(inputFile);
			this.fileSize = f.length();
			this.inputStream = new BufferedInputStream(
					new FileInputStream(inputFile),1024*1000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.scanner = new Scanner(inputStream);
	}

	public boolean hasNextEdge() {
		return scanner.hasNext();
	}

	public long getFileSize() {
		return this.fileSize;
	}

	/**
	 * Reads the lines of the file and returns one by one all the lines that fit
	 * the notation defined above.
	 * 
	 * @return edge
	 */
	public Edge getNextEdge() {
		if (scanner.hasNext()) {
			String edgeString = scanner.nextLine();
			// only take line if it fit our notation
			if (edgeString.matches("^[0-9]+\\s+[0-9]+$")) {
				String[] edge = edgeString.split("\\s+");
				return new Edge(new Vertex(Integer.parseInt(edge[0])), new Vertex(Integer.parseInt(edge[1])));
			} else {
				// skip
				// System.out.println("String didn't match format: '" +
				// edgeString + "'. Line will be ignored.");
			}
		} else {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			scanner.close();
		}
		return null;
	}

	/**
	 * Fills the window with new edges and returns the edges added to the window
	 * 
	 * @param window
	 * @return
	 */
	public List<Edge> fillWindow(EdgeWindow window) {
		List<Edge> addedEdges = new ArrayList<Edge>();
		while (window.hasCapacity() && hasNextEdge()) {
			Edge edge = getNextEdge();
			if (edge != null) {
				addedEdges.add(edge);
				window.addEdge(edge);
			}
		}
		return addedEdges;
	}

}
