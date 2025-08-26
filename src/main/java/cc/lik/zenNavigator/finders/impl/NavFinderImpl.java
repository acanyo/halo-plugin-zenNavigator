package cc.lik.zenNavigator.finders.impl;

import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;
import static run.halo.app.extension.index.query.QueryFactory.equal;

import cc.lik.zenNavigator.entity.NavGroup;
import cc.lik.zenNavigator.entity.NavSite;
import cc.lik.zenNavigator.finders.NavFinder;
import cc.lik.zenNavigator.vo.NavGroupVo;
import cc.lik.zenNavigator.vo.NavSiteVo;
import io.github.resilience4j.core.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.index.query.QueryFactory;

@Component
@RequiredArgsConstructor
public class NavFinderImpl implements NavFinder {
    
    private final ReactiveExtensionClient client;

    @Override
    public Flux<NavGroupVo> listAllGroups() {
        return this.client.listAll(NavGroup.class, ListOptions.builder().build(), defaultGroupSort())
            .map(group -> NavGroupVo.from(group).build());
    }

    @Override
    public Mono<ListResult<NavGroupVo>> listGroups(Integer page, Integer size) {
        return this.client.listBy(NavGroup.class, ListOptions.builder().build(),
                PageRequestImpl.of(page, size, defaultGroupSort()))
            .flatMap(listResult -> Flux.fromStream(listResult.get())
                .map(group -> NavGroupVo.from(group).build())
                .collectList()
                .map(list -> new ListResult<>(
                    listResult.getPage(), listResult.getSize(), listResult.getTotal(), list
                ))
            );
    }

    @Override
    public Mono<NavGroupVo> findGroupByName(String groupName) {
        var options = ListOptions.builder()
            .andQuery(equal("spec.name", groupName))
            .build();
        return this.client.listAll(NavGroup.class, options, defaultGroupSort())
            .next()
            .map(group -> NavGroupVo.from(group).build());
    }

    @Override
    public Flux<NavSiteVo> listAllSites() {
        return this.client.listAll(NavSite.class, ListOptions.builder().build(), defaultSiteSort())
            .map(NavSiteVo::from);
    }

    @Override
    public Mono<ListResult<NavSiteVo>> listSites(Integer page, Integer size) {
        return this.client.listBy(NavSite.class, ListOptions.builder().build(),
                PageRequestImpl.of(page, size, defaultSiteSort()))
            .flatMap(listResult -> Flux.fromStream(listResult.get())
                .map(NavSiteVo::from)
                .collectList()
                .map(list -> new ListResult<>(
                    listResult.getPage(), listResult.getSize(), listResult.getTotal(), list
                ))
            );
    }

    @Override
    public Flux<NavSiteVo> listSitesByGroup(String groupName) {
        var builder = ListOptions.builder();
        if (StringUtils.isNotEmpty(groupName)) {
            builder.andQuery(QueryFactory.equal("spec.groupName", groupName));
        }
        return this.client.listAll(NavSite.class, builder.build(), defaultSiteSort())
            .map(NavSiteVo::from);
    }

    @Override
    public Mono<NavSiteVo> findSiteByName(String siteName) {
        var options = ListOptions.builder()
            .andQuery(equal("spec.name", siteName))
            .build();
        return this.client.listAll(NavSite.class, options, defaultSiteSort())
            .next()
            .map(NavSiteVo::from);
    }

    @Override
    public Flux<NavGroupVo> getFullNavigation() {
        return this.client.listAll(NavGroup.class, ListOptions.builder().build(), defaultGroupSort())
            .concatMap(group -> {
                var builder = NavGroupVo.from(group);
                // 设置站点列表到分类中
                return this.listSitesByGroup(group.getMetadata().getName())
                    .collectList()
                    .doOnNext(builder::sites)
                    .then(Mono.fromSupplier(builder::build));
            });
    }

    /**
     * 分类默认排序
     */
    static Sort defaultGroupSort() {
        return Sort.by(
            asc("spec.priority"),
            desc("metadata.creationTimestamp"),
            asc("metadata.name")
        );
    }

    /**
     * 站点默认排序
     */
    static Sort defaultSiteSort() {
        return Sort.by(
            asc("metadata.creationTimestamp"),
            asc("metadata.name")
        );
    }
}
