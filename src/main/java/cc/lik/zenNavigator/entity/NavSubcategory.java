package cc.lik.zenNavigator.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

/**
 * 二级分类
 * @author Handsome
 * @website https://www.lik.cc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "cc.lik.handsome", version = "v1alpha1",
    kind = "NavSubcategory", plural = "navsubcategory", singular = "navsubcategory")
@Schema(description = "导航子分类实体")
public class NavSubcategory extends AbstractExtension {
    private NavSubcategorySpec spec;
    
    @Data
    @Schema(description = "导航子分类规格")
    public static class NavSubcategorySpec {
        @Schema(description = "所属分类名称")
        private String ownerName;
        
        @Schema(description = "子分类名称")
        private String name;
        
        @Schema(description = "子分类图标")
        private String icon;
    }
} 