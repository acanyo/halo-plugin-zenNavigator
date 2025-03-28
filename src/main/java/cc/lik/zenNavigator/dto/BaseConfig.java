package cc.lik.zenNavigator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseConfig {
    private String navTitle;
    private String navLogo;
    private String iconfont;
    private String bgImages;
    private String transparency;
    private Boolean enableSmart;
    private String content;
}
