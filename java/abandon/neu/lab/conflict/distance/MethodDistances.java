package abandon.neu.lab.conflict.distance;

import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;

public class MethodDistances extends NodeDistances {

	@Override
	public boolean isHostNode(String nodeName) {
		return MavenUtil.i().isHostClass(SootUtil.mthdSig2cls(nodeName));
	}

}
