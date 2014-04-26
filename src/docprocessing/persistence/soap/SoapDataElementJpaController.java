/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.persistence.soap;

import docprocessing.rpc.soap.model.SoapDataElement;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import docprocessing.rpc.soap.model.SoapOperation;
import docprocessing.persistence.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author leandro
 */
public class SoapDataElementJpaController implements Serializable {

    public SoapDataElementJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SoapDataElement soapDataElement) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SoapOperation soapOperation = soapDataElement.getSoapOperation();
            if (soapOperation != null) {
                soapOperation = em.getReference(soapOperation.getClass(), soapOperation.getId());
                soapDataElement.setSoapOperation(soapOperation);
            }
            em.persist(soapDataElement);
            if (soapOperation != null) {
                soapOperation.getDataElements().add(soapDataElement);
                soapOperation = em.merge(soapOperation);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SoapDataElement soapDataElement) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SoapDataElement persistentSoapDataElement = em.find(SoapDataElement.class, soapDataElement.getId());
            SoapOperation soapOperationOld = persistentSoapDataElement.getSoapOperation();
            SoapOperation soapOperationNew = soapDataElement.getSoapOperation();
            if (soapOperationNew != null) {
                soapOperationNew = em.getReference(soapOperationNew.getClass(), soapOperationNew.getId());
                soapDataElement.setSoapOperation(soapOperationNew);
            }
            soapDataElement = em.merge(soapDataElement);
            if (soapOperationOld != null && !soapOperationOld.equals(soapOperationNew)) {
                soapOperationOld.getDataElements().remove(soapDataElement);
                soapOperationOld = em.merge(soapOperationOld);
            }
            if (soapOperationNew != null && !soapOperationNew.equals(soapOperationOld)) {
                soapOperationNew.getDataElements().add(soapDataElement);
                soapOperationNew = em.merge(soapOperationNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = soapDataElement.getId();
                if (findSoapDataElement(id) == null) {
                    throw new NonexistentEntityException("The soapDataElement with id " + id + " no longer exists.");
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
            SoapDataElement soapDataElement;
            try {
                soapDataElement = em.getReference(SoapDataElement.class, id);
                soapDataElement.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The soapDataElement with id " + id + " no longer exists.", enfe);
            }
            SoapOperation soapOperation = soapDataElement.getSoapOperation();
            if (soapOperation != null) {
                soapOperation.getDataElements().remove(soapDataElement);
                soapOperation = em.merge(soapOperation);
            }
            em.remove(soapDataElement);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SoapDataElement> findSoapDataElementEntities() {
        return findSoapDataElementEntities(true, -1, -1);
    }

    public List<SoapDataElement> findSoapDataElementEntities(int maxResults, int firstResult) {
        return findSoapDataElementEntities(false, maxResults, firstResult);
    }

    private List<SoapDataElement> findSoapDataElementEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SoapDataElement.class));
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

    public SoapDataElement findSoapDataElement(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SoapDataElement.class, id);
        } finally {
            em.close();
        }
    }

    public int getSoapDataElementCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SoapDataElement> rt = cq.from(SoapDataElement.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
