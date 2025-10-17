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

import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugin.devel.PluginDeclaration;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class PluginDefinition {

    protected final PluginDeclaration pluginDefinition;

    @Inject
    public PluginDefinition(String name, GradlePluginDevelopmentExtension gradlePlugin) {
        this.pluginDefinition = gradlePlugin.getPlugins().create(toCamelCase(name).replace("org.gradlex.", ""));
    }

    public void id(String id) {
        pluginDefinition.setId(id);
    }

    public void implementationClass(String implementationClass) {
        pluginDefinition.setImplementationClass(implementationClass);
    }

    public void displayName(String displayName) {
        pluginDefinition.setDisplayName(displayName);
    }

    public void description(String description) {
        pluginDefinition.setDescription(description);
    }

    public void tags(String... tags) {
        pluginDefinition.getTags().set(Arrays.asList(tags));
    }

    private static String toCamelCase(String s) {
        var cc = Arrays.stream(s.split("-")).map(segment ->
                segment.substring(0, 1).toUpperCase() + segment.substring(1)).collect(Collectors.joining());
        return cc.substring(0, 1).toLowerCase() + cc.substring(1);
    }
}
