package cc.lik.zenNavigator.vo;

import cc.lik.zenNavigator.entity.NavGroup;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;


@Value
@Builder
public class NavGroupVo implements ExtensionVoOperator {
    MetadataOperator metadata;

    NavGroup.NavGroupSpec spec;

    List<NavSiteVo> sites;
    
    public static NavGroupVoBuilder from(NavGroup navGroup) {
        return NavGroupVo.builder()
            .metadata(navGroup.getMetadata())
            .spec(navGroup.getSpec())
            .sites(List.of());
    }
}
