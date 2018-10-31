package neu.lab.conflict.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import neu.lab.conflict.util.MavenUtil;

public class GraphPrinter {
	public static void printGraph(IGraph graph,String outPath,Collection<String> entries) {
		MavenUtil.i().getLog().info("print graph.....");
		try {
			PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(outPath)));
			for(String entry:entries) {
				printer.println("entry:"+entry);
			}
			for(String name:graph.getAllNode()) {
				StringBuilder sb = new StringBuilder("node:"+name);
				INode node = graph.getNode(name);
				if(node instanceof Node4distance) {
					Node4distance node4branch = (Node4distance)node;
					sb.append(" isHost:"+node4branch.isHostNode());
					sb.append(" isRisk:"+node4branch.isRisk());
				}
				if(node instanceof Node4path) {
					Node4path node4mthdPath = (Node4path)node;
					sb.append(" isHost:"+node4mthdPath.isHostNode());
					sb.append(" isRisk:"+node4mthdPath.isRisk());
				}
				sb.append("\n");
				for(String out:node.getNexts()) {
					
					sb.append(out+"\n");
				}
				printer.println(sb.toString());
			}
			printer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
