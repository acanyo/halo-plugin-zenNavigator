package cc.lik.zenNavigator.service;

import cc.lik.zenNavigator.ZenNavigatorQuery;
import cc.lik.zenNavigator.entity.NavSite;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;


public interface NavSiteService {
    Mono<ListResult<NavSite>> listSite(ZenNavigatorQuery query);
}
