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

package org.gradlex.conventions.base;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.testing.Test;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static org.gradle.api.plugins.JavaBasePlugin.BUILD_DEPENDENTS_TASK_NAME;
import static org.gradle.api.plugins.JavaBasePlugin.BUILD_NEEDED_TASK_NAME;
import static org.gradle.api.plugins.JavaPlugin.CLASSES_TASK_NAME;
import static org.gradle.api.plugins.JavaPlugin.TEST_CLASSES_TASK_NAME;
import static org.gradle.api.plugins.JavaPlugin.TEST_TASK_NAME;
import static org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME;
import static org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_GROUP;
import static org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP;

@NullMarked
public abstract class LifecycleConventionsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        var tasks = project.getTasks();

        tasks.register("qualityCheck", task -> {
            task.setGroup(VERIFICATION_GROUP);
            task.setDescription("Run all spotless and quality checks.");
            task.dependsOn(tasks.named(ASSEMBLE_TASK_NAME));
        });
        tasks.register("qualityGate", task -> {
            task.setGroup(BUILD_GROUP);
            task.setDescription("Apply spotless rules and run all quality checks.");
            task.dependsOn(tasks.named(ASSEMBLE_TASK_NAME));
        });
        tasks.register("quickCheck", task -> {
            task.setGroup(VERIFICATION_GROUP);
            task.setDescription("Runs all of qualityCheck and tests only against the current Gradle version.");
            task.dependsOn(tasks.named("qualityCheck"));
            task.dependsOn(tasks.named(TEST_TASK_NAME));
        });

        // cleanup 'build' group
        var unimportantLifecycleTasks = List.of(
                BUILD_DEPENDENTS_TASK_NAME, BUILD_NEEDED_TASK_NAME, CLASSES_TASK_NAME, TEST_CLASSES_TASK_NAME);
        project.afterEvaluate(__ ->
                tasks.configureEach(task -> {
                    if (unimportantLifecycleTasks.contains(task.getName())) {
                        task.setGroup(null);
                    }
                    if (task instanceof Jar) {
                        task.setGroup(null);
                    }
                    if (task instanceof Test) {
                        task.setGroup(BUILD_GROUP);
                    }
                })
        );
    }
}
