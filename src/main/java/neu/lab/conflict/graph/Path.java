package neu.lab.conflict.graph;

public class Path {
	private String pathStr;
	private boolean isFromHost;
	private int pathLen;

	public int getPathLen() {
		return pathLen;
	}

	public boolean isFromHost() {
		return isFromHost;
	}

	public Path(String path, boolean isFromHost, int pathLen) {
		this.pathStr = path;
		this.isFromHost = isFromHost;
		this.pathLen = pathLen;
	}

	public Path clone() {
		return new Path(pathStr, isFromHost, pathLen);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Path) {
			Path otherPath = (Path) other;
			return pathStr.equals(otherPath.getPathStr());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return pathStr.hashCode();
	}

	@Override
	public String toString() {
		return getPathStr() + " isFromHost:" + isFromHost() + " path length:" + pathLen;
	}

	public void addTail(String node) {
		pathStr = pathStr + "->" + node;
		pathLen++;

	}

	public String getPathStr() {
		return pathStr;
	}
}
