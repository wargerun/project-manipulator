package ProjectManipulator.Commands.Imp;


import ProjectManipulator.CommandBuilder.SystemVersionControlBuilder;
import ProjectManipulator.Commands.CommandType;
import ProjectManipulator.CommandBuilder.GitHelper;
import ProjectManipulator.Helpers.*;
import ProjectManipulator.ProjectProviders.IProjectProcessor;
import ProjectManipulator.ProjectProviders.ProjectProcessorFacade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class VersionProjectScanCommand extends VersionProjectBase {
    private static final String DELIMITER = "\", \"";
    private static final Integer GIT_PARAM_COUNT  = 3;
    private static final String PARAM_PATTERN_PROJECTS = "projects=\\\"(.*?)\\\"";
    private static final String PARAM_PATTERN_AUTO_GIT = "autogit=\\\"(.*?)\\\"";

    private File solutionFile;
    private List<Project> projects;
    private String oldGitBranch;
    private String newGitBranch;
    private File gitFolder;

    @Override
    public CommandType CurrentCommandType() {
        return CommandType.VersionProjectScan;
    }

    @Override
    public void LoadParameters(String param) throws Exception {
        String[] split = param.split(CommandType.COMMAND_DELIMITER);
        ThrowIfCommandTypeInvalid(split[0]);
        projects = new ArrayList();

        final String userInput = param.toLowerCase();
        solutionFile = getFileSolution(userInput);
        String solutionFileStr = trySelectViaRegexpFrom(userInput, PARAM_PATTERN_PROJECTS);

        if (solutionFileStr != null) {
            versionProjectType = VersionProjectType.ListProject;
            projects = collectProjectFilesFromLine(solutionFile.getParent(), solutionFileStr);
        }

        String autoGitStr = trySelectViaRegexpFrom(userInput, PARAM_PATTERN_AUTO_GIT);

        if (autoGitStr != null) {
            final String[] gitParameters = autoGitStr.split(",");

            if (gitParameters.length != GIT_PARAM_COUNT) {
                throw new Exception("Некорректно указаны параметры для AUTO_GIT, ожидалось " + GIT_PARAM_COUNT + " параметра через ,");
            }

            versionProjectType = VersionProjectType.AutoGit;
            gitFolder = new File(gitParameters[0]);
            GitHelper.checkOnGitFolder(gitFolder);
            oldGitBranch = gitParameters[1];
            newGitBranch = gitParameters[2];
        }
    }

    private static List<Project> collectProjectFilesFromLine(final String solutionFolder, String listNonUniqueProjectsStr) {
        final List<Project> projects = new ArrayList();
        // unique list project names
        final List<String> uniqueProjectNames = Arrays.asList(listNonUniqueProjectsStr.split(",")).stream().distinct().collect(Collectors.toList());

        for (String projectName : uniqueProjectNames) {
            final File file = Paths.get(solutionFolder, projectName, projectName + EXTENSION_CS_PROJECT).toFile();

            if (!file.exists()){
                ThrowReasonAsFileNotExist(file.toString());
            }

            projects.add(new Project(file));
        }

        uniqueProjectNames.clear();
        return projects;
    }

    @Override
    public void Execute() throws Exception {
        switch (versionProjectType) {
            case Default:
                projects = scanProjectsFromSolution(solutionFile);
                LoadVersionFromProjectFies();
                break;
            case ListProject:
                LoadVersionFromProjectFies();
                break;
            case AutoGit:
                ScanProjectsDif();
                break;
            default:
                throw new IllegalArgumentException("Тип параметров для команды: "+ versionProjectType + ", не поддерживается ");
        }
    }

    private void ScanProjectsDif() throws Exception {
        SystemVersionControlBuilder controlBuilder = new SystemVersionControlBuilder(gitFolder, oldGitBranch, newGitBranch);
        final List<Project> allProjects = scanProjectsFromSolution(solutionFile);
        final Set<Project> projectChanges = controlBuilder.GetProjectChanges(allProjects);

        projects.addAll(projectChanges);
        LoadVersionFromProjectFies();
    }

    @Override
    public void Display() {
        CommandLineTable table = new CommandLineTable();
        table.setShowVerticalLines(true);
        table.setHeaders("ProjectName", "TargetFramework(version)", "Version");
        projects.sort((o1, o2) -> o1.getFile().compareTo(o2.getFile()));
        projects.forEach(p -> table.addRow(p.getFile().getName(), p.getTargetFramework().toString(), p.getVersion().toString()));
        table.print();

        System.out.printf("Кол-во проектов %d\n", projects.size());
    }

    private static List<Project> scanProjectsFromSolution(File solutionFile) throws Exception {
        List<Project> projects = new ArrayList();
        final BufferedReader reader = new BufferedReader(new FileReader(solutionFile));
        final String mainFolder = solutionFile.getParent();
        String line;

        while ((line = reader.readLine()) != null) {
            String pathCsProject = extractCsProject(line);

            if (pathCsProject != null){
                File projectFile = Paths.get(mainFolder, pathCsProject).toFile();

                if (!projectFile.exists()){
                    ThrowReasonAsFileNotExist(projectFile.toString());
                }

                projects.add(new Project(projectFile));
            }
        }

        return projects;
    }

    private void LoadVersionFromProjectFies() throws Exception {
        for (Project projectFile : projects) {
            final File file = projectFile.getFile();
            final IProjectProcessor projectProcessor = ProjectProcessorFacade.createProjectProcessor(file);

            if (projectProcessor != null) {
                final Version version = projectProcessor.GetProjectVersion(file);
                final TargetFramework targetFramework = projectProcessor.GetTargetFramework();

                projectFile.setVersion(version);
                projectFile.setTargetFramework(targetFramework);
            }
        }
    }

    private static String extractCsProject(String lineFromSolution){
        // TODO TEst this

        // Project("{FAE04EC0-301F-11D3-BF4B-00C04F79EFBC}") = "ISBC.Printing", "ISBC.Printing\ISBC.Printing.csproj", "{8DB0CC14-004A-4809-8C4B-BCE56F239BEA}"
        if (lineFromSolution.contains(EXTENSION_CS_PROJECT)) {
            final int startIndex = lineFromSolution.indexOf(DELIMITER) + DELIMITER.length();
            final int endIndex = lineFromSolution.indexOf(EXTENSION_CS_PROJECT) + EXTENSION_CS_PROJECT.length();

            return lineFromSolution.substring(startIndex, endIndex);
        }

        return null;
    }
}
