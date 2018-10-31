package neu.lab.evoshell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author asus synchronize with file
 */
public class FileSyn {
	private List<String> lines;
	private PrintWriter printer;

	public FileSyn(String stateDir,String fileName) throws IOException {
		File dir = new File(stateDir);
		if (!dir.exists())
			dir.mkdirs();
		lines = readFile(stateDir + fileName);
		printer = new PrintWriter(new BufferedWriter(new FileWriter(stateDir + fileName, true)));
	}
	
	public FileSyn(String filePath) throws IOException {
		File dir = new File(filePath).getParentFile();
		if (!dir.exists())
			dir.mkdirs();
		lines = readFile(filePath);
		printer = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));
	}

	public void add(String line) {
		if (!lines.contains(line)) {
			lines.add(line);
		}
		printer.println(line);
		printer.flush();
	}

	public void closeOut() {
		printer.close();
	}

	public boolean contains(String projectName) {
		return lines.contains(projectName);
	}
	
	public int recordNum() {
		return lines.size();
	}

	private List<String> readFile(String filePath) {
		List<String> fileList = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			while (line != null) {
				if(!"".equals(line)) {
					if (!fileList.contains(line)) {
						fileList.add(line);
					}
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
		}
		return fileList;
	}
}
