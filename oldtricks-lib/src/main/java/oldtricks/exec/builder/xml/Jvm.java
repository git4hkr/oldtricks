package oldtricks.exec.builder.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for jvm complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="jvm">
 *   &lt;complexContent>
 *     &lt;extension base="{http://oldtricks/command-builder}cmd">
 *       &lt;sequence>
 *         &lt;element name="jvmOption" minOccurs="0" form="qualified">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="server" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *                   &lt;element name="useConcMarkSweepGC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *                   &lt;element name="stackSize" type="{http://oldtricks/command-builder}simpleValue" minOccurs="0" form="qualified"/>
 *                   &lt;element name="survivorRatio" type="{http://oldtricks/command-builder}simpleValue" minOccurs="0" form="qualified"/>
 *                   &lt;element name="startHeapSize" type="{http://oldtricks/command-builder}simpleValue" minOccurs="0" form="qualified"/>
 *                   &lt;element name="maxHeapSize" type="{http://oldtricks/command-builder}simpleValue" minOccurs="0" form="qualified"/>
 *                   &lt;element name="newSize" type="{http://oldtricks/command-builder}simpleValue" minOccurs="0" form="qualified"/>
 *                   &lt;element name="maxPermSize" type="{http://oldtricks/command-builder}simpleValue" minOccurs="0" form="qualified"/>
 *                   &lt;element name="withDebug" minOccurs="0" form="qualified">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                           &lt;/sequence>
 *                           &lt;attribute name="port" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="suspend" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="withGcLog" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *                   &lt;element name="withJmx" minOccurs="0" form="qualified">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                           &lt;/sequence>
 *                           &lt;attribute name="port" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="custom" minOccurs="0" form="qualified">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" form="qualified"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="verifyNone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="classpath" minOccurs="0" form="qualified">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="anyJar" maxOccurs="unbounded" minOccurs="0" form="qualified">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                           &lt;/sequence>
 *                           &lt;attribute name="jarDirectory" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0" form="qualified"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="main" minOccurs="0" form="qualified">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="param" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0" form="qualified"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="class" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="executableJar" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "jvm", namespace = "http://oldtricks/command-builder", propOrder = { "jvmOption", "classpath", "main" })
public class Jvm extends Cmd {

	@XmlElement(namespace = "http://oldtricks/command-builder")
	private Jvm.JvmOption jvmOption;
	@XmlElement(namespace = "http://oldtricks/command-builder")
	private Jvm.Classpath classpath;
	@XmlElement(namespace = "http://oldtricks/command-builder")
	private Jvm.Main main;

	/**
	 * Gets the value of the jvmOption property.
	 *
	 * @return possible object is {@link Jvm.JvmOption }
	 *
	 */
	public Jvm.JvmOption getJvmOption() {
		return jvmOption;
	}

	/**
	 * Sets the value of the jvmOption property.
	 *
	 * @param value
	 *            allowed object is {@link Jvm.JvmOption }
	 *
	 */
	public void setJvmOption(Jvm.JvmOption value) {
		this.jvmOption = value;
	}

	/**
	 * Gets the value of the classpath property.
	 *
	 * @return possible object is {@link Jvm.Classpath }
	 *
	 */
	public Jvm.Classpath getClasspath() {
		return classpath;
	}

	/**
	 * Sets the value of the classpath property.
	 *
	 * @param value
	 *            allowed object is {@link Jvm.Classpath }
	 *
	 */
	public void setClasspath(Jvm.Classpath value) {
		this.classpath = value;
	}

	/**
	 * Gets the value of the main property.
	 *
	 * @return possible object is {@link Jvm.Main }
	 *
	 */
	public Jvm.Main getMain() {
		return main;
	}

	/**
	 * Sets the value of the main property.
	 *
	 * @param value
	 *            allowed object is {@link Jvm.Main }
	 *
	 */
	public void setMain(Jvm.Main value) {
		this.main = value;
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
	 *         &lt;element name="anyJar" maxOccurs="unbounded" minOccurs="0" form="qualified">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                 &lt;/sequence>
	 *                 &lt;attribute name="jarDirectory" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0" form="qualified"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 *
	 *
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "anyJar", "value" })
	public static class Classpath {

		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected List<Jvm.Classpath.AnyJar> anyJar;
		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected List<String> value;

		/**
		 * Gets the value of the anyJar property.
		 *
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the anyJar property.
		 *
		 * <p>
		 * For example, to add a new item, do as follows:
		 *
		 * <pre>
		 * getAnyJar().add(newItem);
		 * </pre>
		 *
		 *
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link Jvm.Classpath.AnyJar }
		 *
		 *
		 */
		public List<Jvm.Classpath.AnyJar> getAnyJar() {
			if (anyJar == null) {
				anyJar = new ArrayList<Jvm.Classpath.AnyJar>();
			}
			return this.anyJar;
		}

		/**
		 * Gets the value of the value property.
		 *
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the value property.
		 *
		 * <p>
		 * For example, to add a new item, do as follows:
		 *
		 * <pre>
		 * getValue().add(newItem);
		 * </pre>
		 *
		 *
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link String }
		 *
		 *
		 */
		public List<String> getValue() {
			if (value == null) {
				value = new ArrayList<String>();
			}
			return this.value;
		}

		/**
		 * <p>
		 * Java class for anonymous complex type.
		 *
		 * <p>
		 * The following schema fragment specifies the expected content
		 * contained within this class.
		 *
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
		 *       &lt;/sequence>
		 *       &lt;attribute name="jarDirectory" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 *
		 *
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "")
		public static class AnyJar {

			@XmlAttribute(name = "jarDirectory")
			protected String jarDirectory;

			/**
			 * Gets the value of the jarDirectory property.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getJarDirectory() {
				return jarDirectory;
			}

			/**
			 * Sets the value of the jarDirectory property.
			 *
			 * @param value
			 *            allowed object is {@link String }
			 *
			 */
			public void setJarDirectory(String value) {
				this.jarDirectory = value;
			}

		}

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
	 *       &lt;all>
	 *         &lt;element name="server" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
	 *         &lt;element name="useConcMarkSweepGC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
	 *         &lt;element name="stackSize" type="{http://oldtricks/command-builder}simpleValue" minOccurs="0" form="qualified"/>
	 *         &lt;element name="survivorRatio" type="{http://oldtricks/command-builder}simpleValue" minOccurs="0" form="qualified"/>
	 *         &lt;element name="startHeapSize" type="{http://oldtricks/command-builder}simpleValue" minOccurs="0" form="qualified"/>
	 *         &lt;element name="maxHeapSize" type="{http://oldtricks/command-builder}simpleValue" minOccurs="0" form="qualified"/>
	 *         &lt;element name="newSize" type="{http://oldtricks/command-builder}simpleValue" minOccurs="0" form="qualified"/>
	 *         &lt;element name="maxPermSize" type="{http://oldtricks/command-builder}simpleValue" minOccurs="0" form="qualified"/>
	 *         &lt;element name="withDebug" minOccurs="0" form="qualified">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                 &lt;/sequence>
	 *                 &lt;attribute name="port" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *                 &lt;attribute name="suspend" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *         &lt;element name="withGcLog" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
	 *         &lt;element name="withJmx" minOccurs="0" form="qualified">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                 &lt;/sequence>
	 *                 &lt;attribute name="port" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *         &lt;element name="custom" minOccurs="0" form="qualified">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" form="qualified"/>
	 *                 &lt;/sequence>
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *         &lt;element name="verifyNone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
	 *       &lt;/all>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 *
	 *
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {

	})
	public static class JvmOption {

		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected String server;
		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected String useConcMarkSweepGC;
		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected SimpleValue stackSize;
		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected SimpleValue survivorRatio;
		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected SimpleValue startHeapSize;
		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected SimpleValue maxHeapSize;
		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected SimpleValue newSize;
		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected SimpleValue maxPermSize;
		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected Jvm.JvmOption.WithDebug withDebug;
		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected String withGcLog;
		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected Jvm.JvmOption.WithJmx withJmx;
		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected Jvm.JvmOption.Custom custom;
		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected String verifyNone;

		/**
		 * Gets the value of the server property.
		 *
		 * @return possible object is {@link String }
		 *
		 */
		public String getServer() {
			return server;
		}

		/**
		 * Sets the value of the server property.
		 *
		 * @param value
		 *            allowed object is {@link String }
		 *
		 */
		public void setServer(String value) {
			this.server = value;
		}

		/**
		 * Gets the value of the useConcMarkSweepGC property.
		 *
		 * @return possible object is {@link String }
		 *
		 */
		public String getUseConcMarkSweepGC() {
			return useConcMarkSweepGC;
		}

		/**
		 * Sets the value of the useConcMarkSweepGC property.
		 *
		 * @param value
		 *            allowed object is {@link String }
		 *
		 */
		public void setUseConcMarkSweepGC(String value) {
			this.useConcMarkSweepGC = value;
		}

		/**
		 * Gets the value of the stackSize property.
		 *
		 * @return possible object is {@link SimpleValue }
		 *
		 */
		public SimpleValue getStackSize() {
			return stackSize;
		}

		/**
		 * Sets the value of the stackSize property.
		 *
		 * @param value
		 *            allowed object is {@link SimpleValue }
		 *
		 */
		public void setStackSize(SimpleValue value) {
			this.stackSize = value;
		}

		/**
		 * Gets the value of the survivorRatio property.
		 *
		 * @return possible object is {@link SimpleValue }
		 *
		 */
		public SimpleValue getSurvivorRatio() {
			return survivorRatio;
		}

		/**
		 * Sets the value of the survivorRatio property.
		 *
		 * @param value
		 *            allowed object is {@link SimpleValue }
		 *
		 */
		public void setSurvivorRatio(SimpleValue value) {
			this.survivorRatio = value;
		}

		/**
		 * Gets the value of the startHeapSize property.
		 *
		 * @return possible object is {@link SimpleValue }
		 *
		 */
		public SimpleValue getStartHeapSize() {
			return startHeapSize;
		}

		/**
		 * Sets the value of the startHeapSize property.
		 *
		 * @param value
		 *            allowed object is {@link SimpleValue }
		 *
		 */
		public void setStartHeapSize(SimpleValue value) {
			this.startHeapSize = value;
		}

		/**
		 * Gets the value of the maxHeapSize property.
		 *
		 * @return possible object is {@link SimpleValue }
		 *
		 */
		public SimpleValue getMaxHeapSize() {
			return maxHeapSize;
		}

		/**
		 * Sets the value of the maxHeapSize property.
		 *
		 * @param value
		 *            allowed object is {@link SimpleValue }
		 *
		 */
		public void setMaxHeapSize(SimpleValue value) {
			this.maxHeapSize = value;
		}

		/**
		 * Gets the value of the newSize property.
		 *
		 * @return possible object is {@link SimpleValue }
		 *
		 */
		public SimpleValue getNewSize() {
			return newSize;
		}

		/**
		 * Sets the value of the newSize property.
		 *
		 * @param value
		 *            allowed object is {@link SimpleValue }
		 *
		 */
		public void setNewSize(SimpleValue value) {
			this.newSize = value;
		}

		/**
		 * Gets the value of the maxPermSize property.
		 *
		 * @return possible object is {@link SimpleValue }
		 *
		 */
		public SimpleValue getMaxPermSize() {
			return maxPermSize;
		}

		/**
		 * Sets the value of the maxPermSize property.
		 *
		 * @param value
		 *            allowed object is {@link SimpleValue }
		 *
		 */
		public void setMaxPermSize(SimpleValue value) {
			this.maxPermSize = value;
		}

		/**
		 * Gets the value of the withDebug property.
		 *
		 * @return possible object is {@link Jvm.JvmOption.WithDebug }
		 *
		 */
		public Jvm.JvmOption.WithDebug getWithDebug() {
			return withDebug;
		}

		/**
		 * Sets the value of the withDebug property.
		 *
		 * @param value
		 *            allowed object is {@link Jvm.JvmOption.WithDebug }
		 *
		 */
		public void setWithDebug(Jvm.JvmOption.WithDebug value) {
			this.withDebug = value;
		}

		/**
		 * Gets the value of the withGcLog property.
		 *
		 * @return possible object is {@link String }
		 *
		 */
		public String getWithGcLog() {
			return withGcLog;
		}

		/**
		 * Sets the value of the withGcLog property.
		 *
		 * @param value
		 *            allowed object is {@link String }
		 *
		 */
		public void setWithGcLog(String value) {
			this.withGcLog = value;
		}

		/**
		 * Gets the value of the withJmx property.
		 *
		 * @return possible object is {@link Jvm.JvmOption.WithJmx }
		 *
		 */
		public Jvm.JvmOption.WithJmx getWithJmx() {
			return withJmx;
		}

		/**
		 * Sets the value of the withJmx property.
		 *
		 * @param value
		 *            allowed object is {@link Jvm.JvmOption.WithJmx }
		 *
		 */
		public void setWithJmx(Jvm.JvmOption.WithJmx value) {
			this.withJmx = value;
		}

		/**
		 * Gets the value of the custom property.
		 *
		 * @return possible object is {@link Jvm.JvmOption.Custom }
		 *
		 */
		public Jvm.JvmOption.Custom getCustom() {
			return custom;
		}

		/**
		 * Sets the value of the custom property.
		 *
		 * @param value
		 *            allowed object is {@link Jvm.JvmOption.Custom }
		 *
		 */
		public void setCustom(Jvm.JvmOption.Custom value) {
			this.custom = value;
		}

		/**
		 * Gets the value of the verifyNone property.
		 *
		 * @return possible object is {@link String }
		 *
		 */
		public String getVerifyNone() {
			return verifyNone;
		}

		/**
		 * Sets the value of the verifyNone property.
		 *
		 * @param value
		 *            allowed object is {@link String }
		 *
		 */
		public void setVerifyNone(String value) {
			this.verifyNone = value;
		}

		/**
		 * <p>
		 * Java class for anonymous complex type.
		 *
		 * <p>
		 * The following schema fragment specifies the expected content
		 * contained within this class.
		 *
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
		 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" form="qualified"/>
		 *       &lt;/sequence>
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 *
		 *
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "value" })
		public static class Custom {

			@XmlElement(namespace = "http://oldtricks/command-builder", required = true)
			protected List<String> value;

			/**
			 * Gets the value of the value property.
			 *
			 * <p>
			 * This accessor method returns a reference to the live list, not a
			 * snapshot. Therefore any modification you make to the returned
			 * list will be present inside the JAXB object. This is why there is
			 * not a <CODE>set</CODE> method for the value property.
			 *
			 * <p>
			 * For example, to add a new item, do as follows:
			 *
			 * <pre>
			 * getValue().add(newItem);
			 * </pre>
			 *
			 *
			 * <p>
			 * Objects of the following type(s) are allowed in the list
			 * {@link String }
			 *
			 *
			 */
			public List<String> getValue() {
				if (value == null) {
					value = new ArrayList<String>();
				}
				return this.value;
			}

		}

		/**
		 * <p>
		 * Java class for anonymous complex type.
		 *
		 * <p>
		 * The following schema fragment specifies the expected content
		 * contained within this class.
		 *
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
		 *       &lt;/sequence>
		 *       &lt;attribute name="port" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *       &lt;attribute name="suspend" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 *
		 *
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "")
		public static class WithDebug {

			@XmlAttribute(name = "port")
			protected String port;
			@XmlAttribute(name = "suspend")
			protected String suspend;

			/**
			 * Gets the value of the port property.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getPort() {
				return port;
			}

			/**
			 * Sets the value of the port property.
			 *
			 * @param value
			 *            allowed object is {@link String }
			 *
			 */
			public void setPort(String value) {
				this.port = value;
			}

			/**
			 * Gets the value of the suspend property.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getSuspend() {
				return suspend;
			}

			/**
			 * Sets the value of the suspend property.
			 *
			 * @param value
			 *            allowed object is {@link String }
			 *
			 */
			public void setSuspend(String value) {
				this.suspend = value;
			}

		}

		/**
		 * <p>
		 * Java class for anonymous complex type.
		 *
		 * <p>
		 * The following schema fragment specifies the expected content
		 * contained within this class.
		 *
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
		 *       &lt;/sequence>
		 *       &lt;attribute name="port" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 *
		 *
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "")
		public static class WithJmx {

			@XmlAttribute(name = "port")
			protected String port;

			/**
			 * Gets the value of the port property.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getPort() {
				return port;
			}

			/**
			 * Sets the value of the port property.
			 *
			 * @param value
			 *            allowed object is {@link String }
			 *
			 */
			public void setPort(String value) {
				this.port = value;
			}

		}

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
	 *         &lt;element name="param" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0" form="qualified"/>
	 *       &lt;/sequence>
	 *       &lt;attribute name="class" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *       &lt;attribute name="executableJar" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 *
	 *
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "param" })
	public static class Main {

		@XmlElement(namespace = "http://oldtricks/command-builder")
		protected List<String> param;
		@XmlAttribute(name = "class")
		protected String clazz;
		@XmlAttribute(name = "executableJar")
		protected String executableJar;

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
		 * Gets the value of the clazz property.
		 *
		 * @return possible object is {@link String }
		 *
		 */
		public String getClazz() {
			return clazz;
		}

		/**
		 * Sets the value of the clazz property.
		 *
		 * @param value
		 *            allowed object is {@link String }
		 *
		 */
		public void setClazz(String value) {
			this.clazz = value;
		}

		/**
		 * Gets the value of the executableJar property.
		 *
		 * @return possible object is {@link String }
		 *
		 */
		public String getExecutableJar() {
			return executableJar;
		}

		/**
		 * Sets the value of the executableJar property.
		 *
		 * @param value
		 *            allowed object is {@link String }
		 *
		 */
		public void setExecutableJar(String value) {
			this.executableJar = value;
		}

	}

}
