//----------------------------

int sleep = 2000;
int numberOfDumps = 30;
String folder = System.getProperty("java.io.tmpdir");

//----------------------------

import com.liferay.portal.kernel.util.ThreadUtil;
import java.nio.file.Files;
import java.nio.file.Paths;

long pid = getPID();
String sep = java.nio.file.FileSystems.getDefault().getSeparator();

Thread.start({
	for (int i = 1; i <= numberOfDumps; i++) {
		String threadDump = ThreadUtil.threadDump();

		String path = folder + sep  + "threaddump-" + pid + "-" + getTime() + "-" + i + ".txt";

		System.out.println("Threaddump " + i + ": " + path);

		Files.write(Paths.get(path), threadDump.getBytes());

		Thread.sleep(sleep);
	}
});

out.println("Generating threaddumps in a background thread");
out.println("Threaddumps will be generated in the folder: " + folder );

public static long getPID() {
	String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();

	return Long.parseLong(processName.split("@")[0]);
}

public static String getTime() {
	Calendar now = Calendar.getInstance();

	int hour= now.get(Calendar.HOUR_OF_DAY);
	int minute = now.get(Calendar.MINUTE);
	int second = now.get(Calendar.SECOND);

	return String.format("%02d%02d%02d", hour, minute, second);
}