/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.test;

import java.util.ArrayList;
import java.util.List;
import volgyerdo.value.method.AssemblyInfo;
import volgyerdo.value.method.CompressionInfo;
import volgyerdo.value.method.SSM1Info;
import volgyerdo.value.structure.Value;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class InformationTest {

    private static final Value ASSEMBLY = new AssemblyInfo();
    private static final Value SSM = new SSM1Info();
    private static final Value COMPRESSION = new CompressionInfo();
    
    private static final List<Double> ASSEMBLY_R = new ArrayList<>();
    private static final List<Double> SSM_R = new ArrayList<>();
    private static final List<Double> COMPRESSION_R = new ArrayList<>();

    public static void main(String[] args) {
        String s;

        test("ATGCCCAAGTTAGAAAATGCTCCCCCCCCCTGA");

        test("Hello, World!");

        test("hello world");

        test("123123123123");

        test("aaaa");

        test("vdsf4gfdbőhfdtzurődbhsdh678nméáüő20ac,áűüöoghgt4328öójgbewfdsa");

        test("ababababababababababababababababababababababababababababababab");

        test("11111111111111111111111000000000000000000000000000000000000000");

        test("znTEcarbCXUll8O7vQwiTP5kdGM6bYDWmQK7ze40i9cbglnI6KIgsOM4Bndmsp");
        
        test("1101101111100100111000011000011001011100000100110000111000111001000110110110110010001111011010000011000001001010111010101010100101101111011010010100000011110010011010111111110011110101111000010110001000100011000100100101101111101110001001111011000111010000010011010101111110010101110000011100110010110111011010001010010000010111001000100111000111011100011110111111110101101111101110110001110110010000100000010010010110100001000101000101111111010101001010101000101010100101001011010001010000111101010010001010110101100100011101100110111000000111100011110101110011110110000010101010010000000111001010110101110001011010010100101001011000010111011000001101010100011101111011100010010110000101100010100000001111001111100100001100111100000000101011111010101101100011011000010001001001100001010101110100001000101110010011001011100010001101110000111111011100101011110100001000111000110100000111100011110011100111000000111001100110110010000101000001110100001101111000000111110011010111011001101111110000110000");

        test("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        test("Répa, retek mogyoró, korán reggel ritkán rikkant a rigó!");

        test("""
             In the near-equilibrium regime, with the input of B maintained at a reduced level, the
             homogeneous steady state condition is stable. Equations i) and iv) predominate such that A \u2192
             X \u2192 E appears as the primary global reaction. At low concentrations of B the overall reaction B
             \u2192 D remains in the near equilibrium regime, hence the autocatalytic steps, equations ii) and iii),
             remain insignificant. Any tendency for inhomogeneities to develop due to steps ii) and iii) is
             damped by the random motion of the molecules, appearing as a dissipative process at the
             macromolecular level.
             However, as the concentration of B is increased, that is, as the reaction B \u2192 D moves far
             from equilibrium, a threshold is reached where equations ii) and iii) become significant. The
             homogeneous steady state maintained by random motion becomes superseded, wherein, the
             system begins to exhibit coherent temporal ordering, i.e., concentrations of X and Y at the
             microlevel coherently oscillate periodically with respect to each other. Due to molecular
             diffusion, this temporal ordering of chemical composition becomes manifest as spatial ordering in
             the reaction volume, wherein shells of alternating X, Y concentration expand outward from the
             location of the initial instability, invading the surrounding homogeneous medium with a
             spherically symmetric periodic structure. These shells may themselves be static or propagating.*
             The oscillating reaction system has at the microlevel two degrees of freedom: a state of either
             more of X and less of Y, or a state of more of Y and less of X. This inherent dichotomy becomes
             magnified and expressed as coherent behavior at the multi-molecular macro level due to the
             presence of the autocatalytic step ii) and diffusion.
             Unlike the convection example, the existence of circular causality is here not alone sufficient
             to manifest ordering. This is because the circular causal process here takes place at a microlevel
             uniformly throughout the reaction volume. It is not until this circular causality becomes
             integrated via diffusion and step ii) to produce coherent oscillations that it is able to become
             manifest as spatial ordering at the multi molecular level. Hence, a universal criterion for the
             emergence of order in either type of open system is the emergence of macrolevel circular causal
             behavior, i.e., coherent circular causal behavior at the microlevel dominating the tendency toward
             homogeneity. the former as the latter ( in that sequence) since the former has a higher negentropy, i.e., greater
             order. In actuality, the reverse is true; the individual has to expend more energy to digest the
             steak, than the hamburger; and the nutrient broth, which can be absorbed directly in the stomach
             with minimal digestion, places the least burden on the organism. Since it is calories and not
             entropy which sustains the organism, one would be wiser to choose the soup.
             Another problem with equation (1) is that it combines elements of both structure and
             process, \u2013d e S being the import of a given quantity of structural negentropy and d i S being the
             entropy change due to irreversible processes in the system. While it is thermodynamically
             legitimate to add these quantities, from a conceptual point of view, it is like trying to add apples
             and oranges. In the end you are more confused than ever as to how open systems are able to
             form ordered structures in a spontaneous manner.
             The basic question remains unanswered. How does negentropy naturally arise when all
             spontaneous physical and chemical processes are dissipative, i.e., characterized by entropy
             increase. A system such as a cell is able to assemble macromolecules of immense complexity
             creating an ordered macrolevel structure. But, at the microlevel, all the chemical processes
             involved in this anabolic process are dissipative. While the mechanism of protein synthesis is
             fairly well understood, the question remains; how did the phenomenon of protein synthesis first
             arise? Who taught the cell this trick of generating negentropy using common every day positive
             entropic processes? To avoid the pitfall of vitalism, we must conclude that this phenomenon
             evolved from simple prebiotic ordering principles, and that in the course of evolution, has become
             manifest in the preprogrammed and highly complex processes of the cell.
             Hence, the spontaneous emergence of order at the molecular level must be a property which
             is characteristic of simple open systems. Consequently, to come to an understanding of how
             negentropy arises in open systems, it is best to study simple examples such as the emergence of
             order in thermal convection and in nonlinear chemical reaction systems.
             First, though, I will state some general laws relating to process and structural order.
             1) All elemental processes are dispersive (dissipative).
             2) Physical order, "negentropy" manifests at a macroscopic level when a macrolevel dispersive
             process having many degrees of freedom is intersected and dominated by a macrolevel
             dispersive process having two degrees of freedom. Related to this:
             Order is the emergent expression of a cyclically causal phenomenon, i.e., of self-referential
             causality.
             In the case of thermal convection, such as that produced in a pan of water heated on a stove,
             there are two elemental dissipative processes involved: a) vertical thermal convective dissipation,
             and b) non-directional spatial dissipation of ordered molecular states. In the near equilibrium
             regime, the homogeneous steady state condition is stable. Heat is dissipated upward via thermal
             conduction. Any symmetry-breaking fluctuations, such as the formation of local pockets of
             water at higher or lower densities, are damped by the random motion of the molecules., i.e.,
             process b) dominates process a).
             As the thermal gradient is increased, i.e., as the system is moved further from equilibrium, a
             threshold is reached beyond which the symmetry of the system is broken and where thermal
             convection emerges as the dominant mechanism of dissipation, i.e., process a) supercedes process
             b). The transition from conduction to convection is marked by increased thermal dissipation.
             Hence, in this particular example the change from one mode to the other is itself governed by the
             dispersion principle.""");

        
        System.out.println("-------- AVERAGE -----------");
        
        double assembly = 0;
        for(double r : ASSEMBLY_R){
            assembly += r;
        }
        System.out.println("Assembly: " + (assembly / ASSEMBLY_R.size()));
        
        double ssm = 0;
        for(double r : SSM_R){
            ssm += r;
        }
        System.out.println("SSM: " + (ssm / SSM_R.size()));
        
        double compression = 0;
        for(double r : COMPRESSION_R){
            compression += r;
        }
        System.out.println("Compression: " + (compression / COMPRESSION_R.size()));
    }
    
    private static void test(String s){
        System.out.println("-------------------");
        double assembly = ASSEMBLY.value(s);
        double ssm = SSM.value(s);
        double compression = COMPRESSION.value(s);
        System.out.println("Assembly: " + assembly);
        System.out.println("SSM: " + ssm);
        System.out.println("Compression: " + compression);
        ASSEMBLY_R.add(assembly);
        SSM_R.add(ssm);
        COMPRESSION_R.add(compression);
        System.out.println("S: " + s);
    }

}
