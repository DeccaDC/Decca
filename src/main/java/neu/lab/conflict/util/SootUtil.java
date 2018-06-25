package neu.lab.conflict.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neu.lab.conflict.Conf;
import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.MethodVO;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;

/**
 * @author asus
 *
 */
public class SootUtil {
	public static void modifyLogOut() {
		File outDir = MavenUtil.i().getBuildDir();
		if (!outDir.exists()) {
			outDir.mkdirs();
		}
		try {
			soot.G.v().out = new PrintStream(new File(outDir.getAbsolutePath() + File.separator + "soot.log"));
		} catch (FileNotFoundException e) {
			soot.G.v().out = System.out;
		}
	}

	/**
	 * @param mthdSig
	 *            eg.:<org.slf4j.event.SubstituteLoggingEvent: org.slf4j.event.Level
	 *            getLevel()>
	 * @return eg.: org.slf4j.event.Level getLevel();
	 */
	public static String mthdSig2name(String mthdSig) {
		return mthdSig.substring(mthdSig.indexOf(":") + 1, mthdSig.indexOf(")") + 1);
	}

	/**
	 * @param mthdSig
	 *            eg.:<org.slf4j.event.SubstituteLoggingEvent: org.slf4j.event.Level
	 *            getLevel()>
	 * @return eg.:org.slf4j.event.SubstituteLoggingEvent
	 */
	public static String mthdSig2cls(String mthdSig) {
		return mthdSig.substring(1, mthdSig.indexOf(":"));
	}
	public static List<String> getJarClasses(DepJar depJar) {
		return getJarClasses(depJar.getClassPath());
	}

	public static List<String> getJarClasses(List<String> paths) {
		List<String> allCls = new ArrayList<String>();
		for (String classPath : paths) {
			if (new File(classPath).exists()) {
				if (!classPath.endsWith("tar.gz") && !classPath.endsWith(".pom") && !classPath.endsWith(".war")) {
					allCls.addAll(SourceLocator.v().getClassesUnder(classPath));
				} else {
//					MavenUtil.i().getLog().warn(classPath + " is illegal classpath");
				}
			} else {
//				MavenUtil.i().getLog().warn(classPath + "doesn't exist in local");
			}

		}
		return allCls;
	}

	public static Map<String, ClassVO> getClassTb(List<String> jarPaths) {
		Map<String, ClassVO> clsTb = new HashMap<String, ClassVO>();
		for (String clsSig : SootUtil.getJarClasses(jarPaths)) {
			SootClass sootClass = Scene.v().getSootClass(clsSig);
			ClassVO clsVO = new ClassVO(sootClass.getName());
			clsTb.put(sootClass.getName(), clsVO);
			if (Conf.ONLY_GET_SIMPLE) {// only add simple method in simple class
				if (isSimpleCls(sootClass)) {
					for (SootMethod sootMethod : sootClass.getMethods()) {
						if (sootMethod.getParameterCount() == 0)
							clsVO.addMethod(new MethodVO(sootMethod.getSignature(), clsVO));
					}
				}
			} else {// add all method
				for (SootMethod sootMethod : sootClass.getMethods()) {
					clsVO.addMethod(new MethodVO(sootMethod.getSignature(), clsVO));
				}
			}
		}
		return clsTb;
	}

	public static boolean isSimpleCls(SootClass sootClass) {
		for (SootMethod sootMethod : sootClass.getMethods()) {
			if (sootMethod.isConstructor() && sootMethod.getParameterCount() == 0) // exist constructor that doesn't
																					// need param
				return true;
		}
		return false;
	}
}
