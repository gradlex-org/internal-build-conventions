// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.feature;

import org.gradle.api.provider.Provider;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PublishingDefinition {
    Provider<String> getDisplayName();

    Provider<String> getDescription();
}
