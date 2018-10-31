package neu.lab.evoshell.graph;

import java.util.Collection;
import java.util.Iterator;


/**
 * 
 * @author asus
 *
 */
public class Cross {
	Iterator<String> cross;

	Cross(INode node) {
		Collection<String> inMthds = node.getNexts();
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
