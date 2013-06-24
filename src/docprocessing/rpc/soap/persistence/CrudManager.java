/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.soap.persistence;

import docprocessing.rpc.soap.model.SoapComplexDataElement;
import docprocessing.rpc.soap.model.SoapDataElement;
import docprocessing.rpc.soap.model.SoapOperation;
import docprocessing.rpc.soap.model.SoapService;
import docprocessing.rpc.soap.persistence.exceptions.NonexistentEntityException;
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
    private SoapServiceJpaController serviceJpaController;
    private SoapOperationJpaController operationJpaController;
    private SoapDataElementJpaController dataElementJpaController;
    private SoapComplexDataElementJpaController complexDataElementJpaController;

    public CrudManager() {
        Properties config = new Properties();
        try{
            config.load(CrudManager.class.getResourceAsStream("/config/config.properties"));
            String persistenceUnit = config.getProperty("persistence_unit");
            emf = Persistence.createEntityManagerFactory(persistenceUnit);
            serviceJpaController = new SoapServiceJpaController(emf);
            operationJpaController = new SoapOperationJpaController(emf);
            dataElementJpaController = new SoapDataElementJpaController(emf);
            complexDataElementJpaController = new SoapComplexDataElementJpaController(emf);
        } catch (IOException ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<SoapService> findSoapServices(int maxResults, int firstResult) {
        return serviceJpaController.findSoapServiceEntities(maxResults, firstResult);
    }
    
    public List<SoapService> findSoapServices() {
        return serviceJpaController.findSoapServiceEntities();
    }
    
    public List<SoapOperation> findSoapOperations(int maxResults, int firstResult) {
        return operationJpaController.findSoapOperationEntities(maxResults, firstResult);
    }
    
    public List<SoapOperation> findSoapOperations() {
        return operationJpaController.findSoapOperationEntities();
    }
    
    public List<SoapDataElement> findSoapDataElements(int maxResults, int firstResult) {
        return dataElementJpaController.findSoapDataElementEntities(maxResults, firstResult);
    }
    
    public List<SoapDataElement> findSoapDataElements() {
        return dataElementJpaController.findSoapDataElementEntities();
    }
    
    public SoapService findSoapService(String serviceURI) {
        return serviceJpaController.findSoapService(serviceURI);
    }
    
    public SoapOperation findSoapOperation(long id) {
        return operationJpaController.findSoapOperation(id);
    }
    
    public SoapDataElement finDataElement(long id) {
        return dataElementJpaController.findSoapDataElement(id);
    }
    
    public int getSoapServiceCount() {
        return serviceJpaController.getSoapServiceCount();
    }
    
    public long getSoapOperationCount() {
        return operationJpaController.getSoapOperationCount();
    }
    
    public int getSoapDataElementCount() {
        return dataElementJpaController.getSoapDataElementCount();
    }

    public void createSoapService(SoapService service) {
        serviceJpaController.create(service);
    }
    
    public void createSoapOperation(SoapOperation operation) {
        operationJpaController.create(operation);
    }
    
    public void createSoapDataElement(SoapDataElement data) {
        dataElementJpaController.create(data);
    }
    
    public void createSoapComplexDataElement(SoapComplexDataElement complexData) {
        complexDataElementJpaController.create(complexData);
    }
    
    public void destroySoapService(String serviceURI) {
        try {
            serviceJpaController.destroy(serviceURI);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void destroySoapOperation(long id) {
        try {
            operationJpaController.destroy(id);
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
            serviceJpaController.edit(service);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CrudManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
}
