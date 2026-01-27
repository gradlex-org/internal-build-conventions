// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.check;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradlex.conventions.check.tasks.SourceFolderCheck;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class SourceFoldersCheckConventionsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        var layout = project.getLayout();
        var plugins = project.getPlugins();
        var extensions = project.getExtensions();
        var tasks = project.getTasks();

        plugins.apply(JavaPlugin.class);

        var checkJavaSourceFolders =
                tasks.register("checkJavaSourceFolders", SourceFolderCheck.class, task -> task.getUpToDateFile()
                        .convention(layout.getBuildDirectory().file("tmp/checkJavaSourceFolders")));

        var sourceSets = extensions.getByType(SourceSetContainer.class);
        sourceSets.all(sourceSet -> checkJavaSourceFolders.configure(task -> task.getSources()
                .from(project.files(sourceSet.getJava().getSourceDirectories().getAsFileTree()))));

        tasks.named("qualityCheck", task -> task.dependsOn(checkJavaSourceFolders));
        tasks.named("qualityGate", task -> task.dependsOn(checkJavaSourceFolders));
    }
}
