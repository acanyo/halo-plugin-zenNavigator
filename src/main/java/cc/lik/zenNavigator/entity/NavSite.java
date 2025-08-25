package cc.lik.zenNavigator.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "zenNavigator.lik.cc", version = "v1alpha1", kind = "NavSite",
    plural = "navsite", singular = "navsite")
public class NavSite extends AbstractExtension {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private NavSiteSpec spec;
    @Data
    public static class NavSiteSpec {
        @Schema(description = "网站名称")
        private String name;

        @Schema(description = "网站URL")
        private String url;

        @Schema(description = "网站图标")
        private String icon;
        @Schema(description = "分组名称",requiredMode = Schema.RequiredMode.REQUIRED)
        private String groupName;
        @Schema(description = "网站描述")
        private String description;
    }
}
