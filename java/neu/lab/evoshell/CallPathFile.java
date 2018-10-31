package neu.lab.evoshell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CallPathFile {

	
	private static int riskMthdNum4test = 1;
	private static int exePath4riskMthd = 1;//callPath handled for per riskMethod.
	//	private static String riskMethod4debug = null;

	private String classPath;
	private String pomPath;//D:\cWS\eclipse1\testcase.top
	private String distanceFilePath;
	private List<String> mthdSeq;
	private Map<String, LinkedList<CallPathValidator>> mthd2vlds;

	public CallPathFile(String callPathsFile) throws Exception {
		distanceFilePath = callPathsFile.replace("p_", "d_");
		if (!new File(distanceFilePath).exists()) {
			throw new Exception("DistanceFile for " + callPathsFile + " is not found.");
		}
		mthdSeq = new ArrayList<String>();
		mthd2vlds = new HashMap<String, LinkedList<CallPathValidator>>();
		BufferedReader reader = new BufferedReader(new FileReader(callPathsFile));
		String line = reader.readLine();
		List<String> mthds = new ArrayList<String>();
		List<String> jarPaths = new ArrayList<String>();
		while (line != null) {
			if (!line.equals("")) {
				if (line.startsWith("classPath")) {
					classPath = line.replace("classPath:", "");
				} else if (line.startsWith("pomPath")) {
					pomPath = line.replace("pomPath:", "");
				} else if (line.startsWith("pathLen")) {

				} else {
					//					System.out.println(line);
					String[] mthd_path = line.split("> ");
					if (mthd_path.length == 2) {
						mthds.add(mthd_path[0] + ">");
						jarPaths.add(mthd_path[1]);
					} else {
						mthds.add(mthd_path[0]);
						addCallPathValidator(mthd_path[0], new CallPathValidator(classPath, pomPath, distanceFilePath,
								mthds.toArray(new String[0]), jarPaths.toArray(new String[0])));
						mthds.clear();
						jarPaths.clear();
					}
				}
			}
			line = reader.readLine();
		}
		reader.close();
	}

	public void validatePaths(boolean exeEvo) {
		File modifyDir = new File(ShellConfig.modifyDirPath);
		if(!modifyDir.exists()) {
			modifyDir.mkdirs();
		}
		File testcaseDir = new File(ShellConfig.testcaseDirPath);
		if(!testcaseDir.exists()) {
			testcaseDir.mkdirs();
		}
		for (String mthd : mthdSeq) {
			LinkedList<CallPathValidator> validators = mthd2vlds.get(mthd);
			CallPathValidator validator = validators.poll();
			validator.validateCallPath(exeEvo);
		}
	}

	private void addCallPathValidator(String riskMthd, CallPathValidator validator) {
		if (mthd2vlds.size() < riskMthdNum4test || mthd2vlds.containsKey(riskMthd)) {
			LinkedList<CallPathValidator> validators = mthd2vlds.get(riskMthd);
			if (validators == null) {
				validators = new LinkedList<CallPathValidator>();
				mthd2vlds.put(riskMthd, validators);
			}
			if (validators.size() < exePath4riskMthd && validator.mthdInPath.length > 1) {
				mthdSeq.add(riskMthd);
				validators.add(validator);
			}
		}
	}

//	public static void main(String[] args) throws Exception {
//		new CallPathFile(
//				"D:\\ws_testcase\\distance_path_debug\\path\\com.alibaba+dubbo-rpc-thrift+2.6.2@org.apache.httpcomponents+httpcore@4.4.6.txt")
//						.validatePaths();
//	}

}
