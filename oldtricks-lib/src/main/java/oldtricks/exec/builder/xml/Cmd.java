package oldtricks.exec.builder.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for cmd complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="cmd">
 *   &lt;complexContent>
 *     &lt;extension base="{http://oldtricks/command-builder}abstractCmd">
 *       &lt;sequence>
 *         &lt;element name="cmdline" minOccurs="0" form="qualified">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="param" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" form="qualified"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="command" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="environment" type="{http://oldtricks/command-builder}environment" minOccurs="0" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmd", namespace = "http://oldtricks/command-builder", propOrder = { "cmdline", "environment" })
@XmlSeeAlso({ Jvm.class, Dummy.class })
public class Cmd extends AbstractCmd {

	@XmlElement(namespace = "http://oldtricks/command-builder")
	private Cmd.Cmdline cmdline;
	@XmlElement(namespace = "http://oldtricks/command-builder")
	private Environment environment;

	/**
	 * Gets the value of the cmdline property.
	 *
	 * @return possible object is {@link Cmd.Cmdline }
	 *
	 */
	public final Cmd.Cmdline getCmdline() {
		return cmdline;
	}

	/**
	 * Sets the value of the cmdline property.
	 *
	 * @param value
	 *            allowed object is {@link Cmd.Cmdline }
	 *
	 */
	public void setCmdline(Cmd.Cmdline value) {
		this.cmdline = value;
	}

	/**
	 * Gets the value of the environment property.
	 *
	 * @return possible object is {@link Environment }
	 *
	 */
	public Environment getEnvironment() {
		return environment;
	}

	/**
	 * Sets the value of the environment property.
	 *
	 * @param value
	 *            allowed object is {@link Environment }
	 *
	 */
	public void setEnvironment(Environment value) {
		this.environment = value;
	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 *
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 *
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="param" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" form="qualified"/>
	 *       &lt;/sequence>
	 *       &lt;attribute name="command" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 *
	 *
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "param" })
	public static class Cmdline {

		@XmlElement(namespace = "http://oldtricks/command-builder", required = true)
		private List<String> param;
		@XmlAttribute(name = "command", required = true)
		private String command;

		/**
		 * Gets the value of the param property.
		 *
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the param property.
		 *
		 * <p>
		 * For example, to add a new item, do as follows:
		 *
		 * <pre>
		 * getParam().add(newItem);
		 * </pre>
		 *
		 *
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link String }
		 *
		 *
		 */
		public List<String> getParam() {
			if (param == null) {
				param = new ArrayList<String>();
			}
			return this.param;
		}

		/**
		 * Gets the value of the command property.
		 *
		 * @return possible object is {@link String }
		 *
		 */
		public String getCommand() {
			return command;
		}

		/**
		 * Sets the value of the command property.
		 *
		 * @param value
		 *            allowed object is {@link String }
		 *
		 */
		public void setCommand(String value) {
			this.command = value;
		}

	}

}
