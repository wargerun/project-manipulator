package ProjectManipulator.CommandBuilder;

import ProjectManipulator.Helpers.Project;
import org.eclipse.jgit.diff.DiffEntry;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SystemVersionControlBuilder {
    private final File gitFolder;
    private final String oldGitBranch;
    private final String newGitBranch;
    private static final String GIT_PATH_NULL = "/dev/null";

    public SystemVersionControlBuilder(File gitFolder, String oldGitBranch, String newGitBranch) {
        this.gitFolder = gitFolder;
        this.oldGitBranch = oldGitBranch;
        this.newGitBranch = newGitBranch;
    }

    public Set<Project> GetProjectChanges(List<Project> allProjects) throws Exception {
        final List<DiffEntry> diffBranches = GitHelper.getDiffBranches(gitFolder, oldGitBranch, newGitBranch);
        allProjects.sort((o1, o2) -> o1.getFile().compareTo(o2.getFile()));
        final Set<Project> projectChanges = extractProjectChanges(diffBranches, allProjects);

        return projectChanges;
    }

    private static Set<Project> extractProjectChanges(List<DiffEntry> diffBranches, List<Project> allProjects){
        HashSet<Project> projects = new HashSet(allProjects.size());
        int counter = 1;

        for (DiffEntry diffBranch : diffBranches) {
            if (counter == 78){
                final boolean b = 1 == 1;
            }

            final String oldRelativePath = diffBranch.getOldPath();
            fillProject(allProjects, projects, oldRelativePath);

            if (diffBranch.getChangeType() == DiffEntry.ChangeType.MODIFY ||
                diffBranch.getChangeType() == DiffEntry.ChangeType.RENAME) {
                final String newRelativePath = diffBranch.getNewPath();
                fillProject(allProjects, projects, newRelativePath);
            }

            counter++;
        }

        return projects;
    }

    private static void fillProject(List<Project> allProjects, Set<Project> projects, String relativePath) {
        if (relativePath.equals(GIT_PATH_NULL)){
            return;
        }

        final Project newFirstProjectOrNull = getFirstOrDefault(allProjects, relativePath);

        if (newFirstProjectOrNull != null){
            projects.add(newFirstProjectOrNull);
        }
    }

    private static Project getFirstOrDefault(List<Project> allProjects, String relativePath) {
        return allProjects.stream().filter(project -> {
            final String name = project.getFile().getParentFile().getName() + "/";
            return relativePath.contains(name);
        }).findFirst().orElse(null);
    }
}
