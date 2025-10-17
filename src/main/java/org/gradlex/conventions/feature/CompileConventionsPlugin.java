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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradlex.reproduciblebuilds.ReproducibleBuildsPlugin;
import org.jspecify.annotations.NullMarked;

import static org.gradle.api.plugins.JavaPlugin.COMPILE_JAVA_TASK_NAME;
import static org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME;

@NullMarked
public abstract class CompileConventionsPlugin implements Plugin<Project> {

    private static final int JDK_VERSION = 17;
    private static final int JDK_GRADLE_RT_VERSION = 8;

    @Override
    public void apply(Project project) {
        var plugins = project.getPlugins();
        var extensions = project.getExtensions();
        var tasks = project.getTasks();

        plugins.apply(JavaPlugin.class);
        plugins.apply(ReproducibleBuildsPlugin.class);

        var sourceSets = extensions.getByType(SourceSetContainer.class);
        var java = extensions.getByType(JavaPluginExtension.class);

        sourceSets.forEach(sourceSet -> {
            var classes = tasks.named(sourceSet.getClassesTaskName());
            tasks.named(ASSEMBLE_TASK_NAME, task -> task.dependsOn(classes));
        });

        java.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(JDK_VERSION));
        tasks.named(COMPILE_JAVA_TASK_NAME, JavaCompile.class, task ->
                task.getOptions().getRelease().set(JDK_GRADLE_RT_VERSION));

        tasks.withType(JavaCompile.class).configureEach(task -> {
            task.getOptions().getCompilerArgs().add("-implicit:none");
            task.getOptions().getCompilerArgs().add("-Werror");
            task.getOptions().getCompilerArgs().add("-Xlint:all,-serial");
        });
    }
}
