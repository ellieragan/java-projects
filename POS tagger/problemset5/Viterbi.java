import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * author: ellie boyd
 * date: 05/23/21
 */
public class Viterbi {

    private TreeMap<String, TreeMap<String, Double>> emission; //POS to word
    private TreeMap<String, TreeMap<String, Double>> transition; //POS to POS
    private String start = "#";
    private ArrayList<String> observations = new ArrayList<>(); //list of observed words
    private int unseenPenalty = -100;

    public Viterbi(String tags, String sentences) throws IOException {
        buildEmission(tags, sentences);
        buildTransition(tags);
        observationList(sentences);
        //tagger("PS5/simple-test-sentences.txt");
    }

    /**
     * build emission map, showing how frequently a word is used as a particular part of speech
     * @param tags -- training file with tags
     * @param sentences -- training file with sentences
     * @throws IOException
     */
    private void buildEmission(String tags, String sentences) throws IOException {
        emission = new TreeMap<>();
        BufferedReader reader1 = new BufferedReader(new FileReader(tags));
        String line1 = reader1.readLine();
        BufferedReader reader2 = new BufferedReader(new FileReader(sentences));
        String line2;

        while ((line2 = reader2.readLine()) != null) {
            String lowercase2 = line2.toLowerCase();
            String[] split1 = line1.split(" ");
            String[] split2 = lowercase2.split(" ");
            for (int i = 0; i < split1.length; i++) {
                    if (emission.get(split1[i]) == null) {
                        TreeMap<String, Double> inner = new TreeMap<>();
                        inner.put(split2[i], 1.0);
                        emission.put(split1[i], inner);
                    } else if (emission.get(split1[i]).get(split2[i]) == null) {
                        emission.get(split1[i]).put(split2[i], 1.0);
                    } else {
                        emission.get(split1[i]).put(split2[i], emission.get(split1[i]).get(split2[i]) + 1);
                    }
                }
            line1 = reader1.readLine();
        }
        reader2.close();
        reader1.close();

        int total = 0;
        for (String key : emission.keySet()) {
            //TreeMap<String, Double> map = emission.get(key);
            for (String key2 : emission.get(key).keySet()) {
                total += emission.get(key).get(key2);
            }
            for (String key2 : emission.get(key).keySet()) {
                emission.get(key).put(key2, Math.log(emission.get(key).get(key2) / total));
            }
            total = 0;
        }
    }

    /**
     * builds transition map, showing how frequently a POS maps to another POS
     * @param tags -- file with training tags
     * @throws IOException
     */
    private void buildTransition(String tags) throws IOException {
        transition = new TreeMap<>();
        BufferedReader reader1 = new BufferedReader(new FileReader(tags));
        String line1;
        while ((line1 = reader1.readLine()) != null) {
            String[] split1 = line1.split(" ");
            if (transition.get(start) == null) {
                TreeMap<String, Double> inner = new TreeMap<>();
                inner.put(split1[0], 1.0);
                transition.put(start, inner);
            } else if (transition.get(start).get(split1[0]) == null) {
                transition.get(start).put(split1[0], 1.0);
            } else {
                transition.get(start).put(split1[0], transition.get(start).get(split1[0]) + 1);
            }
            for (int i = 0; i < split1.length - 1; i++) {
                    if (transition.get(split1[i]) == null) {
                        TreeMap<String, Double> inner = new TreeMap<>();
                        inner.put(split1[i + 1], 1.0);
                        transition.put(split1[i], inner);
                    } else if (transition.get(split1[i]).get(split1[i + 1]) == null) {
                        transition.get(split1[i]).put(split1[i + 1], 1.0);
                    } else {
                        transition.get(split1[i]).put(split1[i + 1], transition.get(split1[i]).get(split1[i + 1]) + 1);
                    }
                }
            }
        reader1.close();

        int total = 0;
        for (String key : transition.keySet()) {
            //TreeMap<String, Double> map = transition.get(key);
            for (String key2 : transition.get(key).keySet()) {
                total += transition.get(key).get(key2);
            }
            for (String key2 : transition.get(key).keySet()) {
                transition.get(key).put(key2, Math.log(transition.get(key).get(key2) / total));
            }
            total = 0;
        }
    }

    /**
     * builds a list of word observations
     * @param sentence -- a line of words
     * @throws IOException
     */
    private void observationList(String sentence) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(sentence));
        String line;
        while ((line = reader.readLine()) != null) {
            String lowercase = line.toLowerCase();
            String[] split = lowercase.split(" ");
            for (String word : split) {
                    observations.add(word);
            }
        }
    }

    /**
     * builds a stack of tags for a given list of words
     * @param observations -- an arraylist of strings; words to be tagged
     * @return -- a stack of tags
     */
    private Stack<String> tagger(ArrayList<String> observations) {
        ArrayList<TreeMap<String, String>> backtrace = new ArrayList<>();
        HashMap<Integer, ArrayList<String>> currStates = new HashMap<>();
        TreeMap<String, Double> currScores = new TreeMap<>();
        ArrayList<String> list = new ArrayList<>();
        list.add(start);
        currStates.put(0, list);
        currScores.put(start, 0.0);
        for (int i = 0; i < observations.size(); i++) {
            backtrace.add(new TreeMap<>());
            HashMap<Integer, ArrayList<String>> nextStates = new HashMap<>();
            TreeMap<String, Double> nextScores = new TreeMap<>();
            ArrayList<String> list2 = new ArrayList<>();
            for (String currState : currStates.get(i)) {
                if (!transition.containsKey(currState)) {
                    continue;
                }
                for (String nextState : transition.get(currState).keySet()) {
                    if (!list2.contains(nextState)) {
                        list2.add(nextState);
                        nextStates.put(i + 1, list2);
                    }

                    double nextScore;
                    if (!emission.get(nextState).containsKey(observations.get(i))) {
                        nextScore = currScores.get(currState) + transition.get(currState).get(nextState) + unseenPenalty;
                    }
                    else {
                        nextScore = currScores.get(currState) + transition.get(currState).get(nextState) + emission.get(nextState).get(observations.get(i));
                    }
                    if (!nextScores.containsKey(nextState) || nextScore > nextScores.get(nextState)) {
                        nextScores.put(nextState, nextScore);
                        backtrace.get(i).put(nextState, currState);
                    }
                }
            }
            currStates = nextStates;
            currScores = nextScores;
        }
//        System.out.println(currStates.get(observations.size()));
//        System.out.println(currScores);

        double maxScore = currScores.get(currStates.get(observations.size()).get(0));
        String bestState = currStates.get(observations.size()).get(0);
        for (String key : currScores.keySet()) {
            if (currScores.get(key) > maxScore) {
                maxScore = currScores.get(key);
                bestState = key;
            }
        }
        //System.out.println(backtrace);
        Stack<String> path = new Stack<>();
        path.add(bestState);
        int i = backtrace.size() - 1;
        while (i >= 0) {
            bestState = backtrace.get(i).get(bestState);
            path.add(bestState);
            i --;
        }
        //System.out.println(path);
        return path;
    }

    /**
     * takes a file of test sentences, runs the Viterbi on each line, and compares those tags with the
     * tags in the corresponding test tag file
     * @param fileWithSentences -- test sentence file
     * @param fileWithTags -- test tag file
     * @throws IOException
     */
    private void testCompare(String fileWithSentences, String fileWithTags) throws IOException {
        System.out.println("\ncomparing " + fileWithSentences + " and " + fileWithTags + "...\n");
        BufferedReader reader1 = new BufferedReader(new FileReader(fileWithSentences));
        String line1 = reader1.readLine();
        BufferedReader reader2 = new BufferedReader(new FileReader(fileWithTags));
        String line2;
        ArrayList<String> wrongWords = new ArrayList<>();
        float correct = 0;
        float incorrect = 0;
        while ((line2 = reader2.readLine()) != null) {
            String[] split1 = line1.split(" ");
            String[] split2 = line2.split(" ");
            ArrayList<String> list1 = new ArrayList<>();
            for (String x : split1) { list1.add(x); }
            Stack<String> tagged = tagger(list1);
            tagged.pop();
            for (int i = 0; i < split2.length; i++) {
                if (split2[i].equals(tagged.pop())) {
                    correct ++;
                }
                else {
                    incorrect++;
                    //if (!wrongWords.contains(split1[i])) wrongWords.add(split1[i]);
                }
            }
            line1 = reader1.readLine();
        }
        System.out.println("words correct: " + correct);
        System.out.println("words incorrect: " + incorrect);
        System.out.println((correct / (correct + incorrect)) * 100 + " percent accuracy");
        //System.out.println(wrongWords);
    }


    public static void main(String[] args) {
        try {
            Viterbi viterbi0 = new Viterbi("inputs/simple-train-tags.txt", "inputs/simple-train-sentences.txt");
            Viterbi viterbi1 = new Viterbi("inputs/brown-train-tags.txt", "inputs/brown-train-sentences.txt");

            viterbi0.testCompare("inputs/simple-test-sentences.txt", "inputs/simple-test-tags.txt");
            viterbi1.testCompare("inputs/brown-test-sentences.txt", "inputs/brown-test-tags.txt");

            System.out.println("\nstarting user input testing using brown files for training...\n");
            Scanner in = new Scanner(System.in);
            String line;
            System.out.println("type sentence:");
            while ((line = in.nextLine()) != null) {
                String lowercase = line.toLowerCase();
                String[] split = lowercase.split(" ");
                ArrayList<String> list = new ArrayList<>();
                for (String x : split) {
                    list.add(x);
                }
                Stack<String> path = viterbi1.tagger(list);
                while (path.size() != 0) {
                    System.out.print(path.pop() + " ");
                }
                System.out.println("\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}