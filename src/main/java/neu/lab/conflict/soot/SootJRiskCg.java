package neu.lab.conflict.soot;

import java.util.Arrays;
import java.util.List;

import neu.lab.conflict.GlobalVar;
import neu.lab.conflict.graph.IGraph;
import neu.lab.conflict.risk.jar.DepJarJRisk;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.soot.tf.JRiskCgTf;
import soot.PackManager;
import soot.Transform;

public class SootJRiskCg extends SootAna {
	private static SootJRiskCg instance = new SootJRiskCg();

	private SootJRiskCg() {

	}

	public static SootJRiskCg i() {
		return instance;
	}

	public IGraph getGraph4distance(DepJarJRisk depJarJRisk,JRiskCgTf transformer) {
		MavenUtil.i().getLog().info("use soot to compute reach methods for " + depJarJRisk.toString());
		IGraph graph = null;
		long start = System.currentTimeMillis();
		try {

			SootUtil.modifyLogOut();

			PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));

			soot.Main.main(getArgs(depJarJRisk.getPrcDirPaths().toArray(new String[0])).toArray(new String[0]));

			graph = transformer.getGraph();

		} catch (Exception e) {
			MavenUtil.i().getLog().warn("cg error: ", e);
		}
		soot.G.reset();
		long runtime = (System.currentTimeMillis() - start) / 1000;
		GlobalVar.time2cg += runtime;
		return graph;
	}

	@Override
	protected void addCgArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "cg", "off", }));
	}
}


