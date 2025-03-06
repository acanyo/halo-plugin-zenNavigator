package cc.lik.zenNavigator.service;

import cc.lik.zenNavigator.dto.BaseConfig;
import reactor.core.publisher.Mono;

public interface NavService {
    /**
     * 根据分组名称获取导航配置
     */
    Mono<BaseConfig> getNavConfigByGroupName();
}
