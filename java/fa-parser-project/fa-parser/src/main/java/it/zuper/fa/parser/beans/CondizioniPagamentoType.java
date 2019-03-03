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
 * <p>Java class for CondizioniPagamentoType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CondizioniPagamentoType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;minLength value="4"/&gt;
 *     &lt;maxLength value="4"/&gt;
 *     &lt;enumeration value="TP01"/&gt;
 *     &lt;enumeration value="TP02"/&gt;
 *     &lt;enumeration value="TP03"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CondizioniPagamentoType")
@XmlEnum
public enum CondizioniPagamentoType {


    /**
     * pagamento a rate
     * 
     */
    @XmlEnumValue("TP01")
    TP_01("TP01"),

    /**
     * pagamento completo
     * 
     */
    @XmlEnumValue("TP02")
    TP_02("TP02"),

    /**
     * anticipo
     * 
     */
    @XmlEnumValue("TP03")
    TP_03("TP03");
    private final String value;

    CondizioniPagamentoType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CondizioniPagamentoType fromValue(String v) {
        for (CondizioniPagamentoType c: CondizioniPagamentoType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
