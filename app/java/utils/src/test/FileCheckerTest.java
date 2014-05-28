package test;

import java.util.Set;

import org.joda.time.DateTime;

import file.FileChecker;
import file.FileInfo;

public class FileCheckerTest {

	public static void main(String[] args) {
		long start = DateTime.now().getMillis();
		FileChecker fileChecker = new FileChecker("C:/Users/IBM_ADMIN/Workspace/webadmin", "webadmin72");
		Set<FileInfo> updatedFiles = fileChecker.getUpdatedFiles();
		for (FileInfo fileInfo : updatedFiles) {
			System.out.println(fileInfo);
		}
		System.out.println("Time:" + (DateTime.now().getMillis() - start) + "ms   Total:" + updatedFiles.size());
	}

}
