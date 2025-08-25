package cc.lik.zenNavigator.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "zenNavigator.lik.cc", version = "v1alpha1", kind = "NavGroup",
    plural = "navgroup", singular = "navgroup")
public class NavGroup extends AbstractExtension {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private NavGroupSpec spec;
    @Data
    public static class NavGroupSpec {
        @Schema(description = "分类名称")
        private String name;

        @Schema(description = "分类图标")
        private String icon;

        @Schema(description = "是否显示在顶部菜单")
        private boolean inTopMenu;

        @Schema(description = "优先级")
        private Integer priority;
    }
}
