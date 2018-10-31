package abandon.neu.lab.conflict.risk.ref.tb;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NoLimitRefTb extends RefTb {
	
	public void addByEe(String ee, Collection ers) {
		getNotNullErs(ee).addAll(ers);
	}

	public void addByEr(String er, Collection ees) {
		for (Object ee : ees) {
			getNotNullErs((String) ee).add(er);
		}
	}
}
