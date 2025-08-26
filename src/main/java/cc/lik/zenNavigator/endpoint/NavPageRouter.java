package cc.lik.zenNavigator.endpoint;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

import cc.lik.zenNavigator.service.SettingConfigGetter;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.pf4j.PluginWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.theme.TemplateNameResolver;

@Configuration(proxyBeanMethods = false)
public class NavPageRouter {

    private final TemplateNameResolver templateNameResolver;
    private final SettingConfigGetter settingConfigGetter;
    private final PluginWrapper pluginWrapper;

    public NavPageRouter(TemplateNameResolver templateNameResolver, 
                        SettingConfigGetter settingConfigGetter,
                        PluginWrapper pluginWrapper) {
        this.templateNameResolver = templateNameResolver;
        this.settingConfigGetter = settingConfigGetter;
        this.pluginWrapper = pluginWrapper;
    }

    @Bean
    RouterFunction<ServerResponse> navRouterFunction() {
        return org.springframework.web.reactive.function.server.RouterFunctions.route(
            GET("/nav"), this::renderNavPage);
    }

    Mono<ServerResponse> renderNavPage(ServerRequest request) {
        return settingConfigGetter.getBasicsConfig()
            .map(config -> {
                Map<String, Object> model = new HashMap<>();
                model.put("currentYear", Year.now().getValue());
                model.put("pluginVersion", pluginWrapper.getDescriptor().getVersion());

                Map<String, Object> navigation = new HashMap<>();
                navigation.put("siteName", config.getNavTitle() != null ? config.getNavTitle() : "导航站");
                navigation.put("logo", config.getNavLogo() != null ? config.getNavLogo() : "");
                navigation.put("Iconfont", "");
                navigation.put("bgImages", config.getBgImages() != null && !config.getBgImages().trim().isEmpty() ? 
                    List.of(config.getBgImages().trim().split(",")) : List.of());
                navigation.put("theme", "light");
                navigation.put("transparency", config.getTransparency() != null ? config.getTransparency() : 10);
                navigation.put("enableSmart", config.getEnableSmart() != null ? config.getEnableSmart() : true);
                navigation.put("content", config.getContent() != null ? config.getContent() : "");
                navigation.put("categories", List.of());

                model.put("navigation", navigation);
                return model;
            })
            .flatMap(model -> templateNameResolver.resolveTemplateNameOrDefault(
                request.exchange(), "nav")
                .flatMap(templateName -> ServerResponse.ok().render(templateName, model)));
    }
}


