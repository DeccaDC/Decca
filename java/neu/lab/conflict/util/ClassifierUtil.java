package neu.lab.conflict.util;

public class ClassifierUtil {
	
	/**null classifier in tree may be null or empty-string,this method transforms null classifier to empty-string
	 * @param classifier
	 * @return
	 */
	public static String transformClf(String classifier) {
		if(null ==classifier)
			return "";
		return classifier;
	}
}
