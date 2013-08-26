/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.main;

import docprocessing.rpc.soap.DictGenerator;
import docprocessing.rpc.soap.TextFilesGenerator;
import docprocessing.testing.*;
import docprocessing.rpc.soap.WSDLParsing;
import docprocessing.rpc.soap.persistence.CrudManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leandro Ordonez
 */
public class ExecuteProcessing {

    public static void main(String[] args) {
        try {
            CrudManager manager = new CrudManager();
//            BufferedReader br = new BufferedReader(new FileReader("src/wsdl-reg/service-uris.txt"));
            BufferedReader br = new BufferedReader((args.length != 0 && args[0] != null) ? new FileReader(args[0]) : new FileReader("src/wsdl-reg/service-uris.txt"));
            String wsdlUri;
            int i = 0;
            while ((wsdlUri = br.readLine()) != null) {
                i++;
                System.out.print(i + ". ");
                TextFilesGenerator.DOC_PATH = (args.length > 1 && args[1] != null) ? args[1] : TextFilesGenerator.DOC_PATH; //passing this path as argument to the .jar executable
                WSDLParsing.parseAndStoreService(new URL(wsdlUri), manager);
                //System.out.println("\n----------------------------------------------------------------------------------------\n");
            }
//            DictGenerator.generateDocDictionary(DictGenerator.DOC_PATH);
            DictGenerator.generateDocDictionary(TextFilesGenerator.DOC_PATH);
        } catch (Exception ex) {
            Logger.getLogger(StorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
