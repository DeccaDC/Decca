package abandon.neu.lab.conflict.risk.ref.tb;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public abstract class RefTb implements Iterable<String> {
	/**
	 * key is target-class,value is source-class.A target-class can be called by multiple
	 * class.
	 */
	protected Map<String, Set<String>> ee2ers;

	public RefTb() {
		ee2ers = new HashMap<String, Set<String>>();
	}
	
//	public Collection<String> getAllEe(){
//		return ee2ers.keySet();
//	}

	public int eeSize() {
		return ee2ers.size();
	}
	public Set<String> getErs(String ee) {
		return ee2ers.get(ee);
	}

	@Override
	public Iterator<String> iterator() {
		return ee2ers.keySet().iterator();
	}

	public abstract void addByEe(String ee, Collection ers);

	public abstract void addByEr(String er, Collection ees);

	protected void add(String ee, String er) {
		getNotNullErs(ee).add(er);
	}

	protected Set<String> getNotNullErs(String ee) {
		Set<String> existErs = ee2ers.get(ee);
		if (null == existErs) {
			existErs = new HashSet<String>();
			ee2ers.put(ee, existErs);
		}
		return existErs;
	}
	public Set<String> getRefedEes(){
		Set<String> refedEes = new HashSet<String>();
		for(String ee:ee2ers.keySet()) {
			if(ee2ers.get(ee).size()>0)
				refedEes.add(ee);
		}
		return refedEes;
	}
	public void union(RefTb refTb) {
		for (String ee : refTb) {
			addByEe(ee, refTb.getErs(ee));
		}
	}
//	public ClsRefGraph getClsRefPathGraph() {
//		return ClsRefGraph.getGraph(this);
//	}
}
