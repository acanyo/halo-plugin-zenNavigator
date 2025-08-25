package cc.lik.zenNavigator.service.impl;

import static run.halo.app.extension.router.selector.SelectorUtil.labelAndFieldSelectorToListOptions;

import cc.lik.zenNavigator.ZenNavigatorQuery;
import cc.lik.zenNavigator.entity.NavGroup;
import cc.lik.zenNavigator.service.NavGroupService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.ReactiveExtensionClient;


@Component
class NavGroupServiceImpl implements NavGroupService {

    private final ReactiveExtensionClient client;

    public NavGroupServiceImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Mono<ListResult<NavGroup>> listNavGroup(ZenNavigatorQuery query) {
        return this.client.listBy(
                NavGroup.class,
                toListOptions(query),
                PageRequestImpl.of(query.getPage(), query.getSize(), query.getSort())
            )
            .flatMap(listResult -> Flux.fromStream(listResult.get())
                .collectList()
                .map(groups -> new ListResult<>(
                    listResult.getPage(),
                    listResult.getSize(),
                    listResult.getTotal(),
                    groups
                ))
            );
    }

    ListOptions toListOptions(ZenNavigatorQuery query) {
        return labelAndFieldSelectorToListOptions(
            query.getLabelSelector(), query.getFieldSelector()
        );
    }
}
