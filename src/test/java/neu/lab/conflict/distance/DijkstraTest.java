package neu.lab.conflict.distance;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import abandon.neu.lab.conflict.distance.Dijkstra;
import abandon.neu.lab.conflict.distance.DijkstraMap;
import abandon.neu.lab.conflict.distance.DijkstraNode;

public class DijkstraTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws Exception {
//		long start = System.currentTimeMillis();
//		Set<String> startNds = new HashSet<String>();
//		File f = new File("d:/djGraph.txt");
//		startNds.add("<com.google.common.hash.Hashing$ConcatenatedHashFunction: boolean equals(java.lang.Object)>");
////		File f = new File("projectFile/distance.txt");
////		startNds.add("<neu.lab.plug.testcase.homemade.b.B1: void m2()>");
////		startNds.add("<neu.lab.plug.testcase.homemade.b.B2: void m1()>");
//		Dijkstra dj = loadGraph(f);
//		Map<String, Map<String, Double>> distances = dj.getDistanceTb(startNds);
//
//
//		MethodDistance.i().addDistances(distances);
//		 System.out.println(MethodDistance.i().toString());
//		 System.out.println((System.currentTimeMillis()-start)/1000);
	}

	private Dijkstra loadGraph(File graphFile) throws Exception {
		Dijkstra dj = new DijkstraMap();
		BufferedReader reader = new BufferedReader(new FileReader(graphFile));
		String line = reader.readLine();
		DijkstraNode currentNd = null;
		while (line != null) {
			if (!"".equals(line)&&!"graph:".equals(line)) {
				line = line.replace(",", "");
				if (line.startsWith("node:")) {
					currentNd = new DijkstraNode(line.replace("node:", ""));
					dj.addNode(currentNd);
				} else {
					currentNd.addIn(line);
				}
			}
			line = reader.readLine();
		}
		reader.close();
		return dj;
	}
}
