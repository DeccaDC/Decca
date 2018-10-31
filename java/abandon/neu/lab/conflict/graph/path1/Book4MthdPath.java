package abandon.neu.lab.conflict.graph.path1;

import java.util.ArrayList;
import java.util.List;

import neu.lab.conflict.graph.IRecord;

public class Book4MthdPath extends Book4Path {

	public Book4MthdPath(Node4Path node) {
		super(node);
	}

	/**
	 * @return path from host to conflict node
	 */
	public List<Record4MthdPath> getRiskPath() {
		List<Record4MthdPath> riskPaths = new ArrayList<Record4MthdPath>();
		for (IRecord recordI : getRecords()) {
			Record4MthdPath path = (Record4MthdPath) recordI;
			if (path.isFromHost())
				riskPaths.add(path);
		}
		return riskPaths;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(node.getName() + "\n");
		for (IRecord recordI : getRecords()) {
			Record4MthdPath path = (Record4MthdPath) recordI;
			sb.append("-");
			sb.append(path.getPathStr());
			sb.append("\n");
		}
		return sb.toString();
	}

}
