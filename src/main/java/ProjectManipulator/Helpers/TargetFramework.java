package ProjectManipulator.Helpers;

public final class TargetFramework {

    private final FrameworkType frameworkType;
    private String version;

    public TargetFramework(FrameworkType frameworkType) {
        this.frameworkType = frameworkType;
    }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    @Override
    public String toString() {
        return String.format("%s (%s)", frameworkType, version);
    }
}