
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
 *         &lt;element name="about_connectorResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "aboutConnectorResult"
})
@XmlRootElement(name = "about_connectorResponse")
public class AboutConnectorResponse {

    @XmlElement(name = "about_connectorResult")
    protected String aboutConnectorResult;

    /**
     * Gets the value of the aboutConnectorResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAboutConnectorResult() {
        return aboutConnectorResult;
    }

    /**
     * Sets the value of the aboutConnectorResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAboutConnectorResult(String value) {
        this.aboutConnectorResult = value;
    }

}
