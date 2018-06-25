package neu.lab.conflict.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import neu.lab.conflict.Conf;

public class Book {
	protected Node node;
	private Set<Path> paths;

	public Book(Node node) {
		this.node = node;
		paths = new HashSet<Path>();
	}

	public void addChild(Book childBook) {
		Set<Path> childRecords = childBook.getPaths();
		for (Path path : childRecords) {
			paths.add(path.clone());
		}
	}

	public void addSelf() {
		if (paths.isEmpty()) {
			Path path = new Path(node.getName(), node.isHostNode(), 1);
			paths.add(path);
		} else {
			addNdToAll(node.getName());
		}
	}

	public void addNdToAll(String node) {
		for (Path path : paths) {
			path.addTail(node);
		}
	}

	public Set<Path> getPaths() {
		return paths;
	}

	/**
	 * @return path from host to conflict node
	 */
	public List<Path> getRiskPath() {
		List<Path> riskPaths = new ArrayList<Path>();
		for (Path path : getPaths()) {
			if (path.isFromHost() && path.getPathLen() >= Conf.MIN_PATH_DEP)//path whose depth is 2 is unreasonable.
				riskPaths.add(path);
		}
		return riskPaths;
	}

	// public void copy(Book book) {
	//
	// }
	public String toString() {
		StringBuilder sb = new StringBuilder(node.getName() + "\n");
		for (Path path : paths) {
			sb.append("-");
			sb.append(path.getPathStr());
			sb.append("\n");
		}
		return sb.toString();
	}
}
