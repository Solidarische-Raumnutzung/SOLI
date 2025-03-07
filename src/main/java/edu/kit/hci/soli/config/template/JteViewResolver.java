package edu.kit.hci.soli.config.template;

import edu.kit.hci.soli.config.SoliConfiguration;
import edu.kit.hci.soli.service.UserService;
import gg.jte.TemplateEngine;
import lombok.NonNull;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public class JteViewResolver extends AbstractTemplateViewResolver {
    private final TemplateEngine templateEngine;
    private final SoliConfiguration soliConfiguration;
    private final UserService userService;

    public JteViewResolver(TemplateEngine templateEngine, SoliConfiguration soliConfiguration, UserService userService) {
        this.templateEngine = templateEngine;
        this.soliConfiguration = soliConfiguration;
        this.userService = userService;
        this.setViewClass(this.requiredViewClass());
        this.setSuffix(".jte");
        this.setViewClass(JteView.class);
        this.setContentType(MediaType.TEXT_HTML_VALUE);
        this.setOrder(Ordered.HIGHEST_PRECEDENCE);
        this.setExposeRequestAttributes(false);
    }

    @Override
    protected @NonNull AbstractUrlBasedView instantiateView() {
        return new JteView(templateEngine, soliConfiguration, userService);
    }

    @Override
    protected @NonNull Class<?> requiredViewClass() {
        return JteView.class;
    }
}
