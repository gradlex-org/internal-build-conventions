// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.report;

import buildparameters.BuildParametersExtension;
import buildparameters.GeneratedBuildParametersPlugin;
import com.gradle.CommonCustomUserDataGradlePlugin;
import com.gradle.develocity.agent.gradle.DevelocityConfiguration;
import com.gradle.develocity.agent.gradle.DevelocityPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class DevelocityConventionsPlugin implements Plugin<Settings> {

    @Override
    public void apply(Settings settings) {
        var plugins = settings.getPlugins();
        var extensions = settings.getExtensions();

        plugins.apply(DevelocityPlugin.class);
        plugins.apply(CommonCustomUserDataGradlePlugin.class);
        plugins.apply(GeneratedBuildParametersPlugin.class);

        var develocity = extensions.getByType(DevelocityConfiguration.class);
        var buildParameters = extensions.getByType(BuildParametersExtension.class);

        develocity.buildScan(buildScan -> {
            // required to bind this to a local variable for configuration cache compatibility
            var isCi = buildParameters.getCi();

            buildScan.getTermsOfUseUrl().set("https://gradle.com/help/legal-terms-of-use");
            buildScan.getTermsOfUseAgree().set("yes");
            buildScan.getPublishing().onlyIf(__ -> isCi);
        });
    }
}
