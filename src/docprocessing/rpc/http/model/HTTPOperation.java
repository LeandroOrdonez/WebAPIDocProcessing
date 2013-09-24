/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.http.model;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Leandro Ordonez <leandro.ordonez.ante@gmail.com>
 */
@Entity
public class HTTPOperation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String operationName;
    @Column(length=13000)
    private String operationDocumentation;
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private HTTPService httpService;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HTTPOperation)) {
            return false;
        }
        HTTPOperation other = (HTTPOperation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "docprocessing.rpc.http.model.HTTPOperation[ id=" + id + " ]";
    }

    /**
     * @return the operationName
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * @param operationName the operationName to set
     */
    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    /**
     * @return the operationDocumentation
     */
    public String getOperationDocumentation() {
        return operationDocumentation;
    }

    /**
     * @param operationDocumentation the operationDocumentation to set
     */
    public void setOperationDocumentation(String operationDocumentation) {
        this.operationDocumentation = operationDocumentation;
    }

    /**
     * @return the httpService
     */
    public HTTPService getHttpService() {
        return httpService;
    }

    /**
     * @param httpService the httpService to set
     */
    public void setHttpService(HTTPService httpService) {
        this.httpService = httpService;
    }
    
}
