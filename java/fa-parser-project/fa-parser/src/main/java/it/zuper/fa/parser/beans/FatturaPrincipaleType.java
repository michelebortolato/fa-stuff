//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.01.13 at 03:24:23 PM CET 
//


package it.zuper.fa.parser.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for FatturaPrincipaleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FatturaPrincipaleType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NumeroFatturaPrincipale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}String20Type"/&gt;
 *         &lt;element name="DataFatturaPrincipale" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FatturaPrincipaleType", propOrder = {
    "numeroFatturaPrincipale",
    "dataFatturaPrincipale"
})
public class FatturaPrincipaleType {

    @XmlElement(name = "NumeroFatturaPrincipale", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String numeroFatturaPrincipale;
    @XmlElement(name = "DataFatturaPrincipale", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataFatturaPrincipale;

    /**
     * Gets the value of the numeroFatturaPrincipale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroFatturaPrincipale() {
        return numeroFatturaPrincipale;
    }

    /**
     * Sets the value of the numeroFatturaPrincipale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroFatturaPrincipale(String value) {
        this.numeroFatturaPrincipale = value;
    }

    /**
     * Gets the value of the dataFatturaPrincipale property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataFatturaPrincipale() {
        return dataFatturaPrincipale;
    }

    /**
     * Sets the value of the dataFatturaPrincipale property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataFatturaPrincipale(XMLGregorianCalendar value) {
        this.dataFatturaPrincipale = value;
    }

}
