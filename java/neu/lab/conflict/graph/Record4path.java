package neu.lab.conflict.graph;

public class Record4path extends IRecord {
	private String pathStr;
	private int pathlen;
	private String riskMthd;

	public Record4path(String riskMthd, String pathStr, int pathlen) {
		super();
		this.riskMthd = riskMthd;
		this.pathStr = pathStr;
		this.pathlen = pathlen;
	}

	public String getPathStr() {
		return pathStr;
	}

	public int getPathlen() {
		return pathlen;
	}

	@Override
	public IRecord clone() {
		return null;
	}

	public void setPathStr(String pathStr) {
		this.pathStr = pathStr;
	}

	public void setPathlen(int pathlen) {
		this.pathlen = pathlen;
	}

	public String getRiskMthd() {
		return riskMthd;
	}

	public String toString() {
		return "record:" + System.lineSeparator() + pathStr;
	}
	//	@Override
	//	public void finalize() {
	//		System.out.println("release one record");
	//	}

}
