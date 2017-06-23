package interaction.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class PropertyUtil {

    public static Properties loadProperties(String path) {
        InputStream inputStream = PropertyUtil.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException(path + " not in classpath");
        }
        final Properties properties = new Properties();
        try {
            properties.load(inputStream);
            return properties;
        } catch (final IOException e) {
            throw new RuntimeException("unable to load kata.properties", e);
        }
    }

    public static String getPropertiesFilePath(final String path) {
        URL resource = PropertyUtil.class.getResource(path);
        if (resource == null) {
            throw new RuntimeException(path + " not in classpath");
        }
        return resource.toString();
    }

    public static Properties loadProperties(final File file) {
        final Properties properties = new Properties();
        try {
            FileInputStream inStream = new FileInputStream(file);
            properties.load(inStream);
            return properties;
        } catch (final IOException e) {
            throw new RuntimeException("unable to load kata.properties", e);
        }
    }

}