// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.feature;

import org.asciidoctor.gradle.model5.core.AsciidoctorModelExtension;
import org.asciidoctor.gradle.model5.core.tasks.AsciidoctorTask;
import org.asciidoctor.gradle.model5.jvm.plugins.AsciidoctorjPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.PathSensitivity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AsciidoctorConventionsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        var plugins = project.getPlugins();
        var layout = project.getLayout();
        var tasks = project.getTasks();

        plugins.apply(JavaPlugin.class);
        plugins.apply(AsciidoctorjPlugin.class);

        var snippetsDir = layout.getProjectDirectory().dir("src/docs/snippets");
        var asciidoc = project.getExtensions().getByType(AsciidoctorModelExtension.class);

        asciidoc.getPublications().named("main", main -> {
            main.output("asciidoctorj", "html");
            main.getSourceSet().setSourceDir("src/docs/asciidoc");

            var attributes = main.getSourceSet().getAttributes();
            attributes.add("docinfo", "shared");
            attributes.add("imagesdir", "./images");
            attributes.add("source-highlighter", "prettify");
            attributes.add("tabsize", "4");
            attributes.add("toc", "left");
            attributes.add("tip-caption", "💡");
            attributes.add("note-caption", "ℹ️");
            attributes.add("important-caption", "❗");
            attributes.add("caution-caption", "🔥");
            attributes.add("warning-caption", "⚠️");
            attributes.add("sectanchors", true);
            attributes.add("idprefix", "");
            attributes.add("idseparator", "-");
            attributes.add("samples-path", snippetsDir.getAsFile().toString());
        });

        tasks.withType(AsciidoctorTask.class, task -> {
            task.getInputs()
                    .dir(snippetsDir)
                    .withPropertyName("snippets")
                    .withPathSensitivity(PathSensitivity.RELATIVE);
        });
    }
}
