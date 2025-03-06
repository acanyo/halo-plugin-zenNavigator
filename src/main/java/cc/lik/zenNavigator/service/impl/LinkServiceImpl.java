package cc.lik.zenNavigator.service.impl;

import cc.lik.zenNavigator.dto.BaseConfig;
import cc.lik.zenNavigator.dto.Category;
import cc.lik.zenNavigator.dto.Navigation;
import cc.lik.zenNavigator.dto.Site;
import cc.lik.zenNavigator.dto.Subcategory;
import cc.lik.zenNavigator.entity.Link;
import cc.lik.zenNavigator.entity.LinkGroup;
import cc.lik.zenNavigator.service.LinkService;
import cc.lik.zenNavigator.service.NavService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ReactiveExtensionClient;

@Component
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class LinkServiceImpl implements LinkService {
    private final ReactiveExtensionClient client;
    private final NavService navSvc;

    @Override
    public Flux<Navigation> getAll() {
        // 获取所有数据
        Flux<Link> linkFlux = client.listAll(Link.class, new ListOptions(), null);
        Flux<LinkGroup> linkGroupFlux = client.listAll(LinkGroup.class, new ListOptions(), null);
        Mono<BaseConfig> navConfigMono = navSvc.getNavConfigByGroupName();

        // 组装导航数据
        return navConfigMono.flatMapMany(navConfig -> {
            // 创建 Navigation 对象
            Navigation navigation = createNavigation(navConfig);
            
            // 如果没有分类，返回空导航
            List<String> navCategories = navConfig.getNavCategories();
            if (navCategories == null || navCategories.isEmpty()) {
                return Flux.just(navigation);
            }

            // 处理链接和分组数据
            return linkGroupFlux.collectList()
                .flatMapMany(linkGroups -> {
                    Map<String, LinkGroup> linkGroupMap = createLinkGroupMap(linkGroups);
                    
                    return linkFlux.collectList()
                        .flatMapMany(links -> {
                            Map<String, List<Link>> linkMap = createLinkMap(links);
                            List<Category> categories = createCategories(navCategories, linkGroupMap, linkMap);
                            navigation.setCategories(categories);
                            return Flux.just(navigation);
                        });
                });
        });
    }
    
    private Navigation createNavigation(BaseConfig navConfig) {
        Navigation navigation = new Navigation();
        navigation.setSiteName(navConfig.getNavTitle());
        navigation.setLogo(navConfig.getNavLogo());
        navigation.setBgImages(navConfig.getBgImages());
        navigation.setTheme("light");
        navigation.setIconfont(navConfig.getIconfont());
        navigation.setTransparency(navConfig.getTransparency());
        navigation.setEnableSmart(navConfig.getEnableSmart());
        navigation.setContent(navConfig.getContent());
        return navigation;
    }
    
    private Map<String, LinkGroup> createLinkGroupMap(List<LinkGroup> linkGroups) {
        return linkGroups.stream()
            .collect(Collectors.toMap(
                group -> group.getMetadata().getName(),
                group -> group,
                (g1, g2) -> g1
            ));
    }
    
    private Map<String, List<Link>> createLinkMap(List<Link> links) {
        return links.stream()
            .collect(Collectors.groupingBy(
                link -> link.getSpec().getGroupName()
            ));
    }
    
    private List<Category> createCategories(List<String> navCategories, 
                                          Map<String, LinkGroup> linkGroupMap, 
                                          Map<String, List<Link>> linkMap) {
        List<Category> categories = new ArrayList<>();
        
        for (String categoryName : navCategories) {
            LinkGroup linkGroup = linkGroupMap.get(categoryName);
            if (linkGroup == null) continue;
            
            Category category = createCategory(linkGroup);
            List<Subcategory> subcategories = createSubcategories(linkGroup, linkMap.getOrDefault(categoryName, new ArrayList<>()));
            category.setSubcategories(subcategories);
            categories.add(category);
        }
        
        return categories;
    }
    
    private Category createCategory(LinkGroup linkGroup) {
        Category category = new Category();
        category.setId(linkGroup.getMetadata().getName());
        category.setName(linkGroup.getSpec().getDisplayName());
        
        Map<String, String> annotations = linkGroup.getMetadata().getAnnotations();
        if (annotations != null) {
            category.setIcon(annotations.getOrDefault("catIcon", "ri-folder-line"));
            category.setInTopMenu(Boolean.parseBoolean(annotations.getOrDefault("inTopMenu", "true")));
        } else {
            category.setIcon("ri-folder-line");
            category.setInTopMenu(true);
        }
        
        return category;
    }
    
    private List<Subcategory> createSubcategories(LinkGroup linkGroup, List<Link> groupLinks) {
        // 按catName分组链接
        Map<String, List<Link>> catNameMap = groupLinks.stream()
            .collect(Collectors.groupingBy(link -> 
                link.getMetadata().getAnnotations() != null ? 
                link.getMetadata().getAnnotations().getOrDefault("catName", "默认分类") : 
                "默认分类"
            ));
        
        // 创建子分类列表
        return catNameMap.entrySet().stream()
            .map(entry -> createSubcategory(entry.getKey(), entry.getValue(), linkGroup))
            .collect(Collectors.toList());
    }
    
    private Subcategory createSubcategory(String catName, List<Link> catLinks, LinkGroup linkGroup) {
        Subcategory subcategory = new Subcategory();
        subcategory.setId(UUID.randomUUID().toString());
        subcategory.setOwner_id(linkGroup.getMetadata().getName());
        subcategory.setName(catName);
        
        // 设置图标
        if (!catLinks.isEmpty() && catLinks.get(0).getMetadata().getAnnotations() != null) {
            subcategory.setIcon(catLinks.get(0).getMetadata().getAnnotations().get("catIcon2"));
        } else {
            subcategory.setIcon("ri-folder-line");
        }
        
        // 转换链接为站点
        List<Site> sites = catLinks.stream()
            .map(this::convertLinkToSite)
            .collect(Collectors.toList());
        
        subcategory.setSites(sites);
        return subcategory;
    }
    
    private Site convertLinkToSite(Link link) {
        Site site = new Site();
        site.setName(link.getSpec().getDisplayName());
        site.setUrl(link.getSpec().getUrl());
        site.setIcon(link.getSpec().getLogo());
        site.setDescription(link.getSpec().getDescription());
        return site;
    }
}
