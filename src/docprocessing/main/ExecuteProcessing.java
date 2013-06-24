/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.main;

import docprocessing.rpc.soap.DictGenerator;
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
            BufferedReader br = new BufferedReader(new FileReader("src/wsdl-reg/service-uris.txt"));
            String wsdlUri;
            int i = 0;
            while ((wsdlUri = br.readLine()) != null) {
                i++;
                System.out.print(i + ". ");
                WSDLParsing.parseAndStoreService(new URL(wsdlUri), manager);
                System.out.println("\n----------------------------------------------------------------------------------------\n");
            }
            DictGenerator.generateDocDictionary(DictGenerator.DOC_PATH);
        } catch (Exception ex) {
            Logger.getLogger(StorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
