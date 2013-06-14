/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Leandro Ordonez
 */
public class DirectoryExplorer {

    static int depthLevel = -1;
    public static ArrayList<String> listFiles = new ArrayList<String>();

    public static void listPath(File path) {
        File files[];
        depthLevel++;

        files = path.listFiles();

        Arrays.sort(files);
        for (int i = 0, n = files.length; i < n; i++) {
//            for (int indent = 0; indent < depthLevel; indent++) {
//                System.out.print("  ");
//            }
//            System.out.println(files[i].toString());
//            if (files[i].toString().endsWith(".wsdl")) {
                //System.out.println(files[i].toString());
                listFiles.add(files[i].toString());
//            }
            if (files[i].isDirectory()) {

                listPath(files[i]);
            }
        }
        depthLevel--;
    }

    public static void main(String args[]) throws IOException {
        DirectoryExplorer explorer = new DirectoryExplorer();
        File f = new File("src/wsdl-reg/text-files/doc/");
        explorer.listPath(f);
        ArrayList<String> list = explorer.listFiles;
        int c = 1;
        for (String string : list) {
            System.out.println("File " + c + ": " + string);
            c++;
        }
        File[] listFiles = f.listFiles();
        for (File file: listFiles){
            System.out.println("Path: " + file.getPath());
        }
        
        


//        BufferedReader br = new BufferedReader(new FileReader("src/wsdl-reg/service-paths.txt"));
//        StringBuilder builder = new StringBuilder();
//        String wsdlUri;
//        int i = 0;
//        List<SoapService> services = new ArrayList<>();
//        while ((wsdlUri = br.readLine()) != null) {
////            line = line.substring(line.indexOf(":") + 2);
//            i++;
//            System.out.print(i + ". ");
////            builder.append(line);
//            SoapService parsedService = WSDLParsing.parseService(new URL(wsdlUri));
//            services.add(parsedService);
//            System.out.println("\n----------------------------------------------------------------------------------------");
//            System.out.println("----------------------------------------------------------------------------------------\n");
//        }
//        
//        for (SoapService soapService : services) {
//            for (SoapOperation operation : soapService.getOperations()) {
//                System.out.println(operation.getId() + " -> " + CamelCaseFilter.splitCamelCase(operation.getOperationName()));
//            }
//        }

        
//        br.close();
//        BufferedWriter out = new BufferedWriter(new FileWriter("src/wsdl-reg/service-paths.txt"));
//        out.write(builder.toString());
//        out.close();
    }
}
