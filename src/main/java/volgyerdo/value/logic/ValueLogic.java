/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.value.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.reflect.Modifier;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.JarURLConnection;

import volgyerdo.value.structure.Value;
import volgyerdo.value.structure.BaseValue;

/**
 *
 * @author zsolt
 */
public class ValueLogic {

    /**
     * Finds and returns all classes annotated with BaseValue 
     * in the volgyerdo.value.logic.method package and its subdirectories.
     */
    private static List<Class<?>> findAnnotatedClasses() {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        String packageName = "volgyerdo.value.logic.method";
        String path = packageName.replace('.', '/');
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();
                if ("file".equals(protocol)) {
                    File directory = new File(resource.toURI());
                    if (directory.exists()) {
                        findClassesInDirectory(directory, packageName, annotatedClasses);
                    }
                } else if ("jar".equals(protocol)) {
                    try {
                        JarURLConnection jarConn = (JarURLConnection) resource.openConnection();
                        try (JarFile jarFile = jarConn.getJarFile()) {
                            scanJar(jarFile, path, packageName, annotatedClasses);
                        }
                    } catch (ClassCastException e) {
                        // Fallback: parse jar path manually
                        String file = resource.getFile(); // e.g. file:/path/values-1.0-SNAPSHOT.jar!/volgyerdo/value/logic/method
                        int sep = file.indexOf(".jar!");
                        if (sep != -1) {
                            String jarPath = file.substring(0, sep + 4);
                            if (jarPath.startsWith("file:")) {
                                jarPath = jarPath.substring(5);
                            }
                            jarPath = jarPath.replace("%20", " ");
                            try (JarFile jar = new JarFile(jarPath)) {
                                scanJar(jar, path, packageName, annotatedClasses);
                            } catch (Exception ignore) { }
                        }
                    }
                } else {
                    // Unknown protocol, attempt manual jar parsing if contains .jar!
                    String file = resource.toString();
                    int sep = file.indexOf(".jar!");
                    if (sep != -1) {
                        int start = file.indexOf("file:");
                        if (start != -1) {
                            String jarPath = file.substring(start + 5, sep + 4).replace("%20", " ");
                            try (JarFile jar = new JarFile(jarPath)) {
                                scanJar(jar, path, packageName, annotatedClasses);
                            } catch (Exception ignore) { }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return annotatedClasses;
    }

    private static void scanJar(JarFile jarFile, String path, String packageName, List<Class<?>> annotatedClasses) {
        jarFile.stream()
            .filter(e -> !e.isDirectory())
            .map(JarEntry::getName)
            .filter(name -> name.startsWith(path) && name.endsWith(".class"))
            .forEach(name -> {
                String className = name.replace('/', '.').substring(0, name.length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(BaseValue.class) &&
                            Value.class.isAssignableFrom(clazz) &&
                            !Modifier.isAbstract(clazz.getModifiers()) &&
                            !clazz.isInterface()) {
                        annotatedClasses.add(clazz);
                    }
                } catch (Throwable ex) {
                    // ignore
                }
            });
    }
    
    /**
     * Recursively searches for annotated classes in a directory.
     */
    private static void findClassesInDirectory(File directory, String packageName, List<Class<?>> annotatedClasses) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClassesInDirectory(file, packageName + "." + file.getName(), annotatedClasses);
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(BaseValue.class) && 
                            Value.class.isAssignableFrom(clazz) &&
                            !Modifier.isAbstract(clazz.getModifiers()) &&
                            !clazz.isInterface()) {
                            annotatedClasses.add(clazz);
                        }
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        // Ignore classes that can't be loaded
                    }
                }
            }
        }
    }
    
    /**
     * Creates a list of Value objects from annotated classes.
     */
    public static List<Value> values() {
        List<Value> values = new ArrayList<>();
        List<Class<?>> annotatedClasses = findAnnotatedClasses();
        
        for (Class<?> clazz : annotatedClasses) {
            try {
                Value instance = (Value) clazz.getDeclaredConstructor().newInstance();
                values.add(instance);
            } catch (Exception e) {
                // Ignore classes that can't be instantiated
            }
        }
        
        values.sort((o1, o2) -> o1.name().compareTo(o2.name()));
        return values;
    }
    
    /**
     * Returns all BaseValue annotation objects from annotated classes.
     * 
     * @return list of BaseValue annotations
     */
    public static List<BaseValue> getBaseValueAnnotations() {
        return values().stream()
                .map(value -> value.getClass().getAnnotation(BaseValue.class))
                .filter(annotation -> annotation != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Filtered list of BaseValue annotations by category.
     * 
     * @param category the searched category (null means no filter)
     * @return list of filtered BaseValue annotations
     */
    public static List<BaseValue> getBaseValueAnnotationsByCategory(String category) {
        return values().stream()
                .map(value -> value.getClass().getAnnotation(BaseValue.class))
                .filter(annotation -> annotation != null)
                .filter(annotation -> category == null || category.equals(annotation.category()))
                .collect(Collectors.toList());
    }
    
    /**
     * Filtered list of BaseValue annotations by acronym.
     * 
     * @param acronym the searched acronym (null means no filter)
     * @return list of filtered BaseValue annotations
     */
    public static List<BaseValue> getBaseValueAnnotationsByAcronym(String acronym) {
        return values().stream()
                .map(value -> value.getClass().getAnnotation(BaseValue.class))
                .filter(annotation -> annotation != null)
                .filter(annotation -> acronym == null || acronym.equals(annotation.acronym()))
                .collect(Collectors.toList());
    }
    
    /**
     * Filtered list of BaseValue annotations by class name.
     * 
     * @param className the searched class name (null means no filter)
     * @return list of filtered BaseValue annotations
     */
    public static List<BaseValue> getBaseValueAnnotationsByClassName(String className) {
        return values().stream()
                .filter(value -> className == null || value.getClass().getSimpleName().equals(className))
                .map(value -> value.getClass().getAnnotation(BaseValue.class))
                .filter(annotation -> annotation != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Combined filter method by category, acronym and class name.
     * 
     * @param category the searched category (null means no filter)
     * @param acronym the searched acronym (null means no filter)
     * @param className the searched class name (null means no filter)
     * @return list of filtered BaseValue annotations
     */
    public static List<BaseValue> getBaseValueAnnotationsFiltered(String category, String acronym, String className) {
        return values().stream()
                .filter(value -> className == null || value.getClass().getSimpleName().equals(className))
                .map(value -> value.getClass().getAnnotation(BaseValue.class))
                .filter(annotation -> annotation != null)
                .filter(annotation -> category == null || category.equals(annotation.category()))
                .filter(annotation -> acronym == null || acronym.equals(annotation.acronym()))
                .collect(Collectors.toList());
    }
    
    /**
     * Returns all Value objects with their corresponding BaseValue annotations.
     * 
     * @return list of ValueAnnotationPair objects
     */
    public static List<ValueAnnotationPair> getValueAnnotationPairs() {
        return values().stream()
                .map(value -> {
                    BaseValue annotation = value.getClass().getAnnotation(BaseValue.class);
                    return new ValueAnnotationPair(value, annotation);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Calculates a value using the specified BaseValue ID and input object.
     * Finds the Value implementation by BaseValue.id(), creates an instance, 
     * and calls the appropriate value() method based on the object type.
     * 
     * @param valueId the BaseValue.id() to find the corresponding Value implementation
     * @param object the input object to calculate the value for
     * @return the calculated double value
     * @throws IllegalArgumentException if no Value implementation found for the given ID
     * @throws RuntimeException if Value instantiation or calculation fails
     */
    public static double calculateValueById(long valueId, Object object) {
        if (object == null) {
            return 0.0;
        }
        
        // Find the Value class by BaseValue.id()
        Value valueInstance = null;
        for (Value value : values()) {
            BaseValue annotation = value.getClass().getAnnotation(BaseValue.class);
            if (annotation != null && annotation.id() == valueId) {
                valueInstance = value;
                break;
            }
        }
        
        if (valueInstance == null) {
            throw new IllegalArgumentException("No Value implementation found for BaseValue.id() = " + valueId);
        }
        
        try {
            // Call the appropriate value() method based on object type
            if (object instanceof byte[]) {
                return valueInstance.value((byte[]) object);
            } else if (object instanceof Collection<?>) {
                return valueInstance.value((Collection<?>) object);
            } else if (object instanceof String) {
                return valueInstance.value((String) object);
            } else if (object instanceof boolean[]) {
                return valueInstance.value((boolean[]) object);
            } else if (object instanceof short[]) {
                return valueInstance.value((short[]) object);
            } else if (object instanceof int[]) {
                return valueInstance.value((int[]) object);
            } else if (object instanceof float[]) {
                return valueInstance.value((float[]) object);
            } else if (object instanceof double[]) {
                return valueInstance.value((double[]) object);
            } else if (object instanceof char[]) {
                return valueInstance.value((char[]) object);
            } else if (object instanceof Character[]) {
                return valueInstance.value((Character[]) object);
            } else if (object instanceof Byte[]) {
                return valueInstance.value((Byte[]) object);
            } else if (object instanceof Short[]) {
                return valueInstance.value((Short[]) object);
            } else if (object instanceof Integer[]) {
                return valueInstance.value((Integer[]) object);
            } else if (object instanceof Long[]) {
                return valueInstance.value((Long[]) object);
            } else if (object instanceof String[]) {
                return valueInstance.value((String[]) object);
            } else {
                // Default: use the generic Object method which converts to string
                return valueInstance.value(object);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate value for BaseValue.id() = " + valueId + 
                                     " with object type " + object.getClass().getSimpleName(), e);
        }
    }

    /**
     * Helper class for pairing Value objects with BaseValue annotations.
     */
    public static class ValueAnnotationPair {
        private final Value value;
        private final BaseValue annotation;
        
        public ValueAnnotationPair(Value value, BaseValue annotation) {
            this.value = value;
            this.annotation = annotation;
        }
        
        public Value getValue() {
            return value;
        }
        
        public BaseValue getAnnotation() {
            return annotation;
        }
        
        public String getClassName() {
            return value.getClass().getSimpleName();
        }
    }
}
