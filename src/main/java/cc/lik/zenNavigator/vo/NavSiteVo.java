package cc.lik.zenNavigator.vo;

import cc.lik.zenNavigator.entity.NavSite;
import lombok.Builder;
import lombok.Value;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;


@Value
@Builder
public class NavSiteVo implements ExtensionVoOperator {
    
    MetadataOperator metadata;

    NavSite.NavSiteSpec spec;
    
    public static NavSiteVo from(NavSite navSite) {
        return NavSiteVo.builder()
            .metadata(navSite.getMetadata())
            .spec(navSite.getSpec())
            .build();
    }
}
