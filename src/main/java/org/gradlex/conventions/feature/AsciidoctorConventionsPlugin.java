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

import org.asciidoctor.gradle.base.log.Severity;
import org.asciidoctor.gradle.jvm.AsciidoctorJPlugin;
import org.asciidoctor.gradle.jvm.AsciidoctorTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.PathSensitivity;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

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
            task.notCompatibleWithConfigurationCache(
                    "https://github.com/asciidoctor/asciidoctor-gradle-plugin/issues/564");
            task.getInputs().dir("src/docs/samples")
                    .withPathSensitivity(PathSensitivity.RELATIVE)
                    .withPropertyName("samples");

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
            attributes.put("samples-path", layout.getProjectDirectory().dir("src/docs/samples").getAsFile().toString());
            task.setAttributes(attributes);
        });
    }
}
