/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.util;

import org.jsoup.Jsoup;
import org.xeustechnologies.googleapi.spelling.SpellChecker;
import org.xeustechnologies.googleapi.spelling.SpellRequest;
import org.xeustechnologies.googleapi.spelling.SpellResponse;

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
        SpellChecker checker = new SpellChecker();
        SpellRequest request = new SpellRequest();
        String correctedSentence = "";
        for (String word : words) {
            if (word.length() > 6) {
//                long delay = Math.round(10000 * Math.random());
//                try {
////                https://www.google.com/tbproxy/spell?lang=en&hl=en
//                    Thread.sleep(delay);
//                } catch (InterruptedException ex) {
//                    Thread.currentThread().interrupt();
//                }
                request.setText(word);
                request.setIgnoreDuplicates(true); // Ignore duplicates
                SpellResponse spellResponse = checker.check(request);
                System.out.println((spellResponse.getCorrections() != null) ? pickFirst(spellResponse.getCorrections()[0].getValue()) : word);
                correctedSentence = (spellResponse.getCorrections() != null) ? correctedSentence + pickFirst(spellResponse.getCorrections()[0].getValue()) + " " : correctedSentence + word + " ";
            } else {
                System.out.println(word);
                correctedSentence = correctedSentence + word + " ";
            }
        }
        System.out.println(correctedSentence);
        return correctedSentence;
    }
    
    private static String pickFirst(String correctionWords) {
        return (correctionWords.indexOf("	")!=-1) ? correctionWords.substring(0, correctionWords.indexOf("	")) : correctionWords;
    }

    public static void main(String[] args) {

//        all = re.search(r'<text.*?>(.*)</text', all, flags=re.DOTALL).group(1)
//            all = re.sub(r'\n', ' ', all)
//            all = re.sub(r'\{\{.*?\}\}', r'', all)
//            all = re.sub(r'\[\[Category:.*', '', all)
//            all = re.sub(r'==\s*[Ss]ource\s*==.*', '', all)
//            all = re.sub(r'==\s*[Rr]eferences\s*==.*', '', all)
//            all = re.sub(r'==\s*[Ee]xternal [Ll]inks\s*==.*', '', all)
//            all = re.sub(r'==\s*[Ee]xternal [Ll]inks and [Rr]eferences==\s*', '', all)
//            all = re.sub(r'==\s*[Ss]ee [Aa]lso\s*==.*', '', all)
//            all = re.sub(r'http://[^\s]*', '', all)
//            all = re.sub(r'\[\[Image:.*?\]\]', '', all)
//            all = re.sub(r'Image:.*?\|', '', all)
//            all = re.sub(r'\[\[.*?\|*([^\|]*?)\]\]', r'\1', all)
//            all = re.sub(r'\&lt;.*?&gt;', '', all)

//        System.out.println("Before cleaning: \n\n");
//        String doc = "611223";
//        System.out.println(doc);
//
//        System.out.println("\n\nAfter cleaning: \n\n");
//        System.out.println(DocCleaning.clean(doc));

//        System.out.println(doc.matches("[0-9]*"));
//        System.out.println(doc.replaceAll("[0-9]*", " "));
        
        String splitCamelCase = CamelCaseFilter.splitCamelCase("Service definition of function mms  getMapAroundtopassword");
        String[] split = "disablepasswordchange".split(" ");
        for (String string : split) {
            System.out.println(string);
        }
        
        String words = "zip code	yjkhg";
        
        words = (words.indexOf("	")!=-1) ? words.substring(0, words.indexOf("	")) : words;
        System.out.println(words);


    }
}
