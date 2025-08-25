package cc.lik.zenNavigator.endpoint;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

import cc.lik.zenNavigator.ZenNavigatorQuery;
import cc.lik.zenNavigator.entity.NavGroup;
import cc.lik.zenNavigator.service.NavGroupService;
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
public class NavGroupEndpoint implements CustomEndpoint {

    private final NavGroupService navGroupService;

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        final var tag = "api.zenNavigator.lik.cc/v1alpha1/Nav";
        return route()
            .GET("navgroups", this::listNavGroups,
                builder -> builder.operationId("ListNavGroups")
                    .tag(tag)
                    .description("List navigation groups.")
                    .response(responseBuilder().implementation(
                        ListResult.generateGenericClass(NavGroup.class)))
            )
            .build();
    }

    @Override
    public GroupVersion groupVersion() {
        return GroupVersion.parseAPIVersion("api.zenNavigator.lik.cc/v1alpha1");
    }

    private Mono<ServerResponse> listNavGroups(ServerRequest serverRequest) {
        var request = new ZenNavigatorQuery(serverRequest.exchange());
        return navGroupService.listNavGroup(request)
            .flatMap(groups -> ServerResponse.ok().bodyValue(groups));
    }
}
