// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.feature;

import java.util.Arrays;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.provider.SetProperty;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugin.devel.PluginDeclaration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class PublishingPluginPortalDefinition implements PublishingDefinition {

    private final PluginDeclaration pluginDeclaration;
    private final ProviderFactory providers;

    @Inject
    public PublishingPluginPortalDefinition(
            String id, GradlePluginDevelopmentExtension gradlePlugin, ProviderFactory providers) {
        this.pluginDeclaration =
                gradlePlugin.getPlugins().create(toCamelCase(id).replace("org.gradlex.", ""));
        this.pluginDeclaration.setId(id);
        this.providers = providers;
    }

    public void implementationClass(String implementationClass) {
        pluginDeclaration.setImplementationClass(implementationClass);
    }

    public void displayName(String displayName) {
        pluginDeclaration.setDisplayName(displayName);
    }

    public void description(String description) {
        pluginDeclaration.setDescription(description);
    }

    public void tags(String... tags) {
        pluginDeclaration.getTags().set(Arrays.asList(tags));
    }

    public Provider<String> getId() {
        return providers.provider(pluginDeclaration::getId);
    }

    public Provider<String> getImplementationClass() {
        return providers.provider(pluginDeclaration::getImplementationClass);
    }

    public Provider<String> getDisplayName() {
        return providers.provider(pluginDeclaration::getDisplayName);
    }

    public Provider<String> getDescription() {
        return providers.provider(pluginDeclaration::getDescription);
    }

    public SetProperty<String> getTags() {
        return pluginDeclaration.getTags();
    }

    private static String toCamelCase(String s) {
        var cc = Arrays.stream(s.split("-"))
                .map(segment -> segment.substring(0, 1).toUpperCase() + segment.substring(1))
                .collect(Collectors.joining());
        return cc.substring(0, 1).toLowerCase() + cc.substring(1);
    }
}
