package neu.lab.conflict;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.util.UserConf;
import neu.lab.conflict.writer.UpVerWriter;

/**
 * 目前功能并不完整，只能检测2个jar，多余两个会停止运行
 * 需要修改addnodeadapter
 * @author wangchao
 *
 */
@Mojo(name = "upVersion", defaultPhase = LifecyclePhase.VALIDATE)
public class UpdateVersionMojo extends ConflictMojo{

	@Override
	public void run() {
		new UpVerWriter().write(UserConf.getOutDir4Mac()/*getOutDir()*/ + "versionUpdate.txt");
	}

}
