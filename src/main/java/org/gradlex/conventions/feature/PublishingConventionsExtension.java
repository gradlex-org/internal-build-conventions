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
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.api.publish.maven.MavenPomDeveloper;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public abstract class PublishingConventionsExtension {

    public static final String NAME = "publishingConventions";

    private final Project project;
    private final GradlePluginDevelopmentExtension gradlePlugin;
    private final List<PublishingDefinition> definitions = new ArrayList<>();

    List<Action<MavenPomDeveloper>> developers = new ArrayList<>();

    public PublishingConventionsExtension(
            Project project,
            GradlePluginDevelopmentExtension gradlePlugin
    ) {
        this.project = project;
        this.gradlePlugin = gradlePlugin;
    }

    public void pluginPortal(String id, Action<PublishingPluginPortalDefinition> action) {
        var definition = project.getObjects().newInstance(
                PublishingPluginPortalDefinition.class, id, gradlePlugin, project.getProviders());
        definitions.stream().filter(d -> d instanceof PublishingPluginPortalDefinition).findFirst().ifPresent(
                d -> definition.getTags().set(((PublishingPluginPortalDefinition) d).getTags()));
        action.execute(definition);
        definitions.add(definition);
    }

    public void mavenCentral(Action<PublishingMavenCentralDefinition> action) {
        var definition = project.getObjects().newInstance(PublishingMavenCentralDefinition.class);
        action.execute(definition);
        definitions.add(definition);
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

    public Provider<String> getGitHub() {
        return gradlePlugin.getVcsUrl();
    }

    public Provider<String> getWebsite() {
        return gradlePlugin.getWebsite();
    }

    public Provider<String> getDisplayName() {
        if (definitions.isEmpty()) { throw new GradleException("No publication defined"); }
        return definitions.get(0).getDisplayName();
    }

    public Provider<String> getDescription() {
        if (definitions.isEmpty()) { throw new GradleException("No publication defined"); }
        return definitions.get(0).getDescription();
    }
}
