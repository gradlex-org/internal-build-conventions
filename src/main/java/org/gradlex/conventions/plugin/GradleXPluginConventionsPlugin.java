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

package org.gradlex.conventions.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradlex.conventions.base.DependencyRulesPlugin;
import org.gradlex.conventions.base.LifecycleConventionsPlugin;
import org.gradlex.conventions.check.SpotlessConventionsPlugin;
import org.gradlex.conventions.feature.AsciidoctorConventionsPlugin;
import org.gradlex.conventions.feature.CompileConventionsPlugin;
import org.gradlex.conventions.feature.JavadocConventionsPlugin;
import org.gradlex.conventions.feature.PluginPublishConventionsPlugin;
import org.gradlex.conventions.feature.TestingConventionsPlugin;
import org.gradlex.conventions.report.DevelocityConventionsPlugin;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class GradleXPluginConventionsPlugin  implements Plugin<Settings> {

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
            plugins.apply(PluginPublishConventionsPlugin.class);
            plugins.apply(SpotlessConventionsPlugin.class);
        });
    }
}
