
package oldtricks.exec.builder.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for configuration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configuration">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="cmd" type="{http://oldtricks/command-builder}cmd" form="qualified"/>
 *           &lt;element name="jvm" type="{http://oldtricks/command-builder}jvm" form="qualified"/>
 *           &lt;element name="dummy" type="{http://oldtricks/command-builder}dummy" form="qualified"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configuration", namespace = "http://oldtricks/command-builder", propOrder = {
    "cmdOrJvmOrDummy"
})
public class Configuration {

    @XmlElements({
        @XmlElement(name = "cmd", namespace = "http://oldtricks/command-builder"),
        @XmlElement(name = "jvm", namespace = "http://oldtricks/command-builder", type = Jvm.class),
        @XmlElement(name = "dummy", namespace = "http://oldtricks/command-builder", type = Dummy.class)
    })
    protected List<Cmd> cmdOrJvmOrDummy;

    /**
     * Gets the value of the cmdOrJvmOrDummy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cmdOrJvmOrDummy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCmdOrJvmOrDummy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Cmd }
     * {@link Jvm }
     * {@link Dummy }
     * 
     * 
     */
    public List<Cmd> getCmdOrJvmOrDummy() {
        if (cmdOrJvmOrDummy == null) {
            cmdOrJvmOrDummy = new ArrayList<Cmd>();
        }
        return this.cmdOrJvmOrDummy;
    }

}
