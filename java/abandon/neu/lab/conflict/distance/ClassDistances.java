package abandon.neu.lab.conflict.distance;

import neu.lab.conflict.util.MavenUtil;

public class ClassDistances extends NodeDistances {

	@Override
	public boolean isHostNode(String nodeName) {
		return MavenUtil.i().isHostClass(nodeName);
	}

}
