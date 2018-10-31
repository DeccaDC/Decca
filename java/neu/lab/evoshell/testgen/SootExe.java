package neu.lab.evoshell.testgen;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import soot.PackManager;
import soot.Transform;


public class SootExe {
	public void initProjectInfo(String[] jarFilePath) {

		List<String> args = getArgs(jarFilePath);
		if (args.size() == 0) {
			
		} else {
			SootTf transformer = new SootTf();
			PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));
			
			soot.Main.main(args.toArray(new String[0]));
			soot.G.reset();

		}
	}
	protected List<String> getArgs(String[] jarFilePaths) {
		List<String> argsList = new ArrayList<String>();
		addClassPath(argsList, jarFilePaths);
		if(argsList.size()==0) {//this class can't analysis 
			return argsList;
		}
		
		addGenArg(argsList);
		addCgArgs(argsList);
		addIgrArgs(argsList);

		return argsList;
	}

	protected void addCgArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "cg", "off", }));
	}

	protected void addClassPath(List<String> argsList, String[] jarFilePaths) {
		for (String jarFilePath : jarFilePaths) {
			if (new File(jarFilePath).exists()) {
				if (canAna(jarFilePath)) {
					argsList.add("-process-dir");
					argsList.add(jarFilePath);
				}else {
					System.out.println("add classpath error:can't analysis file " + jarFilePath);
				}
			} else {
				System.out.println("add classpath error:doesn't exist file " + jarFilePath);
			}

		}
	}

	private boolean canAna(String jarFilePath) {
//		return true;
		if(jarFilePath.contains("\\asm\\")&&jarFilePath.contains("6")) {
			return false;
		}
		return true;
	}

	protected void addGenArg(List<String> argsList) {

		argsList.add("-ire");
		argsList.add("-app");
		argsList.add("-allow-phantom-refs");
		argsList.add("-w");

	}

	protected void addIgrArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "wjop", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "wjap", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "jtp", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "jop", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "jap", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "bb", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "tag", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-f", "n", }));
	}
}
