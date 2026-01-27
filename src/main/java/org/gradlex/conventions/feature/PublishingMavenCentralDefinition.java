// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.feature;

import org.gradle.api.provider.Property;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class PublishingMavenCentralDefinition implements PublishingDefinition {

    public abstract Property<String> getDisplayName();

    public abstract Property<String> getDescription();

    public void displayName(String displayName) {
        getDisplayName().set(displayName);
    }

    public void description(String description) {
        getDescription().set(description);
    }
}
