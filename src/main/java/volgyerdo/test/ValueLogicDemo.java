package volgyerdo.test;

import volgyerdo.value.logic.ValueLogic;
import volgyerdo.value.structure.ValueType;
import java.util.List;

/**
 * Demonstráció a ValueLogic új funkcionalitásához.
 * 
 * @author Volgyerdo Nonprofit Kft.
 */
public class ValueLogicDemo {
    
    public static void main(String[] args) {
        
        System.out.println("=== Összes ValueType annotáció ===");
        List<ValueType> allAnnotations = ValueLogic.getValueTypeAnnotations();
        allAnnotations.forEach(annotation -> {
            System.out.printf("Név: %s, Kategória: %s, Mozaikszó: %s%n", 
                annotation.name(), annotation.category(), annotation.acronym());
        });
        
        System.out.println("\n=== Information kategóriájú algoritmusok ===");
        List<ValueType> infoAnnotations = ValueLogic.getValueTypeAnnotationsByCategory("information");
        infoAnnotations.forEach(annotation -> {
            System.out.printf("Név: %s, Mozaikszó: %s%n", 
                annotation.name(), annotation.acronym());
        });
        
        System.out.println("\n=== Assembly kategóriájú algoritmusok ===");
        List<ValueType> assemblyAnnotations = ValueLogic.getValueTypeAnnotationsByCategory("assembly");
        assemblyAnnotations.forEach(annotation -> {
            System.out.printf("Név: %s, Mozaikszó: %s%n", 
                annotation.name(), annotation.acronym());
        });
        
        System.out.println("\n=== Entropy kategóriájú algoritmusok ===");
        List<ValueType> entropyAnnotations = ValueLogic.getValueTypeAnnotationsByCategory("entropy");
        entropyAnnotations.forEach(annotation -> {
            System.out.printf("Név: %s, Mozaikszó: %s%n", 
                annotation.name(), annotation.acronym());
        });
        
        System.out.println("\n=== GZIP algoritmus keresése mozaikszó alapján ===");
        List<ValueType> gzipAnnotations = ValueLogic.getValueTypeAnnotationsByAcronym("IGZIP");
        gzipAnnotations.forEach(annotation -> {
            System.out.printf("Név: %s, Leírás: %s%n", 
                annotation.name(), annotation.description());
            System.out.printf("Pszeudokód: %s%n", annotation.pseudo());
        });
        
        System.out.println("\n=== Value-Annotation párok ===");
        List<ValueLogic.ValueAnnotationPair> pairs = ValueLogic.getValueAnnotationPairs();
        pairs.stream().limit(5).forEach(pair -> {
            System.out.printf("Osztály: %s, Annotáció név: %s%n", 
                pair.getClassName(), 
                pair.getAnnotation() != null ? pair.getAnnotation().name() : "nincs annotáció");
        });
        
        System.out.println("\n=== Kombinált szűrés: information kategória és ISH mozaikszó ===");
        List<ValueType> filteredAnnotations = ValueLogic.getValueTypeAnnotationsFiltered("information", "ISH", null);
        filteredAnnotations.forEach(annotation -> {
            System.out.printf("Név: %s, Kategória: %s, Mozaikszó: %s%n", 
                annotation.name(), annotation.category(), annotation.acronym());
        });
    }
}
