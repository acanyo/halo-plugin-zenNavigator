package cc.lik.zenNavigator.endpoint;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

import cc.lik.zenNavigator.ZenNavigatorQuery;
import cc.lik.zenNavigator.entity.NavSite;
import cc.lik.zenNavigator.service.NavSiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import run.halo.app.extension.GroupVersion;
import run.halo.app.extension.ListResult;

@Component
@RequiredArgsConstructor
public class NavSiteEndpoint implements CustomEndpoint {

    private final NavSiteService navSiteService;

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        final var tag = "api.zenNavigator.lik.cc/v1alpha1/Nav";
        return route()
            .GET("navsites", this::listNavSites,
                builder -> {
                    builder.operationId("ListNavSites")
                        .description("List navigation sites.")
                        .tag(tag)
                        .response(responseBuilder().implementation(
                            ListResult.generateGenericClass(NavSite.class)));

                    ZenNavigatorQuery.buildParameters(builder);
                }
            )
            .build();
    }

    @Override
    public GroupVersion groupVersion() {
        return GroupVersion.parseAPIVersion("api.zenNavigator.lik.cc/v1alpha1");
    }

    private Mono<ServerResponse> listNavSites(ServerRequest serverRequest) {
        ZenNavigatorQuery query = new ZenNavigatorQuery(serverRequest.exchange());
        return navSiteService.listSite(query)
            .flatMap(sites -> ServerResponse.ok().bodyValue(sites));
    }

}
