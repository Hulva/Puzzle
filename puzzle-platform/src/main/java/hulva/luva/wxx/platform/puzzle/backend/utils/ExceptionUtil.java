package hulva.luva.wxx.platform.puzzle.backend.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Hulva Luva.H
 * @since 2020-02-12
 *
 */
public class ExceptionUtil {

	public static String getFullException(Throwable ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return sw.toString();
	}
}
