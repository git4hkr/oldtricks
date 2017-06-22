package oldtricks.blogic.mybatis;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j()
public class CustomClassPathMapperScanner {
	static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
	private String resourcePattern = DEFAULT_RESOURCE_PATTERN;
	private ResourcePatternResolver resourcePatternResolver;
	private CachingMetadataReaderFactory metadataReaderFactory;

	public CustomClassPathMapperScanner() {
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
	}

	public Set<String> doScan(String... basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		Set<String> candidates = new HashSet<>();
		for (String basePackage : basePackages) {
			Set<String> __ = findCandidateComponents(basePackage);
			candidates.addAll(__);
		}
		return candidates;
	}

	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
	}

	protected String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(basePackage);
	}

	public Set<String> findCandidateComponents(String basePackage) {
		Set<String> candidate = new HashSet<>();
		try {
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ resolveBasePackage(basePackage) + '/' + this.resourcePattern;
			Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
			boolean traceEnabled = log.isTraceEnabled();
			for (Resource resource : resources) {
				if (traceEnabled) {
					log.trace("Scanning " + resource);
				}
				if (resource.isReadable()) {
					try {
						MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
						for (String anno : metadataReader.getAnnotationMetadata().getAnnotationTypes()) {
							if ("org.apache.ibatis.annotations.Mapper".equals(anno)
									&& metadataReader.getClassMetadata().isInterface()) {
								candidate.add(metadataReader.getClassMetadata().getClassName());
								log.info("Found Mapper interface: {}", metadataReader.getClassMetadata().getClassName());
							}
						}
					} catch (Throwable ex) {
						throw new BeanDefinitionStoreException("Failed to read candidate component class: " + resource,
								ex);
					}
				} else {
					if (traceEnabled) {
						log.trace("Ignored because not readable: " + resource);
					}
				}
			}
		} catch (IOException ex) {
			throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
		}
		return candidate;
	}

}
