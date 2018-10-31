package neu.lab.conflict.graph;

public class Record4distance extends IRecord {
	private String riskMthd;//riskMethod name
	private double branch;
	private double distance;

	public Record4distance(String name, double branch, double distance) {
		super();
		this.riskMthd = name;
		this.branch = branch;
		this.distance = distance;
	}

	public double getBranch() {
		return branch;
	}

	
	public double getDistance() {
		return distance;
	}

	public String getName() {
		return riskMthd;
	}

	@Override
	public IRecord clone() {
		return new Record4distance(riskMthd, branch, distance);
	}

	public void updateBranch(double branch2) {
		if (branch2 < branch) {
			branch = branch2;
		}

	}

	public void updateDistance(double distance2) {
		if (distance2 < distance) {
			distance = distance2;
		}
	}

	@Override
	public String toString() {
		return "Record4distance [riskMthd=" + riskMthd + ", branch=" + branch + ", distance=" + distance + "]";
	}

}
