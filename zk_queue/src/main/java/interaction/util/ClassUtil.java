package interaction.util;

import org.I0Itec.zkclient.ExceptionUtil;

import java.lang.reflect.Field;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class ClassUtil {

    /**
     *
     * @param <T>
     * @param className
     * @param instanceOfClass
     * @return the class object for a given class name
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> forName(String className, Class<T> instanceOfClass) {
        try {
            Class<?> loadedClass = Class.forName(className);
            if (!instanceOfClass.isAssignableFrom(loadedClass)) {
                throw new IllegalStateException("Class " + className + " does not implement " + instanceOfClass.getName());
            }
            return (Class<T>) loadedClass;
        } catch (ClassNotFoundException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    /**
     * @param <T>
     * @param clazz
     * @return a new instance of the given class
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("could not instantiate class " + clazz.getName(), e);
        }
    }

    /**
     *
     * @param object
     * @param fieldName
     * @return the value of the (private) field of the given object with the given
     *         name
     */
    public static Object getPrivateFieldValue(Object object, String fieldName) {
        return getPrivateFieldValue(object.getClass(), object, fieldName);
    }

    /**
     * @param clazz
     * @param object
     * @param fieldName
     * @return the value of the (private) field of the given object declared in
     *         the given class with the given name
     */
    public static Object getPrivateFieldValue(Class<?> clazz, Object object, String fieldName) {
        Field field = null;
        do {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // proceed with superclass
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        try {
            if (field == null) {
                throw new NoSuchFieldException("no field '" + fieldName + "' in object " + object);
            }
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

}
