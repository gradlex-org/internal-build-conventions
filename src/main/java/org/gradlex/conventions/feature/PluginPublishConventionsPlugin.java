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

import buildparameters.BuildParametersExtension;
import buildparameters.GeneratedBuildParametersPlugin;
import com.gradle.publish.PublishPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;
import org.jspecify.annotations.NullMarked;

import static org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME;

@NullMarked
public abstract class PluginPublishConventionsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        var plugins = project.getPlugins();
        var extensions = project.getExtensions();
        var tasks = project.getTasks();

        plugins.apply(PublishPlugin.class);
        plugins.apply(SigningPlugin.class);
        plugins.apply(GeneratedBuildParametersPlugin.class);

        var java = extensions.getByType(JavaPluginExtension.class);
        var gradlePlugin = extensions.getByType(GradlePluginDevelopmentExtension.class);
        var publishing = extensions.getByType(PublishingExtension.class);
        var signing = extensions.getByType(SigningExtension.class);
        var buildParameters = extensions.getByType(BuildParametersExtension.class);
        var pluginPublishConventions = extensions.create(
                PluginPublishConventionsExtension.NAME, PluginPublishConventionsExtension.class,
                project, gradlePlugin);

        tasks.named("publishPlugins", task -> task.dependsOn(CHECK_TASK_NAME));

        java.withJavadocJar();
        java.withSourcesJar();

        signing.useInMemoryPgpKeys(
                buildParameters.getSigning().getKey(),
                buildParameters.getSigning().getPassphrase()
        );

        publishing.getPublications().withType(MavenPublication.class).configureEach(p -> {
            p.getPom().getName().set(pluginPublishConventions.getDisplayName());
            p.getPom().getDescription().set(pluginPublishConventions.getDescription());
            p.getPom().getUrl().set(pluginPublishConventions.getWebsite());
            p.getPom().licenses(licenses -> {
                licenses.license(l -> {
                    l.getName().set("Apache-2.0");
                    l.getUrl().set("http://www.apache.org/licenses/LICENSE-2.0.txt");
                });
            });
            p.getPom().scm(scm -> scm.getUrl().set(pluginPublishConventions.getGitHub()));
            p.getPom().developers(d -> pluginPublishConventions.developers.forEach(d::developer));
        });
    }
}
