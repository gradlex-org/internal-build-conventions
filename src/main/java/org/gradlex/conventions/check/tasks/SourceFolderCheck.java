// SPDX-License-Identifier: Apache-2.0
package org.gradlex.conventions.check.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

public abstract class SourceFolderCheck extends DefaultTask {

    @InputFiles
    public abstract ConfigurableFileCollection getSources();

    @OutputFile
    public abstract RegularFileProperty getUpToDateFile();

    @TaskAction
    public void check() throws IOException {
        List<String> notAcceptedFile = getSources().getFiles().stream()
                .map(File::getName)
                .filter(name -> !name.endsWith(".java") || name.equals("package-info.java"))
                .toList();
        if (!notAcceptedFile.isEmpty()) {
            throw new RuntimeException("Following files are not allowed in 'java' source folders:\n - "
                    + String.join("\n - ", notAcceptedFile));
        }
        var upToDatePath = getUpToDateFile().get().getAsFile().toPath();
        Files.createDirectories(upToDatePath.getParent());
        Files.writeString(upToDatePath, "");
    }
}
