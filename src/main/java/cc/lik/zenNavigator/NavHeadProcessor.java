package cc.lik.zenNavigator;

import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.theme.dialect.TemplateHeadProcessor;

@Component
@RequiredArgsConstructor
public class NavHeadProcessor implements TemplateHeadProcessor {

    static final PropertyPlaceholderHelper PROPERTY_PLACEHOLDER_HELPER =
        new PropertyPlaceholderHelper("${", "}");

    private final PluginWrapper pluginWrapper;

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model,
        IElementModelStructureHandler structureHandler) {
        final IModelFactory modelFactory = context.getModelFactory();
        model.add(modelFactory.createText(friendsRssComponentScript()));
        return Mono.empty();
    }

    private String friendsRssComponentScript() {

        final Properties properties = new Properties();
        properties.setProperty("version", pluginWrapper.getDescriptor().getVersion());

        return PROPERTY_PLACEHOLDER_HELPER.replacePlaceholders("""
            <!-- ZenNavigator start -->
            <script defer src="/plugins/ZenNavigator/assets/static/js/nav-scripts.js?version=${version}"></script>
            <script defer src="/plugins/ZenNavigator/assets/static/js/tailwind-config.js?version=${version}"></script>
            <link rel="stylesheet" href="/plugins/ZenNavigator/assets/static/css/nav-styles.css?version=${version}" />
            <link rel="stylesheet" href="/plugins/ZenNavigator/assets/static/css/nav.css?version=${version}" />
            <!-- ZenNavigator end -->
            """, properties);
    }
}
