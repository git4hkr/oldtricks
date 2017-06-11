package oldtricks.exec.builder.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for abstractCmd complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="abstractCmd">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="workdir" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="inheritIO" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "abstractCmd", namespace = "http://oldtricks/command-builder")
@XmlSeeAlso({ Cmd.class })
public class AbstractCmd {

	@XmlAttribute(name = "id", required = true)
	private String id;
	@XmlAttribute(name = "workdir")
	private String workdir;
	@XmlAttribute(name = "inheritIO")
	private Boolean inheritIO;

	/**
	 * Gets the value of the id property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * Gets the value of the workdir property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getWorkdir() {
		return workdir;
	}

	/**
	 * Sets the value of the workdir property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setWorkdir(String value) {
		this.workdir = value;
	}

	/**
	 * Gets the value of the inheritIO property.
	 *
	 * @return possible object is {@link Boolean }
	 *
	 */
	public Boolean isInheritIO() {
		return inheritIO;
	}

	/**
	 * Sets the value of the inheritIO property.
	 *
	 * @param value
	 *            allowed object is {@link Boolean }
	 *
	 */
	public void setInheritIO(Boolean value) {
		this.inheritIO = value;
	}

}
