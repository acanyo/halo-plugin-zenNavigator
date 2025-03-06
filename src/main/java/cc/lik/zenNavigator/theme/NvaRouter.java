package cc.lik.zenNavigator.theme;

import cc.lik.zenNavigator.service.LinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.theme.TemplateNameResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * 导航路由配置
 * 处理导航页面和API请求
 *
 * @author lik
 * @since 1.0.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class NvaRouter {

    private final LinkService linkSvc;
    private final TemplateNameResolver templateNameResolver;

    /**
     * 配置路由
     * @return 路由函数
     */
    @Bean
    public RouterFunction<ServerResponse> navRouter() {
        return RouterFunctions
                .route()
                .GET("/nav", this::handleNavPage)
                .GET("/api/navigation", this::handleNavApi)
                .build();
    }

    /**
     * 处理导航页面请求
     * @param request 服务请求
     * @return 服务响应
     */
    private Mono<ServerResponse> handleNavPage(ServerRequest request) {
        Map<String, Object> model = new HashMap<>();
        model.put("currentYear", java.time.Year.now().getValue());
        
        return linkSvc.getAll()
            .collectList()
            .flatMap(navigations -> {
                Object navigationData = navigations.isEmpty() ? new HashMap<>() : navigations.get(0);
                model.put("navigation", navigationData);
                
                return templateNameResolver.resolveTemplateNameOrDefault(request.exchange(), "nav")
                    .flatMap(templateName -> ServerResponse.ok().render(templateName, model));
            });
    }

    /**
     * 处理导航API请求
     * @param request 服务请求
     * @return 服务响应
     */
    private Mono<ServerResponse> handleNavApi(ServerRequest request) {
        return linkSvc.getAll()
            .collectList()
            .flatMap(navigations -> {
                Object responseData = navigations.isEmpty() ? new HashMap<>() : navigations.get(0);
                return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(responseData);
            });
    }
}
