/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.http;

import docprocessing.util.CamelCaseFilter;
import docprocessing.util.POSTagger;
import docprocessing.util.Pair;
import docprocessing.util.suffixtree.AbstractSuffixTree;
import docprocessing.util.suffixtree.SimpleSuffixTree;
import docprocessing.util.suffixtree.SuffixTreeNode;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    public static final Map<String, String> encodedElements = Collections
            .unmodifiableMap(new HashMap<String, String>() {
        {
            put(HTMLElementName.HTML, "0");
            put(HTMLElementName.BODY, "1");
            put(HTMLElementName.DIV, "2");
            put(HTMLElementName.P, "3");
            put(HTMLElementName.TABLE, "4");
            put(HTMLElementName.THEAD, "5");
            put(HTMLElementName.TBODY, "6");
            put(HTMLElementName.TFOOT, "7");
            put(HTMLElementName.CAPTION, "8");
            put(HTMLElementName.TH, "9");
            put(HTMLElementName.TR, "A");
            put(HTMLElementName.TD, "B");
            put(HTMLElementName.UL, "C");
            put(HTMLElementName.OL, "D");
            put(HTMLElementName.LI, "E");
            put(HTMLElementName.DL, "F");
            put(HTMLElementName.DT, "G");
            put(HTMLElementName.DD, "H");
            put(HTMLElementName.FORM, "I");
            put(HTMLElementName.INPUT, "J");
            put(HTMLElementName.H1, "K");
            put(HTMLElementName.H2, "L");
            put(HTMLElementName.H3, "M");
            put(HTMLElementName.H4, "N");
            put(HTMLElementName.H5, "O");
            put(HTMLElementName.H6, "P");
        }
    });
    public static final Map<String, String> decodedElements = Collections
            .unmodifiableMap(new HashMap<String, String>() {
        {
            put("0", HTMLElementName.HTML);
            put("1", HTMLElementName.BODY);
            put("2", HTMLElementName.DIV);
            put("3", HTMLElementName.P);
            put("4", HTMLElementName.TABLE);
            put("5", HTMLElementName.THEAD);
            put("6", HTMLElementName.TBODY);
            put("7", HTMLElementName.TFOOT);
            put("8", HTMLElementName.CAPTION);
            put("9", HTMLElementName.TH);
            put("A", HTMLElementName.TR);
            put("B", HTMLElementName.TD);
            put("C", HTMLElementName.UL);
            put("D", HTMLElementName.OL);
            put("E", HTMLElementName.LI);
            put("F", HTMLElementName.DL);
            put("G", HTMLElementName.DT);
            put("H", HTMLElementName.DD);
            put("I", HTMLElementName.FORM);
            put("J", HTMLElementName.INPUT);
            put("K", HTMLElementName.H1);
            put("L", HTMLElementName.H2);
            put("M", HTMLElementName.H3);
            put("N", HTMLElementName.H4);
            put("O", HTMLElementName.H5);
            put("P", HTMLElementName.H6);
        }
    });

    public static void main(String[] args) {
        if (false) {
            System.getProperties().put("http.proxyHost", "proxy.unicauca.edu.co");
            System.getProperties().put("http.proxyPort", "3128");
        }
        List<String> sourcesUrls = Arrays.asList(
                "http://blogspam.net/api/1.0/",
//                "http://help.4shared.com/index.php/SOAP_API#addToFavorites");//,
                "http://developer.affili.net/desktopdefault.aspx/tabid-93",
                "http://www.benchmarkemail.com/API/Library",
                "http://www.holidaywebservice.com/ServicesAvailable_HolidayService2.aspx",
                "http://business.intuit.com/boorah/docs/syndication/integration.html",
                "http://aws.amazon.com/es/sqs/",
                "http://aws.amazon.com/es/simpledb/",
                "http://www.ebi.ac.uk/Tools/webservices/services/eb-eye");
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
//            Element operationContainer1 = getOperationContainer(cleanedHtml.getFirstElement(), operationList, electedTag);
////            Element operationContainer2 = getOperationContainer2(cleanedHtml.getFirstElement(), operationList, electedTag);
//
//            StringBuilder result = new StringBuilder();
//            for (StartTag st : operationContainer1.getAllStartTags()) {
////                result.append("<").append(st.getName()).append(">");
//                result.append(encodedElements.get(st.getName()));
//                System.out.print("<" + st.getName() + ">");
//            }
//            String operationContainerTest1 = result.toString();
//            System.out.println("\n\n" + operationContainerTest1 + "\n\n");
//
//            AbstractSuffixTree tree = new SimpleSuffixTree(operationContainerTest1);
//            String pattern;
//            for (SuffixTreeNode node : tree.bestNodes) {
////                System.out.println(node.printResult() + " repetitions=" + node.visits);
//                if (node.visits == operationList.size()) {
//                    pattern = node.printResult();
//                    System.out.println(translatePattern(pattern));
//                    break;
//                }
//            }
//            System.out.println("\n\n");
//            result = new StringBuilder();
//            for (StartTag st : operationContainer2.getAllStartTags()) {
//                result.append("<").append(st.getName()).append(">");
//                System.out.print("<" + st.getName() + ">");
//            }
//            String operationContainerTest2 = result.toString();
//
//            System.out.println("\nFor " + sourceUrlString + ": " + operationContainerTest1.equals(operationContainerTest2)
//                    + "\n-----------------------------------------------------------------------------------------------------------------------------"
//                    + "\n-----------------------------------------------------------------------------------------------------------------------------");
        }
    }

    /**
     *
     * @param pattern
     * @return
     */
    public static String translatePattern(String pattern) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < pattern.length(); i++) {
            result.append("<").append(decodedElements.get(String.valueOf(pattern.charAt(i)))).append(">");
        }
        return result.toString();
    }

    /**
     *
     * @param sourceUrlString
     * @return
     */
    public static OutputDocument cleanHTML(String sourceUrlString) {
        return removeElements(sourceUrlString, HTMLElementName.SCRIPT, HTMLElementName.HEAD, HTMLElementName.LINK, HTMLElementName.IMG, HTMLElementName.HR, HTMLElementName.BR, HTMLElementName.S, HTMLElementName.COLGROUP, HTMLElementName.NOSCRIPT, HTMLElementName.SOURCE, HTMLElementName.NOFRAMES, HTMLElementName.SELECT, HTMLElementName.STYLE, HTMLElementName.DATALIST, HTMLElementName.DEL);
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
            String htmlText = Util.getString(new InputStreamReader(sourceUrl.openStream())).replaceAll("(var\\s.*(;|\"))|(&lt;.*&gt;)", "");
//            String htmlText = Util.getString(new InputStreamReader(sourceUrl.openStream())).replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"");
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
            outputDocument.remove(source.getAllElements(StartTagType.SERVER_COMMON_COMMENT));
            outputDocument.remove(source.getAllElements(StartTagType.SERVER_COMMON_ESCAPED));
            String outputString = outputDocument.toString();
            source = new Source(outputString);
            for (StartTag st : source.getAllStartTags()) {
//                if (st.getName().equals("span")){// || st.getName().equals("acronym")) {
//                    System.out.println(st.getElement().toString());
//                    System.out.println(st.getElement().getTextExtractor().toString());
//                }
                if (st.getName().matches("em|a|b|i|acronym|blockquote|strong|code|sup|sub|small|big|pre|span|font|label|tt|legend|ins")) {
                    outputString = removeFormattingTag(st, outputString);
                    continue;
                }
                String cleanStartTag = getStartTagHTML(st).toString();
                outputString = outputString.replace(st.toString(), cleanStartTag);
            }
            source = new Source(outputString);
            outputDocument = new OutputDocument(source);
            outputDocument.remove(source.getAllElements(StartTagType.UNREGISTERED));
            outputDocument.remove(source.getAllElements(StartTagType.XML_DECLARATION));
            outputDocument.remove(source.getAllElements(StartTagType.XML_PROCESSING_INSTRUCTION));
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
     * @param startTag
     * @param outputString
     * @return
     */
    public static String removeFormattingTag(StartTag startTag, String outputString) {
        StartTag parentTag = startTag.getElement().getParentElement().getStartTag();
//        if (startTag.getName().equals("span") && startTag.getElement().getTextExtractor().toString().equals("getSharedDirItems")) {
//            System.out.println("STOOOOP!!");
//        }
        if (!(parentTag.getName().matches("em|a|b|i|acronym|blockquote|strong|code|sup|sub|small|big|pre|span|font|label|tt|legend|ins"))) {
            if (startTag.getName().equals("a")) {
//                String href = startTag.getAttributeValue("href");
//                if (href != null && href.equals("#getSharedDirItems")) {
//                    System.out.println("STOP!!");
//                    System.out.println("Starts with #: " + href.startsWith("#"));
//                    System.out.println(startTag.getElement().toString());
//                    System.out.println(startTag.getElement().getContent().toString());
//                }
                outputString = outputString.replaceFirst(Pattern.quote(startTag.getElement().toString()), ((startTag.getAttributeValue("href") != null) && startTag.getAttributeValue("href").startsWith("#")) ? "" : startTag.getElement().getContent().toString());
            } else {
                outputString = outputString.replaceFirst(Pattern.quote(startTag.getElement().toString()), startTag.getElement().getContent().toString());
//            if (outputString.indexOf(startTag.getElement().toString()) != -1) {
//                System.out.println("\nSomething went wrong!: " + startTag.getElement().toString());
//            } else {
//                System.out.println("\n" + startTag.getElement().toString() + "\n\nwas replaced with:\n\n" + startTag.getElement().getContent().toString());
//            }
            }
        } else {
//            outputString = removeFormattingTag(parentTag, outputString);
//            if (startTag.getName().equals("a")) {
//                outputString = outputString.replaceFirst(Pattern.quote(startTag.getElement().toString()), ((startTag.getAttributeValue("href") != null) && startTag.getAttributeValue("href").startsWith("#")) ? "" : startTag.getElement().getContent().toString());
//            } else {
//                outputString = outputString.replaceFirst(Pattern.quote(startTag.getElement().toString()), startTag.getElement().getContent().toString());
//            }
            outputString = removeFormattingTag(parentTag, outputString);
            if (!(parentTag.getName().equals("a") && parentTag.getAttributeValue("href") != null && parentTag.getAttributeValue("href").startsWith("#"))) {
                if (startTag.getName().equals("a")) {
                    outputString = outputString.replaceFirst(Pattern.quote(startTag.getElement().toString()), ((startTag.getAttributeValue("href") != null) && startTag.getAttributeValue("href").startsWith("#")) ? "" : startTag.getElement().getContent().toString());
                } else {
                    outputString = outputString.replaceFirst(Pattern.quote(startTag.getElement().toString()), startTag.getElement().getContent().toString());
                }
            }
//            outputString = outputString.replaceFirst(Pattern.quote(startTag.getElement().toString()), startTag.getElement().getContent().toString());
        }
        return outputString;
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
//        Pattern p = Pattern.compile("([a-zA-Z_0-9]+(_VB))(.*)([a-zA-Z_0-9]+(_NN))");//, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
//        Matcher m = p.matcher(tagString);
////        return tagString.matches("([a-zA-Z_0-9]+(_VB))(.*)([a-zA-Z_0-9]+(_NN))");
//        return m.find();
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
        List<String> camelCaseWords = CamelCaseFilter.getCamelCaseWords(htmlSource.toString());
        HashMap<String, List<String>> tagMap = new HashMap<>();
        for (Pair p : indexedCamelCaseWords) {
            String ccWord = p.getRight().toString();
            Element enclosingElement = htmlSource.getEnclosingElement((int) p.getLeft());
//            if (CamelCaseFilter.getCamelCaseWords(enclosingElement.getContent().toString()).size() > 1) {
//                continue;
//            }
//            Element parentElement = enclosingElement.getParentElement();
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
            for (String operation : operationMap.keySet()) {
                System.out.println("\nOperation " + operation + ":");
                System.out.print("\t" + operationMap.get(operation) + "\n");
            }
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
                if (ccWords.size() < operationList.size() - 1) {
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
