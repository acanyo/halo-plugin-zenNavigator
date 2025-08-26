package cc.lik.zenNavigator.service.impl;

import static run.halo.app.extension.router.selector.SelectorUtil.labelAndFieldSelectorToListOptions;

import cc.lik.zenNavigator.ZenNavigatorQuery;
import cc.lik.zenNavigator.entity.NavSite;
import cc.lik.zenNavigator.service.NavSiteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.index.query.QueryFactory;

@Component
class NavSiteServiceImpl implements NavSiteService {

    private final ReactiveExtensionClient client;

    public NavSiteServiceImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Mono<ListResult<NavSite>> listSite(ZenNavigatorQuery query) {
        return this.client.listBy(
            NavSite.class,
            toListOptions(query),
            PageRequestImpl.of(query.getPage(), query.getSize(), query.getSort())
        );
    }

    ListOptions toListOptions(ZenNavigatorQuery query) {
        var builder = ListOptions.builder(labelAndFieldSelectorToListOptions(
            query.getLabelSelector(), query.getFieldSelector())
        );

        if (StringUtils.isNotBlank(query.getKeyword())) {
            builder.andQuery(QueryFactory.contains("spec.name", query.getKeyword()));
        }
        if (StringUtils.isNotBlank(query.getGroup())) {
            builder.andQuery(QueryFactory.equal("spec.groupName", query.getGroup()));
        }
        return builder.build();
    }
}
