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
import java.util.regex.Pattern;
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
        List<String> sourcesUrls = Arrays.asList(
                "http://blogspam.net/api/1.0/");//,
//                "http://help.4shared.com/index.php/SOAP_API",
//                "http://developer.affili.net/desktopdefault.aspx/tabid-93");//,
//                "http://www.benchmarkemail.com/API/Library",
//                "http://www.holidaywebservice.com/ServicesAvailable_HolidayService2.aspx",
//                "http://business.intuit.com/boorah/docs/syndication/integration.html",
//                "http://aws.amazon.com/es/sqs/",
//                "http://aws.amazon.com/es/simpledb/",
//                "http://www.ebi.ac.uk/Tools/webservices/services/eb-eye");
        for (String sourceUrlString : sourcesUrls) {
            OutputDocument output = cleanHTML(sourceUrlString);
            Source cleanedHtml = new Source(output.toString());
//        System.out.println(cleanedHtml.toString());
            HashMap<String, List<String>> tagMap = getTagMap(cleanedHtml);
//        System.out.println(tagMap);
            String electedTag = getMostPopularTag(tagMap);
            System.out.println("Elected Tag: " + electedTag + " (" + tagMap.get(electedTag).size() + " operations)");
            List<String> operationList = tagMap.get(electedTag);
            HashMap<String, String> operationMap = getOperationMap(cleanedHtml, operationList, electedTag);
//        for (StartTag st : cleanedHtml.getAllStartTags()) {
//            System.out.print("<" + st.getName() + ">");
//        }
            Element operationContainer1 = getOperationContainer(cleanedHtml.getFirstElement(), operationList, electedTag);
            Element operationContainer2 = getOperationContainer2(cleanedHtml.getFirstElement(), operationList, electedTag);

            StringBuilder result = new StringBuilder();
            for (StartTag st : operationContainer1.getAllStartTags()) {
                result.append("<").append(st.getName()).append(">");
                System.out.print("<" + st.getName() + ">");
            }
            String operationContainerTest1 = result.toString();
            System.out.println("\n\n");
            result = new StringBuilder();
            for (StartTag st : operationContainer2.getAllStartTags()) {
                result.append("<").append(st.getName()).append(">");
                System.out.print("<" + st.getName() + ">");
            }
            String operationContainerTest2 = result.toString();

            System.out.println("\nFor " + sourceUrlString + ": " + operationContainerTest1.equals(operationContainerTest2)
                    + "\n-----------------------------------------------------------------------------------------------------------------------------"
                    + "\n-----------------------------------------------------------------------------------------------------------------------------");
        }
    }

    /**
     *
     * @param sourceUrlString
     * @return
     */
    public static OutputDocument cleanHTML(String sourceUrlString) {
        return removeElements(sourceUrlString, HTMLElementName.SCRIPT, HTMLElementName.HEAD, HTMLElementName.LINK, HTMLElementName.IMG, HTMLElementName.HR, HTMLElementName.BR, HTMLElementName.S, HTMLElementName.COLGROUP, HTMLElementName.NOSCRIPT, HTMLElementName.SOURCE, HTMLElementName.NOFRAMES, HTMLElementName.SELECT, HTMLElementName.STYLE);
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
//                if (st.getName().equals("span")){// || st.getName().equals("acronym")) {
//                    System.out.println(st.getElement().toString());
//                    System.out.println(st.getElement().getTextExtractor().toString());
//                }
                if (st.getName().matches("em|a|b|i|acronym|blockquote|strong|code|sup|sub|small|big|pre|span|font")) {
                    Element pt = st.getElement().getParentElement();
                    if (!(pt.getName().matches("em|a|b|i|acronym|blockquote|strong|code|sup|sub|small|big|pre|span|font"))) {
                        outputString = outputString.replaceFirst(Pattern.quote(st.getElement().toString()), st.getElement().getContent().toString());
                        if(outputString.indexOf(st.getElement().toString())!= -1){
                            System.out.println("\nSomething went wrong!: " + st.getElement().toString());
                        } else {
                            System.out.println("\n" + st.getElement().toString() + "\n\nwas replaced with:\n\n" + st.getElement().getContent().toString());
                        }
                        continue;
                    }
                    else {
//                        System.out.println("\nSomething went wrong!: \n\n" + st.getElement().toString() + "\n\n !!!! Parent: " + pt.toString());
                        outputString = outputString.replaceFirst(Pattern.quote(pt.toString()), pt.getContent().toString());
                        outputString = outputString.replaceFirst(Pattern.quote(st.getElement().toString()), st.getElement().getContent().toString());
                        if(outputString.indexOf(st.getElement().toString())!= -1){
                            System.out.println("\nSomething went wrong!: " + st.getElement().toString());
                        } else {
                            System.out.println("\n" + st.getElement().toString() + "\n\nwas replaced with:\n\n" + st.getElement().getContent().toString());
                        }
                    }
                }
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
//                if (tagMap.containsKey(parentElement.getName())) {
                if (tagMap.containsKey(enclosingElement.getName())) {
//                    if (!tagMap.get(parentElement.getName()).contains(ccWord)) {
//                        tagMap.get(parentElement.getName()).add(ccWord);
                    if (!tagMap.get(enclosingElement.getName()).contains(ccWord)) {
                        tagMap.get(enclosingElement.getName()).add(ccWord);
                    }
                } else {
                    List<String> ccWords = new ArrayList<>();
                    ccWords.add(ccWord);
//                    tagMap.put(parentElement.getName(), ccWords);
                    tagMap.put(enclosingElement.getName(), ccWords);
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

//    public static HashMap<String, String> getOperationMap(Source cleanedHtml, List<String> operationList, String electedTag) {
//        System.out.println("Lista de Operaciones: " + operationList);
//        try {
//            HashMap<String, String> operationMap = new HashMap<>();
//            for (Element e : cleanedHtml.getAllElements()) {
//                Source elementSource = new Source(e.getContent());
//                elementSource.fullSequentialParse();
//                List<Pair<Integer, String>> indexedCamelCaseWords = CamelCaseFilter.getIndexedCamelCaseWords(e.getContent().toString());
//                List<Pair<Integer, String>> operations = new ArrayList<>();
//                for (Pair<Integer, String> pair : indexedCamelCaseWords) {
//                    if (operationList.contains(pair.getRight())) {
//                        operations.add(pair);
//                    }
//                }
//                int numberOfMatches = 0;
//                Pair<Integer, String> match = null;
//                for (Pair<Integer, String> ccWordPair : operations) {
////                    Element enclosingElement = elementSource.getEnclosingElement((int) ccWordPair.getLeft());
////                    System.out.println(ccWordPair);
////                    Element parentElement = enclosingElement.getParentElement();
//                    if (e.getName().equals(electedTag)) {
//                        match = ccWordPair;
//                        numberOfMatches = numberOfMatches += 1;
//                        if (numberOfMatches > 1) {
//                            match = null;
//                            break;
//                        }
//                    }
//
//                }
//                if (match != null) {
//                    operationMap.put(match.getRight(), e.getTextExtractor().toString());
//                }
//            }
//            System.out.println("Operation Map: " + operationMap + " -- " + operationMap.size() + " operations");
//            return operationMap;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    public static HashMap<String, String> getOperationMap(Source cleanedHtml, List<String> operationList, String electedTag) {
        System.out.println("Lista de Operaciones: " + operationList);
        try {
            HashMap<String, String> operationMap = new HashMap<>();

            for (Element e : cleanedHtml.getAllElements(electedTag)) {
                Source elementSource = new Source(e.getContent());
                elementSource.fullSequentialParse();
                List<String> camelCaseWords = CamelCaseFilter.getCamelCaseWords(e.getContent().toString());
//                List<String> operations = new ArrayList<>();
                for (String ccOperation : camelCaseWords) {
                    if (operationList.contains(ccOperation)) {
//                        operations.add(pair);
                        operationMap.put(ccOperation, e.getTextExtractor().toString());
                    }
                }
            }
            System.out.println(/*"Operation Map: " + operationMap +*/"-- " + operationMap.size() + " operations");
//            for (String operation : operationMap.keySet()) {
//                System.out.println("\nOperation " + operation + ":");
//                System.out.print("\t" + operationMap.get(operation) + "\n");
//            }
            System.out.println();
            return operationMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Element getOperationContainer(Element element, List<String> operationList, String electedTag) {
        for (Element childElement : element.getAllElements(electedTag)) {
            while (childElement.getParentElement() != null) {
                List<String> ccWords = CamelCaseFilter.getCamelCaseWords(childElement.getContent().toString());
                ccWords.retainAll(operationList);
                if (ccWords.isEmpty() || (ccWords.size() > 1 && childElement.getName().equals(electedTag))) {
                    break;
                }
                Element parent = childElement.getParentElement();
                ccWords = CamelCaseFilter.getCamelCaseWords(parent.getContent().toString());
                ccWords.retainAll(operationList);
                if (ccWords.isEmpty()) {
                    break;
                }
                HashSet hs = new HashSet();
                hs.addAll(ccWords);
                ccWords.clear();
                ccWords.addAll(hs);
                if (ccWords.size() < operationList.size()-1) {
                    childElement = parent;
                } else {
                    return parent;
                }
            }
        }
        return element;
//        for (Element childElement : element.getAllElements()) {
//            if (childElement.getDepth() == element.getDepth()+1) {
//                List<String> ccWords = CamelCaseFilter.getCamelCaseWords(childElement.getContent().toString());
//                ccWords.retainAll(operationList);
//                if (ccWords.size() >= operationList.size()) {
//                    return getOperationContainer(childElement, operationList, electedTag);
//                }
//            }
//        }
//        return element;
    }

    public static Element getOperationContainer2(Element element, List<String> operationList, String electedTag) {
        for (Element childElement : element.getAllElements()) {
            if (childElement.getDepth() == element.getDepth() + 1) {
                List<String> ccWords = CamelCaseFilter.getCamelCaseWords(childElement.getContent().toString());
                ccWords.retainAll(operationList);
                if (ccWords.size() >= operationList.size()) {
                    return getOperationContainer2(childElement, operationList, electedTag);
                }
            }
        }
        return element;
    }
}
