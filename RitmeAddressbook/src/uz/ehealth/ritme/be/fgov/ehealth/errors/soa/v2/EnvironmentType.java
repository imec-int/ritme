//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.07.13 at 03:52:20 PM CEST 
//


package uz.ehealth.ritme.be.fgov.ehealth.errors.soa.v2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EnvironmentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EnvironmentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Development"/>
 *     &lt;enumeration value="Test"/>
 *     &lt;enumeration value="Integration"/>
 *     &lt;enumeration value="Acceptation"/>
 *     &lt;enumeration value="Simulation"/>
 *     &lt;enumeration value="Production"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@SuppressWarnings("ALL") //Generated file
@XmlType(name = "EnvironmentType")
@XmlEnum
public enum EnvironmentType {

    @XmlEnumValue("Development")
    DEVELOPMENT("Development"),
    @XmlEnumValue("Test")
    TEST("Test"),
    @XmlEnumValue("Integration")
    INTEGRATION("Integration"),
    @XmlEnumValue("Acceptation")
    ACCEPTATION("Acceptation"),
    @XmlEnumValue("Simulation")
    SIMULATION("Simulation"),
    @XmlEnumValue("Production")
    PRODUCTION("Production");
    private final String value;

    EnvironmentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnvironmentType fromValue(String v) {
        for (EnvironmentType c: EnvironmentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}