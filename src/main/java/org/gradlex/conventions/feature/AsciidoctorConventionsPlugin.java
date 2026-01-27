// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.feature;

import java.util.HashMap;
import java.util.Map;
import org.asciidoctor.gradle.base.log.Severity;
import org.asciidoctor.gradle.jvm.AsciidoctorJPlugin;
import org.asciidoctor.gradle.jvm.AsciidoctorTask;
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
        plugins.apply(AsciidoctorJPlugin.class);

        tasks.named("asciidoctor", AsciidoctorTask.class, task -> {
            var snippetsDir = layout.getProjectDirectory().dir("src/docs/snippets");

            task.notCompatibleWithConfigurationCache(
                    "https://github.com/asciidoctor/asciidoctor-gradle-plugin/issues/564");
            task.getInputs()
                    .dir(snippetsDir)
                    .withPropertyName("snippets")
                    .withPathSensitivity(PathSensitivity.RELATIVE);

            task.setFailureLevel(Severity.WARN);

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("docinfodir", "src/docs/asciidoc");
            attributes.put("docinfo", "shared");
            attributes.put("imagesdir", "./images");
            attributes.put("source-highlighter", "prettify");
            attributes.put("tabsize", "4");
            attributes.put("toc", "left");
            attributes.put("tip-caption", "💡");
            attributes.put("note-caption", "ℹ️");
            attributes.put("important-caption", "❗");
            attributes.put("caution-caption", "🔥");
            attributes.put("warning-caption", "⚠️");
            attributes.put("sectanchors", true);
            attributes.put("idprefix", "");
            attributes.put("idseparator", "-");
            attributes.put("samples-path", snippetsDir.getAsFile().toString());
            task.setAttributes(attributes);
        });
    }
}
