/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.soap.persistence;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import docprocessing.rpc.soap.model.SoapService;
import docprocessing.rpc.soap.model.SoapDataElement;
import docprocessing.rpc.soap.model.SoapOperation;
import docprocessing.rpc.soap.persistence.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author leandro
 */
public class SoapOperationJpaController implements Serializable {

    public SoapOperationJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SoapOperation soapOperation) {
        if (soapOperation.getDataElements() == null) {
            soapOperation.setDataElements(new ArrayList<SoapDataElement>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SoapService soapService = soapOperation.getSoapService();
            if (soapService != null) {
                soapService = em.getReference(soapService.getClass(), soapService.getServiceURI());
                soapOperation.setSoapService(soapService);
            }
//            List<SoapDataElement> attachedDataElements = new ArrayList<SoapDataElement>();
//            for (SoapDataElement dataElementsSoapDataElementToAttach : soapOperation.getDataElements()) {
//                dataElementsSoapDataElementToAttach = em.getReference(dataElementsSoapDataElementToAttach.getClass(), dataElementsSoapDataElementToAttach.getId());
//                attachedDataElements.add(dataElementsSoapDataElementToAttach);
//            }
//            soapOperation.setDataElements(attachedDataElements);
            em.persist(soapOperation);
//            if (soapService != null) {
//                soapService.getOperations().add(soapOperation);
//                soapService = em.merge(soapService);
//            }
//            for (SoapDataElement dataElementsSoapDataElement : soapOperation.getDataElements()) {
//                SoapOperation oldSoapOperationOfDataElementsSoapDataElement = dataElementsSoapDataElement.getSoapOperation();
//                dataElementsSoapDataElement.setSoapOperation(soapOperation);
//                dataElementsSoapDataElement = em.merge(dataElementsSoapDataElement);
//                if (oldSoapOperationOfDataElementsSoapDataElement != null) {
//                    oldSoapOperationOfDataElementsSoapDataElement.getDataElements().remove(dataElementsSoapDataElement);
//                    oldSoapOperationOfDataElementsSoapDataElement = em.merge(oldSoapOperationOfDataElementsSoapDataElement);
//                }
//            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SoapOperation soapOperation) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SoapOperation persistentSoapOperation = em.find(SoapOperation.class, soapOperation.getId());
            SoapService soapServiceOld = persistentSoapOperation.getSoapService();
            SoapService soapServiceNew = soapOperation.getSoapService();
            List<SoapDataElement> dataElementsOld = persistentSoapOperation.getDataElements();
            List<SoapDataElement> dataElementsNew = soapOperation.getDataElements();
            if (soapServiceNew != null) {
                soapServiceNew = em.getReference(soapServiceNew.getClass(), soapServiceNew.getServiceURI());
                soapOperation.setSoapService(soapServiceNew);
            }
            List<SoapDataElement> attachedDataElementsNew = new ArrayList<SoapDataElement>();
            for (SoapDataElement dataElementsNewSoapDataElementToAttach : dataElementsNew) {
                dataElementsNewSoapDataElementToAttach = em.getReference(dataElementsNewSoapDataElementToAttach.getClass(), dataElementsNewSoapDataElementToAttach.getId());
                attachedDataElementsNew.add(dataElementsNewSoapDataElementToAttach);
            }
            dataElementsNew = attachedDataElementsNew;
            soapOperation.setDataElements(dataElementsNew);
            soapOperation = em.merge(soapOperation);
            if (soapServiceOld != null && !soapServiceOld.equals(soapServiceNew)) {
                soapServiceOld.getOperations().remove(soapOperation);
                soapServiceOld = em.merge(soapServiceOld);
            }
            if (soapServiceNew != null && !soapServiceNew.equals(soapServiceOld)) {
                soapServiceNew.getOperations().add(soapOperation);
                soapServiceNew = em.merge(soapServiceNew);
            }
            for (SoapDataElement dataElementsOldSoapDataElement : dataElementsOld) {
                if (!dataElementsNew.contains(dataElementsOldSoapDataElement)) {
                    dataElementsOldSoapDataElement.setSoapOperation(null);
                    dataElementsOldSoapDataElement = em.merge(dataElementsOldSoapDataElement);
                }
            }
            for (SoapDataElement dataElementsNewSoapDataElement : dataElementsNew) {
                if (!dataElementsOld.contains(dataElementsNewSoapDataElement)) {
                    SoapOperation oldSoapOperationOfDataElementsNewSoapDataElement = dataElementsNewSoapDataElement.getSoapOperation();
                    dataElementsNewSoapDataElement.setSoapOperation(soapOperation);
                    dataElementsNewSoapDataElement = em.merge(dataElementsNewSoapDataElement);
                    if (oldSoapOperationOfDataElementsNewSoapDataElement != null && !oldSoapOperationOfDataElementsNewSoapDataElement.equals(soapOperation)) {
                        oldSoapOperationOfDataElementsNewSoapDataElement.getDataElements().remove(dataElementsNewSoapDataElement);
                        oldSoapOperationOfDataElementsNewSoapDataElement = em.merge(oldSoapOperationOfDataElementsNewSoapDataElement);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = soapOperation.getId();
                if (findSoapOperation(id) == null) {
                    throw new NonexistentEntityException("The soapOperation with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SoapOperation soapOperation;
            try {
                soapOperation = em.getReference(SoapOperation.class, id);
                soapOperation.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The soapOperation with id " + id + " no longer exists.", enfe);
            }
            SoapService soapService = soapOperation.getSoapService();
            if (soapService != null) {
                soapService.getOperations().remove(soapOperation);
                soapService = em.merge(soapService);
            }
            List<SoapDataElement> dataElements = soapOperation.getDataElements();
            for (SoapDataElement dataElementsSoapDataElement : dataElements) {
                dataElementsSoapDataElement.setSoapOperation(null);
                dataElementsSoapDataElement = em.merge(dataElementsSoapDataElement);
            }
            em.remove(soapOperation);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SoapOperation> findSoapOperationEntities() {
        return findSoapOperationEntities(true, -1, -1);
    }

    public List<SoapOperation> findSoapOperationEntities(int maxResults, int firstResult) {
        return findSoapOperationEntities(false, maxResults, firstResult);
    }

    private List<SoapOperation> findSoapOperationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SoapOperation.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public SoapOperation findSoapOperation(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SoapOperation.class, id);
        } finally {
            em.close();
        }
    }

    public long getSoapOperationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SoapOperation> rt = cq.from(SoapOperation.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return (Long) q.getSingleResult();
        } finally {
            em.close();
        }
    }
    
}
