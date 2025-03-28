package cc.lik.zenNavigator.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

/**
 * 导航网址
 * @author Handsome
 * @website https://www.lik.cc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "cc.lik.handsome", version = "v1alpha1",
    kind = "NavSite", plural = "navsite", singular = "navsite")
@Schema(description = "导航网址实体")
public class NavSite extends AbstractExtension {
    private NavSiteSpec spec;
    
    @Data
    @Schema(description = "导航网址规格")
    public static class NavSiteSpec {
        @Schema(description = "网站名称")
        private String name;
        
        @Schema(description = "所属分类名称")
        private String ownerName;
        
        @Schema(description = "网站URL")
        private String url;
        
        @Schema(description = "网站图标")
        private String icon;
        
        @Schema(description = "网站描述")
        private String description;
    }
} 