package ProjectManipulator.ProjectProviders.Impl;

import ProjectManipulator.Helpers.FrameworkType;
import ProjectManipulator.Helpers.TargetFramework;
import ProjectManipulator.Helpers.Version;
import ProjectManipulator.ProjectProviders.IProjectProcessor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetFrameworkProjectProcessor implements IProjectProcessor {
    public static final String PROPERTIES_ASSEMBLY_INFO = "Properties\\AssemblyInfo.cs";
    /**
     * \n\[assembly: Assembly(FileVersion|Version)\(\"(.+)"\)\]
     */
    public static final String PATTERN_ASSEMBLY_VERSION = "\\n\\[assembly: Assembly(FileVersion|Version)\\(\"(.+)\"\\)]";
    public static final Integer CAPTURE_GROUP_VERSION = 2;
    private TargetFramework targetFramework = new TargetFramework(FrameworkType.NetFramework);;

    public NetFrameworkProjectProcessor(String version) {
        targetFramework.setVersion(version);
    }

    @Override
    public TargetFramework GetTargetFramework() {
        return targetFramework;
    }

    @Override
    public Version GetProjectVersion(File file) throws IOException {
        final String project = file.getParent();
        final File assemblyInfoFile = Paths.get(project, PROPERTIES_ASSEMBLY_INFO).toFile();

        // Create matcher on file
        Pattern pattern = Pattern.compile(PATTERN_ASSEMBLY_VERSION);
        final CharSequence charSequence = fromFile(assemblyInfoFile);
        Matcher matcher = pattern.matcher(charSequence);

        // Find all matches
        while (matcher.find()) {
            // Get the matching string
            /*
            * Capture groups
            * #0 - [assembly: AssemblyVersion("1.0.0.0")]
            * #1 - Version
            * #2 - 1.0.0.0
            */
            // TODO max version need
            final String version = matcher.group(CAPTURE_GROUP_VERSION);
            return new Version(version);
        }

        return null;
    }

    // Converts the contents of a file into a CharSequence
    private static CharSequence fromFile(File assemblyInfoFile) throws IOException {
        FileInputStream input = new FileInputStream(assemblyInfoFile);
        FileChannel channel = input.getChannel();

        // Create a read-only CharBuffer on the file
        ByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, (int)channel.size());
        CharBuffer charBuffer = Charset.forName("utf-8").newDecoder().decode(byteBuffer);
        return charBuffer;
    }

    @Override
    public void SetProjectVersion(File file, Version version) {
        throw new NotImplementedException();
    }
}
