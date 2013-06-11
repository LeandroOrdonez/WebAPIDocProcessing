/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.soap.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author leandro
 */
@Entity
@Table(name="SOAP_SERVICE")
public class SoapService implements Serializable {
    private static final long serialVersionUID = 1L;
    
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
    @Id
    private String serviceURI;
    private String serviceName;
    @Column(length=13000)
    private String serviceDocumentation;
    @OneToMany(mappedBy = "soapService", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<SoapOperation> operations;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serviceURI != null ? serviceURI.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SoapService)) {
            return false;
        }
        SoapService other = (SoapService) object;
        if ((this.serviceURI == null && other.serviceURI != null) || (this.serviceURI != null && !this.serviceURI.equals(other.serviceURI))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "docprocessing.rpc.soap.persistence.SoapService[ serviceURI=" + serviceURI + " ]";
    }

    /**
     * @return the serviceURI
     */
    public String getServiceURI() {
        return serviceURI;
    }

    /**
     * @param serviceURI the serviceURI to set
     */
    public void setServiceURI(String serviceURI) {
        this.serviceURI = serviceURI;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the serviceDocumentation
     */
    public String getServiceDocumentation() {
        return serviceDocumentation;
    }

    /**
     * @param serviceDocumentation the serviceDocumentation to set
     */
    public void setServiceDocumentation(String serviceDocumentation) {
        this.serviceDocumentation = serviceDocumentation;
    }

    /**
     * @return the operations
     */
    public List<SoapOperation> getOperations() {
        return operations;
    }

    /**
     * @param operations the operations to set
     */
    public void setOperations(List<SoapOperation> operations) {
        this.operations = operations;
    }
    
}
