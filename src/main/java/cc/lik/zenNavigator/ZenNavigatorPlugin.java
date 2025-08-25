package cc.lik.zenNavigator;

import static run.halo.app.extension.index.IndexAttributeFactory.simpleAttribute;

import cc.lik.zenNavigator.entity.NavCategory;
import org.springframework.stereotype.Component;
import run.halo.app.extension.Scheme;
import run.halo.app.extension.SchemeManager;
import run.halo.app.extension.index.IndexSpec;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;

/**
 * <p>Plugin main class to manage the lifecycle of the plugin.</p>
 * <p>This class must be public and have a public constructor.</p>
 * <p>Only one main class extending {@link BasePlugin} is allowed per plugin.</p>
 *
 * @author guqing
 * @since 1.0.0
 */
@Component
public class ZenNavigatorPlugin extends BasePlugin {
    private final SchemeManager schemeManager;

    public ZenNavigatorPlugin(PluginContext pluginContext, SchemeManager schemeManager) {
        super(pluginContext);
        this.schemeManager = schemeManager;
    }


    @Override
    public void start() {
        schemeManager.register(NavCategory.class, indexSpecs -> {
            indexSpecs.add(new IndexSpec()
                .setName("spec.name")
                .setIndexFunc(
                    simpleAttribute(NavCategory.class, syncFriendPost -> syncFriendPost.getSpec().getName())));
        });
    }

    @Override
    public void stop() {
        Scheme categoryScheme = schemeManager.get(NavCategory.class);
        schemeManager.unregister(categoryScheme);
    }
}
