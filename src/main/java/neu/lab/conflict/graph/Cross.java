package neu.lab.conflict.graph;

import java.util.Iterator;
import java.util.Set;


/**
 * 
 * @author asus
 *
 */
public class Cross {
	Iterator<String> cross;

	Cross(Node node) {
		Set<String> inMthds = node.getInNds();
		if (null != inMthds) {
			this.cross = inMthds.iterator();
		} else {
			this.cross = null;
		}
	}

	boolean hasBranch() {
		if (null == cross)
			return false;
		return cross.hasNext();
	}

	String getBranch() {
		return cross.next();
	}
}
