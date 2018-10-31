package neu.lab.conflict.soot;


import soot.jimple.toolkits.callgraph.Edge;

public class ClassThrowTf extends ThrowRiskTf {

	@Override
	protected boolean isRiskCall(Edge edge) {
		return thrownMthds.contains(edge.tgt().getSignature());
	}

}
