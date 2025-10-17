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

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.api.publish.maven.MavenPomDeveloper;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NullMarked
public abstract class PluginPublishConventionsExtension extends PluginDefinition {

    public static final String NAME = "pluginPublishConventions";

    private final Project project;
    private final GradlePluginDevelopmentExtension gradlePlugin;

    List<Action<MavenPomDeveloper>> developers = new ArrayList<>();

    public PluginPublishConventionsExtension(
            Project project,
            GradlePluginDevelopmentExtension gradlePlugin
    ) {
        super(project.getName(), gradlePlugin);
        this.project = project;
        this.gradlePlugin = gradlePlugin;
    }

    public void additionalPlugin(String id, Action<PluginDefinition> action) {
        PluginDefinition plugin = project.getObjects().newInstance(PluginDefinition.class, id, gradlePlugin);
        plugin.id(id);
        plugin.pluginDefinition.getTags().set(getTags());
        action.execute(plugin);
    }

    public void gitHub(String gitHub) {
        gradlePlugin.getVcsUrl().set(gitHub);
        gradlePlugin.getWebsite().convention(gitHub);
    }

    public void website(String website) {
        gradlePlugin.getWebsite().set(website);
    }

    public void developer(Action<MavenPomDeveloper> action) {
        developers.add(action);
    }

    public Provider<String> getId() {
        return project.getProviders().provider(pluginDefinition::getId);
    }

    public Provider<String> getImplementationClass() {
        return project.getProviders().provider(pluginDefinition::getImplementationClass);
    }

    public Provider<String> getDisplayName() {
        return project.getProviders().provider(pluginDefinition::getDisplayName);
    }

    public Provider<String> getDescription() {
        return project.getProviders().provider(pluginDefinition::getDescription);
    }

    public Provider<Set<String>> getTags() {
        return pluginDefinition.getTags();
    }

    public Provider<String> getGitHub() {
        return gradlePlugin.getVcsUrl();
    }

    public Provider<String> getWebsite() {
        return gradlePlugin.getWebsite();
    }
}
