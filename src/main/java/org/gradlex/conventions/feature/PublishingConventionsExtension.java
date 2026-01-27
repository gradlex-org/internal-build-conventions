// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.feature;

import java.util.ArrayList;
import java.util.List;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.api.publish.maven.MavenPomDeveloper;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class PublishingConventionsExtension {

    public static final String NAME = "publishingConventions";

    private final Project project;
    private final GradlePluginDevelopmentExtension gradlePlugin;
    private final List<PublishingDefinition> definitions = new ArrayList<>();

    List<Action<MavenPomDeveloper>> developers = new ArrayList<>();

    public PublishingConventionsExtension(Project project, GradlePluginDevelopmentExtension gradlePlugin) {
        this.project = project;
        this.gradlePlugin = gradlePlugin;
    }

    public void pluginPortal(String id, Action<PublishingPluginPortalDefinition> action) {
        var definition = project.getObjects()
                .newInstance(PublishingPluginPortalDefinition.class, id, gradlePlugin, project.getProviders());
        definitions.stream()
                .filter(d -> d instanceof PublishingPluginPortalDefinition)
                .findFirst()
                .ifPresent(d -> definition.getTags().set(((PublishingPluginPortalDefinition) d).getTags()));
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
        if (definitions.isEmpty()) {
            throw new GradleException("No publication defined");
        }
        return definitions.get(0).getDisplayName();
    }

    public Provider<String> getDescription() {
        if (definitions.isEmpty()) {
            throw new GradleException("No publication defined");
        }
        return definitions.get(0).getDescription();
    }
}
