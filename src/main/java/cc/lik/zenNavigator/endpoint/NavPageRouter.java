package cc.lik.zenNavigator.endpoint;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static run.halo.app.theme.router.PageUrlUtils.totalPage;

import cc.lik.zenNavigator.finders.NavFinder;
import cc.lik.zenNavigator.service.SettingConfigGetter;
import cc.lik.zenNavigator.vo.NavGroupVo;
import cc.lik.zenNavigator.vo.NavSiteVo;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.pf4j.PluginWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import run.halo.app.theme.TemplateNameResolver;
import run.halo.app.theme.router.PageUrlUtils;
import run.halo.app.theme.router.UrlContextListResult;

/**
 * 提供导航页面的路由处理
 */
@Component
@AllArgsConstructor
public class NavPageRouter {
    
    private static final String GROUP_PARAM = "group";
    
    private final NavFinder navFinder;
    private final SettingConfigGetter settingConfigGetter;
    private final TemplateNameResolver templateNameResolver;
    private final PluginWrapper pluginWrapper;

    /**
     * 提供导航页面路由
     */
    @Bean
    RouterFunction<ServerResponse> navRouterFunction() {
        return route(GET("/nav"),
            handlerFunction()
        );
    }
    
    private HandlerFunction<ServerResponse> handlerFunction() {
        return request -> templateNameResolver.resolveTemplateNameOrDefault(request.exchange(), "nav")
            .flatMap(templateName -> {
                Map<String, Object> model = new java.util.HashMap<>();
                model.put("groups", navGroups());
                model.put("sites", navSites(request));
                model.put("navigation", getNavigationConfig());
                model.put("currentYear", java.time.Year.now().getValue());
                model.put("pluginVersion", pluginWrapper.getDescriptor().getVersion());
                String cg = groupPathQueryParam(request);
                model.put("currentGroup", cg == null ? "" : cg);
                return ServerResponse.ok().render(templateName, model);
            });
    }
    
    private Mono<UrlContextListResult<NavSiteVo>> navSites(ServerRequest request) {
        String path = request.path();
        int pageNum = pageNumInPathVariable(request);
        String group = groupPathQueryParam(request);
        return Mono.just(20)
            .flatMap(pageSize -> navFinder.listSites(pageNum, pageSize)
                .map(list -> {
                    UrlContextListResult.Builder<NavSiteVo> builder = new UrlContextListResult.Builder<>();
                    builder.listResult(list);
                    builder.nextUrl(appendGroupParam(
                        PageUrlUtils.nextPageUrl(path, totalPage(list)), group));
                    builder.prevUrl(appendGroupParam(PageUrlUtils.prevPageUrl(path), group));
                    return builder.build();
                })
            );
    }
    
    private static String appendGroupParam(String path, String group) {
        return UriComponentsBuilder.fromPath(path)
            .queryParamIfPresent(GROUP_PARAM, Optional.ofNullable(group))
            .build()
            .toString();
    }
    
    private int pageNumInPathVariable(ServerRequest request) {
        String page = request.pathVariables().get("page");
        return NumberUtils.toInt(page, 1);
    }
    
    private String groupPathQueryParam(ServerRequest request) {
        return request.queryParam(GROUP_PARAM)
            .filter(StringUtils::isNotBlank)
            .orElse(null);
    }
    
    private Mono<Map<String, Object>> getNavigationConfig() {
        return this.settingConfigGetter.getBasicsConfig()
            .map(config -> {
                Map<String, Object> configMap = new java.util.HashMap<>();
                configMap.put("siteName", config.getNavTitle() != null ? config.getNavTitle() : "导航站");
                configMap.put("logo", config.getNavLogo() != null ? config.getNavLogo() : "");
                configMap.put("bgImages", config.getBgImages() != null ? config.getBgImages() : "");
                configMap.put("theme", "light");
                configMap.put("transparency", config.getTransparency() != null ? config.getTransparency() : 10);
                configMap.put("enableSmart", config.getEnableSmart() != null ? config.getEnableSmart() : true);
                configMap.put("content", config.getContent() != null ? config.getContent() : "");
                boolean showCopyright = !(Boolean.TRUE.equals(config.getDisableCopyright()));
                configMap.put("showCopyright", showCopyright);
                return configMap;
            })
            .defaultIfEmpty(Map.of(
                "siteName", "导航站",
                "logo", "",
                "bgImages", "",
                "theme", "light",
                "transparency", 10,
                "enableSmart", true,
                "content", "",
                "showCopyright", true
            ));
    }
    
    private Mono<List<NavGroupVo>> navGroups() {
        return navFinder.getFullNavigation().collectList();
    }
}


