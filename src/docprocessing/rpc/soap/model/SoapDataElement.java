/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.soap.model;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author leandro
 */
@Entity
@Table(name="SOAP_DATA")
public class SoapDataElement implements Serializable {
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private SoapComplexDataElement soapComplexDataElement;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String dataElementName;
    private String dataType;
    private boolean direction; //true: input; false: output
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private SoapOperation soapOperation;

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
        if (!(object instanceof SoapDataElement)) {
            return false;
        }
        SoapDataElement other = (SoapDataElement) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "docprocessing.rpc.soap.persistence.SoapDataElement[ dataName=" + dataElementName + " ]";
    }

    /**
     * @return the soapOperation
     */
    public SoapOperation getSoapOperation() {
        return soapOperation;
    }

    /**
     * @param soapOperation the soapOperation to set
     */
    public void setSoapOperation(SoapOperation soapOperation) {
        this.soapOperation = soapOperation;
    }

    /**
     * @return the dataElementName
     */
    public String getDataElementName() {
        return dataElementName;
    }

    /**
     * @param dataElementName the dataElementName to set
     */
    public void setDataElementName(String dataElementName) {
        this.dataElementName = dataElementName;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the direction
     */
    public boolean isDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    /**
     * @return the soapComplexDataElement
     */
    public SoapComplexDataElement getSoapComplexDataElement() {
        return soapComplexDataElement;
    }

    /**
     * @param soapComplexDataElement the soapComplexDataElement to set
     */
    public void setSoapComplexDataElement(SoapComplexDataElement soapComplexDataElement) {
        this.soapComplexDataElement = soapComplexDataElement;
    }
    
}
