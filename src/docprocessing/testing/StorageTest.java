/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.testing;

import docprocessing.rpc.soap.WSDLParsing;
import docprocessing.rpc.soap.model.SoapService;
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
public class StorageTest {

    public static void main(String[] args) {
        try {
//            SoapService parsedService = WSDLParsing.parseService(new URL("http://ws.cdyne.com/psaddress/addresslookup.asmx?wsdl"));
//            SoapService parsedService = WSDLParsing.parseService(new URL("http://www.redtagmarket.com/notify/eBayNotification.asmx?wsdl"));
//            SoapService parsedService = WSDLProcessing.parseService(new URL("http://www.webservicex.com/globalweather.asmx?wsdl"));
//            CrudManager manager = new CrudManager();
//            manager.createSoapService(parsedService);
//            SoapOperation operation = manager.findSoapOperation(Long.valueOf(5));
//            System.out.println("Operation name: " + operation.getOperationName());
//            for(SoapDataElement dataElement : operation.getDataElements()) {
//                System.out.println("    Operation " + (dataElement.isDirection()?"input: ":"output: ") + dataElement.getDataElementName() + "(" + dataElement.getDataType() + ")");
//            } 

            CrudManager manager = new CrudManager();
            BufferedReader br = new BufferedReader(new FileReader("src/wsdl-reg/service-paths.txt"));
            StringBuilder builder = new StringBuilder();
            String wsdlUri;
            int i = 0;
            while ((wsdlUri = br.readLine()) != null) {
//            line = line.substring(line.indexOf(":") + 2);
                i++;
                System.out.print(i + ". ");
//            builder.append(line);
                SoapService parsedService = WSDLParsing.parseService(new URL(wsdlUri));
                manager.createSoapService(parsedService);
                System.out.println("\n----------------------------------------------------------------------------------------");
                System.out.println("----------------------------------------------------------------------------------------\n");
            }

        } catch (Exception ex) {
            Logger.getLogger(StorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
