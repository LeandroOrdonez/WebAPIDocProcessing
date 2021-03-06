/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.soap;

import docprocessing.rpc.soap.model.SoapComplexDataElement;
import docprocessing.rpc.soap.model.SoapDataElement;
import docprocessing.rpc.soap.model.SoapOperation;
import docprocessing.util.CamelCaseFilter;
import docprocessing.util.DocCleaning;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leandro Ordonez
 */
public class TextFilesGenerator {

    public static String DOC_PATH = "src/wsdl-reg/text-files/doc/";
    public static String DATA_PATH = "src/wsdl-reg/text-files/data/";
    public static String INPUT_PATH = "src/wsdl-reg/text-files/input/";
    public static String OUTPUT_PATH = "src/wsdl-reg/text-files/output/";
    public static Properties ACRONYMS;

    public static void init() {
        if (ACRONYMS == null) {
            ACRONYMS = new Properties();
            try {
                ACRONYMS.load(TextFilesGenerator.class.getResourceAsStream("/docprocessing/util/acronyms.properties"));
            } catch (IOException ex) {
                ex.printStackTrace();
                Logger.getLogger(TextFilesGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void generateDocumentationFile(SoapOperation operation) {
        FileOutputStream out = null;
        try {
            String fileName = DOC_PATH + operation.getId() + ".txt";
            File f = new File(fileName);
            f.createNewFile();
            // Text file structure 1: 1-line: Operation Name OR Operation Documentation
//            String content = (operation.getOperationDocumentation().isEmpty() || operation.getOperationDocumentation().equalsIgnoreCase("This method requires a custom soap header set by the caller")) ? acronymResolver(CamelCaseFilter.splitCamelCase(operation.getOperationName())) + "\n"
//                    : acronymResolver(DocCleaning.clean(operation.getOperationDocumentation()));

            // Text file structure 2: 1-line: Operation Name
            //                        2-line: Operation Documentation (when available) 
            //                        3-line: Service Documentation (when available) OR Service Name
//            String content = acronymResolver(CamelCaseFilter.splitCamelCase(operation.getOperationName())) + "\n";
//            content = !operation.getOperationDocumentation().equalsIgnoreCase("This method requires a custom soap header set by the caller") ? content + acronymResolver(DocCleaning.clean(operation.getOperationDocumentation())) + "\n" : content;
//            content = operation.getSoapService().getServiceDocumentation().isEmpty() ? content + acronymResolver(CamelCaseFilter.splitCamelCase(operation.getSoapService().getServiceName())) : content + acronymResolver(DocCleaning.clean(operation.getSoapService().getServiceDocumentation()));
//            content = DocCleaning.spellCorrection(content);

            //** Added just for testing purposes */
            // Text file structure 3: 1-line: Operation Name OR Operation Documentation               
            //                        n-line: Name of the n-data element of the operation
            String content = acronymResolver(CamelCaseFilter.splitCamelCase(operation.getOperationName())) + "\n";
            content = !operation.getOperationDocumentation().equalsIgnoreCase("This method requires a custom soap header set by the caller") ? content + acronymResolver(DocCleaning.clean(operation.getOperationDocumentation())) + "\n" : content;
            content = operation.getSoapService().getServiceDocumentation().isEmpty() ? content + acronymResolver(CamelCaseFilter.splitCamelCase(operation.getSoapService().getServiceName())) + "\n" : content + acronymResolver(DocCleaning.clean(operation.getSoapService().getServiceDocumentation())) + "\n";
            for (SoapDataElement data : operation.getDataElements()) {
                if (data instanceof SoapComplexDataElement) {
                    for (SoapDataElement element : getDataElements((SoapComplexDataElement)data)) {
                        content += acronymResolver(CamelCaseFilter.splitCamelCase(element.getDataElementName())) + "\n";
                    }
                } else {
                    content += acronymResolver(CamelCaseFilter.splitCamelCase(data.getDataElementName())) + "\n";
                }
            }
            content = DocCleaning.spellCorrection(content);
            //***********************************/
            out = new FileOutputStream(fileName);
            out.write(content.getBytes());
//            System.out.println(operation.getId() + ": " + content);

        } catch (IOException ex) {
            Logger.getLogger(TextFilesGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateDataTypesFile(SoapOperation operation) {
        FileOutputStream out = null;
        try {
            String fileName = DATA_PATH + operation.getId() + ".txt";
            File f = new File(fileName);
            f.createNewFile();
            String content = CamelCaseFilter.splitCamelCase(operation.getOperationName()) + "\n";
            for (SoapDataElement data : operation.getDataElements()) {
                content += CamelCaseFilter.splitCamelCase(data.getDataElementName()) + "\n";
            }
            out = new FileOutputStream(fileName);
            out.write(content.getBytes());

        } catch (IOException ex) {
            Logger.getLogger(TextFilesGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateInputTypesFile(SoapOperation operation) {
        FileOutputStream out = null;
        try {
            String fileName = INPUT_PATH + operation.getId() + ".txt";
            File f = new File(fileName);
            f.createNewFile();
            String content = CamelCaseFilter.splitCamelCase(operation.getOperationName()) + "\n";
            for (SoapDataElement data : operation.getDataElements()) {
                if (data.isDirection()) {
                    content += CamelCaseFilter.splitCamelCase(data.getDataElementName()) + "\n";
                }
            }
            out = new FileOutputStream(fileName);
            out.write(content.getBytes());

        } catch (IOException ex) {
            Logger.getLogger(TextFilesGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateOutputTypesFile(SoapOperation operation) {
        FileOutputStream out = null;
        try {
            String fileName = OUTPUT_PATH + operation.getId() + ".txt";
            File f = new File(fileName);
            f.createNewFile();
            String content = CamelCaseFilter.splitCamelCase(operation.getOperationName()) + "\n";
            for (SoapDataElement data : operation.getDataElements()) {
                if (!data.isDirection()) {
                    content += CamelCaseFilter.splitCamelCase(data.getDataElementName()) + "\n";
                }
            }
            out = new FileOutputStream(fileName);
            out.write(content.getBytes());

        } catch (IOException ex) {
            Logger.getLogger(TextFilesGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String acronymResolver(String documentation) {
        String result = "";
        for (String word : documentation.split(" ")) {
            if (ACRONYMS.containsKey(word)) {
                word = ACRONYMS.getProperty(word);
            }
            result += word + " ";
        }
        return result;
    }
    
    private static List<SoapDataElement> getDataElements(SoapComplexDataElement data) {
        List<SoapDataElement> dataElements = new ArrayList<>();
        for (SoapDataElement soapDataElement : data.getDataElements()) {
            if(soapDataElement instanceof SoapComplexDataElement) {
                dataElements.addAll(getDataElements((SoapComplexDataElement)soapDataElement));
            } else {
                dataElements.add(soapDataElement);
            }
        }
        return dataElements;
    }
}
