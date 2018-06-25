package neu.lab.conflict.graph.filter;

import java.util.ArrayList;
import java.util.List;

import neu.lab.conflict.Conf;

public class FilterInvoker {

	private List<Filter> filters;

	public FilterInvoker() {
		filters = new ArrayList<Filter>();
		if (Conf.FLT_OBJ)
			filters.add(ObjFilter.i());
		if (Conf.FLT_SET)
			filters.add(SetFilter.i());
	}

	public boolean shouldFlt(String mthdSig) {
		for (Filter filter : filters) {
			if (filter.shdFltM(mthdSig))
				return true;
		}
		return false;
	}
}
