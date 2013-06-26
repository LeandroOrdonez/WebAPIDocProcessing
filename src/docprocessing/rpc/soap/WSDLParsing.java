/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.soap;

import docprocessing.rpc.soap.model.SoapComplexDataElement;
import docprocessing.rpc.soap.model.SoapDataElement;
import docprocessing.rpc.soap.model.SoapOperation;
import docprocessing.rpc.soap.model.SoapService;
import docprocessing.rpc.soap.persistence.CrudManager;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.ow2.easywsdl.schema.api.*;
import org.ow2.easywsdl.schema.impl.ComplexTypeImpl;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.*;

/**
 *
 * @author Leandro Ordonez
 */
public class WSDLParsing {

    static int depthLevel = -1;
    static HashMap<QName, List<Element>> schema = new HashMap<>();
    static List<QName> bucleControl;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
//            URL serviceA = new URL("http://www.redtagmarket.com/notify/eBayNotification.asmx?wsdl");
            URL serviceA = new URL("http://s3.amazonaws.com/doc/2006-03-01/AmazonS3.wsdl");
            WSDLParsing processing = new WSDLParsing();
            processing.parseService(serviceA);

        } catch (MalformedURLException ex) {
            Logger.getLogger(WSDLParsing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static SoapService parseService(URL serviceURI) {
        try {
            // Read a WSDL 1.1 or 2.0
            WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
            Description desc = reader.read(serviceURI);
//            Description desc = reader.read(new URL("http://www.redtagmarket.com/notify/eBayNotification.asmx?wsdl"));
//            Description desc = reader.read(new URL("http://www.webservicex.com/globalweather.asmx?wsdl"));
//            Description desc = reader.read(new URL("http://termsandconditions.nestleusa.com/readtermsandconditionsservice.asmx?wsdl"));
//            Description desc = reader.read(new URL("http://ilion-strategies.com/ws/validphonemx.asmx?wsdl"));
//            Description desc = reader.read(new URL("http://ws.cdyne.com/psaddress/addresslookup.asmx?wsdl"));

//            // Write a WSDL 1.1 or 2.0 (depend of desc version)
////            Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(desc);
//            // Create a WSDL 1.1 or 2.0
////            Description desc11 = WSDLFactory.newInstance().newDescription(WSDLVersionConstants.WSDL11);
////            Description desc20 = WSDLFactory.newInstance().newDescription(WSDLVersionConstants.WSDL20);
            Service service = desc.getServices().get(0);
            System.out.println("Parsing Service: " + service.getEndpoints().get(0).getAddress() + "?wsdl");
            SoapService soapService = new SoapService();
            soapService.setServiceURI(service.getEndpoints().get(0).getAddress() + "?wsdl");
            soapService.setServiceName(service.getQName().getLocalPart());
            Documentation serviceDoc = service.getDocumentation();
            soapService.setServiceDocumentation(serviceDoc != null ? serviceDoc.getContent() : "");
            List<Endpoint> endpoints = service.getEndpoints();
            List<SoapOperation> soapOperations = new ArrayList<>();
            for (Endpoint endpoint : endpoints) {
                if (!(endpoint.getName().contains("Http") || endpoint.getName().contains("Soap12"))) {
                    Binding binding = endpoint.getBinding();
////                System.out.println("Loaded Binding: " + binding);
                    InterfaceType aInterface = binding.getInterface();
////                System.out.println("Loaded Interface: " + aInterface);
                    List<Operation> operations = aInterface.getOperations();
                    int progress = 0;
                    int progressBarLength = 40;
                    String mark = "";
                    String left = StringUtils.repeat(" ", progressBarLength);
                    for (Operation operation : operations) {
//                        System.out.println("\tParsing " + progress + "/" + operations.size() + " operations...");
                        progress++;
                        int current = (int) Math.round(((progress * 1.0) / operations.size()) * progressBarLength);
                        mark = StringUtils.repeat("=", current);
                        left = StringUtils.repeat(" ", progressBarLength - current);
                        System.out.print("\t Processing: [" + mark + left + "] (" + progress + "/" + operations.size() + ") Operations\r");
                        SoapOperation soapOperation = new SoapOperation();
                        soapOperation.setOperationName(operation.getQName().getLocalPart());
                        soapOperation.setPattern(operation.getPattern().name());
                        Documentation operationDoc = operation.getDocumentation();
                        soapOperation.setOperationDocumentation(operationDoc != null ? operationDoc.getContent() : "");
                        soapOperation.setSoapService(soapService);
                        Input input = operation.getInput();
//                        System.out.println("\nLoaded Input Message: " + input.getMessageName().getLocalPart());
//                        if(input.getMessageName().getLocalPart().equals("addRequest")){
//                            System.out.println("STOP HERE!");
//                        }
                        List<Part> inputParts = input.getParts();
                        List<SoapDataElement> operationData = new ArrayList<>();
                        for (Part part : inputParts) {
                            bucleControl = new ArrayList<>();
                            Element inputElement = part.getElement();
                            if (inputElement == null) {
                                SoapDataElement dataElement = new SoapDataElement();
                                dataElement.setDataElementName(part.getPartQName().getLocalPart());
                                dataElement.setDirection(true);
                                dataElement.setSoapComplexDataElement(null);
                                dataElement.setDataType(part.getType().getQName() != null ? part.getType().getQName().getLocalPart() : "N/A");
                                dataElement.setSoapOperation(soapOperation);
                                operationData.add(dataElement);
                                depthLevel++;
//                                for (int indent = 0; indent < depthLevel; indent++) {
//                                    System.out.print("  ");
//                                }
//                                System.out.println("Element: " + part.getPartQName().getLocalPart() + " (" + dataElement.getDataType() + ")");
                                depthLevel--;
                                continue;
                            }
                            if (inputElement.getType() instanceof ComplexTypeImpl) {
                                SoapComplexDataElement dataElement = new SoapComplexDataElement();
                                dataElement.setDataElementName(inputElement.getQName().getLocalPart());
                                dataElement.setDirection(true);
                                dataElement.setSoapComplexDataElement(null);
                                dataElement.setSoapOperation(soapOperation);
                                dataElement.setDataElements(elementProcessing(inputElement, dataElement));
                                operationData.add(dataElement);
                            } else {
                                SoapDataElement dataElement = new SoapDataElement();
                                dataElement.setDataElementName(inputElement.getQName().getLocalPart());
                                dataElement.setDirection(true);
                                dataElement.setSoapComplexDataElement(null);
                                dataElement.setDataType(inputElement.getType().getQName() != null ? inputElement.getType().getQName().getLocalPart() : "N/A");
                                dataElement.setSoapOperation(soapOperation);
                                operationData.add(dataElement);
                                depthLevel++;
//                                for (int indent = 0; indent < depthLevel; indent++) {
//                                    System.out.print("  ");
//                                }
//                                System.out.println("Element: " + inputElement.getQName().getLocalPart() + " (" + dataElement.getDataType() + ")");
                                depthLevel--;
                            }

                        }
                        Output output = operation.getOutput();
////                        System.out.println("\nLoaded Output Message: " + output.getMessageName().getLocalPart());
////                        if(output.getMessageName().getLocalPart().equals("OMS_GetUserlistOut")){
////                            System.out.println("STOP HERE!");
////                        }
                        List<Part> outputParts = output.getParts();
                        for (Part part : outputParts) {
                            bucleControl = new ArrayList<>();
                            Element outputElement = part.getElement();
                            if (outputElement == null) {
                                SoapDataElement dataElement = new SoapDataElement();
                                dataElement.setDataElementName(part.getPartQName().getLocalPart());
                                dataElement.setDirection(false);
                                dataElement.setSoapComplexDataElement(null);
                                dataElement.setDataType(part.getType() != null ? part.getType().getQName().getLocalPart() : "N/A");
                                dataElement.setSoapOperation(soapOperation);
                                operationData.add(dataElement);
                                depthLevel++;
//                                for (int indent = 0; indent < depthLevel; indent++) {
//                                    System.out.print("  ");
//                                }
//                                System.out.println("Element: " + part.getPartQName().getLocalPart() + " (" + dataElement.getDataType() + ")");
                                depthLevel--;
                                continue;
                            }
                            if (outputElement.getType() instanceof ComplexTypeImpl) {
                                SoapComplexDataElement dataElement = new SoapComplexDataElement();
                                dataElement.setDataElementName(outputElement.getQName().getLocalPart());
                                dataElement.setDirection(false);
                                dataElement.setSoapComplexDataElement(null);
                                dataElement.setSoapOperation(soapOperation);
                                dataElement.setDataElements(elementProcessing(outputElement, dataElement));
                                operationData.add(dataElement);
                            } else {
                                SoapDataElement dataElement = new SoapDataElement();
                                dataElement.setDataElementName(outputElement.getQName().getLocalPart());
                                dataElement.setDirection(false);
                                dataElement.setSoapComplexDataElement(null);
                                dataElement.setDataType(outputElement.getType().getQName() != null ? outputElement.getType().getQName().getLocalPart() : "N/A");
                                dataElement.setSoapOperation(soapOperation);
                                operationData.add(dataElement);
                                depthLevel++;
//                                for (int indent = 0; indent < depthLevel; indent++) {
//                                    System.out.print("  ");
//                                }
//                                System.out.println("Element: " + outputElement.getQName().getLocalPart() + " (" + dataElement.getDataType() + ")");
                                depthLevel--;
                            }
                        }
                        soapOperation.setDataElements(operationData);
                        soapOperations.add(soapOperation);
                    }
                    System.out.print("\t Processing: [" + mark + left + "] Done!\n");
                }
            }
            soapService.setOperations(soapOperations);
////            System.out.println("\nSchema: " + schema);
            return soapService;

        } catch (XmlException | IOException | URISyntaxException ex) {
            Logger.getLogger(WSDLParsing.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }

    }

    public static SoapService parseAndStoreService(URL serviceURI, CrudManager manager) {
        try {
            TextFilesGenerator.init(); //initialize the text file generator.
            WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
            Description desc = reader.read(serviceURI);
            Service service = desc.getServices().get(0);
            System.out.println("Parsing Service: " + service.getEndpoints().get(0).getAddress() + "?wsdl                                  \n");
            SoapService soapService = new SoapService();
            soapService.setServiceURI(service.getEndpoints().get(0).getAddress() + "?wsdl");
            soapService.setServiceName(service.getQName().getLocalPart());
            Documentation serviceDoc = service.getDocumentation();
            soapService.setServiceDocumentation(serviceDoc != null ? serviceDoc.getContent() : "");
            manager.createSoapService(soapService);
            List<Endpoint> endpoints = service.getEndpoints();
            List<SoapOperation> soapOperations = new ArrayList<>();
            for (Endpoint endpoint : endpoints) {
                if (!(endpoint.getName().contains("Http") || endpoint.getName().contains("Soap12"))) {
                    Binding binding = endpoint.getBinding();
                    InterfaceType aInterface = binding.getInterface();
                    List<Operation> operations = aInterface.getOperations();
                    int progress = 0;
                    int progressBarLength = 40;
                    String mark = "";
                    String left = StringUtils.repeat(" ", progressBarLength);
                    for (Operation operation : operations) {
//                        System.out.println("\tParsing " + progress + "/" + operations.size() + " operations...");
                        progress++;
                        int current = (int) Math.round(((progress * 1.0) / operations.size()) * progressBarLength);
                        mark = StringUtils.repeat("=", current);
                        left = StringUtils.repeat(" ", progressBarLength - current);
                        System.out.print("\t Processing: [" + mark + left + "] (" + progress + "/" + operations.size() + ") Operations\r");
                        SoapOperation soapOperation = new SoapOperation();
                        soapOperation.setOperationName(operation.getQName().getLocalPart());
                        soapOperation.setPattern(operation.getPattern().name());
                        Documentation operationDoc = operation.getDocumentation();
                        soapOperation.setOperationDocumentation(operationDoc != null ? operationDoc.getContent() : "");
                        soapOperation.setSoapService(soapService);
                        Input input = operation.getInput();
                        List<Part> inputParts = input.getParts();
                        List<SoapDataElement> operationData = new ArrayList<>();
                        for (Part part : inputParts) {
                            bucleControl = new ArrayList<>();
                            Element inputElement = part.getElement();
                            if (inputElement == null) {
                                SoapDataElement dataElement = new SoapDataElement();
                                dataElement.setDataElementName(part.getPartQName().getLocalPart());
                                dataElement.setDirection(true);
                                dataElement.setSoapComplexDataElement(null);
                                dataElement.setDataType(part.getType().getQName() != null ? part.getType().getQName().getLocalPart() : "N/A");
                                dataElement.setSoapOperation(soapOperation);
                                operationData.add(dataElement);
                                depthLevel++;
//                                for (int indent = 0; indent < depthLevel; indent++) {
//                                    System.out.print("  ");
//                                }
//                                System.out.println("Element: " + part.getPartQName().getLocalPart() + " (" + dataElement.getDataType() + ")");
                                depthLevel--;
                                continue;
                            }
                            if (inputElement.getType() instanceof ComplexTypeImpl) {
                                SoapComplexDataElement dataElement = new SoapComplexDataElement();
                                dataElement.setDataElementName(inputElement.getQName().getLocalPart());
                                dataElement.setDirection(true);
                                dataElement.setSoapComplexDataElement(null);
                                dataElement.setSoapOperation(soapOperation);
                                dataElement.setDataElements(elementProcessing(inputElement, dataElement));
                                operationData.add(dataElement);
                            } else {
                                SoapDataElement dataElement = new SoapDataElement();
                                dataElement.setDataElementName(inputElement.getQName().getLocalPart());
                                dataElement.setDirection(true);
                                dataElement.setSoapComplexDataElement(null);
                                dataElement.setDataType(inputElement.getType().getQName() != null ? inputElement.getType().getQName().getLocalPart() : "N/A");
                                dataElement.setSoapOperation(soapOperation);
                                operationData.add(dataElement);
                                depthLevel++;
//                                for (int indent = 0; indent < depthLevel; indent++) {
//                                    System.out.print("  ");
//                                }
//                                System.out.println("Element: " + inputElement.getQName().getLocalPart() + " (" + dataElement.getDataType() + ")");
                                depthLevel--;
                            }

                        }
                        Output output = operation.getOutput();
                        List<Part> outputParts = output.getParts();
                        for (Part part : outputParts) {
                            bucleControl = new ArrayList<>();
                            Element outputElement = part.getElement();
                            if (outputElement == null) {
                                SoapDataElement dataElement = new SoapDataElement();
                                dataElement.setDataElementName(part.getPartQName().getLocalPart());
                                dataElement.setDirection(false);
                                dataElement.setSoapComplexDataElement(null);
                                dataElement.setDataType(part.getType() != null ? part.getType().getQName().getLocalPart() : "N/A");
                                dataElement.setSoapOperation(soapOperation);
                                operationData.add(dataElement);
                                depthLevel++;
//                                for (int indent = 0; indent < depthLevel; indent++) {
//                                    System.out.print("  ");
//                                }
//                                System.out.println("Element: " + part.getPartQName().getLocalPart() + " (" + dataElement.getDataType() + ")");
                                depthLevel--;
                                continue;
                            }
                            if (outputElement.getType() instanceof ComplexTypeImpl) {
                                SoapComplexDataElement dataElement = new SoapComplexDataElement();
                                dataElement.setDataElementName(outputElement.getQName().getLocalPart());
                                dataElement.setDirection(false);
                                dataElement.setSoapComplexDataElement(null);
                                dataElement.setSoapOperation(soapOperation);
                                dataElement.setDataElements(elementProcessing(outputElement, dataElement));
                                operationData.add(dataElement);
                            } else {
                                SoapDataElement dataElement = new SoapDataElement();
                                dataElement.setDataElementName(outputElement.getQName().getLocalPart());
                                dataElement.setDirection(false);
                                dataElement.setSoapComplexDataElement(null);
                                dataElement.setDataType(outputElement.getType().getQName() != null ? outputElement.getType().getQName().getLocalPart() : "N/A");
                                dataElement.setSoapOperation(soapOperation);
                                operationData.add(dataElement);
                                depthLevel++;
//                                for (int indent = 0; indent < depthLevel; indent++) {
//                                    System.out.print("  ");
//                                }
//                                System.out.println("Element: " + outputElement.getQName().getLocalPart() + " (" + dataElement.getDataType() + ")");
                                depthLevel--;
                            }
                        }
                        soapOperation.setDataElements(operationData);
//                        long operationId = manager.getSoapOperationCount() + 1;
//                        soapOperation.setId(operationId);
                        manager.createSoapOperation(soapOperation);
                        TextFilesGenerator.generateDocumentationFile(soapOperation);
                        soapOperations.add(soapOperation);
                    }
                }
            }
            soapService.setOperations(soapOperations);
            manager.editSoapService(soapService);
////            System.out.println("\nSchema: " + schema);
            return soapService;

        } catch (XmlException | IOException | URISyntaxException ex) {
            Logger.getLogger(WSDLParsing.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }
    }

    public static List<SoapDataElement> elementProcessing(Element e, SoapComplexDataElement dataElement) throws XmlException {
        depthLevel++;
        List<SoapDataElement> items = new ArrayList<>();
        Type type = e.getType();
        String typeName;
        if (((type != null && type.getQName() != null) ? schema.containsKey(type.getQName()) : false) || schema.containsKey(e.getQName())) {
////            depthLevel++;
////                List<SoapDataElement> dataElements = new ArrayList<>();
//            for (int indent = 0; indent < depthLevel; indent++) {
//                System.out.print("  ");
//            }
            typeName = type.getQName() != null ? type.getQName().getLocalPart() : "N/A";
//            System.out.println("Element: [" + dataElement.getDataElementName() + "]" + e.getQName().getLocalPart() + " (" + typeName + ")");
            if (!(bucleControl.contains(e.getQName()) || bucleControl.contains((type.getQName() != null) ? type.getQName() : e.getQName()))) {
                bucleControl.add((type != null) ? type.getQName() : e.getQName());
////                System.out.println(schema);
                List<Element> iterableElements = (type.getQName() != null) ? schema.get(type.getQName()) : schema.get(e.getQName());
                for (Element element : (iterableElements != null ? iterableElements : schema.get(e.getQName()))) {
                    SoapDataElement data;
                    if (element.getType() instanceof ComplexTypeImpl && (((ComplexTypeImpl) element.getType()).getSimpleContent() == null)) {
                        data = new SoapComplexDataElement();
                        data.setDataElementName(element.getQName().getLocalPart());
                        data.setDirection(dataElement.isDirection());
                        data.setSoapComplexDataElement(dataElement);
                        data.setSoapOperation(dataElement.getSoapOperation());
                        depthLevel++;
//                        for (int indent = 0; indent < depthLevel; indent++) {
//                            System.out.print("  ");
//                        }
//                        System.out.println("Element: [" + dataElement.getDataElementName() + "]" + element.getQName().getLocalPart() + " (" + element.getType().getQName().getLocalPart() + ")");
                        depthLevel--;
                        ((SoapComplexDataElement) data).setDataElements(elementProcessing(element, (SoapComplexDataElement) data));
                        items.add(data);
                    } else {
                        data = new SoapDataElement();
                        data.setDataElementName(element.getQName().getLocalPart());
                        data.setDirection(dataElement.isDirection());
                        data.setSoapComplexDataElement(dataElement);
                        data.setDataType(element.getType() != null ? (element.getType().getQName() != null ? element.getType().getQName().getLocalPart() : "N/A") : "N/A");
                        data.setSoapOperation(dataElement.getSoapOperation());
                        depthLevel++;
//                        for (int indent = 0; indent < depthLevel; indent++) {
//                            System.out.print("  ");
//                        }
//                        System.out.println("Element: [" + dataElement.getDataElementName() + "]" + element.getQName().getLocalPart() + " (" + data.getDataType() + ")");
                        depthLevel--;
                        items.add(data);
                    }
                }

            }
            dataElement.setDataType(typeName);
            depthLevel--;
            return items;
        } else {

            typeName = (type != null && type.getQName() != null) ? type.getQName().getLocalPart() : "N/A";
        }
//        for (int indent = 0; indent < depthLevel; indent++) {
//            System.out.print("  ");
//        }
//        System.out.println("Element: [" + (dataElement != null && dataElement.getSoapComplexDataElement() != null ? dataElement.getSoapComplexDataElement().getDataElementName() : "") + "]" + e.getQName().getLocalPart() + " (" + typeName + ")");
        dataElement.setDataType(typeName);
        if (!(bucleControl.contains(e.getQName()) || bucleControl.contains((type != null) ? type.getQName() : e.getQName()))) {
            bucleControl.add((type != null) ? (type.getQName() != null ? type.getQName() : e.getQName()) : e.getQName());
            ComplexContent complexContent;
            SimpleContent simpleContent;
            Sequence sequence;
            List<Element> innerElements = new ArrayList<>();
            if (type instanceof ComplexTypeImpl) {
                if (((ComplexTypeImpl) type).getSequence() != null) {
                    sequence = ((ComplexTypeImpl) type).getSequence();
                    innerElements = sequence.getElements();
                } else if (((ComplexTypeImpl) type).getComplexContent() != null) {
                    complexContent = ((ComplexTypeImpl) type).getComplexContent();
                    sequence = (complexContent != null) ? complexContent.getExtension().getSequence() : null;
                    innerElements = sequence != null ? sequence.getElements() : innerElements;
                } else if (((ComplexTypeImpl) type).getSimpleContent() != null) {
                    depthLevel--;
////                    System.out.println("<!-- SimpleContent Detected --> " + type + " (" + ((ComplexTypeImpl) type).getSimpleContent().getExtension().getOtherAttributes().toString() + ")");
                    simpleContent = ((ComplexTypeImpl) type).getSimpleContent();
////                    System.out.println("SimpleContent: " + simpleContent.getExtension().getBase().getQName().getLocalPart());
                    return items;
                } else {
                    depthLevel--; //provisional
////                    System.out.println("<!-- Provisional -->");
                    return items;
                }

            } else {
                depthLevel--;
                return items;
            }
            if (!innerElements.isEmpty()) {
                for (Element innerElement : innerElements) {
                    SoapDataElement data;
                    if (innerElement.getType() instanceof ComplexTypeImpl) {
                        data = new SoapComplexDataElement();
                        data.setDataElementName(innerElement.getQName().getLocalPart());
                        data.setDirection(dataElement.isDirection());
                        data.setSoapComplexDataElement(dataElement);
                        data.setSoapOperation(dataElement.getSoapOperation());
                        ((SoapComplexDataElement) data).setDataElements(elementProcessing(innerElement, (SoapComplexDataElement) data));
                        items.add(data);
                    } else {
                        data = new SoapDataElement();
                        data.setDataElementName(innerElement.getQName().getLocalPart());
                        data.setDirection(dataElement.isDirection());
                        data.setSoapComplexDataElement(dataElement);
                        data.setDataType(innerElement.getType() != null ? (innerElement.getType().getQName() != null ? innerElement.getType().getQName().getLocalPart() : "N/A") : "N/A");
                        data.setSoapOperation(dataElement.getSoapOperation());
                        depthLevel++;
//                        for (int indent = 0; indent < depthLevel; indent++) {
//                            System.out.print("  ");
//                        }
//                        System.out.println("Element: [" + dataElement.getDataElementName() + "]" + innerElement.getQName().getLocalPart() + " (" + data.getDataType() + ")");
                        depthLevel--;
                        items.add(data);
                    }
                }
            }
//            for (int indent = 0; indent < depthLevel; indent++) {
//                System.out.print("  ");
//            }
            depthLevel--;
//            if (type != null && type.getQName() != null) {
//                System.out.println("|--------- Putting type (" + type.getQName().getLocalPart() + ") into the schema map ---------|");
//                schema.put(type.getQName(), innerElements);
//            } else {
//                System.out.println("|--------- Putting element (" + e.getQName().getLocalPart() + ") into the schema map ---------|");
//                schema.put(e.getQName(), innerElements);
//            }
        } else {
            depthLevel--;
        }
        return items;
    }
}
