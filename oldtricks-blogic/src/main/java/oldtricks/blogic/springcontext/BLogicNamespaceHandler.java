package oldtricks.blogic.springcontext;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class BLogicNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("annotation-driven", new BLogicBeanDefinitionParser());

	}

	public class BLogicBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {
		@Override
		protected Class<?> getBeanClass(Element element) {
			return BLogicFunctionEnhanceBeanPostProcessor.class;
		}

		@Override
		protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
			String id = super.resolveId(element, definition, parserContext);
			if (!StringUtils.hasText(id)) {
				id = "blogic-annotation-driven";
			}
			return id;
		}
	}

}
