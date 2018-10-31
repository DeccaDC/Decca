package neu.lab.conflict;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.util.UserConf;
import neu.lab.conflict.writer.VersionWriter;

@Mojo(name = "findVersion", defaultPhase = LifecyclePhase.VALIDATE)
public class FindVersionMojo extends ConflictMojo{

	@Override
	public void run() {
		new VersionWriter().write(UserConf.getOutDir() + "projectVersions.txt");
	}

}
