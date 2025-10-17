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
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.gradle.testing.base.TestingExtension;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public abstract class TestingConventionsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        var plugins = project.getPlugins();
        var extensions = project.getExtensions();
        var layout = project.getLayout();

        plugins.apply(JavaPlugin.class);

        var testing = extensions.getByType(TestingExtension.class);
        var javaToolchains = extensions.getByType(JavaToolchainService.class);

        var samplesDir = layout.getProjectDirectory().dir("samples");
        var docsSamplesDir = layout.getProjectDirectory().dir("src/docs/samples");

        var testSuite = testing.getSuites().named("test", JvmTestSuite.class, suite -> {
            suite.useJUnitJupiter();
            suite.getTargets().configureEach(target ->
                    target.getTestTask().configure(testTask -> {
                                testTask.setMaxParallelForks(4);
                                if (samplesDir.getAsFile().exists()) {
                                    testTask.getInputs().dir(samplesDir);
                                }
                                if (docsSamplesDir.getAsFile().exists()) {
                                    testTask.getInputs().dir(docsSamplesDir);
                                }
                            }
                    )
            );
            suite.dependencies(dependencies-> {
                dependencies.getImplementation().add("org.assertj:assertj-core:3.27.6");
                dependencies.getImplementation().add("org.gradle.exemplar:samples-check:1.0.3");
                dependencies.getImplementation().add("org.junit.vintage:junit-vintage-engine");
            });
        });

        extensions.create(TestingConventionsExtension.NAME, TestingConventionsExtension.class,
                testSuite.get(), javaToolchains);
    }
}
