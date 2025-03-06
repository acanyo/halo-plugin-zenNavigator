package cc.lik.zenNavigator.service;

import cc.lik.zenNavigator.dto.Navigation;
import reactor.core.publisher.Flux;

public interface LinkService {
    /**
     * 根据链接分组名获取链接集合
     */
    Flux<Navigation> getAll();
}
