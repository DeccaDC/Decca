package abandon.neu.lab.conflict.distance;


public class NameAndDist implements Comparable<NameAndDist> {

	String name;
	Double distance;

	public NameAndDist(String name, Double distance) {
		this.name = name;
		assert(distance>=0);
		this.distance = distance;
	}

	@Override
	public int compareTo(NameAndDist o) {
		double diff = distance - o.distance;
//		return (int)diff;
		if(diff!=0) {
			return (int)diff;
		}else {
			return name.hashCode()-o.hashCode();
		}
	}

	@Override
	public String toString() {
		return name + " " + distance;
	}
	
//	@Override
//	public boolean equals(Object obj) {
//		if(obj instanceof NameAndDist) {
//			NameAndDist other = (NameAndDist)obj;
//		}
//	}

//	public static void main(String[] args) {
//		NameAndDist d1 = new NameAndDist("a", 2.0);
//		NameAndDist d2 = new NameAndDist("b", 1.0);
//		TreeSet<NameAndDist> s = new TreeSet<NameAndDist>();
//		s.add(d2);
//		s.add(d1);
//		d2.distance=3.0;
//		
//		while(!s.isEmpty()) {
//			System.out.println(s.pollFirst());
//		}
//	}
}
