/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.http.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Leandro Ordonez <leandro.ordonez.ante@gmail.com>
 */
@Entity
public class HTTPService implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String serviceURI;
    @OneToMany(mappedBy = "httpService", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<HTTPOperation> operations;

    public String getServiceURI() {
        return serviceURI;
    }

    public void setServiceURI(String serviceURI) {
        this.serviceURI = serviceURI;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serviceURI != null ? serviceURI.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HTTPService)) {
            return false;
        }
        HTTPService other = (HTTPService) object;
        if ((this.serviceURI == null && other.serviceURI != null) || (this.serviceURI != null && !this.serviceURI.equals(other.serviceURI))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "docprocessing.rpc.http.model.HTTPService[ id=" + serviceURI + " ]";
    }

    /**
     * @return the operations
     */
    public List<HTTPOperation> getOperations() {
        return operations;
    }

    /**
     * @param operations the operations to set
     */
    public void setOperations(List<HTTPOperation> operations) {
        this.operations = operations;
    }
    
}
