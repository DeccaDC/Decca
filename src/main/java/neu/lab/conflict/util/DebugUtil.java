package neu.lab.conflict.util;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class DebugUtil {
	private static Map<String, PrintWriter> path2printer = new HashMap<String, PrintWriter>();

	public static void print(String outFilePath, String content) {
		try {
			PrintWriter printer = path2printer.get(outFilePath);
			if(printer==null) {
				printer = new PrintWriter(new FileWriter(outFilePath));
				path2printer.put(outFilePath, printer);
			}
			printer.println(content);
			printer.flush();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("ioException:", e);
		}
	}
}
