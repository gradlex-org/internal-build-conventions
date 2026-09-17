// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.report;

import buildparameters.BuildParametersExtension;
import buildparameters.GeneratedBuildParametersPlugin;
import com.gradle.CommonCustomUserDataGradlePlugin;
import com.gradle.develocity.agent.gradle.DevelocityConfiguration;
import com.gradle.develocity.agent.gradle.DevelocityPlugin;
import com.gradle.develocity.agent.gradle.scan.BuildScanPublishingConfiguration.PublishingContext;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class DevelocityConventionsPlugin implements Plugin<Settings> {

    private static final String SERVER = "https://community.develocity.cloud";
    private static final String PROJECT_ID = "gradlex-org";

    @Override
    public void apply(Settings settings) {
        var plugins = settings.getPlugins();
        var extensions = settings.getExtensions();

        plugins.apply(DevelocityPlugin.class);
        plugins.apply(CommonCustomUserDataGradlePlugin.class);
        plugins.apply(GeneratedBuildParametersPlugin.class);

        var develocity = extensions.getByType(DevelocityConfiguration.class);
        var buildParameters = extensions.getByType(BuildParametersExtension.class);

        var isCi = buildParameters.getCi();
        var hasAccessKey = buildParameters.getDevelocity().getAccessKey().isPresent();

        develocity.getServer().set(SERVER);
        develocity.getProjectId().set(PROJECT_ID);

        develocity.buildScan(buildScan -> {
            buildScan.getUploadInBackground().set(!isCi);
            buildScan.getPublishing().onlyIf(PublishingContext::isAuthenticated);
            buildScan.getObfuscation().ipAddresses(addresses -> addresses.stream()
                    .map(__ -> "0.0.0.0")
                    .toList());
        });

        settings.buildCache(buildCache -> {
            buildCache.local(local -> local.setEnabled(true));
            buildCache.remote(develocity.getBuildCache(), remote -> {
                remote.setEnabled(true);
                // Do not push on forks where access key is not present
                remote.setPush(isCi && hasAccessKey);
            });
        });
    }
}
