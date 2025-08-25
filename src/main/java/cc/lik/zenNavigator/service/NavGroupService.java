package cc.lik.zenNavigator.service;

import cc.lik.zenNavigator.ZenNavigatorQuery;
import cc.lik.zenNavigator.entity.NavGroup;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;

public interface NavGroupService {
    
    Mono<ListResult<NavGroup>> listNavGroup(ZenNavigatorQuery request);
    
}
