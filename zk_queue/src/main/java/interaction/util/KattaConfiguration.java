package interaction.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;
import java.util.Set;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class KattaConfiguration implements Serializable {

    private final static Logger LOG = Logger.getLogger(KattaConfiguration.class);
    private static final long serialVersionUID = 1L;
    protected Properties _properties;
    private final String _resourcePath;

    public KattaConfiguration(final String path) {
        _properties = PropertyUtil.loadProperties(path);
        _resourcePath = PropertyUtil.getPropertiesFilePath(path);
    }

    public KattaConfiguration(File file) {
        _properties = PropertyUtil.loadProperties(file);
        _resourcePath = file.getAbsolutePath();
    }

    public KattaConfiguration(Properties properties, String filePath) {
        _properties = properties;
        _resourcePath = filePath;
    }

    public KattaConfiguration() {
        _properties = new Properties();
        _resourcePath = null;
    }

    public String getResourcePath() {
        return _resourcePath;
    }

    public boolean containsProperty(final String key) {
        return _properties.containsKey(key);
    }

    public String getProperty(final String key) {
        final String value = _properties.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("no property with key '" + key + "' found");
        }
        return value;
    }

    public String getProperty(final String key, final String defaultValue) {
        String value = _properties.getProperty(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    public void setProperty(String key, String value) {
        _properties.setProperty(key, value);
    }

    protected void setProperty(String key, long value) {
        _properties.setProperty(key, Long.toString(value));
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(key, Boolean.toString(defaultValue)));
    }

    public int getInt(final String key) {
        return Integer.parseInt(getProperty(key));
    }

    public int getInt(final String key, final int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (NumberFormatException e) {
            LOG.warn("failed to parse value '" + getProperty(key) + "' for key '" + key + "' - returning default value '"
                    + defaultValue + "'");
            return defaultValue;
        } catch (IllegalStateException e) {
            return defaultValue;
        }
    }

    public float getFloat(final String key, final float defaultValue) {
        try {
            return Float.parseFloat(getProperty(key));
        } catch (NumberFormatException e) {
            LOG.warn("failed to parse value '" + getProperty(key) + "' for key '" + key + "' - returning default value '"
                    + defaultValue + "'");
            return defaultValue;
        } catch (IllegalStateException e) {
            return defaultValue;
        }
    }

    public File getFile(final String key) {
        return new File(getProperty(key));
    }

    public Class<?> getClass(final String key) {
        final String className = getProperty(key);
        return ClassUtil.forName(className, Object.class);
    }

    public Class<?> getClass(final String key, Class<?> defaultValue) {
        final String className = getProperty(key, defaultValue.getName());
        return ClassUtil.forName(className, Object.class);
    }

    public Set<String> getKeys() {
        return _properties.stringPropertyNames();
    }

    public Properties getPropertiesCopy() {
        return new Properties(_properties);
    }
}
