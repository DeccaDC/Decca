package neu.lab.evoshell;

import java.util.HashSet;
import java.util.Set;


/**
 * neu.lab.plug.testcase.homemade.host.H1.<init>(Lneu/lab/plug/testcase/homemade/a/A1;)V
 * 1.V 2.<> 3.return value 4.; 5.Lneu 6.I int 7./
 * 
 * @author asus
 *
 */
public class MthdFormatUtil {
	private static Set<String> baseTypeChar;
	static {
		baseTypeChar = new HashSet<String>();
		baseTypeChar.add("Z");
		baseTypeChar.add("C");
		baseTypeChar.add("B");
		baseTypeChar.add("S");
		baseTypeChar.add("I");
		baseTypeChar.add("F");
		baseTypeChar.add("J");
		baseTypeChar.add("D");
	}

	public static String evoMthd2cls(String evoMthd) {
		return evoMthd.substring(0, evoMthd.lastIndexOf("."));
	}
	
	/**
	 * @param mthdSig
	 *            e.g.:<org.slf4j.event.SubstituteLoggingEvent: org.slf4j.event.Level
	 *            getLevel()>
	 * @return e.g.:org.slf4j.event.SubstituteLoggingEvent
	 */
	public static String sootMthd2cls(String sootMthd) {
		return sootMthd.substring(1, sootMthd.indexOf(":"));
	}

	/**
	 * <neu.lab.plug.testcase.homemade.b.B1: void m2()>
	 * 
	 * @param sootMthd
	 * @return
	 */
	public static String soot2std(String sootMthd) {
		String[] cls_suffix = sootMthd.split(":");
		String cls = cls_suffix[0].substring(1);
		String[] ret_name_args = cls_suffix[1].split("\\(");
		String[] emp_ret_name = ret_name_args[0].split(" ");
		String ret = emp_ret_name[1];
		String name = emp_ret_name[2];
		String args = ret_name_args[1].substring(0, ret_name_args[1].length() - 2);
		return cls + "." + name + "(" + args + ")" + ret;
	}

	/**
	 * transform evosuite-method
	 * 
	 * @param evoMthd
	 * @return neu.lab.plug.testcase.homemade.host.H1.<init>(Lneu/lab/plug/testcase/homemade/a/A1;)V
	 *         neu.lab.plug.testcase.homemade.host.H1.<init>(neu.lab.plug.testcase.homemade.a.A1)
	 */
	public static String evo2std(String evoMthd) {
		// neu.lab.testcase.middle.Mid.<init>
		// Lneu/lab/testcase/bottom/B;)V
		String[] spilrStrs = evoMthd.split("\\(");
		// neu.lab.testcase.middle.Mid.<init>
		String pre = spilrStrs[0];
		// Lneu/lab/testcase/bottom/B;
		// V
		String[] args_ret = spilrStrs[1].split("\\)");
		String oldArgsStr = args_ret[0];
		// System.out.println(argsStr);
		String newArgsStr = evoArg2std(oldArgsStr);
		return pre + "(" + newArgsStr + ")" + evoCls2std(args_ret[1].replace(";", ""));
		// return pre+"("+
		// return ("<"+evoMthd.substring(0, evoMthd.length()-1)+">").
	}

	private static String evoArg2std(String oldArgsStr) {
		// System.out.println("old arg:"+oldArgsStr);
		if ("".equals(oldArgsStr))
			return "";
		String newArgsStr = "";
		int arrDen = 0;
		int startIndex = 0;
		int endIndex = 0;
		String firstChar = "";
		while (endIndex != oldArgsStr.length()) {
			String indexChar = "" + oldArgsStr.charAt(endIndex);
			if (indexChar.equals("[")) {// get a [
				arrDen++;
			} else if (";".equals(indexChar) && "L".equals(firstChar)) {// met L && met indexChae
				String oldCls = oldArgsStr.substring(startIndex, endIndex);
				String newCls = evoCls2std(oldCls.replace("[", "")) + repeatChar("[]", arrDen);
				newArgsStr = newArgsStr + "," + newCls;
				startIndex = endIndex + 1;
				arrDen = 0;
				firstChar = "";
			} else if (baseTypeChar.contains(indexChar) && "".equals(firstChar)) {
				String oldCls = oldArgsStr.substring(startIndex, endIndex + 1);
				String newCls = evoCls2std(oldCls.replace("[", "")) + repeatChar("[]", arrDen);
				newArgsStr = newArgsStr + "," + newCls;
				startIndex = endIndex + 1;
				arrDen = 0;
				firstChar = "";
			} else if ("".equals(firstChar) && "L".equals(indexChar)) {//
				firstChar = "L";
			}
			endIndex++;
		}
		// System.out.println("new Arg:"+newArgsStr);
		return newArgsStr.substring(1);
	}

	public static String evoCls2std(String evoCls) {
		String stdCls = "";
		switch (evoCls) {
		case "V":
			stdCls = "void";
			break;
		case "B":
			stdCls = "byte";
			break;
		case "Z":
			stdCls = "boolean";
			break;
		case "C":
			stdCls = "char";
			break;
		case "S":
			stdCls = "short";
			break;
		case "I":
			stdCls = "int";
			break;
		case "F":
			stdCls = "float";
			break;
		case "J":
			stdCls = "long";
			break;
		case "D":
			stdCls = "double";
			break;
		default:
			if (evoCls.startsWith("L")) {
				stdCls = evoCls.substring(1, evoCls.length()).replace("/", ".");
			} else {
				stdCls = evoCls.replace("/", ".");
			}
			break;
		}
		return stdCls;
	}

	/**
	 * @param charStr
	 *            []
	 * @param time
	 *            2
	 * @return [][]
	 */
	private static String repeatChar(String charStr, int time) {
		String result = "";
		for (int i = 0; i < time; i++) {
			result += charStr;
		}
		return result;
	}

	/**
	 * neu.lab.plug.testcase.homemade.host.H1.<init>(neu.lab.plug.testcase.homemade.a.A1)
	 * neu.lab.plug.testcase.homemade.host.H1.<init>(Lneu/lab/plug/testcase/homemade/a/A1;)V
	 * 
	 * @param stdMthd
	 * @return
	 */
	public static String std2evo(String stdMthd) {
		// neu.lab.testcase.top.Top.m
		// int,java.lang.String)void
		String[] name_suf = stdMthd.split("\\(");
		String pre = name_suf[0];// neu.lab.testcase.top.Top.m
		// int,java.lang.String
		// void
		String[] args_ret = name_suf[1].split("\\)");
		String newArgs = "";
		for (String stdCls : args_ret[0].split(",")) {
			if (!stdCls.equals("")) {
				newArgs += stdCls2evo(stdCls);
			}
		}
		// System.out.println(args_ret[1]);
		// System.out.println( stdCls2evo(args_ret[1]));
		return pre + "(" + newArgs + ")" + stdCls2evo(args_ret[1]);
	}

	/**
	 * @param stdCls
	 *            double[][][]
	 * @return [[[D
	 */
	public static String stdCls2evo(String stdCls) {
		int arrIndex = stdCls.indexOf("[]");
		int arrDen = 0;
		if (arrIndex != -1) {
			arrDen = (stdCls.length() - arrIndex) / 2;
			stdCls = stdCls.substring(0, arrIndex);
		}
		// System.out.println(stdCls);
		String evoCls = "";
		switch (stdCls) {
		case "void":
			evoCls = "V";
			break;
		case "boolean":
			evoCls = "Z";
			break;
		case "char":
			evoCls = "C";
			break;
		case "byte":
			evoCls = "B";
			break;
		case "short":
			evoCls = "S";
			break;
		case "int":
			evoCls = "I";
			break;
		case "float":
			evoCls = "F";
			break;
		case "long":
			evoCls = "J";
			break;
		case "double":
			evoCls = "D";
			break;
		default:
			evoCls = ("L" + stdCls).replace(".", "/") + ";";
			break;
		}
		return repeatChar("[", arrDen) + evoCls;
	}
	public static String soot2evo(String sootMthd) {
		return std2evo(soot2std(sootMthd));
	}

	public static void main(String[] args) {
		// <com.fasterxml.jackson.core.JsonFactory: boolean requiresPropertyOrdering()>

		// System.out.println(Util.soot2std("<com.fasterxml.jackson.core.JsonFactory:
		// boolean requiresPropertyOrdering()>"));
		// System.out.println(Util.std2evo("com.fasterxml.jackson.core.JsonFactory.requiresPropertyOrdering()boolean"));

//		System.out.println(
//				"neu.lab.testcase.middle.Mid.m2(int)void".equals(Util.evo2std("neu.lab.testcase.middle.Mid.m2(I)V")));
//		System.out.println("neu.lab.testcase.middle.Mid.<init>(neu.lab.testcase.bottom.B)void"
//				.equals(Util.evo2std("neu.lab.testcase.middle.Mid.<init>(Lneu/lab/testcase/bottom/B;)V")));
//		System.out.println("neu.lab.testcase.top.Top.m2(java.lang.String,int)void"
//				.equals(Util.evo2std("neu.lab.testcase.top.Top.m2(Ljava/lang/String;I)V")));
//		System.out.println("neu.lab.testcase.top.Top.m(int,java.lang.String)void"
//				.equals(Util.evo2std("neu.lab.testcase.top.Top.m(ILjava/lang/String;)V")));
		
		String evo = "neu.lab.testcase.top.ClassTop.m1(Ljava/lang/String;I)V";
		String soot = "<neu.lab.testcase.top.ClassTop: void m1(java.lang.String,int)>";
		System.out.println(soot2evo(soot));
		testPrint(evo, soot);
//		exeTest();
	}
	
	private static void exeTest() {
		// normal
		String evo = "math.stat.descriptive.moment.ThirdMoment.copy"
				+ "(Lmath/stat/descriptive/moment/ThirdMoment;Lmath/stat/descriptive/moment/ThirdMoment;)V";
		String soot = "<math.stat.descriptive.moment.ThirdMoment: void copy"
				+ "(math.stat.descriptive.moment.ThirdMoment,math.stat.descriptive.moment.ThirdMoment)>";
		testPrint(evo, soot);
		
		// has object return
				evo = "math.stat.descriptive.moment.GeometricMean.clear()Ljava/lang/Object;";
				soot = "<math.stat.descriptive.moment.GeometricMean: java.lang.Object clear()>";
				testPrint(evo, soot);
		// no arg
		evo = "math.stat.descriptive.moment.GeometricMean.clear()V";
		soot = "<math.stat.descriptive.moment.GeometricMean: void clear()>";
		testPrint(evo, soot);
		// multiple base type ; single-dimension-array
		evo = "math.stat.descriptive.moment.GeometricMean.evaluate([DII)D";
		soot = "<math.stat.descriptive.moment.GeometricMean: double evaluate(double[],int,int)>";
		testPrint(evo, soot);
		// continuous-single-dimension-array
		evo = "math.util.FastMath.splitMult([D[D[D)V";
		soot = "<math.util.FastMath: void splitMult(double[],double[],double[])>";
		testPrint(evo, soot);
		// multiple-dimension-array
		evo = "math.analysis.interpolation.TrivariateRealGridInterpolator.interpolate"
				+ "([D[D[D[[[D)Lmath/analysis/TrivariateRealFunction;";
		soot = "<math.analysis.interpolation.TrivariateRealGridInterpolator:"
				+ " math.analysis.TrivariateRealFunction interpolate(double[],double[],double[],double[][][])>";
		testPrint(evo, soot);
	}

	private static void testPrint(String evo, String soot) {
		String std = MthdFormatUtil.evo2std(evo);
		System.out.println("std:" + std);
		System.out.println("std:" + MthdFormatUtil.soot2std(soot));
		System.out.println("evo:" + evo);
		System.out.println("evo:" + MthdFormatUtil.std2evo(std));
		System.out.println();
	}

}

// System.out.println(Util.evo2std("neu.lab.plug.testcase.homemade.host.H1.m2()V"));
// System.out.println(
// Util.evo2std("neu.lab.plug.testcase.homemade.host.H1.<init>(Lneu/lab/plug/testcase/homemade/a/A1;)V"));
//
// System.out.println(Util.soot2std("<neu.lab.testcase.top.Top: void
// m2(int)>")); //
//
// System.out.println(Util.soot2std(
// "<neu.lab.plug.testcase.homemade.host.H1: void
// <init>(neu.lab.plug.testcase.homemade.a.A1)>"));
//
// System.out.println(
// Util.std2evo("neu.lab.plug.testcase.homemade.host.H1.<init>(neu.lab.plug.testcase.homemade.a.A1)void"));
//
// System.out.println(Util.std2evo("neu.lab.testcase.bottom.B.m2()void"));
