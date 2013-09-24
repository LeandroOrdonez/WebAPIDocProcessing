/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.persistence.http;

import docprocessing.persistence.exceptions.NonexistentEntityException;
import docprocessing.rpc.http.model.HTTPOperation;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import docprocessing.rpc.http.model.HTTPService;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leandro Ordonez <leandro.ordonez.ante@gmail.com>
 */
public class HTTPOperationJpaController implements Serializable {

    public HTTPOperationJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(HTTPOperation HTTPOperation) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            HTTPService httpService = HTTPOperation.getHttpService();
            if (httpService != null) {
                httpService = em.getReference(httpService.getClass(), httpService.getServiceURI());
                HTTPOperation.setHttpService(httpService);
            }
            em.persist(HTTPOperation);
            if (httpService != null) {
                httpService.getOperations().add(HTTPOperation);
                httpService = em.merge(httpService);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(HTTPOperation HTTPOperation) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            HTTPOperation persistentHTTPOperation = em.find(HTTPOperation.class, HTTPOperation.getId());
            HTTPService httpServiceOld = persistentHTTPOperation.getHttpService();
            HTTPService httpServiceNew = HTTPOperation.getHttpService();
            if (httpServiceNew != null) {
                httpServiceNew = em.getReference(httpServiceNew.getClass(), httpServiceNew.getServiceURI());
                HTTPOperation.setHttpService(httpServiceNew);
            }
            HTTPOperation = em.merge(HTTPOperation);
            if (httpServiceOld != null && !httpServiceOld.equals(httpServiceNew)) {
                httpServiceOld.getOperations().remove(HTTPOperation);
                httpServiceOld = em.merge(httpServiceOld);
            }
            if (httpServiceNew != null && !httpServiceNew.equals(httpServiceOld)) {
                httpServiceNew.getOperations().add(HTTPOperation);
                httpServiceNew = em.merge(httpServiceNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = HTTPOperation.getId();
                if (findHTTPOperation(id) == null) {
                    throw new NonexistentEntityException("The hTTPOperation with id " + id + " no longer exists.");
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
            HTTPOperation HTTPOperation;
            try {
                HTTPOperation = em.getReference(HTTPOperation.class, id);
                HTTPOperation.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The HTTPOperation with id " + id + " no longer exists.", enfe);
            }
            HTTPService httpService = HTTPOperation.getHttpService();
            if (httpService != null) {
                httpService.getOperations().remove(HTTPOperation);
                httpService = em.merge(httpService);
            }
            em.remove(HTTPOperation);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<HTTPOperation> findHTTPOperationEntities() {
        return findHTTPOperationEntities(true, -1, -1);
    }

    public List<HTTPOperation> findHTTPOperationEntities(int maxResults, int firstResult) {
        return findHTTPOperationEntities(false, maxResults, firstResult);
    }

    private List<HTTPOperation> findHTTPOperationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(HTTPOperation.class));
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

    public HTTPOperation findHTTPOperation(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(HTTPOperation.class, id);
        } finally {
            em.close();
        }
    }

    public int getHTTPOperationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<HTTPOperation> rt = cq.from(HTTPOperation.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
