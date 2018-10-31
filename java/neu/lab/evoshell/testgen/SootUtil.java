package neu.lab.evoshell.testgen;

import soot.SootClass;
import soot.SootMethod;

/**
 * @author asus
 *
 */
public class SootUtil {

	/**
	 * @param mthdSig
	 *            e.g.:<org.slf4j.event.SubstituteLoggingEvent: org.slf4j.event.Level
	 *            getLevel()>
	 * @return e.g.: org.slf4j.event.Level getLevel();
	 */
	public static String mthdSig2name(String mthdSig) {
		return mthdSig.substring(mthdSig.indexOf(":") + 1, mthdSig.indexOf(")") + 1);
	}

	/**
	 * @param mthdSig
	 *            e.g.:<org.slf4j.event.SubstituteLoggingEvent: org.slf4j.event.Level
	 *            getLevel()>
	 * @return e.g.:org.slf4j.event.SubstituteLoggingEvent
	 */
	public static String mthdSig2cls(String mthdSig) {
		return mthdSig.substring(1, mthdSig.indexOf(":"));
	}

	//	public static String cls2pck(String mthdSig) {
	//		
	//	}

	public static boolean isSimpleCls(SootClass sootClass) {
		for (SootMethod sootMethod : sootClass.getMethods()) {
			if (sootMethod.isConstructor() && sootMethod.getParameterCount() == 0) // exist constructor that doesn't
																					// need param
				return true;
		}
		return false;
	}

	/**
	 * @param split '.','\'
	 * @return org.apache.storm.kafka.monitor
	 * org\apache\storm\kafka\monitor
	 */
	public static String cls2pck(String originCls, String split) {
		StringBuilder sb = new StringBuilder("");
		String[] pckNames = originCls.split("\\.");
		for (int i = 0; i < pckNames.length - 1; i++) {
			sb.append(pckNames[i]);
			sb.append(split);
		}
//		System.out.println("originCls:" + originCls);
		if (pckNames.length > 1 )
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

}
