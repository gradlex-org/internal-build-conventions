// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradlex.conventions.base.DependencyRulesPlugin;
import org.gradlex.conventions.base.LifecycleConventionsPlugin;
import org.gradlex.conventions.check.SourceFoldersCheckConventionsPlugin;
import org.gradlex.conventions.check.SpotlessConventionsPlugin;
import org.gradlex.conventions.feature.AsciidoctorConventionsPlugin;
import org.gradlex.conventions.feature.CompileConventionsPlugin;
import org.gradlex.conventions.feature.JavadocConventionsPlugin;
import org.gradlex.conventions.feature.PublishingConventionsPlugin;
import org.gradlex.conventions.feature.TestingConventionsPlugin;
import org.gradlex.conventions.report.DevelocityConventionsPlugin;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class GradleXPluginConventionsPlugin implements Plugin<Settings> {

    @Override
    public void apply(Settings settings) {
        var settingsPlugins = settings.getPlugins();
        var repositories = settings.getDependencyResolutionManagement().getRepositories();

        repositories.gradlePluginPortal();
        settingsPlugins.apply(DevelocityConventionsPlugin.class);

        settings.getGradle().getLifecycle().beforeProject(project -> {
            var plugins = project.getPlugins();

            project.setGroup("org.gradlex");

            plugins.apply(LifecycleConventionsPlugin.class);
            plugins.apply(DependencyRulesPlugin.class);
            plugins.apply(CompileConventionsPlugin.class);
            plugins.apply(JavadocConventionsPlugin.class);
            plugins.apply(TestingConventionsPlugin.class);
            plugins.apply(AsciidoctorConventionsPlugin.class);
            plugins.apply(PublishingConventionsPlugin.class);
            plugins.apply(SourceFoldersCheckConventionsPlugin.class);
            plugins.apply(SpotlessConventionsPlugin.class);
        });
    }
}
