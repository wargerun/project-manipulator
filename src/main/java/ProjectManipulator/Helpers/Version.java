package ProjectManipulator.Helpers;

import java.util.Arrays;

public final class Version implements Comparable<Version> {

    private final int build;
    private final int major;
    private final int minor;
    private final int revision;

    public Version (String versionStr) {
        int[] versions = Arrays.stream(versionStr.split("\\.")).mapToInt(v -> Integer.parseInt(v)).toArray();

        if (versions.length != 4) {
            throw new ArrayIndexOutOfBoundsException("Некорректная версия из строки: " + versionStr);
        }

        CheckParameters (versions[0], versions[1], versions[2], versions[3]);

        this.major = versions[0];
        this.minor = versions[1];
        this.build = versions[2];
        this.revision = versions[3];
    }

    public Version (int major, int minor, int build, int revision) {
        CheckParameters(major, minor, build, revision);

        this.major = major;
        this.minor = minor;
        this.build = build;
        this.revision = revision;
    }

    private void CheckParameters(int major, int minor, int build, int revision) {
        if (major < 0)
            throw new ArrayIndexOutOfBoundsException();
        if (minor < 0)
            throw new ArrayIndexOutOfBoundsException();
        if (build < 0)
            throw new ArrayIndexOutOfBoundsException();
        if (revision < 0)
            throw new ArrayIndexOutOfBoundsException();
    }

    public int getBuild() {
        return build;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRevision() {
        return revision;
    }

    public String toString() {
        return String.format("%d.%d.%d.%d", major, minor, build, revision);
    }

    public int compareTo(Version version) {
        if (version == null)
            return 1;

        if (this.major != version.getMajor())
            if (this.major > version.getMajor())
                return 1;
            else
                return -1;

        if (this.minor != version.getMinor())
            if (this.minor > version.getMinor())
                return 1;
            else
                return -1;

        if (this.build != version.getBuild())
            if (this.build > version.getBuild())
                return 1;
            else
                return -1;

        if (this.revision != version.getRevision())
            if (this.revision > version.getRevision())
                return 1;
            else
                return -1;

        return 0;
    }
}