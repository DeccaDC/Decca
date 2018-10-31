package abandon.neu.lab.conflict.risk.ref.tb;

import java.util.Collection;
import java.util.Set;

public class LimitRefTb extends RefTb {
	private Set<String> validEes;

	public LimitRefTb(Set<String> validEes2) {
		super();
		this.validEes = validEes2;
	}

	@Override
	public void addByEe(String ee, Collection ers) {
		if (validEes.contains(ee))
			getNotNullErs(ee).addAll(ers);

	}

	@Override
	public void addByEr(String er, Collection ees) {
		for (Object ee : ees) {
			if (validEes.contains(ee))
				getNotNullErs((String) ee).add(er);
		}
	}
	
}
