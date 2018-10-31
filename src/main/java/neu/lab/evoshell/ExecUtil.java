package neu.lab.evoshell;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

public class ExecUtil {

	public static void exeCmd(String mvnCmd) throws ExecuteException, IOException {
		exeCmd(mvnCmd, 0, null);
	}

	/**
	 * @param mvnCmd
	 * @param timeout 0 for no limit.
	 * @throws ExecuteException null for 
	 * @throws IOException
	 */
	public static void exeCmd(String mvnCmd, long timeout, String logPath) throws ExecuteException, IOException {
		System.out.println("----execute cmd:" + mvnCmd);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("start time：" + sdf.format(new Date()));

		CommandLine cmdLine = CommandLine.parse(mvnCmd);
		DefaultExecutor executor = new DefaultExecutor();
		if (timeout != 0) {
			ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
			executor.setWatchdog(watchdog);
		}
		if (logPath != null) {
			executor.setStreamHandler(new PumpStreamHandler(new FileOutputStream(logPath)));
		}
		executor.execute(cmdLine);
	}

	//	public static void exeCmd(String mvnCmd,String logPath) throws ExecuteException, IOException {
	//		System.out.println("----execute cmd:" + mvnCmd);
	//		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//		 System.out.println("start time：" + sdf.format(new Date()));
	//		 CommandLine cmdLine = CommandLine.parse(mvnCmd);
	//		 DefaultExecutor executor = new DefaultExecutor();
	//		 if(logPath!=null) {
	//			 executor.setStreamHandler(new PumpStreamHandler(new FileOutputStream(logPath)));
	//		 }
	//		 executor.execute(cmdLine);
	//	}

}
