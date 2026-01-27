// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.feature;

import static org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP;

import java.util.Arrays;
import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public abstract class TestingConventionsExtension {

    public static final String NAME = "testingConventions";

    private final JvmTestSuite suite;
    private final JavaToolchainService javaToolchains;

    public TestingConventionsExtension(JvmTestSuite suite, JavaToolchainService javaToolchains) {
        this.suite = suite;
        this.javaToolchains = javaToolchains;
    }

    public void testGradleVersions(String... gradleVersions) {
        Arrays.stream(gradleVersions).forEach(gradleVersionUnderTest -> suite.getTargets()
                .register("test" + gradleVersionUnderTest, target -> target.getTestTask()
                        .configure(testTask -> {
                            testTask.setGroup(VERIFICATION_GROUP);
                            testTask.setDescription("Runs tests against Gradle" + gradleVersionUnderTest);
                            testTask.systemProperty("gradleVersionUnderTest", gradleVersionUnderTest);
                            testTask.useJUnitPlatform(junit -> junit.excludeTags("no-cross-version"));
                            if (gradleVersionUnderTest.startsWith("6")) {
                                testTask.getJavaLauncher().set(javaToolchains.launcherFor(tc -> tc.getLanguageVersion()
                                        .set(JavaLanguageVersion.of(11))));
                            }
                        })));
    }
}
