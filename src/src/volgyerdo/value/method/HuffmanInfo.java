/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
import java.util.Collection;
import volgyerdo.value.structure.Value;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.Map;

public class HuffmanInfo implements Value{

    @Override
    public String name() {
        return "Huffman information";
    }
    
    @Override
    public double value(String text) {
        if (text == null || text.length() < 1) {
            return 0;
        }
        Node root = buildHuffmanTree(text);
        Map<Character, Integer> codeTable = new HashMap<>();
        buildCodeTable(codeTable, root, new StringBuilder());
        long info = 0;
        for (char ch : text.toCharArray()) {
            info += codeTable.get(ch);
        }
        return info;
    }

    private void buildCodeTable(Map<Character, Integer> codeTable, Node node, StringBuilder sb) {
        if (node != null) {
            if (node.left == null && node.right == null) {
                codeTable.put(node.ch, sb.length());
            }
            sb.append('0');
            buildCodeTable(codeTable, node.left, sb);
            sb.deleteCharAt(sb.length() - 1);

            sb.append('1');
            buildCodeTable(codeTable, node.right, sb);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    private  Node buildHuffmanTree(String text) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char ch : text.toCharArray()) {
            freq.put(ch, freq.getOrDefault(ch, 0) + 1);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>((l, r) -> l.frequency - r.frequency);
        for (var entry : freq.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        if (freq.size() == 1) {
            // Add an extra node with a dummy character to handle single character input
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

    @Override
    public double value(Object object) {
        return 0;
    }

    @Override
    public double value(boolean[] values) {
        return 0;
    }

    @Override
    public double value(byte[] values) {
        return 0;
    }

    @Override
    public double value(short[] values) {
        return 0;
    }

    @Override
    public double value(int[] values) {
        return 0;
    }

    @Override
    public double value(float[] values) {
        return 0;
    }

    @Override
    public double value(double[] values) {
        return 0;
    }

    @Override
    public double value(char[] values) {
        return 0;
    }

    @Override
    public double value(String[] values) {
        return 0;
    }

    @Override
    public double value(Collection values) {
        return 0;
    }

    private  class Node {

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

}
