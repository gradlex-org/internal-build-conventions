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

import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

import static org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP;

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
        Arrays.stream(gradleVersions).forEach(gradleVersionUnderTest ->
                suite.getTargets().register("test" + gradleVersionUnderTest, target ->
                        target.getTestTask().configure(testTask -> {
                            testTask.setGroup(VERIFICATION_GROUP);
                            testTask.setDescription("Runs tests against Gradle" + gradleVersionUnderTest);
                            testTask.systemProperty("gradleVersionUnderTest", gradleVersionUnderTest);
                            testTask.useJUnitPlatform(junit -> junit.excludeTags(
                                    "no-cross-version",
                                    "org.gradlex.testing.NoCrossVersion"
                            ));
                            if (gradleVersionUnderTest.startsWith("6")) {
                                testTask.getJavaLauncher().set(javaToolchains.launcherFor(tc ->
                                        tc.getLanguageVersion().set(JavaLanguageVersion.of(11))));
                            }
                        })
                )
        );
    }
}
