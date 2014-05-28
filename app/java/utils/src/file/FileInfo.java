package file;

import java.io.File;

import org.joda.time.DateTime;

public class FileInfo implements Comparable<FileInfo> {

    public static final String THESAME = "same";

    public static final String CHANGED = "changed";

    public static final String CREATED = "created";

    private String status;

    private final String workspace;

    private final String project;

    private final String cmvcPath;

    private final String lastUpdate;

    public FileInfo(String status, String workspace, String project, String cmvcPath) {
        this.status = status == null ? THESAME : status;
        this.workspace = workspace == null ? "" : workspace;
        this.project = project == null ? "" : project;
        this.cmvcPath = cmvcPath == null ? "" : cmvcPath;
        File file = new File(workspace + "/" + project + cmvcPath);
        if (file.exists()) {
            lastUpdate = new DateTime(file.lastModified()).toString("yyyy-MM-dd hh:mm:ss");
        } else {
            lastUpdate = "uncertain";
        }
    }

    public String getStatus() {
        return status;
    }

    public String getWorkspace() {
        return workspace;
    }

    public String getProject() {
        return project;
    }

    public String getCmvcPath() {
        return cmvcPath;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public int hashCode() {
        return cmvcPath.hashCode();
    }

    public int compareTo(FileInfo fileInfo) {
        return cmvcPath.compareTo(fileInfo.getCmvcPath());
    }

    public String toString() {
        return lastUpdate + " " + status + " " + cmvcPath;
    }

}
