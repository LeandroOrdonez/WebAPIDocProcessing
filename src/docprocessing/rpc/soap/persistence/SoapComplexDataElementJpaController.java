/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.soap.persistence;

import docprocessing.rpc.soap.model.SoapComplexDataElement;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import docprocessing.rpc.soap.model.SoapOperation;
import docprocessing.rpc.soap.persistence.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author leandro
 */
public class SoapComplexDataElementJpaController implements Serializable {

    public SoapComplexDataElementJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SoapComplexDataElement soapComplexDataElement) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SoapOperation soapOperation = soapComplexDataElement.getSoapOperation();
            if (soapOperation != null) {
                soapOperation = em.getReference(soapOperation.getClass(), soapOperation.getId());
                soapComplexDataElement.setSoapOperation(soapOperation);
            }
            em.persist(soapComplexDataElement);
            if (soapOperation != null) {
                soapOperation.getDataElements().add(soapComplexDataElement);
                soapOperation = em.merge(soapOperation);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SoapComplexDataElement soapComplexDataElement) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SoapComplexDataElement persistentSoapComplexDataElement = em.find(SoapComplexDataElement.class, soapComplexDataElement.getId());
            SoapOperation soapOperationOld = persistentSoapComplexDataElement.getSoapOperation();
            SoapOperation soapOperationNew = soapComplexDataElement.getSoapOperation();
            if (soapOperationNew != null) {
                soapOperationNew = em.getReference(soapOperationNew.getClass(), soapOperationNew.getId());
                soapComplexDataElement.setSoapOperation(soapOperationNew);
            }
            soapComplexDataElement = em.merge(soapComplexDataElement);
            if (soapOperationOld != null && !soapOperationOld.equals(soapOperationNew)) {
                soapOperationOld.getDataElements().remove(soapComplexDataElement);
                soapOperationOld = em.merge(soapOperationOld);
            }
            if (soapOperationNew != null && !soapOperationNew.equals(soapOperationOld)) {
                soapOperationNew.getDataElements().add(soapComplexDataElement);
                soapOperationNew = em.merge(soapOperationNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = soapComplexDataElement.getId();
                if (findSoapComplexDataElement(id) == null) {
                    throw new NonexistentEntityException("The soapComplexDataElement with id " + id + " no longer exists.");
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
            SoapComplexDataElement soapComplexDataElement;
            try {
                soapComplexDataElement = em.getReference(SoapComplexDataElement.class, id);
                soapComplexDataElement.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The soapComplexDataElement with id " + id + " no longer exists.", enfe);
            }
            SoapOperation soapOperation = soapComplexDataElement.getSoapOperation();
            if (soapOperation != null) {
                soapOperation.getDataElements().remove(soapComplexDataElement);
                soapOperation = em.merge(soapOperation);
            }
            em.remove(soapComplexDataElement);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SoapComplexDataElement> findSoapComplexDataElementEntities() {
        return findSoapComplexDataElementEntities(true, -1, -1);
    }

    public List<SoapComplexDataElement> findSoapComplexDataElementEntities(int maxResults, int firstResult) {
        return findSoapComplexDataElementEntities(false, maxResults, firstResult);
    }

    private List<SoapComplexDataElement> findSoapComplexDataElementEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SoapComplexDataElement.class));
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

    public SoapComplexDataElement findSoapComplexDataElement(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SoapComplexDataElement.class, id);
        } finally {
            em.close();
        }
    }

    public int getSoapComplexDataElementCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SoapComplexDataElement> rt = cq.from(SoapComplexDataElement.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
