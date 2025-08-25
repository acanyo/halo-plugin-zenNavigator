package cc.lik.zenNavigator.endpoint;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public NavPageRouter(TemplateNameResolver templateNameResolver) {
        this.templateNameResolver = templateNameResolver;
    }

    @Bean
    RouterFunction<ServerResponse> navRouterFunction() {
        return org.springframework.web.reactive.function.server.RouterFunctions.route(
            GET("/nav"), this::renderNavPage);
    }

    Mono<ServerResponse> renderNavPage(ServerRequest request) {
        Map<String, Object> model = new HashMap<>();
        model.put("currentYear", Year.now().getValue());

        Map<String, Object> navigation = new HashMap<>();
        navigation.put("siteName", "导航站");
        navigation.put("logo", "");
        navigation.put("Iconfont", "");
        navigation.put("bgImages", List.of());
        navigation.put("theme", "light");
        navigation.put("transparency", 10);
        navigation.put("enableSmart", true);
        navigation.put("content", "");
        navigation.put("categories", List.of());

        model.put("navigation", navigation);

        return templateNameResolver.resolveTemplateNameOrDefault(
                request.exchange(), "nav")
            .flatMap(templateName -> ServerResponse.ok().render(templateName, model));
    }
}


