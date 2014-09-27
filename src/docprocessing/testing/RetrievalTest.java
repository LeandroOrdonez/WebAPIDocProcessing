/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.testing;

import docprocessing.rpc.soap.TextFilesGenerator;
import docprocessing.rpc.soap.model.SoapOperation;
import docprocessing.persistence.CrudManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leandro Ordonez
 */
public class RetrievalTest {

    public static void main(String[] args) {
        try {
            CrudManager manager = new CrudManager();
            //            List<SoapOperation> findSoapOperations = manager.findSoapOperations();
            //            for (SoapOperation operation : findSoapOperations) {
            //                System.out.println(operation.getId() + " -> " + CamelCaseFilter.splitCamelCase(operation.getOperationName()));
            //            }

//            SoapOperation operation = manager.findSoapOperation(Long.valueOf(1088));
//            for (SoapDataElement data : operation.getDataElements()) {
//                if (data.isDirection()) {
//                    System.out.println(data.getDataElementName() + " (" + (data instanceof SoapComplexDataElement) + ")");
//                }
//            }

            List<SoapOperation> findSoapOperations = manager.findSoapOperations();
            TextFilesGenerator.init();
//            System.out.println("Number of operations: " + findSoapOperations.size());
            for (SoapOperation operation : findSoapOperations) {
//                System.out.println("    operationID: " + operation.getId());
//                if (operation.getId() >= 714) {
                    TextFilesGenerator.generateDocumentationFile(operation);
//                }
//                TextFilesGenerator.generateDataTypesFile(operation);
//                TextFilesGenerator.generateInputTypesFile(operation);
//                TextFilesGenerator.generateOutputTypesFile(operation);
            }
//            List<SoapOperation> findSoapOperations = manager.findSoapOperations();
//            for (SoapOperation operation : findSoapOperations) {
//                System.out.println("\nOperation Documentation (Before cleaning): \n\n" + operation.getOperationDocumentation());
//                System.out.println("\nOperation Documentation (After cleaning): \n\n" + DocCleaning.clean(operation.getOperationDocumentation()));
//            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(RetrievalTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
