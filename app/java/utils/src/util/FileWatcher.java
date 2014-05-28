package util;

import java.io.File;
import java.util.SortedMap;
import java.util.TreeMap;

public class FileWatcher {

    public static SortedMap<Long, String> sortFileByLastModification(String path) {
        SortedMap<Long, String> ret = new TreeMap<Long, String>();
        if (path != null) {
            File currentFile = new File(path);
            if (currentFile.isDirectory()) {
                File[] files = currentFile.listFiles();
                for (File file : files) {
                    ret.putAll(sortFileByLastModification(file.getPath()));
                }
            } else if (currentFile.isFile()) {
                ret.put(currentFile.lastModified(), currentFile.getPath());
            }
        }
        return ret;
    }

    public static void main(String[] args) {
        String sourceCodePath = "C:/Users/IBM_ADMIN/Workspace/WebAdmin/WebAdmin72/java.pgm/zui.dgo/com/ibm/as400/httpsvr";
        SortedMap<Long, String> sourceCodeMap = sortFileByLastModification(sourceCodePath);
        System.out.println(sourceCodeMap.lastKey() + " " + sourceCodeMap.get(sourceCodeMap.lastKey()));

        String metadataPath = "C:/Users/IBM_ADMIN/Workspace/WebAdmin/.metadata";
        SortedMap<Long, String> metadataMap = sortFileByLastModification(metadataPath).tailMap(sourceCodeMap.lastKey());
        for (Long lastModification : metadataMap.keySet()) {
            System.out.println(lastModification + " " + metadataMap.get(lastModification));
        }
    }

}
