package abandon.neu.lab.conflict.graph.mthdprob;

import neu.lab.conflict.graph.IRecord;

public class Record4MthdProb extends IRecord {
	private String tgtMthd;
	private Double prob;
	private Double distance;

	public Record4MthdProb(String mthd, Double prob, Double pathLen) {
		this.tgtMthd = mthd;
		this.prob = prob;
		this.distance = pathLen;
	}

	public String getTgtMthd() {
		return tgtMthd;
	}

	public Double getProb() {
		return prob;
	}
	
	public void updateDistance(Double distance2) {
		if (distance2 < this.distance) {
			this.distance = distance2;
		}
	}

	public void updateProb(Double prob2) {
		if (prob2 < this.prob) {
			this.prob = prob2;
		}
		// this.prob = this.prob + prob2;
	}

	public Double getDistance() {
		return distance;
	}

	@Override
	public IRecord clone() {
		return new Record4MthdProb(tgtMthd, prob, distance);
	}

	@Override
	public String toString() {
		return "Record4MthdProb [tgtMthd=" + tgtMthd + ", prob=" + prob + ", pathLen=" + distance + "]";
	}

}
