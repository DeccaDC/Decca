package neu.lab.conflict.soot.tf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import neu.lab.conflict.graph.Graph4path;
import neu.lab.conflict.graph.Node4path;
import neu.lab.conflict.risk.jar.DepJarJRisk;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.vo.MethodCall;
import soot.Scene;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class JRiskMthdPathCgTf extends JRiskCgTf{

	public JRiskMthdPathCgTf(DepJarJRisk depJarJRisk) {
		super(depJarJRisk);
	}

	@Override
	protected void formGraph() {
		if (graph == null) {
			MavenUtil.i().getLog().info("start form graph...");
			// get call-graph.
			Map<String, Node4path> name2node = new HashMap<String, Node4path>();
			List<MethodCall> mthdRlts = new ArrayList<MethodCall>();
			CallGraph cg = Scene.v().getCallGraph();
			Iterator<Edge> ite = cg.iterator();
			while (ite.hasNext()) {
				Edge edge = ite.next();

				String srcMthdName = edge.src().getSignature();
				String tgtMthdName = edge.tgt().getSignature();
				// //TODO1
				// if("<com.fasterxml.jackson.core.JsonFactory: boolean
				// requiresPropertyOrdering()>".equals(tgtMthdName)) {
				// MavenUtil.i().getLog().info("srcMthdName:"+srcMthdName);
				// }
				String srcClsName = edge.src().getDeclaringClass().getName();
				String tgtClsName = edge.tgt().getDeclaringClass().getName();
				if (edge.src().isJavaLibraryMethod() || edge.tgt().isJavaLibraryMethod()) {
					// filter relation contains javaLibClass
				} else if (conflictJarClses.contains(SootUtil.mthdSig2cls(srcMthdName))
						&& conflictJarClses.contains(SootUtil.mthdSig2cls(tgtMthdName))) {
					// filter relation inside conflictJar
				} else {
					if (!name2node.containsKey(srcMthdName)) {
						name2node.put(srcMthdName, new Node4path(srcMthdName, isHostClass(srcClsName)&&!edge.src().isPrivate(),
								riskMthds.contains(srcMthdName)));
					}
					if (!name2node.containsKey(tgtMthdName)) {
						name2node.put(tgtMthdName, new Node4path(tgtMthdName, isHostClass(tgtClsName)&&!edge.tgt().isPrivate(),
								riskMthds.contains(tgtMthdName)));
					}
					mthdRlts.add(new MethodCall(srcMthdName, tgtMthdName));
				}
			}
			graph = new Graph4path(name2node, mthdRlts);
			MavenUtil.i().getLog().info("end form graph.");
		}
	}

	@Override
	protected void initMthd2branch() {
		
	}

}
