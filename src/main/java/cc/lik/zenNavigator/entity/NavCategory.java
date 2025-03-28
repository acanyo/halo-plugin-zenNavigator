package cc.lik.zenNavigator.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

/**
 * 一级分类
 * @author Handsome
 * @website https://www.lik.cc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "cc.lik.handsome", version = "v1alpha1",
    kind = "NavCategory", plural = "navcategory", singular = "navcategory")
@Schema(description = "导航分类实体")
public class NavCategory extends AbstractExtension {
    private NavCategorySpec spec;
    
    @Data
    @Schema(description = "导航分类规格")
    public static class NavCategorySpec {
        @Schema(description = "分类名称")
        private String name;
        
        @Schema(description = "分类图标")
        private String icon;
        
        @Schema(description = "是否显示在顶部菜单")
        private boolean inTopMenu;
    }
}