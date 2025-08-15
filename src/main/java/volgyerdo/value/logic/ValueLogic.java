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

import volgyerdo.value.structure.Value;
import volgyerdo.value.structure.ValueType;

/**
 *
 * @author zsolt
 */
public class ValueLogic {

    /**
     * Megkeresi és visszaadja az összes ValueType annotációval ellátott osztályt 
     * a volgyerdo.value.logic.method csomagban és alkönyvtáraiban.
     */
    private static List<Class<?>> findAnnotatedClasses() {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        String packageName = "volgyerdo.value.logic.method";
        
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());
                
                if (directory.exists()) {
                    findClassesInDirectory(directory, packageName, annotatedClasses);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return annotatedClasses;
    }
    
    /**
     * Rekurzívan keresi az annotált osztályokat egy könyvtárban.
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
     * Létrehozza a Value objektumok listáját az annotált osztályokból.
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
     * Visszaadja az összes annotált osztály ValueType annotáció objektumait.
     * 
     * @return a ValueType annotációk listája
     */
    public static List<ValueType> getValueTypeAnnotations() {
        return values().stream()
                .map(value -> value.getClass().getAnnotation(ValueType.class))
                .filter(annotation -> annotation != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Szűrt lista a ValueType annotációkból kategória alapján.
     * 
     * @param category a keresett kategória (null esetén nem szűr)
     * @return a szűrt ValueType annotációk listája
     */
    public static List<ValueType> getValueTypeAnnotationsByCategory(String category) {
        return values().stream()
                .map(value -> value.getClass().getAnnotation(ValueType.class))
                .filter(annotation -> annotation != null)
                .filter(annotation -> category == null || category.equals(annotation.category()))
                .collect(Collectors.toList());
    }
    
    /**
     * Szűrt lista a ValueType annotációkból mozaikszó (acronym) alapján.
     * 
     * @param acronym a keresett mozaikszó (null esetén nem szűr)
     * @return a szűrt ValueType annotációk listája
     */
    public static List<ValueType> getValueTypeAnnotationsByAcronym(String acronym) {
        return values().stream()
                .map(value -> value.getClass().getAnnotation(ValueType.class))
                .filter(annotation -> annotation != null)
                .filter(annotation -> acronym == null || acronym.equals(annotation.acronym()))
                .collect(Collectors.toList());
    }
    
    /**
     * Szűrt lista a ValueType annotációkból osztálynév alapján.
     * 
     * @param className a keresett osztálynév (null esetén nem szűr)
     * @return a szűrt ValueType annotációk listája
     */
    public static List<ValueType> getValueTypeAnnotationsByClassName(String className) {
        return values().stream()
                .filter(value -> className == null || value.getClass().getSimpleName().equals(className))
                .map(value -> value.getClass().getAnnotation(ValueType.class))
                .filter(annotation -> annotation != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Kombinált szűrő metódus kategória, mozaikszó és osztálynév alapján.
     * 
     * @param category a keresett kategória (null esetén nem szűr)
     * @param acronym a keresett mozaikszó (null esetén nem szűr)
     * @param className a keresett osztálynév (null esetén nem szűr)
     * @return a szűrt ValueType annotációk listája
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
     * Visszaadja az összes Value objektumot a hozzájuk tartozó ValueType annotációval együtt.
     * 
     * @return ValueAnnotationPair objektumok listája
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
     * Segéd osztály Value objektum és ValueType annotáció párosításához.
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
