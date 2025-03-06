package cc.lik.zenNavigator.service.impl;

import cc.lik.zenNavigator.Constant;
import cc.lik.zenNavigator.dto.BaseConfig;
import cc.lik.zenNavigator.service.NavService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;
import java.util.ArrayList;
import java.util.List;

@Component
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class NavServiceImpl implements NavService {
    private final ReactiveSettingFetcher settingFetcher;

    @Override
    public Mono<BaseConfig> getNavConfigByGroupName() {
        return settingFetcher.get(Constant.CONFIG_BASICES)
            .switchIfEmpty(Mono.error(new RuntimeException("配置不存在")))
            .flatMap(item -> {
                JsonNode categoriesNode = item.path("navCategories");
                List<String> navCategories = new ArrayList<>();
                if (categoriesNode.isArray()) {
                    categoriesNode.forEach(elem -> navCategories.add(elem.asText()));
                }
                BaseConfig config = new BaseConfig(
                    item.path("navTitle").asText("Handsome导航"),
                    item.path("navLogo").asText(),
                    item.path("iconfont").asText(),
                    item.path("bgImages").asText(),
                    item.path("transparency").asText(),
                    item.path("enableSmart").asBoolean(),
                    item.path("content").asText(),
                    navCategories
                );
                return Mono.just(config);
            });
    }
}
