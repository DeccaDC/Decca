package neu.lab.conflict.vo;

public class MethodCall {
	private String src;
	private String target;

	public MethodCall(String src, String target) {
		this.src = src;
		this.target = target;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MethodCall) {
			MethodCall rlt = (MethodCall) obj;
			return src.equals(rlt.getSrc()) && target.equals(rlt.getTgt());
		} else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return src.hashCode() * 31 + target.hashCode();
	}

	public String getSrc() {
		return src;
	}

	public String getTgt() {
		return target;
	}

	@Override
	public String toString() {
		return src + " to " + target;
	}

}
