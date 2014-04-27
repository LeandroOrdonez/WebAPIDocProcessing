/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.main;

import docprocessing.rpc.soap.DictGenerator;
import docprocessing.rpc.soap.TextFilesGenerator;
import docprocessing.testing.*;
import docprocessing.rpc.soap.WSDLParsing;
import docprocessing.persistence.CrudManager;
import docprocessing.rpc.soap.model.SoapService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
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
            File file = new File("/home/leandro/development/Topicalizer Backups/2014.04.26/service-timer.csv");
            if (!file.exists()) {
                file.createNewFile();
            }
            long t0 = System.currentTimeMillis();
            while ((wsdlUri = br.readLine()) != null) {
                ArrayList<String> lines = new ArrayList<String>();
                String line = null;
                FileReader fr = new FileReader(file);
                BufferedReader timerbr = new BufferedReader(fr);
                while ((line = timerbr.readLine()) != null) {
                    lines.add(line);
                }
                // if file doesnt exists, then create it
                fr.close();
                timerbr.close();
                i++;
                System.out.print(i + ". ");
                TextFilesGenerator.DOC_PATH = (args.length > 1 && args[1] != null) ? args[1] : TextFilesGenerator.DOC_PATH; //passing this path as argument to the .jar executable
                SoapService service = WSDLParsing.parseAndStoreService(new URL(wsdlUri), manager);

                if (service != null) {
                    String newLine = "Service," + i + ",Operations," + service.getOperations().size() + "," + (System.currentTimeMillis() - t0);
                    lines.add(newLine);

                    FileWriter fw = new FileWriter(file.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);
                    for (String l : lines) {
                        bw.write(l);
                        bw.newLine();
                    }
                    bw.close();
                }
                //System.out.println("\n----------------------------------------------------------------------------------------\n");
            }
//            DictGenerator.generateDocDictionary(DictGenerator.DOC_PATH);
            DictGenerator.generateDocDictionary(TextFilesGenerator.DOC_PATH);
            br.close();
        } catch (Exception ex) {
            Logger.getLogger(StorageTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
