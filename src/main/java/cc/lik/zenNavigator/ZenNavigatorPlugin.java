package cc.lik.zenNavigator;

import static run.halo.app.extension.index.IndexAttributeFactory.simpleAttribute;

import cc.lik.zenNavigator.entity.NavGroup;
import cc.lik.zenNavigator.entity.NavSite;
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
        schemeManager.register(NavSite.class, indexSpecs -> {
            indexSpecs.add(new IndexSpec()
                .setName("spec.name")
                .setIndexFunc(
                    simpleAttribute(NavSite.class, site -> site.getSpec().getName())));
        });
        schemeManager.register(NavSite.class, indexSpecs -> {
            indexSpecs.add(new IndexSpec()
                .setName("spec.url")
                .setIndexFunc(
                    simpleAttribute(NavSite.class, site -> site.getSpec().getName())));
        });
        schemeManager.register(NavSite.class, indexSpecs -> {
            indexSpecs.add(new IndexSpec()
                .setName("spec.icon")
                .setIndexFunc(
                    simpleAttribute(NavSite.class, site -> site.getSpec().getName())));
        });
        schemeManager.register(NavSite.class, indexSpecs -> {
            indexSpecs.add(new IndexSpec()
                .setName("spec.description")
                .setIndexFunc(
                    simpleAttribute(NavSite.class, site -> site.getSpec().getName())));
        });
        schemeManager.register(NavGroup.class, indexSpecs -> {
            indexSpecs.add(new IndexSpec()
                .setName("spec.priority")
                .setIndexFunc(
                    simpleAttribute(NavGroup.class, group -> group.getSpec().getName())));
        });
        schemeManager.register(NavGroup.class, indexSpecs -> {
            indexSpecs.add(new IndexSpec()
                .setName("spec.name")
                .setIndexFunc(
                    simpleAttribute(NavGroup.class, group -> group.getSpec().getName())));
        });
    }

    @Override
    public void stop() {
        Scheme siteScheme = schemeManager.get(NavSite.class);
        Scheme groupScheme = schemeManager.get(NavGroup.class);
        schemeManager.unregister(siteScheme);
        schemeManager.unregister(groupScheme);
    }
}
