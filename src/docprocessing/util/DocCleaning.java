/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.util;

import org.jsoup.Jsoup;

/**
 *
 * @author Leandro Ordonez
 */
public class DocCleaning {

    public static String clean(String documentation) {
        String result = documentation;
        result = Jsoup.parse(documentation).text();
        return CamelCaseFilter.splitCamelCase(result.replaceAll("http://[^\\s]*", " ")
                .replaceAll("\\[\\w*]", " ")
                .replaceAll("(<[^>]+>)", " ")
                .replaceAll("[^a-zA-Z]", " "));
//                .replaceAll("[0-9]*", " ");
//        return Jsoup.clean(documentation, Whitelist.simpleText());
    }

    public static String spellCorrection(String sentence) {
        String[] words = sentence.split(" ");
        String correctedSentence = "";
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (word.trim().length() > 6) {
                correctedSentence = correctedSentence + TrieSpellChecker.compoundSplitter(word.trim()) + " ";
            } else {
//                System.out.println(word.trim());
                correctedSentence = correctedSentence + word.trim() + " ";
            }
        }
//        System.out.println(correctedSentence);
        return correctedSentence;
    }

//    private static String pickFirst(String correctionWords) {
//        return (correctionWords.indexOf("	")!=-1) ? correctionWords.substring(0, correctionWords.indexOf("	")) : correctionWords;
//    }
    public static void main(String[] args) {

//        System.out.println("Before cleaning: \n\n");
//        String doc = "611223";
//        System.out.println(doc);
//
//        System.out.println("\n\nAfter cleaning: \n\n");
//        System.out.println(DocCleaning.clean(doc));

//        System.out.println(doc.matches("[0-9]*"));
//        System.out.println(doc.replaceAll("[0-9]*", " "));

//        String splitCamelCase = CamelCaseFilter.splitCamelCase("Service definition of function mms  getMapAround");
//        String[] split = "disablepasswordchange".split(" ");
//        for (String string : split) {
//            System.out.println(string);
//        }
//        
//        String words = "zip code	anything";
//        
//        words = (words.indexOf("	")!=-1) ? words.substring(0, words.indexOf("	")) : words;
//        System.out.println(words);
        spellCorrection("house number numeric");

    }
}
