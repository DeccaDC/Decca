package neu.lab.conflict.soot;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import neu.lab.conflict.GlobalVar;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.vo.DepJar;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;

/**
 * filter for the riskMethod whose class exists in usedJar. hasFatherImpl
 * 
 * @author asus
 *
 */
public class SootRiskMthdFilter extends SootAna {

	public void filterRiskMthds(Collection<String> mthds2test) {
		long start = System.currentTimeMillis();
		try {
			SootUtil.modifyLogOut();

			RiskMthdFilterTf transformer = new RiskMthdFilterTf(mthds2test);
			PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));

			soot.Main.main(getArgs(DepJars.i().getUsedJarPaths().toArray(new String[0])).toArray(new String[0]));

		} catch (Exception e) {
			MavenUtil.i().getLog().warn("error when filter risk methods: ", e);

		}
		soot.G.reset();
		long runtime = (System.currentTimeMillis() - start) / 1000;
		GlobalVar.time2filterRiskMthd += runtime;
	}

	/**
	 * 多态过滤方法1
	 * 
	 * @param mthds2test
	 * @param depJar
	 */
	public void filterRiskMthds(Collection<String> mthds2test, DepJar depJar) {
		long start = System.currentTimeMillis();
		try {
			SootUtil.modifyLogOut();

			RiskMthdFilterTf transformer = new RiskMthdFilterTf(mthds2test);
			PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));

			soot.Main.main(getArgs(DepJars.i().getUsedJarPaths(depJar).toArray(new String[0])).toArray(new String[0]));

		} catch (Exception e) {
			MavenUtil.i().getLog().warn("error when filter risk methods: ", e);

		}
		soot.G.reset();
		long runtime = (System.currentTimeMillis() - start) / 1000;
		GlobalVar.time2filterRiskMthd += runtime;
	}

	@Override
	protected void addCgArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "cg", "off", }));
	}

}

class RiskMthdFilterTf extends SceneTransformer {

	private Collection<String> mthds2test;

	public RiskMthdFilterTf(Collection<String> mthds2test) {
		super();
		this.mthds2test = mthds2test;
	}

	@Override
	protected void internalTransform(String arg0, Map<String, String> arg1) {
		filterRiskMthds();
	}

	private void filterRiskMthds() {
		try {

			Iterator<String> ite = mthds2test.iterator();
			while (ite.hasNext()) {
				String testMthd = ite.next();
				// <neu.lab.plug.testcase.homemade.b.B2: void m1()>
				String[] pre_suf = testMthd.split(":");
				String className = pre_suf[0].substring(1);// neu.lab.plug.testcase.homemade.b.B2 去掉<
				String mthdSuffix = pre_suf[1];// void m1()>
				if (!Scene.v().containsClass(className)) {// weird class
//				MavenUtil.i().getLog().info("remove weird method:" + testMthd);
//				ite.remove();
				} else if (hasFatherImpl(className, mthdSuffix)) {
					// MavenUtil.i().getLog().info("remove father-implement-method:" + testMthd);
					ite.remove();
				}
				// MavenUtil.i().getLog().info(hasFatherImpl(className, mthdSuffix)+"");
			}

		} catch (Exception e) {
			MavenUtil.i().getLog().warn(e);
		} 
	}

	private boolean hasFatherImpl(String className, String mthdSuffix) {
		try {
			SootClass sootCls = Scene.v().getSootClass(className);
			while (sootCls.hasSuperclass()) {
				sootCls = sootCls.getSuperclass();
				String fathMthdSig = "<" + sootCls.getName() + ":" + mthdSuffix;
				// MavenUtil.i().getLog().info("super:"+fathMthdSig);
				if (Scene.v().containsMethod(fathMthdSig)) {
					// MavenUtil.i().getLog().info("contains");
					SootMethod fatherMthd = Scene.v().getMethod(fathMthdSig);
					if (fatherMthd.isConcrete() || fatherMthd.isNative()) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			MavenUtil.i().getLog().warn(e);
		}
		return false;
	}
}