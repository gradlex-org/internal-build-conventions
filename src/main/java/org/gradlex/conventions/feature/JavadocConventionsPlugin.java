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
