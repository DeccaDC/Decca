package neu.lab.evoshell.debug;

public class LoadClassEn {

	public static void main(String[] args) throws ClassNotFoundException {
		String class2load = "neu.lab.plug.testcase.asm.App";
		Class.forName(class2load);
		System.out.println("load " + class2load + "success");
	}
}
