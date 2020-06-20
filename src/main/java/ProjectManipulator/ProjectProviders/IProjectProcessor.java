package ProjectManipulator.ProjectProviders;

import ProjectManipulator.Helpers.TargetFramework;
import ProjectManipulator.Helpers.Version;
import java.io.File;
import java.io.IOException;

public interface IProjectProcessor {
    TargetFramework GetTargetFramework();
    Version GetProjectVersion(File file) throws IOException;
    void SetProjectVersion(File file, Version version);
}
