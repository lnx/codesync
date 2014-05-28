package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.Gson;

public class FileChecker {

	public static final String FILE_FILTER = ".*\\.((c)|(cpp)|(css)|(h)|(html)|(java)|(js)|(py))";

	private final String workspace;

	private final String name;

	private final String propertiesIndexDir;

	private final Set<FileInfo> updatedFiles = new TreeSet<FileInfo>();

	public FileChecker(String workspace, String name) {
		this.workspace = workspace == null ? "" : workspace.trim();
		this.name = name == null ? "" : name.trim();
		this.propertiesIndexDir = this.workspace + "/.metadata/.plugins/org.eclipse.core.resources/.projects/" + this.name + "/.indexes";
		walk(propertiesIndexDir);
	}

	public void refresh() {
		walk(propertiesIndexDir);
	}

	public Set<FileInfo> getUpdatedFiles() {
		return updatedFiles;
	}

	public String toString() {
		return "{\"updatedFiles\":" + new Gson().toJson(getUpdatedFiles()) + "}";
	}

	private void walk(String path) {
		if (path != null) {
			File file = new File(path);
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					walk(f.getPath());
				}
			} else if (file.isFile()) {
				if (file.getName().equals("properties.index")) {
					parse(file);
				}
			}
		}
	}

	private void parse(File file) {
		String[] parts = read(file).trim().split("[ ]+");
		int i = 0;
		while (i < parts.length) {
			if (parts[i].matches(FILE_FILTER)) {
				String cmvcPath = parts[i++].trim().replaceAll("/+", "/").replaceFirst(".*?/", "/");
				String left = "";
				while (i < parts.length && !parts[i].matches(FILE_FILTER)) {
					left += " " + parts[i++];
				}
				if (left.contains("Created true")) {
					updatedFiles.add(new FileInfo(FileInfo.CREATED, workspace, name, cmvcPath));
				} else if (left.contains("Dirty true")) {
					updatedFiles.add(new FileInfo(FileInfo.CHANGED, workspace, name, cmvcPath));
				}
			} else {
				i++;
			}
		}
	}

	private String read(File file) {
		String ret = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			br.skip(7);
			StringBuilder sb = new StringBuilder();
			for (int ch = br.read(), prev = -1; ch != -1; ch = br.read()) {
				if (isPintableCharacters(ch)) {
					sb.append(Character.toString((char) ch));
				} else {
					if (isPintableCharacters(prev)) {
						sb.append(" ");
					}
				}
				prev = ch;
			}
			ret = sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	private boolean isPintableCharacters(int ch) {
		return ch >= 32 && ch <= 126;
	}

	public static void main(String[] args) {
		System.out.println(args.length >= 2 ? new FileChecker(args[0], args[1]) : new FileChecker("", ""));
	}

}
