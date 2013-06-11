/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.soap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leandro Ordonez
 */
public class DictGenerator {

    public static String DOC_PATH = "src/wsdl-reg/text-files/doc/";
    public static String DATA_PATH = "src/wsdl-reg/text-files/data/";
    public static String INPUT_PATH = "src/wsdl-reg/text-files/input/";
    public static String OUTPUT_PATH = "src/wsdl-reg/text-files/output/";
    public static Properties STOPWORDS = new Properties();

    public static void generateDocDictionary(String path) {
        Properties currencyCodes = new Properties();
        Properties acronyms = new Properties();
        try {
            currencyCodes.load(DictGenerator.class.getResourceAsStream("/docprocessing/util/currencycodes.properties"));
            acronyms.load(DictGenerator.class.getResourceAsStream("/docprocessing/util/acronyms.properties"));
            STOPWORDS.load(DictGenerator.class.getResourceAsStream("/docprocessing/util/stopwords.properties"));
        } catch (IOException ex) {
            Logger.getLogger(DictGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        File pathFile = new File(path);
        File[] files = pathFile.listFiles();
        HashMap<String, Integer> words = new HashMap<>();
        for (File file : files) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] wordsInLine = line.split(" ");
                    for (String word : wordsInLine) {
                        if (!currencyCodes.containsKey(word) && !word.isEmpty()) {
//                            if (acronyms.containsKey(word)) {
//                                word = acronyms.getProperty(word);
//                                String[] acronymRes = word.split(" ");
//                                for (String w : acronymRes) {
//                                    w = w.toLowerCase().trim();
//                                    if (words.containsKey(w)) {
//                                        Integer count = words.get(w);
//                                        words.put(w, count + 1);
//                                    } else {
//                                        words.put(w, 1);
//                                    }
//                                }
//                            } else {
                            word = word.toLowerCase().trim();
                            if (words.containsKey(word)) {
                                Integer count = words.get(word);
                                words.put(word, count + 1);
                            } else {
                                words.put(word, 1);
                            }
//                            }
                        }

                    }
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(DictGenerator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DictGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Words: " + words + "\n Dict. size: " + words.size());
        FileOutputStream out = null;
        try {
            File f = new File("/home/leandro/development/python/onlineldavb/dictnostops.txt");
            f.createNewFile();
            out = new FileOutputStream(f);
            for (String word : words.keySet()) {
                if (words.get(word) >= 2 && words.get(word) < 1000 && !word.matches("[0-9]*|\\w") && !STOPWORDS.containsKey(word)) {
                    word = word + "\n";
                    out.write(word.getBytes());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DictGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//        String line = "Get Weather Gent";
//        String [] words = line.split(" ");
//        for (String string : words) {
//            System.out.println(string);
//        }
        generateDocDictionary(DOC_PATH);
    }
}
