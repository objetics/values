/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.Map;

public class HuffmanShannonInfo {

    public static long information(String text) {
        Node root = buildHuffmanTree(text);
        Map<Character, String> codeTable = new HashMap<>();
        buildCodeTable(codeTable, root, new StringBuilder());

        StringBuilder encodedText = new StringBuilder();
        for (char ch : text.toCharArray()) {
            encodedText.append(codeTable.get(ch));
        }
        
        return (long)ShannonInfo1.information(encodedText.toString());
    }

    public static void buildCodeTable(Map<Character, String> codeTable, Node node, StringBuilder sb) {
        if (node != null) {
            if (node.left == null && node.right == null) {
                if (sb.length() == 0) { // Ha csak egy csomópont van, akkor alapértelmezett kód
                    codeTable.put(node.ch, "0");
                } else {
                    codeTable.put(node.ch, sb.toString());
                }
            } else {
                sb.append('0');
                buildCodeTable(codeTable, node.left, sb);
                sb.deleteCharAt(sb.length() - 1);

                sb.append('1');
                buildCodeTable(codeTable, node.right, sb);
                sb.deleteCharAt(sb.length() - 1);
            }
        }
    }

    private static Node buildHuffmanTree(String text) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char ch : text.toCharArray()) {
            freq.put(ch, freq.getOrDefault(ch, 0) + 1);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>((l, r) -> l.frequency - r.frequency);
        for (var entry : freq.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        if (freq.size() == 1) { // Ha csak egy karakter van, adjunk hozzá egy dummy csomópontot
            pq.add(new Node('\0', 1));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            int sum = left.frequency + right.frequency;
            pq.add(new Node('\0', sum, left, right));
        }

        return pq.poll();
    }

    static class Node {
        char ch;
        int frequency;
        Node left = null, right = null;

        Node(char ch, int frequency) {
            this.ch = ch;
            this.frequency = frequency;
        }

        Node(char ch, int frequency, Node left, Node right) {
            this.ch = ch;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }
    }

    public static void main(String[] args) {
        String text = "Igen, a Huffman-kódolás különlegessége, hogy optimális a változó hosszúságú kódok szempontjából, amikor számos különböző karakter van jelen a szövegben. Azonban, ha a szöveg csak egyetlen karaktert tartalmaz, akkor a Huffman-kódolás nem fogja csökkenteni a kódolt szöveg méretét.";
        long info = information(text);
        System.out.println("Original: " + text.length() * 8);
        System.out.println("Encoded: " + info);
    }
}
