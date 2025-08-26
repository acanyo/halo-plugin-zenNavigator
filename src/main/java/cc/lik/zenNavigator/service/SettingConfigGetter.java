package cc.lik.zenNavigator.service;

import lombok.Data;
import reactor.core.publisher.Mono;

public interface SettingConfigGetter {
    Mono<BasicsConfig> getBasicsConfig();

    @Data
    class BasicsConfig {
        public static final String GROUP = "basics";

        private String navTitle;
        private String navLogo;
        private String bgImages;
        private Integer transparency;
        private Boolean enableSmart;
        private String content;
    }
}
