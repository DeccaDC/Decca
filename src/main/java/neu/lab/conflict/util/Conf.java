package neu.lab.conflict.util;

public class Conf {
	public static final boolean CLASS_DUP = false;
	public static final boolean FLT_INTERFACE = false;

	public static final boolean FLT_CALL = false;// should filter method call before form graph
	public static final boolean FLT_OBJ = false;
	public static final boolean FLT_SET = false;

	public static final boolean FLT_DANGER_IMPL = false;
	public static final int DANGER_IMPL_T = 2;

	public static final boolean FLT_NODE = false;// filter node before find path

	public static final boolean PRINT_CONFUSED_METHOD = true;
//	public static final int MIN_PATH_DEP = 3;
//	public static final int MAX_PATH_DEP = 8;
	
	//TODO path depth
	public static int DOG_DEP_FOR_DIS ;//final path may be larger than PATH_DEP when child book is existed.
	public static int DOG_DEP_FOR_PATH ;//final path may be larger than PATH_DEP when child book is existed.
	public static String callConflict;
	public static boolean findAllpath;
	
	
	public static boolean ONLY_GET_SIMPLE = false;
	
	public static boolean DEL_LONGTIME = true;
	
	public static boolean DEL_OPTIONAL = false;
	
	public static boolean PRINT_JAR_DUP_RISK = true;
	
	public static boolean ANA_FROM_HOST = true;
	
	public static boolean CNT_RISK_CLASS_METHOD = true;//if methods that are in risk-class is risk-method.
	
//	public static final String outSir = "D:\\ws\\sta\\";
}

//public class Conf {
//	public static final boolean CLASS_DUP = false;
//	public static final boolean FLT_INTERFACE = false;
//
//	public static final boolean FLT_CALL = false;// should filter method call before form graph
//	public static final boolean FLT_OBJ = false;
//	public static final boolean FLT_SET = false;
//
//	public static final boolean FLT_DANGER_IMPL = false;
//	public static final int DANGER_IMPL_T = 4;
//
//	public static final boolean FLT_NODE = true;// filter node before find path
//
//	public static final boolean PRINT_CONFUSED_METHOD = true;
//	public static final int MIN_PATH_DEP = 2;
//	public static final int PATH_DEP = 5;
//}