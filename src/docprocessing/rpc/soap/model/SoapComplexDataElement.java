/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package docprocessing.rpc.soap.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author leandro
 */
@Entity
@Table(name="SOAP_COMPLEXDATA")
public class SoapComplexDataElement extends SoapDataElement implements Serializable {
    private static final long serialVersionUID = 1L;
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
    @OneToMany(mappedBy = "soapComplexDataElement", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<SoapDataElement> dataElements;

//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (id != null ? id.hashCode() : 0);
//        return hash;
//    }

//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof SoapComplexDataElement)) {
//            return false;
//        }
//        SoapComplexDataElement other = (SoapComplexDataElement) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "docprocessing.rpc.soap.persistence.SoapComplexDataElement[ complexDataName=" + getDataElementName() + " ]";
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
    
}
