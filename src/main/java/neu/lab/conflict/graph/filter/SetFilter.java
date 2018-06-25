package neu.lab.conflict.graph.filter;

import java.util.HashSet;
import java.util.Set;

public class SetFilter implements Filter {
	private static SetFilter instance;
	private Set<String> blackMthds;

	private SetFilter() {
		blackMthds = new HashSet<String>();
		blackMthds.add("java.util.Collection");

		blackMthds.add("java.util.Iterator");

		blackMthds.add("java.util.Map");
		blackMthds.add("java.util.Map$Entry");

	}

	public static SetFilter i() {
		if (instance == null)
			instance = new SetFilter();
		return instance;
	}

	@Override
	public boolean shdFltM(String mthdSig) {
		for (String blackMthd : blackMthds) {
			if (mthdSig.contains(blackMthd))
				return true;
		}
		return false;
	}
}
