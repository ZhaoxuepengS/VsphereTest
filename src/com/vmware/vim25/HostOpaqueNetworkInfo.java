
package com.vmware.vim25;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HostOpaqueNetworkInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HostOpaqueNetworkInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:vim25}DynamicData">
 *       &lt;sequence>
 *         &lt;element name="opaqueNetworkId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="opaqueNetworkName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="opaqueNetworkType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HostOpaqueNetworkInfo", propOrder = {
    "opaqueNetworkId",
    "opaqueNetworkName",
    "opaqueNetworkType"
})
public class HostOpaqueNetworkInfo
    extends DynamicData
{

    @XmlElement(required = true)
    protected String opaqueNetworkId;
    @XmlElement(required = true)
    protected String opaqueNetworkName;
    @XmlElement(required = true)
    protected String opaqueNetworkType;

    /**
     * Gets the value of the opaqueNetworkId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpaqueNetworkId() {
        return opaqueNetworkId;
    }

    /**
     * Sets the value of the opaqueNetworkId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpaqueNetworkId(String value) {
        this.opaqueNetworkId = value;
    }

    /**
     * Gets the value of the opaqueNetworkName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpaqueNetworkName() {
        return opaqueNetworkName;
    }

    /**
     * Sets the value of the opaqueNetworkName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpaqueNetworkName(String value) {
        this.opaqueNetworkName = value;
    }

    /**
     * Gets the value of the opaqueNetworkType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpaqueNetworkType() {
        return opaqueNetworkType;
    }

    /**
     * Sets the value of the opaqueNetworkType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpaqueNetworkType(String value) {
        this.opaqueNetworkType = value;
    }

}
