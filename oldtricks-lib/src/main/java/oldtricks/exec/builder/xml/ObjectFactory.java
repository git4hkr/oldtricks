package oldtricks.exec.builder.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the oldtricks.exec.builder.xml package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

	private static final QName CONFIGURATION_QNAME = new QName("http://oldtricks/command-builder", "configuration");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: oldtricks.exec.builder.xml
	 *
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link Cmd }
	 *
	 */
	public Cmd createCmd() {
		return new Cmd();
	}

	/**
	 * Create an instance of {@link Jvm }
	 *
	 */
	public Jvm createJvm() {
		return new Jvm();
	}

	/**
	 * Create an instance of {@link Jvm.Classpath }
	 *
	 */
	public Jvm.Classpath createJvmClasspath() {
		return new Jvm.Classpath();
	}

	/**
	 * Create an instance of {@link Jvm.JvmOption }
	 *
	 */
	public Jvm.JvmOption createJvmJvmOption() {
		return new Jvm.JvmOption();
	}

	/**
	 * Create an instance of {@link Configuration }
	 *
	 */
	public Configuration createConfiguration() {
		return new Configuration();
	}

	/**
	 * Create an instance of {@link Environment }
	 *
	 */
	public Environment createEnvironment() {
		return new Environment();
	}

	/**
	 * Create an instance of {@link AbstractCmd }
	 *
	 */
	public AbstractCmd createAbstractCmd() {
		return new AbstractCmd();
	}

	/**
	 * Create an instance of {@link Entry }
	 *
	 */
	public Entry createEntry() {
		return new Entry();
	}

	/**
	 * Create an instance of {@link Dummy }
	 *
	 */
	public Dummy createDummy() {
		return new Dummy();
	}

	/**
	 * Create an instance of {@link Env }
	 *
	 */
	public Env createEnv() {
		return new Env();
	}

	/**
	 * Create an instance of {@link BooleanValue }
	 *
	 */
	public BooleanValue createBooleanValue() {
		return new BooleanValue();
	}

	/**
	 * Create an instance of {@link SimpleValue }
	 *
	 */
	public SimpleValue createSimpleValue() {
		return new SimpleValue();
	}

	/**
	 * Create an instance of {@link IntValue }
	 *
	 */
	public IntValue createIntValue() {
		return new IntValue();
	}

	/**
	 * Create an instance of {@link Cmd.Cmdline }
	 *
	 */
	public Cmd.Cmdline createCmdCmdline() {
		return new Cmd.Cmdline();
	}

	/**
	 * Create an instance of {@link Jvm.Main }
	 *
	 */
	public Jvm.Main createJvmMain() {
		return new Jvm.Main();
	}

	/**
	 * Create an instance of {@link Jvm.Classpath.AnyJar }
	 *
	 */
	public Jvm.Classpath.AnyJar createJvmClasspathAnyJar() {
		return new Jvm.Classpath.AnyJar();
	}

	/**
	 * Create an instance of {@link Jvm.JvmOption.WithDebug }
	 *
	 */
	public Jvm.JvmOption.WithDebug createJvmJvmOptionWithDebug() {
		return new Jvm.JvmOption.WithDebug();
	}

	/**
	 * Create an instance of {@link Jvm.JvmOption.WithJmx }
	 *
	 */
	public Jvm.JvmOption.WithJmx createJvmJvmOptionWithJmx() {
		return new Jvm.JvmOption.WithJmx();
	}

	/**
	 * Create an instance of {@link Jvm.JvmOption.Custom }
	 *
	 */
	public Jvm.JvmOption.Custom createJvmJvmOptionCustom() {
		return new Jvm.JvmOption.Custom();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link Configuration }
	 * {@code >}
	 *
	 */
	@XmlElementDecl(namespace = "http://oldtricks/command-builder", name = "configuration")
	public JAXBElement<Configuration> createConfiguration(Configuration value) {
		return new JAXBElement<Configuration>(CONFIGURATION_QNAME, Configuration.class, null, value);
	}

}
