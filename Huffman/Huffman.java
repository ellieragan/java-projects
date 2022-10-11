import java.io.*;
import java.util.*;

/**
 * Huffman tree compressor/decompressor
 * @author Ellie Boyd
 * 05-05-21
 */

public class Huffman {
    private PriorityQueue<BinaryTree<InfoStorer>> queue; //priority queue of tree(s)
    private BinaryTree<InfoStorer> tree; //tree linking characters to frequencies
    private TreeMap<Character, String> map; //map linking characters to the path of 0s and 1s required to find them in the tree
    private BufferedReader input; //to read the starting file
    private String fileName; //name of the starting file


    /**
     * initializes new Huffman
     */
    public Huffman(String fileName) {
        this.fileName = fileName;
    }

    /**
     * converts file1.txt to string
     * @param input -- buffered reader
     * @return -- a string of the file
     * @throws IOException -- in case the input name is wrong and there's an error reading it
     */
    public String readFile(BufferedReader input) throws IOException {
        StringBuilder file = new StringBuilder();
        String line;

        while ((line = input.readLine()) != null) {
            file.append(line);
            file.append(System.lineSeparator());
        }
        return file.toString();
    }

    /**
     * maps characters from the file to the frequencies with which they occur
     * @return -- a treeMap 'frequency'
     * @throws IOException -- in case of an error with the readFile
     */
    public TreeMap<Character, Integer> map() throws IOException {
        input = new BufferedReader(new FileReader(fileName));

        String string = readFile(input); //gets string of file
        TreeMap<Character, Integer> frequency = new TreeMap<>();

        for (Character character : string.toCharArray()) { //merges the characters into a treemap
            frequency.merge(character, 1, Integer::sum);
        }
        return frequency;
    }

    /**
     * comparator class to sort tree objects by their frequencies for the priority queue
     */
    public static class TreeComparator implements Comparator<BinaryTree<InfoStorer>> {
        @Override
        public int compare(BinaryTree<InfoStorer> a, BinaryTree<InfoStorer> b) {
            return Integer.compare(a.getData().getFrequency(), b.getData().getFrequency());
        }
    }

    /**
     * takes the TreeMap created by map() and makes each key into its own tree,
     * adding the trees to the priority queue from least to most frequently occurring
     */
    public void makeTrees() throws IOException {
        TreeMap<Character, Integer> frequency = map();
        queue = new PriorityQueue<>(new TreeComparator());
        for (Character key : frequency.keySet()) {
            InfoStorer node = new InfoStorer(key, frequency.get(key));
            BinaryTree<InfoStorer> tree = new BinaryTree<>(node);
            queue.add(tree);
        }
    }

    /**
     * takes the priority queue created by makeTrees() and merges all trees
     * in it into one large tree
     */
    public void combineTrees() {
        if (queue.peek() == null) return;
        BinaryTree<InfoStorer> T1 = queue.poll();
        BinaryTree<InfoStorer> T2 = queue.poll();
        InfoStorer node = new InfoStorer(T1.getData().getFrequency() + T2.getData().getFrequency());
        BinaryTree<InfoStorer> T = new BinaryTree<>(node, T1, T2);
        queue.add(T);
        if (queue.size() == 1) tree = T;
        else combineTrees();
    }

    /**
     * takes the tree created by combineTrees() and uses it to make a map connecting
     * each character to the path of 1s and 0s required to find it
     * @param tree -- tree of characters/frequencies
     */
    public void codeMap(BinaryTree<InfoStorer> tree) {
        map = new TreeMap<>();
        String code = ""; //empty string that will fill with the 1s and 0s
        traversal(tree, map, code); //recursive helper method
    }

    /**
     * recursively helps codeMap by traversing the tree
     * @param tree -- tree of characters/frequencies
     * @param map -- the treeMap to which the paths and characters are being added
     * @param code -- the list of 0s and 1s describing the path to each character
     */
    public void traversal(BinaryTree<InfoStorer> tree, TreeMap<Character, String> map, String code) {
        if (tree == null) return;
        if (tree.hasLeft()) { traversal(tree.getLeft(), map, code + "0"); }
        if (tree.hasRight()) { traversal(tree.getRight(), map, code + "1"); }
        else { map.put(tree.getData().getCharacter(), code); }
    }

    /**
     * makes huffman tree for file and uses it to compress the file
     * @throws IOException -- in case of error reading file
     */
    public void compressor() throws IOException {
        makeTrees();
        combineTrees();
        codeMap(tree);
        input = new BufferedReader(new FileReader(fileName));
        String string = fileName.substring(0, fileName.length()-4); //separates file name from the .txt
        BufferedBitWriter bitOutput = new BufferedBitWriter(string + "_compressed.txt");
        int value;
        while ((value = input.read()) != -1) {
            String code = map.get((char)value);
            for (Character c : code.toCharArray()) {
                if (c == '0') bitOutput.writeBit(false);
                if (c == '1') bitOutput.writeBit(true);
            }
        }
        input.close();
        bitOutput.close();
    }

    /**
     * takes a bit file, uses the original huffman tree created during the compression, and decompresses it
     * @throws IOException -- in case of issues reading the file
     */
    public void decompressor() throws IOException {
//        makeTrees();
//        combineTrees();
//        codeMap(tree);
        BinaryTree<InfoStorer> newTree = tree;
        String string = fileName.substring(0, fileName.length()-4);
        BufferedBitReader input = new BufferedBitReader(string + "_compressed.txt");
        BufferedWriter output = new BufferedWriter(new FileWriter(string + "_decompressed.txt"));
        while (input.hasNext()) {
            boolean b = input.readBit();
            if (b) { //the bit is 'true' move right
                if (newTree.hasRight()) {
                    newTree = newTree.getRight();
                }
                if (newTree.isLeaf()) {
                    output.write(newTree.data.getCharacter());
                    newTree = tree;
                }
            }
            else { //if the bit is 'false' move left
                if (newTree.hasLeft()) {
                    newTree = newTree.getLeft();
                }
                if (newTree.isLeaf()) {
                    output.write(newTree.data.getCharacter());
                    newTree = tree;
                }
            }
        }
        input.close();
        output.close();
    }


    public static void main(String[] args) throws IOException {
        Huffman huffman0 = new Huffman("inputs/file1.txt"); //my test of no characters
        Huffman huffman1 = new Huffman("inputs/file2.txt"); //test of one character
        Huffman huffman2 = new Huffman("inputs/file3.txt"); //test of one repeated character
        Huffman huffman3 = new Huffman("inputs/WarAndPeace.txt");
        Huffman huffman4 = new Huffman("inputs/USConstitution.txt");

        huffman0.compressor();
        huffman0.decompressor();
        huffman1.compressor();
        huffman1.decompressor();
        huffman2.compressor();
        huffman2.decompressor();
        huffman3.compressor();
        huffman3.decompressor();
        huffman4.compressor();
        huffman4.decompressor();

    }
}
