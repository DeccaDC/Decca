package neu.lab.conflict.writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.NodeAdapter;

public class VersionWriter {
	public void write(String outPath) {
		 String groupId = "";
		 String artifactId ="";
		try {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(outPath));
				groupId = reader.readLine();
				artifactId = reader.readLine();
				reader.close();
			} catch (Exception e) {
			}
			PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(new File(outPath), true)));
			for (NodeAdapter node : NodeAdapters.i().getAllNodeAdapter()) {
				if(groupId.equals(node.getGroupId())&&artifactId.equals(node.getArtifactId())) {
					printer.println("===============projectPath->" + MavenUtil.i().getProjectInfo());
					printer.println(node.getVersion()+":"+node.getWholePath());
				}
			}
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write versionupdate:", e);
		}
	}

}
