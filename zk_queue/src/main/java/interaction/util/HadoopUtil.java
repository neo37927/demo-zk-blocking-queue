package interaction.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class HadoopUtil {
    public static FileSystem getFileSystem(Path path) throws IOException {
        synchronized (FileSystem.class) {
            // had once a ConcurrentModificationException
            return FileSystem.get(path.toUri(), new Configuration());
        }
    }
}
