package oldtricks.exec.builder.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for dummy complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="dummy">
 *   &lt;complexContent>
 *     &lt;extension base="{http://oldtricks/command-builder}cmd">
 *       &lt;sequence>
 *         &lt;element name="stdout" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="stderr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *       &lt;/sequence>
 *       &lt;attribute name="exitCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sleepMs" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="handler" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dummy", namespace = "http://oldtricks/command-builder", propOrder = { "stdout", "stderr" })
public class Dummy extends Cmd {

	@XmlElement(namespace = "http://oldtricks/command-builder")
	protected String stdout;
	@XmlElement(namespace = "http://oldtricks/command-builder")
	protected String stderr;
	@XmlAttribute(name = "exitCode")
	protected String exitCode;
	@XmlAttribute(name = "sleepMs")
	protected String sleepMs;
	@XmlAttribute(name = "handler")
	protected String handler;

	/**
	 * Gets the value of the stdout property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getStdout() {
		return stdout;
	}

	/**
	 * Sets the value of the stdout property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setStdout(String value) {
		this.stdout = value;
	}

	/**
	 * Gets the value of the stderr property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getStderr() {
		return stderr;
	}

	/**
	 * Sets the value of the stderr property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setStderr(String value) {
		this.stderr = value;
	}

	/**
	 * Gets the value of the exitCode property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getExitCode() {
		return exitCode;
	}

	/**
	 * Sets the value of the exitCode property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setExitCode(String value) {
		this.exitCode = value;
	}

	/**
	 * Gets the value of the sleepMs property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getSleepMs() {
		return sleepMs;
	}

	/**
	 * Sets the value of the sleepMs property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setSleepMs(String value) {
		this.sleepMs = value;
	}

	/**
	 * Gets the value of the handler property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getHandler() {
		return handler;
	}

	/**
	 * Sets the value of the handler property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setHandler(String value) {
		this.handler = value;
	}

}
