package ProjectManipulator.CommandBuilder;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.util.List;

public class GitHelper {

    public static void checkOnGitFolder(File someFile) {
        final String gitTemplate = ".git";

        if (!someFile.exists() ||
                !someFile.getName().equals(gitTemplate) ||
                !someFile.isDirectory()) {

            throw new InvalidPathException(someFile.toString(), "Каталог не был найден или не является папкой Гита");
        }
    }

    public static List<DiffEntry> getDiffBranches(File gitDir, String oldGitBranch, String newGitBranch) throws Exception {
        checkOnGitFolder(gitDir);
        final Repository repository = FileRepositoryBuilder.create(gitDir);

        // the diff works on TreeIterators, we prepare two for the two branches
        AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, oldGitBranch);
        AbstractTreeIterator newTreeParser = prepareTreeParser(repository, newGitBranch);

        // then the procelain diff-command returns a list of diff entries
        List<DiffEntry> diff = new Git(repository).diff().setOldTree(oldTreeParser).setNewTree(newTreeParser).call();

        return diff;
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws Exception {
        // from the commit we can build the tree which allows us to construct the TreeParser
        Ref head = repository.exactRef(ref);

        if (head == null) {
            throw new Exception(String.format("Ветка \"%s\" не найдена", ref));
        }

        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(head.getObjectId());
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();
            return treeParser;
        }
    }
}
