package neu.lab.evoshell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class FileUtil {
	public static void copyFile(String srcPath, String tgtPath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(srcPath));
			PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(tgtPath)));
			String line = reader.readLine();
			while (line != null) {
				printer.println(line);
				line = reader.readLine();
			}
			reader.close();
			printer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean moveFile(String srcFileName, String tgtDir) {

		File srcFile = new File(srcFileName);
		if (!srcFile.exists() || !srcFile.isFile())
			return false;

		File destDir = new File(tgtDir);
		if (!destDir.exists())
			destDir.mkdirs();

		return srcFile.renameTo(new File(tgtDir + File.separator + srcFile.getName()));
	}

//	public static void delFolder(String folderPath) {
//		try {
//			FileUtil.delAllFile(folderPath); 
//			String filePath = folderPath;
//			filePath = filePath.toString();
//			java.io.File myFilePath = new java.io.File(filePath);
//			myFilePath.delete(); 
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	
//	public static boolean delAllFile(String path) {
//		boolean flag = false;
//		File file = new File(path);
//		if (!file.exists()) {
//			return flag;
//		}
//		if (!file.isDirectory()) {
//			return flag;
//		}
//		String[] tempList = file.list();
//		File temp = null;
//		for (int i = 0; i < tempList.length; i++) {
//			if (path.endsWith(File.separator)) {
//				temp = new File(path + tempList[i]);
//			} else {
//				temp = new File(path + File.separator + tempList[i]);
//			}
//			if (temp.isFile()) {
//				temp.delete();
//			}
//			if (temp.isDirectory()) {
//				delAllFile(path + "/" + tempList[i]);
//				delFolder(path + "/" + tempList[i]);
//				flag = true;
//			}
//		}
//		return flag;
//	}
	
	/**
	 * @param path D:\ws_testcase\testcase\
	 * @param keepDir true-only delete files in directory.
	 */
	public static void delFolder(String path,boolean keepDir) {
		File file = new File(path);
		if(!file.exists())
			return;
		if(file.isFile()) {
			file.delete();
		}else {
			for(File children:file.listFiles()) {
				delFolder(children.getAbsolutePath(),false);
			}
			if(!keepDir) {
				file.delete();
			}
		}
	}
//	public static void main(String[] args) {
//		deleteFolder("D:\\ws_testcase\\testcase",true);
//	}
}
