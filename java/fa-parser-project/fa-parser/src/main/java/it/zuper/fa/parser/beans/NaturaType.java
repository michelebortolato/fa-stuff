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
 * <p>Java class for NaturaType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NaturaType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="N1"/&gt;
 *     &lt;enumeration value="N2"/&gt;
 *     &lt;enumeration value="N3"/&gt;
 *     &lt;enumeration value="N4"/&gt;
 *     &lt;enumeration value="N5"/&gt;
 *     &lt;enumeration value="N6"/&gt;
 *     &lt;enumeration value="N7"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "NaturaType")
@XmlEnum
public enum NaturaType {


    /**
     * Escluse ex. art. 15
     * 
     */
    @XmlEnumValue("N1")
    N_1("N1"),

    /**
     * Non soggette
     * 
     */
    @XmlEnumValue("N2")
    N_2("N2"),

    /**
     * Non Imponibili
     * 
     */
    @XmlEnumValue("N3")
    N_3("N3"),

    /**
     * Esenti
     * 
     */
    @XmlEnumValue("N4")
    N_4("N4"),

    /**
     * Regime del margine
     * 
     */
    @XmlEnumValue("N5")
    N_5("N5"),

    /**
     * Inversione contabile (reverse charge)
     * 
     */
    @XmlEnumValue("N6")
    N_6("N6"),

    /**
     * IVA assolta in altro stato UE (vendite a distanza ex art. 40 commi 3 e 4 e art. 41 comma 1 lett. b, DL 331/93; prestazione di servizi di telecomunicazioni, tele-radiodiffusione ed elettronici ex art. 7-sexies lett. f, g, DPR 633/72 e art. 74-sexies, DPR 633/72)
     * 
     */
    @XmlEnumValue("N7")
    N_7("N7");
    private final String value;

    NaturaType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NaturaType fromValue(String v) {
        for (NaturaType c: NaturaType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
