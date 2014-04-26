/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.persistence;

import docprocessing.persistence.soap.SoapServiceJpaController;
import docprocessing.persistence.soap.SoapDataElementJpaController;
import docprocessing.persistence.soap.SoapComplexDataElementJpaController;
import docprocessing.persistence.soap.SoapOperationJpaController;
import docprocessing.rpc.soap.model.SoapComplexDataElement;
import docprocessing.rpc.soap.model.SoapDataElement;
import docprocessing.rpc.soap.model.SoapOperation;
import docprocessing.rpc.soap.model.SoapService;
import docprocessing.persistence.exceptions.NonexistentEntityException;
import docprocessing.persistence.http.HTTPOperationJpaController;
import docprocessing.persistence.http.HTTPServiceJpaController;
import docprocessing.rpc.http.model.HTTPOperation;
import docprocessing.rpc.http.model.HTTPService;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author leandro
 */
public class CrudManager {
    private EntityManagerFactory emf;
    private SoapServiceJpaController soapServiceJpaController;
    private SoapOperationJpaController soapOperationJpaController;
    private SoapDataElementJpaController dataElementJpaController;
    private SoapComplexDataElementJpaController complexDataElementJpaController;
    private HTTPServiceJpaController hTTPServiceJpaController;
    private HTTPOperationJpaController hTTPOperationJpaController;

    public CrudManager() {
        Properties config = new Properties();
        try{
            config.load(CrudManager.class.getResourceAsStream("/config/config.properties"));
            String persistenceUnit = config.getProperty("persistence_unit");
            emf = Persistence.createEntityManagerFactory(persistenceUnit);
            soapServiceJpaController = new SoapServiceJpaController(emf);
            soapOperationJpaController = new SoapOperationJpaController(emf);
            dataElementJpaController = new SoapDataElementJpaController(emf);
            complexDataElementJpaController = new SoapComplexDataElementJpaController(emf);
            hTTPServiceJpaController = new HTTPServiceJpaController(emf);
            hTTPOperationJpaController = new HTTPOperationJpaController(emf);
        } catch (IOException ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<SoapService> findSoapServices(int maxResults, int firstResult) {
        return soapServiceJpaController.findSoapServiceEntities(maxResults, firstResult);
    }
    
    public List<SoapService> findSoapServices() {
        return soapServiceJpaController.findSoapServiceEntities();
    }
    
    public List<SoapOperation> findSoapOperations(int maxResults, int firstResult) {
        return soapOperationJpaController.findSoapOperationEntities(maxResults, firstResult);
    }
    
    public List<SoapOperation> findSoapOperations() {
        return soapOperationJpaController.findSoapOperationEntities();
    }
    
    public List<HTTPService> findHttpServices(int maxResults, int firstResult) {
        return hTTPServiceJpaController.findHTTPServiceEntities(maxResults, firstResult);
    }
    
    public List<HTTPService> findHttpServices() {
        return hTTPServiceJpaController.findHTTPServiceEntities();
    }
    
    public List<HTTPOperation> findHttpOperations(int maxResults, int firstResult) {
        return hTTPOperationJpaController.findHTTPOperationEntities(maxResults, firstResult);
    }
    
    public List<HTTPOperation> findHttpOperations() {
        return hTTPOperationJpaController.findHTTPOperationEntities();
    }
    
    public List<SoapDataElement> findSoapDataElements(int maxResults, int firstResult) {
        return dataElementJpaController.findSoapDataElementEntities(maxResults, firstResult);
    }
    
    public List<SoapDataElement> findSoapDataElements() {
        return dataElementJpaController.findSoapDataElementEntities();
    }
    
    public SoapService findSoapService(String serviceURI) {
        return soapServiceJpaController.findSoapService(serviceURI);
    }
    
    public SoapOperation findSoapOperation(long id) {
        return soapOperationJpaController.findSoapOperation(id);
    }
    
    public SoapDataElement finDataElement(long id) {
        return dataElementJpaController.findSoapDataElement(id);
    }
    
    public int getSoapServiceCount() {
        return soapServiceJpaController.getSoapServiceCount();
    }
    
    public long getSoapOperationCount() {
        return soapOperationJpaController.getSoapOperationCount();
    }
    
    public int getHttpServiceCount() {
        return hTTPServiceJpaController.getHTTPServiceCount();
    }
    
    public long getHttpOperationCount() {
        return hTTPOperationJpaController.getHTTPOperationCount();
    }
    
    public int getSoapDataElementCount() {
        return dataElementJpaController.getSoapDataElementCount();
    }

    public void createSoapService(SoapService service) {
        soapServiceJpaController.create(service);
    }
    
    public void createSoapOperation(SoapOperation operation) {
        soapOperationJpaController.create(operation);
    }
    
    public void createHttpService(HTTPService service) {
        hTTPServiceJpaController.create(service);
    }
    
    public void createHttpOperation(HTTPOperation operation) {
        hTTPOperationJpaController.create(operation);
    }
    
    public void createSoapDataElement(SoapDataElement data) {
        dataElementJpaController.create(data);
    }
    
    public void createSoapComplexDataElement(SoapComplexDataElement complexData) {
        complexDataElementJpaController.create(complexData);
    }
    
    public void destroySoapService(String serviceURI) {
        try {
            soapServiceJpaController.destroy(serviceURI);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void destroySoapOperation(long id) {
        try {
            soapOperationJpaController.destroy(id);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void destroyHttpService(String serviceURI) {
        try {
            hTTPServiceJpaController.destroy(serviceURI);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void destroyHttpOperation(long id) {
        try {
            hTTPOperationJpaController.destroy(id);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void destroySoapDataElement(long id) {
        try {
            dataElementJpaController.destroy(id);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void destroySoapComplexData(long id) {
        try {
            complexDataElementJpaController.destroy(id);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void editSoapService(SoapService service) {
        try {
            soapServiceJpaController.edit(service);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void editHttpService(HTTPService service) {
        try {
            hTTPServiceJpaController.edit(service);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
}
