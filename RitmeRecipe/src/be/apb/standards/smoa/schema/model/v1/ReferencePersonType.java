//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.10.10 at 05:20:59 PM CEST 
//


package be.apb.standards.smoa.schema.model.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import be.apb.standards.smoa.schema.id.v1.AbstractPersonIdType;


/**
 * <p>Java class for ReferencePersonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferencePersonType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.apb.be/standards/smoa/schema/model/v1}AbstractPersonType">
 *       &lt;sequence>
 *         &lt;element name="personId" type="{http://www.apb.be/standards/smoa/schema/id/v1}Abstract-PersonIdType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferencePersonType", propOrder = {
    "personId"
})
public class ReferencePersonType
    extends AbstractPersonType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected AbstractPersonIdType personId;

    /**
     * Gets the value of the personId property.
     * 
     * @return
     *     possible object is
     *     {@link AbstractPersonIdType }
     *     
     */
    public AbstractPersonIdType getPersonId() {
        return personId;
    }

    /**
     * Sets the value of the personId property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractPersonIdType }
     *     
     */
    public void setPersonId(AbstractPersonIdType value) {
        this.personId = value;
    }

}