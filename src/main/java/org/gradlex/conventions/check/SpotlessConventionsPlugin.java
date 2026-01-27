// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.check;

import com.diffplug.gradle.spotless.SpotlessExtension;
import com.diffplug.gradle.spotless.SpotlessPlugin;
import com.diffplug.spotless.LineEnding;
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

        spotless.setLineEndings(LineEnding.UNIX);

        // format the source code
        spotless.java(java -> {
            java.targetExclude("build");
            java.palantirJavaFormat();
            java.licenseHeader("// SPDX-License-Identifier: Apache-2.0\n", "package|import");
        });
        // separate 'package-info' formatting due to https://github.com/diffplug/spotless/issues/532
        spotless.format("javaPackageInfoFiles", java -> {
            java.targetExclude("build");
            java.target("src/**/package-info.java");
            java.licenseHeader("// SPDX-License-Identifier: Apache-2.0\n", "package|import|@");
        });

        // format the build itself
        spotless.kotlinGradle(gradle -> gradle.ktfmt().kotlinlangStyle().configure(conf -> conf.setMaxWidth(120)));
    }
}
