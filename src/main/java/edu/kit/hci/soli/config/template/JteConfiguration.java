package edu.kit.hci.soli.config.template;

import edu.kit.hci.soli.SoliApplication;
import edu.kit.hci.soli.config.SoliConfiguration;
import edu.kit.hci.soli.service.UserService;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class JteConfiguration {
    @Bean
    public ViewResolver jteViewResolve(TemplateEngine templateEngine, SoliConfiguration soliConfiguration, UserService userService) {
        return new JteViewResolver(templateEngine, soliConfiguration, userService);
    }

    @Bean
    public TemplateEngine templateEngine(SoliConfiguration soliConfiguration) {
        TemplateEngine templateEngine;
        if (soliConfiguration.isDevelopmentMode()) {
            templateEngine = TemplateEngine.create(
                    new DirectoryCodeResolver(Path.of("src", "main", "jte")),
                    Paths.get("jte-classes"),
                    ContentType.Html,
                    SoliApplication.class.getClassLoader()
            );
        } else {
            templateEngine = TemplateEngine.createPrecompiled(
                    null,
                    ContentType.Html,
                    null,
                    "edu.kit.hci.soli.view.jte"
            );
        }
        templateEngine.setBinaryStaticContent(true);
        templateEngine.setTrimControlStructures(true);
        return templateEngine;
    }
}
