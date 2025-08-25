package cc.lik.zenNavigator.endpoint;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

import cc.lik.zenNavigator.ZenNavigatorQuery;
import cc.lik.zenNavigator.dto.LinkDetailDTO;
import cc.lik.zenNavigator.entity.NavSite;
import cc.lik.zenNavigator.service.NavSiteService;
import cc.lik.zenNavigator.util.LinkRequest;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import run.halo.app.extension.GroupVersion;
import run.halo.app.extension.ListResult;
import run.halo.app.infra.utils.PathUtils;

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
            .GET("link-detail", this::getLinkDetail, builder -> {
                builder.operationId("GetLinkDetail")
                    .description("Get link detail by id")
                    .tag(tag)
                    .parameter(parameterBuilder()
                        .name("url")
                        .description("Link url")
                        .in(ParameterIn.QUERY)
                        .implementation(String.class)
                        .required(true)
                    )
                    .response(responseBuilder().implementation(LinkDetailDTO.class));
            })
            .build();
    }

    @Override
    public GroupVersion groupVersion() {
        return GroupVersion.parseAPIVersion("api.zenNavigator.lik.cc/v1alpha1");
    }
    private Mono<ServerResponse> getLinkDetail(ServerRequest request) {
        final var url = request.queryParam("url")
            .filter(PathUtils::isAbsoluteUri)
            .orElseThrow(() -> new ServerWebInputException("Invalid url."));
        return Mono.fromSupplier(() -> LinkRequest.getLinkDetail(url))
            .subscribeOn(Schedulers.boundedElastic())
            .publishOn(Schedulers.parallel())
            .flatMap(dto -> ServerResponse.ok().bodyValue(dto));
    }
    private Mono<ServerResponse> listNavSites(ServerRequest serverRequest) {
        ZenNavigatorQuery query = new ZenNavigatorQuery(serverRequest.exchange());
        return navSiteService.listSite(query)
            .flatMap(sites -> ServerResponse.ok().bodyValue(sites));
    }

}
