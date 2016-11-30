package de.ipvs.fachstudie.graphpartitioning.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.ipvs.fachstudie.graphpartitioning.model.Edge;
import de.ipvs.fachstudie.graphpartitioning.model.Vertex;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.LimitedMemory;
import de.ipvs.fachstudie.graphpartitioning.partitioner.memory.Memory;

/**
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 */
public class LimitedMemoryTest {
	private Memory memory1;
	private Memory memory2;
	private Memory memory3;
	private int partitionId = 0;
	private int partitionsCount = 4;
	private int memorySize = 5;

	@Before
	public void setUp() {
		// 4 partitions, memory size = 3, use limitation = true
		memory1 = new LimitedMemory(partitionsCount, memorySize, true);
		memory2 = new LimitedMemory(partitionsCount, memorySize, false);
		memory3 = new LimitedMemory(partitionsCount, memorySize, false);
	}

	@Test
	public void testMemoryContent() {
		Edge e;
		Edge firstEdge = new Edge(new Vertex(88), new Vertex(99));
		memory1.store(firstEdge, 1);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 5; j++) {
				e = new Edge(new Vertex(i), new Vertex(j));
				partitionId++;
				partitionId = partitionId % partitionsCount;
				memory1.store(e, partitionId);
				// vertex 99 will never be deleted
				memory1.getPartitionIdsOfVertex(99);
				assertTrue(memory1.getPartitionInfo(1).containsReplicationOfVertex(99));
			}
		}
	}

	@Test
	public void testUnlimited() {
		Edge e;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 5; j++) {
				e = new Edge(new Vertex(i), new Vertex(j));
				memory3.store(e, 0);
			}
		}

		for (int j = 0; j < 10; j++) {
			assertTrue(memory3.getPartitionIdsOfVertex(j).size() == 1);
		}

	}

	@Test
	public void testGetReplications() {
		for (int i = 1; i < 10; i++) {
			memory2.store(new Edge(new Vertex(0), new Vertex(i)), 0);
		}
		memory2.store(new Edge(new Vertex(1), new Vertex(2)), 1);
		memory2.store(new Edge(new Vertex(3), new Vertex(1)), 2);
		memory2.store(new Edge(new Vertex(4), new Vertex(1)), 3);

		assertTrue(memory2.getPartitionIdsOfVertex(3).size() == 2);
		assertTrue(memory2.getPartitionIdsOfVertex(1).size() == 4);
	}

}
