package abandon.neu.lab.conflict.graph.path1;

public class Record4MthdPath extends Record4Path {

	protected boolean isFromHost;

	public boolean isFromHost() {
		return isFromHost;
	}

	public Record4MthdPath(String path, boolean isFromHost, int pathLen) {
		super(path, pathLen);
		this.isFromHost = isFromHost;
	}

	@Override
	public String toString() {
		return getPathStr() + " isFromHost:" + isFromHost() + " path length:" + pathLen;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Record4MthdPath) {
			Record4MthdPath otherPath = (Record4MthdPath) other;
			return pathStr.equals(otherPath.getPathStr());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return pathStr.hashCode();
	}

	public Record4Path clone() {
		return new Record4MthdPath(pathStr, isFromHost, pathLen);
	}

}
