import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class ProjectData {
	public static CountDownLatch latch;
	public static int maxThreadCount = 5;
	public static Semaphore semaphore = new Semaphore(ProjectData.maxThreadCount);

	public static String fileName = "../../chromium/media";
	public static String commitCountScript = "git log --oneline --since=\"6 months ago\" | wc -l";
	public static int commitCount;
	public static int topAuthorsCount;
	
	public static String getTopFilesScript(int fileCount){
		String script =  "git log --pretty=format: --name-only --since=\"6 months ago\" | sort | uniq -c | sort -rg | head -" + Integer.toString(fileCount);
		return script;
	}
}
