//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.01.13 at 03:24:23 PM CET 
//


package it.zuper.fa.parser.beans;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FormatoTrasmissioneType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FormatoTrasmissioneType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;length value="5"/&gt;
 *     &lt;enumeration value="FPA12"/&gt;
 *     &lt;enumeration value="FPR12"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FormatoTrasmissioneType")
@XmlEnum
public enum FormatoTrasmissioneType {


    /**
     * Fattura verso PA
     * 
     */
    @XmlEnumValue("FPA12")
    FPA_12("FPA12"),

    /**
     * Fattura verso privati
     * 
     */
    @XmlEnumValue("FPR12")
    FPR_12("FPR12");
    private final String value;

    FormatoTrasmissioneType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FormatoTrasmissioneType fromValue(String v) {
        for (FormatoTrasmissioneType c: FormatoTrasmissioneType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}