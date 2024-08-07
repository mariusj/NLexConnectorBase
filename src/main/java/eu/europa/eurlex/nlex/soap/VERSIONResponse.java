
package eu.europa.eurlex.nlex.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VERSIONResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "versionResult"
})
@XmlRootElement(name = "VERSIONResponse")
public class VERSIONResponse {

    @XmlElement(name = "VERSIONResult")
    protected String versionResult;

    /**
     * Gets the value of the versionResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVERSIONResult() {
        return versionResult;
    }

    /**
     * Sets the value of the versionResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVERSIONResult(String value) {
        this.versionResult = value;
    }

}
