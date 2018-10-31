package neu.lab.conflict.graph;

import java.util.Collection;

public interface IGraph {
	public INode getNode(String nodeName);
	public Collection<String> getAllNode();
}
