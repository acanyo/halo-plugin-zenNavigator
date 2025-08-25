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
        private String bgImages; // 支持字符串或数组，按前端脚本兼容
        private Integer transparency; // 0-100
        private Boolean enableSmart;
        private String content; // HTML 片段
    }
}
