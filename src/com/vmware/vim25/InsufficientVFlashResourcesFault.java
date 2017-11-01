
package com.vmware.vim25;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InsufficientVFlashResourcesFault complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsufficientVFlashResourcesFault">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:vim25}InsufficientResourcesFault">
 *       &lt;sequence>
 *         &lt;element name="freeSpace" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="requestedSpace" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsufficientVFlashResourcesFault", propOrder = {
    "freeSpace",
    "requestedSpace"
})
public class InsufficientVFlashResourcesFault
    extends InsufficientResourcesFault
{

    protected long freeSpace;
    protected long requestedSpace;

    /**
     * Gets the value of the freeSpace property.
     * 
     */
    public long getFreeSpace() {
        return freeSpace;
    }

    /**
     * Sets the value of the freeSpace property.
     * 
     */
    public void setFreeSpace(long value) {
        this.freeSpace = value;
    }

    /**
     * Gets the value of the requestedSpace property.
     * 
     */
    public long getRequestedSpace() {
        return requestedSpace;
    }

    /**
     * Sets the value of the requestedSpace property.
     * 
     */
    public void setRequestedSpace(long value) {
        this.requestedSpace = value;
    }

}
