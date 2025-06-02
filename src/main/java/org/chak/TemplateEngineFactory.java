package org.chak;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

public class TemplateEngineFactory {

    private TemplateEngineFactory() {}

    public static TemplateEngine create(final String templateDir) {
        final FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
        fileTemplateResolver.setPrefix(templateDir);
        fileTemplateResolver.setSuffix(".html");
        fileTemplateResolver.setTemplateMode(TemplateMode.HTML);
        fileTemplateResolver.setCharacterEncoding("UTF-8");

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(fileTemplateResolver);
        return templateEngine;
    }
}
