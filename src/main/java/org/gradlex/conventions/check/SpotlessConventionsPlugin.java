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

import com.diffplug.gradle.spotless.SpotlessExtension;
import com.diffplug.gradle.spotless.SpotlessPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradlex.conventions.base.LifecycleConventionsPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class SpotlessConventionsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        var plugins = project.getPlugins();
        var extensions = project.getExtensions();
        var tasks = project.getTasks();

        plugins.apply(SpotlessPlugin.class);
        plugins.apply(LifecycleConventionsPlugin.class);

        var spotless = extensions.getByType(SpotlessExtension.class);

        tasks.named("qualityCheck", task -> task.dependsOn(tasks.named("spotlessCheck")));
        tasks.named("qualityGate", task -> task.dependsOn(tasks.named("spotlessApply")));

        // format the source code
        spotless.java(java -> {
            java.palantirJavaFormat();
            java.licenseHeader("// SPDX-License-Identifier: Apache-2.0\n", "package|import");
        });
        spotless.format("javaPackageInfoFiles", java -> {
            // add a separate extension due to https://github.com/diffplug/spotless/issues/532
            java.target("src/**/package-info.java");

            java.licenseHeader("// SPDX-License-Identifier: Apache-2.0\n", "package|import|@");
        });

        // format the build itself
        spotless.kotlinGradle(gradle -> gradle.ktfmt().kotlinlangStyle().configure(conf -> conf.setMaxWidth(120)));
    }
}
