/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.http;

import docprocessing.util.CamelCaseFilter;
import docprocessing.util.POSTagger;
import docprocessing.util.Pair;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.HTMLElements;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.Util;

/**
 *
 * @author Leandro Ordonez <leandro.ordonez.ante@gmail.com>
 */
public class HTTPDocProcessing {

//     list of HTML attributes that will be retained in the final output:
    private static final Set<String> VALID_ATTRIBUTE_NAMES = new HashSet<String>(Arrays.asList(new String[]{
        //		"id","class","href","target","title"
        "title"
    }));

    public static void main(String[] args) {
        if (false) {
            System.getProperties().put("http.proxyHost", "proxy.unicauca.edu.co");
            System.getProperties().put("http.proxyPort", "3128");
        }
        String sourceUrlString = "http://www.benchmarkemail.com/API/Library";
//        String sourceUrlString = "http://www.holidaywebservice.com/ServicesAvailable_HolidayService2.aspx";

//        OutputDocument output = removeElements(sourceUrlString, HTMLElementName.SCRIPT, HTMLElementName.HEAD, HTMLElementName.LINK, HTMLElementName.IMG);
        OutputDocument output = cleanHTML(sourceUrlString);
//        System.out.println(output.toString());
//        System.out.println(output.getSegment().toString());
//        System.out.println(CamelCaseFilter.splitCamelCase("getArtist getAddress checkAddress getMovie GetWeather ResumingGame"));
//        System.out.println(CamelCaseFilter.getCamelCaseWords("getArtist getAddress checkAddress getMovie GetWeather ResumingGame"));
//        System.out.println(CamelCaseFilter.getIndexedCamelCaseWords("<td width=\"83%\" valign=\"top\" class=\"doc_table_body\"><a href=\"/API/Doc/emailCopy\">emailCopy</a> (<span class=\"codename\">string</span> <span class=\"codetype\">token</span>, <span class=\"codename\">string</span> <span class=\"codetype\">emailid</span>)"));
//        System.out.println(CamelCaseFilter.getIndexedCamelCaseWords("primera operacion: getArtist, segunda: getAddress checkAddress getMovie GetWeather ResumingGame"));
//        List<String> camelCaseList = CamelCaseFilter.getCamelCaseWords("getArtist getAddress checkAddress getMovie GetWeather ResumingGame");
//        for (String ccWord : camelCaseList) {
//            System.out.println(isOperation(ccWord));
//        }
        Source cleanedHtml = new Source(output.toString());
        //        System.out.println(cleanedHtml.toString());
        HashMap<String, List<String>> tagMap = getTagMap(cleanedHtml);
//        System.out.println(tagMap);
        String electedTag = getMostPopularTag(tagMap);
        System.out.println("Elected Tag: " + electedTag + " (" + tagMap.get(electedTag).size() + " operations)");
        List<String> operationList = tagMap.get(electedTag);
        HashMap<String, String> operationMap = getOperationMap(cleanedHtml, operationList, electedTag);

    }

    /**
     *
     * @param sourceUrlString
     * @return
     */
    public static OutputDocument cleanHTML(String sourceUrlString) {
        return removeElements(sourceUrlString, HTMLElementName.SCRIPT, HTMLElementName.HEAD, HTMLElementName.LINK, HTMLElementName.IMG);
    }

    /**
     *
     * @param sourceUrlString
     * @param elements
     * @return
     */
    public static OutputDocument removeElements(String sourceUrlString, String... elements) {
        if (sourceUrlString.indexOf(':') == -1) {
            sourceUrlString = "file:" + sourceUrlString;
        }
        try {
            URL sourceUrl = new URL(sourceUrlString);
            String htmlText = Util.getString(new InputStreamReader(sourceUrl.openStream()));
            Source source = new Source(htmlText);
            OutputDocument outputDocument = new OutputDocument(source);
//            List<Element> elementsToDelete = new ArrayList<>();
            for (String element : elements) {
                outputDocument.remove(source.getAllElements(element));
            }
            outputDocument.remove(source.getAllElements(StartTagType.COMMENT));
            outputDocument.remove(source.getAllElements(StartTagType.DOCTYPE_DECLARATION));
            outputDocument.remove(source.getAllElements(StartTagType.XML_DECLARATION));
            outputDocument.remove(source.getAllElements(StartTagType.CDATA_SECTION));
            outputDocument.remove(source.getAllElements(StartTagType.SERVER_COMMON));
            String outputString = outputDocument.toString();
            source = new Source(outputString);
            for (StartTag st : source.getAllStartTags()) {
                String cleanStartTag = getStartTagHTML(st).toString();
                outputString = outputString.replace(st.toString(), cleanStartTag);
            }
            source = new Source(outputString);
            outputDocument = new OutputDocument(source);
//            System.out.println(outputDocument.toString());
            return outputDocument;
        } catch (MalformedURLException ex) {
            Logger.getLogger(HTTPDocProcessing.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(HTTPDocProcessing.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     * @param ccWord
     * @return
     */
    public static boolean isOperation(String ccWord) {
        POSTagger tagger = POSTagger.getInstance();
        String tagString = tagger.tagString(CamelCaseFilter.splitCamelCase(ccWord));
//        System.out.println(tagString);
        return tagString.contains("_VB") && tagString.contains("_NN");
    }

    /**
     *
     * @param tagMap
     * @return
     */
    public static String getMostPopularTag(HashMap<String, List<String>> tagMap) {
        String electedTag = null;
        int maxSize = 0;
        for (String tag : tagMap.keySet()) {
            if (tagMap.get(tag).size() > maxSize) {
                electedTag = tag;
                maxSize = tagMap.get(tag).size();
            }
        }
        return electedTag;
    }

    /**
     *
     * @param htmlSource
     * @return
     */
    public static HashMap<String, List<String>> getTagMap(Source htmlSource) {
        htmlSource.fullSequentialParse();
        List<Pair<Integer, String>> indexedCamelCaseWords = CamelCaseFilter.getIndexedCamelCaseWords(htmlSource.toString());
        HashMap<String, List<String>> tagMap = new HashMap<>();
        for (Pair p : indexedCamelCaseWords) {
            String ccWord = p.getRight().toString();
            Element enclosingElement = htmlSource.getEnclosingElement((int) p.getLeft());
            Element parentElement = enclosingElement.getParentElement();
//            System.out.println(parentElement.getName() + ", " + ccWord);
            if (isOperation(ccWord)) {
                if (tagMap.containsKey(parentElement.getName())) {
                    if (!tagMap.get(parentElement.getName()).contains(ccWord)) {
                        tagMap.get(parentElement.getName()).add(ccWord);
                    }
                } else {
                    List<String> ccWords = new ArrayList<>();
                    ccWords.add(ccWord);
                    tagMap.put(parentElement.getName(), ccWords);
                }
            }
        }
        return tagMap;
    }

    private static CharSequence getStartTagHTML(StartTag startTag) {
        // tidies and filters out non-approved attributes
        StringBuilder sb = new StringBuilder();
        sb.append('<').append(startTag.getName());
        for (Attribute attribute : startTag.getAttributes()) {
            if (VALID_ATTRIBUTE_NAMES.contains(attribute.getKey())) {
                sb.append(' ').append(attribute.getName());
                if (attribute.getValue() != null) {
                    sb.append("=\"");
                    sb.append(CharacterReference.encode(attribute.getValue()));
                    sb.append('"');
                }
            }
        }
        if (startTag.getElement().getEndTag() == null && !HTMLElements.getEndTagOptionalElementNames().contains(startTag.getName())) {
            sb.append(" /");
        }
        sb.append('>');
        return sb;
    }
    
    public static HashMap<String, String> getOperationMap(Source cleanedHtml, List<String> operationList, String electedTag) {
        System.out.println("Lista de Operaciones: " + operationList);
        try {
            HashMap<String, String> operationMap = new HashMap<>();
            for (Element e : cleanedHtml.getAllElements()) {
                Source elementSource = new Source(e.getContent());
                elementSource.fullSequentialParse();
                List<Pair<Integer, String>> indexedCamelCaseWords = CamelCaseFilter.getIndexedCamelCaseWords(e.getContent().toString());
                List<Pair<Integer, String>> operations = new ArrayList<>();
                for (Pair<Integer, String> pair : indexedCamelCaseWords) {
                    if (operationList.contains(pair.getRight())) {
                        operations.add(pair);
                    }
                }
                int numberOfMatches = 0;
                Pair<Integer, String> match = null;
                for (Pair<Integer, String> ccWordPair : operations) {
//                    Element enclosingElement = elementSource.getEnclosingElement((int) ccWordPair.getLeft());
//                    System.out.println(ccWordPair);
//                    Element parentElement = enclosingElement.getParentElement();
                    if (e.getName().equals(electedTag)) {
                        match = ccWordPair;
                        numberOfMatches = numberOfMatches += 1;
                        if (numberOfMatches > 1) {
                            match = null;
                            break;
                        }
                    }

                }
                if (match != null) {
                    operationMap.put(match.getRight(), e.getTextExtractor().toString());
                }
            }
            System.out.println("Mapa de Operaciones: " + operationMap);
            return operationMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
