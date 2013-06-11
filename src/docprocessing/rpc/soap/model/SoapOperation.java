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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author leandro
 */
@Entity
@Table(name="SOAP_OPERATION")
public class SoapOperation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String operationName;
    @Column(length=13000)
    private String operationDocumentation;
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private SoapService soapService;
    @OneToMany(mappedBy = "soapOperation", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<SoapDataElement> dataElements;
    private String pattern; //IN_OUT, IN_ONLY, OUT_ONLY

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
        if (!(object instanceof SoapOperation)) {
            return false;
        }
        SoapOperation other = (SoapOperation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "docprocessing.rpc.soap.persistence.SoapOperation[ OpName=" + operationName + " ]";
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
     * @return the soapService
     */
    public SoapService getSoapService() {
        return soapService;
    }

    /**
     * @param soapService the soapService to set
     */
    public void setSoapService(SoapService soapService) {
        this.soapService = soapService;
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
     * @return the dataElements
     */
    public List<SoapDataElement> getDataElements() {
        return dataElements;
    }

    /**
     * @param dataElements the dataElements to set
     */
    public void setDataElements(List<SoapDataElement> dataElements) {
        this.dataElements = dataElements;
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
}
