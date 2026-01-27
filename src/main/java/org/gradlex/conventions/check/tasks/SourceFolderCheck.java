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

package org.gradlex.conventions.check.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public abstract class SourceFolderCheck extends DefaultTask {

    @InputFiles
    public abstract ConfigurableFileCollection getSources();

    @OutputFile
    public abstract RegularFileProperty getUpToDateFile();

    @TaskAction
    public void check() throws IOException {
        List<String> notAcceptedFile = getSources().getFiles().stream().map(File::getName).filter(name ->
                !name.endsWith(".java") || name.equals("package-info.java")).toList();
        if (!notAcceptedFile.isEmpty()) {
            throw new RuntimeException("Following files are not allowed in 'java' source folders:\n - " +
                    String.join("\n - ", notAcceptedFile));
        }
        var upToDatePath = getUpToDateFile().get().getAsFile().toPath();
        Files.createDirectories(upToDatePath.getParent());
        Files.writeString(upToDatePath, "");
    }
}
