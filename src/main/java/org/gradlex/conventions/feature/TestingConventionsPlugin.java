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
import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.gradle.testing.base.TestingExtension;
import org.jspecify.annotations.NullMarked;

import static org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public abstract class TestingConventionsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        var plugins = project.getPlugins();
        var extensions = project.getExtensions();
        var tasks = project.getTasks();
        var layout = project.getLayout();

        plugins.apply(JavaPlugin.class);

        var testing = extensions.getByType(TestingExtension.class);
        var javaToolchains = extensions.getByType(JavaToolchainService.class);

        var samplesDir = layout.getProjectDirectory().dir("samples");
        var snippetsDir = layout.getProjectDirectory().dir("src/docs/snippets");

        // Unite tests with cross-version support
        var testSuite = testing.getSuites().named("test", JvmTestSuite.class, suite -> {
            suite.useJUnitJupiter();
            suite.dependencies(dependencies-> dependencies.getImplementation().add(TestingVersionCatalog.assertJ));
        });

        extensions.create(TestingConventionsExtension.NAME, TestingConventionsExtension.class,
                testSuite.get(), javaToolchains);

        // tested samples
        testing.getSuites().register("testSamples", JvmTestSuite.class, suite -> {
            tasks.named(CHECK_TASK_NAME, task -> task.dependsOn(suite));
            suite.getTargets().configureEach(target ->
                    target.getTestTask().configure(testTask -> {
                                testTask.setMaxParallelForks(4);
                                if (samplesDir.getAsFile().exists()) {
                                    testTask.getInputs()
                                            .dir(samplesDir)
                                            .withPathSensitivity(PathSensitivity.RELATIVE)
                                            .withPropertyName("samples");
                                }
                                if (snippetsDir.getAsFile().exists()) {
                                    testTask.getInputs()
                                            .dir(snippetsDir)
                                            .withPathSensitivity(PathSensitivity.RELATIVE)
                                            .withPropertyName("snippets");
                                }
                            }
                    )
            );
            suite.dependencies(dependencies-> {
                dependencies.getImplementation().add(TestingVersionCatalog.exemplarSamplesCheck);
                dependencies.getImplementation().add("org.junit.vintage:junit-vintage-engine");
                // address https://github.com/gradlex-org/java-module-packaging/security/dependabot/1
                dependencies.getImplementation().addConstraint(
                        dependencies.constraint(TestingVersionCatalog.commonsLang3));
            });
        });
    }
}
