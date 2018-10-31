package neu.lab.conflict;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.writer.StaWriter;

@Mojo(name = "sta", defaultPhase = LifecyclePhase.VALIDATE)
public class StaMojo extends ConflictMojo{

	@Override
	public void run() {
		new StaWriter().writeResult();
	}
	
}
