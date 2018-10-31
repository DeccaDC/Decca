package neu.lab.evoshell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author asus
 *move testCase to main.
 */
public class TestCaseUtil {

	private static final String evoClass = "EvoTest";

	public static void moveEvo(String evoResultPath, String outPath) {
		//TODO printDebugCopy
		FileUtil.copyFile(evoResultPath, ShellConfig.tmpWsDir + "evoTest.java");
		new File(outPath).getParentFile().mkdirs();
		List<String> mthdNames = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(evoResultPath));
			String line = reader.readLine();
			while (line != null) {
				if (line.startsWith("public class ")) {//change className
					sb.append("class " + evoClass + "{" + System.lineSeparator());
				} else if (line.startsWith("      try {")) {
					sb.append("      {");
				} else if (line.startsWith("  public void ")) {//change to static and get name
					sb.append(line.replace("  public void ", "  public static void ") + System.lineSeparator());
					mthdNames.add(extraMthdName(line));
				}else if(line.contains("MockPrintStream")) {
					sb.append("      java.io.ByteArrayOutputStream mockPrintStream0 = new java.io.ByteArrayOutputStream();" + System.lineSeparator());
				} else if (!shouldFilter(line)) {
					sb.append(line + System.lineSeparator());
				}

				line = reader.readLine();
			}
			//end evosuite class.
			sb.append(getMainClass(mthdNames) + System.lineSeparator());
			reader.close();

			PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(outPath)));
			printer.println(sb.toString());
			printer.close();
			
			FileUtil.copyFile(outPath, ShellConfig.tmpWsDir + "finalTest.java");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getMainClass(List<String> mthdNames) {
		StringBuilder sb = new StringBuilder();
		sb.append("public class Test1{" + System.lineSeparator());
		sb.append("	public static void main(String[] args)  throws Throwable  {" + System.lineSeparator());
		for (String mthdName : mthdNames) {
			sb.append("        " + evoClass);
			sb.append(".");
			sb.append(mthdName);
			sb.append("();" + System.lineSeparator());
		}
		sb.append("	}" + System.lineSeparator());
		sb.append("}");
		return sb.toString();
	}

	/**  public static void test0()  throws Throwable  {
	 * @param name
	 * @return test0
	 */
	private static String extraMthdName(String line) {
		return line.split("\\(")[0].substring(line.indexOf("test"));
	}

	private static boolean shouldFilter(String line) {
		//		return true;
		if (line.contains("org.junit")) {
			return true;
		}
		if (line.contains("org.evosuite.runtime")) {
			return true;
		}
		if (line.startsWith("  @Test")) {
			return true;
		}
		if (line.startsWith("@RunWith")) {
			return true;
		}
		if (line.contains("verifyException(\"")) {
			return true;
		}
		if (line.contains("} catch(NoSuchMethodError e) {")) {
			return true;
		}
		if (line.contains("} catch(NoClassDefFoundError e) {")) {
			return true;
		}
		return false;
	}

	//	public static void main(String[] args) {
	//		TestCaseUtil.moveEvo("D:\\ws_testcase\\testcase\\evosuite-tests\\org\\apache\\storm\\kafka\\monitor\\KafkaOffsetLagUtil_ESTest.java", 
	//				"D:\\cWS\\notepad++\\main.java");
	//	}
}
