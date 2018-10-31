package neu.lab.evoshell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.commons.exec.ExecuteException;

public class ListReader {

	static Map<String, String> module2conflict = new java.util.LinkedHashMap<String, String>();
//	"D:\\ws_testcase\\reportProject\\"
	static String baseDir = "";

	public static void main(String[] args) {
		try {
//			"D:\\cWS\\notepad++\\reportList_origin.txt"
//			"reportList.txt"
			BufferedReader reader = new BufferedReader(new FileReader("D:\\cWS\\notepad++\\reportList_origin.txt"));
			String line = reader.readLine();
			String module = null;
			while (line != null) {
				System.out.println();
				if (!line.equals("")) {
					if (line.startsWith("module:"))
						module = extractModule(line);
					if (line.startsWith("conflict:")) {
						String conflict = extractConflict(line);
						module2conflict.put(module, conflict);
					}
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(module2conflict.size());
		writeResult();
//		exeAllProject();
	}

	private static String getCmd(String module, String conflict) {
		String cmd = "D:\\cTool\\apache-maven-3.2.5\\bin\\mvn.bat -f=" + baseDir + module + " -DcallConflict=\"" + conflict
				+ "\" -Dmaven.test.skip=true package neu.lab:decca:1.0:debug2 -DpathDepth=100 -e";
		return cmd;
	}

	private static void exeAllProject() {
		int doneCnt = 0;
		for(String module:module2conflict.keySet()) {
			try {
				ExecUtil.exeCmd(getCmd(module,module2conflict.get(module)));
			} catch (ExecuteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			doneCnt++;
		}
		System.out.println("doneCnt/all:"+doneCnt+"/"+module2conflict.keySet());
	}

	private static void writeResult() {
		try {
			PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter("reportList_after.txt")));
			for (String module : module2conflict.keySet()) {
				System.out.println(module);
				printer.println("module:" +baseDir+ module);
				printer.println("conflict:" + module2conflict.get(module));
				printer.println();
			}
			printer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String extractModule(String line) {
				StringBuilder module = new StringBuilder("");
				String[] dirInPath = line.split("\\\\");
				for (int i = 3; i < dirInPath.length-1; i++) {
					module.append(dirInPath[i]+"\\");
				}
				module.append(dirInPath[dirInPath.length-1]);
				return module.toString();
//		return line.replace("module:", "");
	}

	private static String extractConflict(String line) {
				String[] dirInPath = line.split("\\\\");
				return dirInPath[dirInPath.length-1].split("@")[1];
//		return line.replace("conflict:", "");
	}
	
	
}
