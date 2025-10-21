/*
 * Copyright the GradleX team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradlex.conventions.feature;

import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.provider.SetProperty;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugin.devel.PluginDeclaration;
import org.jspecify.annotations.NullMarked;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Collectors;

@NullMarked
public abstract class PublishingPluginPortalDefinition implements PublishingDefinition {

    private final PluginDeclaration pluginDeclaration;
    private final ProviderFactory providers;

    @Inject
    public PublishingPluginPortalDefinition(String id, GradlePluginDevelopmentExtension gradlePlugin, ProviderFactory providers) {
        this.pluginDeclaration = gradlePlugin.getPlugins().create(toCamelCase(id).replace("org.gradlex.", ""));
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
        var cc = Arrays.stream(s.split("-")).map(segment ->
                segment.substring(0, 1).toUpperCase() + segment.substring(1)).collect(Collectors.joining());
        return cc.substring(0, 1).toLowerCase() + cc.substring(1);
    }
}
