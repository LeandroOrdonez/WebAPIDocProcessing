/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.persistence.soap;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import docprocessing.rpc.soap.model.SoapOperation;
import docprocessing.rpc.soap.model.SoapService;
import docprocessing.persistence.exceptions.NonexistentEntityException;
import docprocessing.persistence.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author leandro
 */
public class SoapServiceJpaController implements Serializable {

    public SoapServiceJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SoapService soapService) {
        if (soapService.getOperations() == null) {
            soapService.setOperations(new ArrayList<SoapOperation>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
//            List<SoapOperation> attachedOperations = new ArrayList<SoapOperation>();
//            for (SoapOperation operationsSoapOperationToAttach : soapService.getOperations()) {
//                operationsSoapOperationToAttach = em.getReference(operationsSoapOperationToAttach.getClass(), operationsSoapOperationToAttach.getId());
//                attachedOperations.add(operationsSoapOperationToAttach);
//            }
//            soapService.setOperations(attachedOperations);
            if (findSoapService(soapService.getServiceURI()) != null) {
                throw new PreexistingEntityException("Duplicate entry for key 'PRIMARY' on SOAP_SERVICE");
            }
            em.persist(soapService);
//            for (SoapOperation operationsSoapOperation : soapService.getOperations()) {
//                SoapService oldSoapServiceOfOperationsSoapOperation = operationsSoapOperation.getSoapService();
//                operationsSoapOperation.setSoapService(soapService);
//                operationsSoapOperation = em.merge(operationsSoapOperation);
//                if (oldSoapServiceOfOperationsSoapOperation != null) {
//                    oldSoapServiceOfOperationsSoapOperation.getOperations().remove(operationsSoapOperation);
//                    oldSoapServiceOfOperationsSoapOperation = em.merge(oldSoapServiceOfOperationsSoapOperation);
//                }
//            }
            em.getTransaction().commit();
        } catch (PreexistingEntityException ex) {
            Logger.getLogger(SoapServiceJpaController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SoapService soapService) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SoapService persistentSoapService = em.find(SoapService.class, soapService.getServiceURI());
            List<SoapOperation> operationsOld = persistentSoapService.getOperations();
            List<SoapOperation> operationsNew = soapService.getOperations();
            List<SoapOperation> attachedOperationsNew = new ArrayList<SoapOperation>();
            for (SoapOperation operationsNewSoapOperationToAttach : operationsNew) {
                operationsNewSoapOperationToAttach = em.getReference(operationsNewSoapOperationToAttach.getClass(), operationsNewSoapOperationToAttach.getId());
                attachedOperationsNew.add(operationsNewSoapOperationToAttach);
            }
            operationsNew = attachedOperationsNew;
            soapService.setOperations(operationsNew);
            soapService = em.merge(soapService);
            for (SoapOperation operationsOldSoapOperation : operationsOld) {
                if (!operationsNew.contains(operationsOldSoapOperation)) {
                    operationsOldSoapOperation.setSoapService(null);
                    operationsOldSoapOperation = em.merge(operationsOldSoapOperation);
                }
            }
            for (SoapOperation operationsNewSoapOperation : operationsNew) {
                if (!operationsOld.contains(operationsNewSoapOperation)) {
                    SoapService oldSoapServiceOfOperationsNewSoapOperation = operationsNewSoapOperation.getSoapService();
                    operationsNewSoapOperation.setSoapService(soapService);
                    operationsNewSoapOperation = em.merge(operationsNewSoapOperation);
                    if (oldSoapServiceOfOperationsNewSoapOperation != null && !oldSoapServiceOfOperationsNewSoapOperation.equals(soapService)) {
                        oldSoapServiceOfOperationsNewSoapOperation.getOperations().remove(operationsNewSoapOperation);
                        oldSoapServiceOfOperationsNewSoapOperation = em.merge(oldSoapServiceOfOperationsNewSoapOperation);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = soapService.getServiceURI();
                if (findSoapService(id) == null) {
                    throw new NonexistentEntityException("The soapService with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String serviceURI) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SoapService soapService;
            try {
                soapService = em.getReference(SoapService.class, serviceURI);
                soapService.getServiceURI();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The soapService with id " + serviceURI + " no longer exists.", enfe);
            }
            List<SoapOperation> operations = soapService.getOperations();
            for (SoapOperation operationsSoapOperation : operations) {
                operationsSoapOperation.setSoapService(null);
                operationsSoapOperation = em.merge(operationsSoapOperation);
            }
            em.remove(soapService);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SoapService> findSoapServiceEntities() {
        return findSoapServiceEntities(true, -1, -1);
    }

    public List<SoapService> findSoapServiceEntities(int maxResults, int firstResult) {
        return findSoapServiceEntities(false, maxResults, firstResult);
    }

    private List<SoapService> findSoapServiceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SoapService.class));
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

    public SoapService findSoapService(String serviceURI) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SoapService.class, serviceURI);
        } finally {
            em.close();
        }
    }

    public int getSoapServiceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SoapService> rt = cq.from(SoapService.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
