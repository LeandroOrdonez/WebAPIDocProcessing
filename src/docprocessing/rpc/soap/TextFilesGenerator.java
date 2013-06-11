/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.soap;

import docprocessing.rpc.soap.model.SoapDataElement;
import docprocessing.rpc.soap.model.SoapOperation;
import docprocessing.util.CamelCaseFilter;
import docprocessing.util.DocCleaning;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
            String content = (operation.getOperationDocumentation().isEmpty() || operation.getOperationDocumentation().equalsIgnoreCase("This method requires a custom soap header set by the caller")) ? acronymResolver(CamelCaseFilter.splitCamelCase(operation.getOperationName())) + "\n"
                    : acronymResolver(DocCleaning.clean(operation.getOperationDocumentation()));

//            String content = acronymResolver(CamelCaseFilter.splitCamelCase(operation.getOperationName()))+ "\n";
//            content =  !operation.getOperationDocumentation().equalsIgnoreCase("This method requires a custom soap header set by the caller") ? content + acronymResolver(DocCleaning.clean(operation.getOperationDocumentation())) + "\n" : content;
//            content = operation.getSoapService().getServiceDocumentation().isEmpty() ? content + acronymResolver(CamelCaseFilter.splitCamelCase(operation.getSoapService().getServiceName())) : content + acronymResolver(DocCleaning.clean(operation.getSoapService().getServiceDocumentation()));

            /* Added for testing purposes */
//            for (SoapDataElement data : operation.getDataElements()) {
//                content += DocCleaning.spellCorrection(acronymResolver(CamelCaseFilter.splitCamelCase(data.getDataElementName()))) + "\n";
//            }
            /**
             * ***************************
             */
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
}
