/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.persistence.http;

import docprocessing.persistence.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import docprocessing.rpc.http.model.HTTPOperation;
import docprocessing.rpc.http.model.HTTPService;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leandro Ordonez <leandro.ordonez.ante@gmail.com>
 */
public class HTTPServiceJpaController implements Serializable {

    public HTTPServiceJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(HTTPService HTTPService) {
        if (HTTPService.getOperations() == null) {
            HTTPService.setOperations(new ArrayList<HTTPOperation>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<HTTPOperation> attachedOperations = new ArrayList<HTTPOperation>();
            for (HTTPOperation operationsHTTPOperationToAttach : HTTPService.getOperations()) {
                operationsHTTPOperationToAttach = em.getReference(operationsHTTPOperationToAttach.getClass(), operationsHTTPOperationToAttach.getId());
                attachedOperations.add(operationsHTTPOperationToAttach);
            }
            HTTPService.setOperations(attachedOperations);
            em.persist(HTTPService);
            for (HTTPOperation operationsHTTPOperation : HTTPService.getOperations()) {
                HTTPService oldHttpServiceOfOperationsHTTPOperation = operationsHTTPOperation.getHttpService();
                operationsHTTPOperation.setHttpService(HTTPService);
                operationsHTTPOperation = em.merge(operationsHTTPOperation);
                if (oldHttpServiceOfOperationsHTTPOperation != null) {
                    oldHttpServiceOfOperationsHTTPOperation.getOperations().remove(operationsHTTPOperation);
                    oldHttpServiceOfOperationsHTTPOperation = em.merge(oldHttpServiceOfOperationsHTTPOperation);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(HTTPService HTTPService) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            HTTPService persistentHTTPService = em.find(HTTPService.class, HTTPService.getServiceURI());
            List<HTTPOperation> operationsOld = persistentHTTPService.getOperations();
            List<HTTPOperation> operationsNew = HTTPService.getOperations();
            List<HTTPOperation> attachedOperationsNew = new ArrayList<HTTPOperation>();
            for (HTTPOperation operationsNewHTTPOperationToAttach : operationsNew) {
                operationsNewHTTPOperationToAttach = em.getReference(operationsNewHTTPOperationToAttach.getClass(), operationsNewHTTPOperationToAttach.getId());
                attachedOperationsNew.add(operationsNewHTTPOperationToAttach);
            }
            operationsNew = attachedOperationsNew;
            HTTPService.setOperations(operationsNew);
            HTTPService = em.merge(HTTPService);
            for (HTTPOperation operationsOldHTTPOperation : operationsOld) {
                if (!operationsNew.contains(operationsOldHTTPOperation)) {
                    operationsOldHTTPOperation.setHttpService(null);
                    operationsOldHTTPOperation = em.merge(operationsOldHTTPOperation);
                }
            }
            for (HTTPOperation operationsNewHTTPOperation : operationsNew) {
                if (!operationsOld.contains(operationsNewHTTPOperation)) {
                    HTTPService oldHttpServiceOfOperationsNewHTTPOperation = operationsNewHTTPOperation.getHttpService();
                    operationsNewHTTPOperation.setHttpService(HTTPService);
                    operationsNewHTTPOperation = em.merge(operationsNewHTTPOperation);
                    if (oldHttpServiceOfOperationsNewHTTPOperation != null && !oldHttpServiceOfOperationsNewHTTPOperation.equals(HTTPService)) {
                        oldHttpServiceOfOperationsNewHTTPOperation.getOperations().remove(operationsNewHTTPOperation);
                        oldHttpServiceOfOperationsNewHTTPOperation = em.merge(oldHttpServiceOfOperationsNewHTTPOperation);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = HTTPService.getServiceURI();
                if (findHTTPService(id) == null) {
                    throw new NonexistentEntityException("The hTTPService with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            HTTPService HTTPService;
            try {
                HTTPService = em.getReference(HTTPService.class, id);
                HTTPService.getServiceURI();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The HTTPService with id " + id + " no longer exists.", enfe);
            }
            List<HTTPOperation> operations = HTTPService.getOperations();
            for (HTTPOperation operationsHTTPOperation : operations) {
                operationsHTTPOperation.setHttpService(null);
                operationsHTTPOperation = em.merge(operationsHTTPOperation);
            }
            em.remove(HTTPService);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<HTTPService> findHTTPServiceEntities() {
        return findHTTPServiceEntities(true, -1, -1);
    }

    public List<HTTPService> findHTTPServiceEntities(int maxResults, int firstResult) {
        return findHTTPServiceEntities(false, maxResults, firstResult);
    }

    private List<HTTPService> findHTTPServiceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(HTTPService.class));
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

    public HTTPService findHTTPService(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(HTTPService.class, id);
        } finally {
            em.close();
        }
    }

    public int getHTTPServiceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<HTTPService> rt = cq.from(HTTPService.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
