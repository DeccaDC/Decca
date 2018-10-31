package neu.lab.conflict.util;

public class MathUtil {
	public static double getQuotient(int dividend, int divisor) {
		if (divisor == 0)
			return -2;
		return ((double) dividend) / divisor;
	}
}
