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

        var checkJavaSourceFolders = tasks.register("checkJavaSourceFolders", SourceFolderCheck.class,
                task -> task.getUpToDateFile().convention(layout.getBuildDirectory().file("tmp/checkJavaSourceFolders")));

        var sourceSets = extensions.getByType(SourceSetContainer.class);
        sourceSets.all(sourceSet -> checkJavaSourceFolders.configure(task ->
                task.getSources().from(project.files(sourceSet.getJava().getSourceDirectories().getAsFileTree()))));

        tasks.named("qualityCheck", task -> task.dependsOn(checkJavaSourceFolders));
        tasks.named("qualityGate", task -> task.dependsOn(checkJavaSourceFolders));
    }
}
