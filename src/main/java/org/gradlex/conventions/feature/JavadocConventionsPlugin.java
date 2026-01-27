// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.feature;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.gradlex.reproduciblebuilds.ReproducibleBuildsPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class JavadocConventionsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        var plugins = project.getPlugins();
        var tasks = project.getTasks();

        plugins.apply(JavaPlugin.class);
        plugins.apply(ReproducibleBuildsPlugin.class);

        tasks.withType(Javadoc.class).configureEach(task -> {
            var options = (StandardJavadocDocletOptions) task.getOptions();
            options.addStringOption("Xdoclint:all,-missing", "-Xwerror");
        });
    }
}
