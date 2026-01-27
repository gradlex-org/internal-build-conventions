// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.feature;

import static org.gradle.api.plugins.JavaPlugin.COMPILE_JAVA_TASK_NAME;
import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME;
import static org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.tasks.Jar;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradlex.reproduciblebuilds.ReproducibleBuildsPlugin;
import org.jspecify.annotations.NullMarked;

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
        tasks.named(COMPILE_JAVA_TASK_NAME, JavaCompile.class, task -> task.getOptions()
                .getRelease()
                .set(JDK_GRADLE_RT_VERSION));

        tasks.withType(JavaCompile.class).configureEach(task -> {
            task.getOptions().getCompilerArgs().add("-implicit:none");
            task.getOptions().getCompilerArgs().add("-Werror");
            task.getOptions().getCompilerArgs().add("-Xlint:all,-serial");
        });

        tasks.named(JAR_TASK_NAME, Jar.class, task -> {
            task.into(
                    "META-INF",
                    meta -> meta.from(project.getLayout().getSettingsDirectory().file("LICENSE.txt")));
            task.getManifest().getAttributes().put("Implementation-Title", project.getName());
            task.getManifest().getAttributes().put("Implementation-Version", project.getVersion());
        });
    }
}
