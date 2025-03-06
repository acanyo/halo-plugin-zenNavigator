package cc.lik.zenNavigator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Navigation {
    private String siteName;
    private String logo;
    private String theme;
    private String bgImages;
    private String iconfont;
    private String transparency;
    private Boolean enableSmart;
    private String content;
    private List<Category> categories;
}
