package neu.lab.evoshell.debug;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;

import neu.lab.evoshell.ExecUtil;

public class DebugEn {
	public static void main(String[] args) throws ExecuteException, IOException {
		String classPath = "D:\\ws_testcase\\modifyCp\\;"+
	"D:\\cEnvironment\\repository\\org\\ow2\\asm\\asm\\6.0\\asm-6.0.jar;D:\\cEnvironment\\repository\\org\\ow2\\asm\\asm-util\\6.0\\asm-util-6.0.jar;D:\\cEnvironment\\repository\\org\\ow2\\asm\\asm-tree\\6.0\\asm-tree-6.0.jar;D:\\cWS\\eclipse1\\evoshell\\target\\classes;D:\\cEnvironment\\repository\\junit\\junit\\4.12\\junit-4.12.jar;D:\\cEnvironment\\repository\\com\\fasterxml\\jackson\\datatype\\jackson-datatype-joda\\2.6.5\\jackson-datatype-joda-2.6.5.jar;D:\\cEnvironment\\repository\\org\\ff4j\\ff4j-core\\1.7.1\\ff4j-core-1.7.1.jar;D:\\cEnvironment\\repository\\javax\\validation\\validation-api\\1.1.0.Final\\validation-api-1.1.0.Final.jar;D:\\cEnvironment\\repository\\com\\google\\code\\findbugs\\annotations\\2.0.1\\annotations-2.0.1.jar;D:\\cEnvironment\\repository\\com\\fasterxml\\jackson\\core\\jackson-core\\2.6.4\\jackson-core-2.6.4.jar;D:\\cEnvironment\\repository\\org\\reflections\\reflections\\0.9.10\\reflections-0.9.10.jar;D:\\cEnvironment\\repository\\org\\javassist\\javassist\\3.18.1-GA\\javassist-3.18.1-GA.jar;D:\\cEnvironment\\repository\\com\\fasterxml\\jackson\\core\\jackson-databind\\2.6.4\\jackson-databind-2.6.4.jar;D:\\cEnvironment\\repository\\org\\slf4j\\slf4j-api\\1.7.7\\slf4j-api-1.7.7.jar;D:\\cEnvironment\\repository\\io\\swagger\\swagger-jaxrs\\1.5.4\\swagger-jaxrs-1.5.4.jar;D:\\ws\\github_snapshot\\ff4j-1.7.1\\ff4j-webapi\\target\\classes;D:\\cEnvironment\\repository\\io\\swagger\\swagger-annotations\\1.5.4\\swagger-annotations-1.5.4.jar;D:\\cEnvironment\\repository\\javax\\ws\\rs\\jsr311-api\\1.1.1\\jsr311-api-1.1.1.jar;D:\\cEnvironment\\repository\\io\\swagger\\swagger-models\\1.5.4\\swagger-models-1.5.4.jar;D:\\cEnvironment\\repository\\org\\ff4j\\ff4j-utils-json\\1.7.1\\ff4j-utils-json-1.7.1.jar;D:\\cEnvironment\\repository\\org\\yaml\\snakeyaml\\1.16\\snakeyaml-1.16.jar;D:\\cEnvironment\\repository\\com\\fasterxml\\jackson\\module\\jackson-module-jaxb-annotations\\2.6.5\\jackson-module-jaxb-annotations-2.6.5.jar;D:\\cEnvironment\\repository\\javax\\annotation\\jsr250-api\\1.0\\jsr250-api-1.0.jar;D:\\cEnvironment\\repository\\com\\fasterxml\\jackson\\dataformat\\jackson-dataformat-xml\\2.6.5\\jackson-dataformat-xml-2.6.5.jar;D:\\cEnvironment\\repository\\org\\codehaus\\woodstox\\stax2-api\\3.1.4\\stax2-api-3.1.4.jar;D:\\cEnvironment\\repository\\com\\google\\guava\\guava\\18.0\\guava-18.0.jar;D:\\cEnvironment\\repository\\com\\fasterxml\\jackson\\dataformat\\jackson-dataformat-yaml\\2.6.5\\jackson-dataformat-yaml-2.6.5.jar;D:\\cEnvironment\\repository\\org\\apache\\commons\\commons-lang3\\3.0\\commons-lang3-3.0.jar;D:\\cEnvironment\\repository\\com\\fasterxml\\jackson\\jaxrs\\jackson-jaxrs-base\\2.6.5\\jackson-jaxrs-base-2.6.5.jar;D:\\cEnvironment\\repository\\joda-time\\joda-time\\2.8.2\\joda-time-2.8.2.jar;D:\\cEnvironment\\repository\\org\\hamcrest\\hamcrest-core\\1.3\\hamcrest-core-1.3.jar;D:\\cEnvironment\\repository\\com\\fasterxml\\jackson\\core\\jackson-annotations\\2.6.4\\jackson-annotations-2.6.4.jar;D:\\cEnvironment\\repository\\com\\fasterxml\\jackson\\jaxrs\\jackson-jaxrs-json-provider\\2.6.5\\jackson-jaxrs-json-provider-2.6.5.jar;D:\\cEnvironment\\repository\\io\\swagger\\swagger-core\\1.5.4\\swagger-core-1.5.4.jar";
		String cmd = "java -cp "
				+ classPath+" "
				+ "neu.lab.evoshell.debug.LoadClassEn"
				+ "";
		System.out.println(cmd);

//		ExecUtil.exeCmd(cmd);
	}
	
}
