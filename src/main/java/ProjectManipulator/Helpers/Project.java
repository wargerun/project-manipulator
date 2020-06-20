package ProjectManipulator.Helpers;


import java.io.File;

public class Project {
    private final File file;
    private TargetFramework targetFramework;
    private Version version;

    public Project(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public Version getVersion() {
        return version;
    }
    public void setVersion(Version version) {
        this.version = version;
    }

    public TargetFramework getTargetFramework() {return targetFramework;}
    public void setTargetFramework(TargetFramework targetFramework) { this.targetFramework = targetFramework; }
}
