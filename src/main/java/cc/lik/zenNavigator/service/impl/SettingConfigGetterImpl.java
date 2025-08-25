package cc.lik.zenNavigator.service.impl;

import cc.lik.zenNavigator.service.SettingConfigGetter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;

@Component
@RequiredArgsConstructor
public class SettingConfigGetterImpl implements SettingConfigGetter {
    private final ReactiveSettingFetcher settingFetcher;

    @Override
    public Mono<BasicsConfig> getBasicsConfig() {
        return settingFetcher.fetch(BasicsConfig.GROUP, BasicsConfig.class)
            .defaultIfEmpty(new BasicsConfig());
    }
}
