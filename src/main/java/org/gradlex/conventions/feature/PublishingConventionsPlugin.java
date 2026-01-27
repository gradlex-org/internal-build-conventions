// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.feature;

import static org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME;

import buildparameters.BuildParametersExtension;
import buildparameters.GeneratedBuildParametersPlugin;
import com.gradle.publish.PublishPlugin;
import nmcp.NmcpAggregationExtension;
import nmcp.internal.DefaultNmcpAggregationExtensionPlugin;
import nmcp.internal.DefaultNmcpExtensionPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class PublishingConventionsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        var plugins = project.getPlugins();
        var extensions = project.getExtensions();
        var tasks = project.getTasks();
        var dependencies = project.getDependencies();

        plugins.apply(PublishPlugin.class);
        plugins.apply(DefaultNmcpExtensionPlugin.class);
        plugins.apply(DefaultNmcpAggregationExtensionPlugin.class);
        plugins.apply(SigningPlugin.class);
        plugins.apply(GeneratedBuildParametersPlugin.class);

        var java = extensions.getByType(JavaPluginExtension.class);
        var gradlePlugin = extensions.getByType(GradlePluginDevelopmentExtension.class);
        var publishing = extensions.getByType(PublishingExtension.class);
        var nmcpAggregation = extensions.getByType(NmcpAggregationExtension.class);
        var signing = extensions.getByType(SigningExtension.class);
        var buildParameters = extensions.getByType(BuildParametersExtension.class);
        var pluginPublishConventions = extensions.create(
                PublishingConventionsExtension.NAME, PublishingConventionsExtension.class, project, gradlePlugin);

        tasks.named("publishPlugins", task -> task.dependsOn(CHECK_TASK_NAME));

        java.withJavadocJar();
        java.withSourcesJar();

        // signing
        signing.setRequired(!buildParameters.getSigning().getDisable());
        if (signing.isRequired()) {
            signing.useInMemoryPgpKeys(
                    buildParameters.getSigning().getKey(),
                    buildParameters.getSigning().getPassphrase());
        }

        // Maven Central
        dependencies.add("nmcpAggregation", project.project(project.getPath())); // for NmcpAggregationPlugin
        nmcpAggregation.centralPortal(central -> {
            central.getUsername().set(buildParameters.getMavenCentral().getUsername());
            central.getPassword().set(buildParameters.getMavenCentral().getPassword());
            central.getPublishingType().set("AUTOMATIC"); // "USER_MANAGED"
        });

        // Metadata for all publications
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
