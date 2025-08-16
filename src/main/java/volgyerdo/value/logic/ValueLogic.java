/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.value.logic;

import java.util.ArrayList;
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
import volgyerdo.value.structure.ValueType;

/**
 *
 * @author zsolt
 */
public class ValueLogic {

    /**
     * Finds and returns all classes annotated with ValueType 
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
                    if (clazz.isAnnotationPresent(ValueType.class) &&
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
                        if (clazz.isAnnotationPresent(ValueType.class) && 
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
     * Returns all ValueType annotation objects from annotated classes.
     * 
     * @return list of ValueType annotations
     */
    public static List<ValueType> getValueTypeAnnotations() {
        return values().stream()
                .map(value -> value.getClass().getAnnotation(ValueType.class))
                .filter(annotation -> annotation != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Filtered list of ValueType annotations by category.
     * 
     * @param category the searched category (null means no filter)
     * @return list of filtered ValueType annotations
     */
    public static List<ValueType> getValueTypeAnnotationsByCategory(String category) {
        return values().stream()
                .map(value -> value.getClass().getAnnotation(ValueType.class))
                .filter(annotation -> annotation != null)
                .filter(annotation -> category == null || category.equals(annotation.category()))
                .collect(Collectors.toList());
    }
    
    /**
     * Filtered list of ValueType annotations by acronym.
     * 
     * @param acronym the searched acronym (null means no filter)
     * @return list of filtered ValueType annotations
     */
    public static List<ValueType> getValueTypeAnnotationsByAcronym(String acronym) {
        return values().stream()
                .map(value -> value.getClass().getAnnotation(ValueType.class))
                .filter(annotation -> annotation != null)
                .filter(annotation -> acronym == null || acronym.equals(annotation.acronym()))
                .collect(Collectors.toList());
    }
    
    /**
     * Filtered list of ValueType annotations by class name.
     * 
     * @param className the searched class name (null means no filter)
     * @return list of filtered ValueType annotations
     */
    public static List<ValueType> getValueTypeAnnotationsByClassName(String className) {
        return values().stream()
                .filter(value -> className == null || value.getClass().getSimpleName().equals(className))
                .map(value -> value.getClass().getAnnotation(ValueType.class))
                .filter(annotation -> annotation != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Combined filter method by category, acronym and class name.
     * 
     * @param category the searched category (null means no filter)
     * @param acronym the searched acronym (null means no filter)
     * @param className the searched class name (null means no filter)
     * @return list of filtered ValueType annotations
     */
    public static List<ValueType> getValueTypeAnnotationsFiltered(String category, String acronym, String className) {
        return values().stream()
                .filter(value -> className == null || value.getClass().getSimpleName().equals(className))
                .map(value -> value.getClass().getAnnotation(ValueType.class))
                .filter(annotation -> annotation != null)
                .filter(annotation -> category == null || category.equals(annotation.category()))
                .filter(annotation -> acronym == null || acronym.equals(annotation.acronym()))
                .collect(Collectors.toList());
    }
    
    /**
     * Returns all Value objects with their corresponding ValueType annotations.
     * 
     * @return list of ValueAnnotationPair objects
     */
    public static List<ValueAnnotationPair> getValueAnnotationPairs() {
        return values().stream()
                .map(value -> {
                    ValueType annotation = value.getClass().getAnnotation(ValueType.class);
                    return new ValueAnnotationPair(value, annotation);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Helper class for pairing Value objects with ValueType annotations.
     */
    public static class ValueAnnotationPair {
        private final Value value;
        private final ValueType annotation;
        
        public ValueAnnotationPair(Value value, ValueType annotation) {
            this.value = value;
            this.annotation = annotation;
        }
        
        public Value getValue() {
            return value;
        }
        
        public ValueType getAnnotation() {
            return annotation;
        }
        
        public String getClassName() {
            return value.getClass().getSimpleName();
        }
    }
}
