package abandon.neu.lab.conflict.graph.path1;

import neu.lab.conflict.graph.IRecord;

public abstract class Record4Path extends IRecord{
	protected String pathStr;
	
	protected int pathLen;

	public int getPathLen() {
		return pathLen;
	}

	public Record4Path(String pathStr,  int pathLen) {
		this.pathStr = pathStr;
		this.pathLen = pathLen;
	}

	public void addTail(String node) {
//		System.out.println();
		pathStr = pathStr + " to " + node;
		pathLen++;
	}

	public String getPathStr() {
		return pathStr;
	}
}
