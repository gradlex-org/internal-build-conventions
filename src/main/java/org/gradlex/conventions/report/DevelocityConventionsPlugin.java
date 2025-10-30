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
