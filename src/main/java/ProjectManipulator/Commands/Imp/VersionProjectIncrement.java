package ProjectManipulator.Commands.Imp;

import ProjectManipulator.Commands.CommandType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VersionProjectIncrement extends VersionProjectBase {

    private List<File> projectFiles;

    @Override
    public CommandType CurrentCommandType() {
        return CommandType.VersionProjectIncrement;
    }

    @Override
    public void LoadParameters(String param) throws Exception {
        // 1;[путь к файлу солюшена (.sln) файла];[список проектов через ',']
        String[] split = param.split(CommandType.COMMAND_DELIMITER);

        if (split.length != 3){
            throw new Exception("Ожидалось 3 параметров");
        }

        ThrowIfCommandTypeInvalid(split[0]);
        final File fileSolution = getFileSolution(split[1]);
        final String solutionFolder = fileSolution.getParent();
        projectFiles = new ArrayList();
        // unique list project names
        final List<String> projectNames = Arrays.asList(split[2].split(",")).stream().distinct().collect(Collectors.toList());

        for (String projectName : projectNames) {
            final File file = Paths.get(solutionFolder, projectName, projectName + EXTENSION_CS_PROJECT).toFile();

            if (!file.exists()){
                throw new Exception(String.format("Файл %s не существует", file));
            }

            projectFiles.add(file);
        }

        projectNames.clear();
    }

    @Override
    public void Execute() throws IOException {
        for (File file : projectFiles) {

        }
    }

    @Override
    public void Display() {
        projectFiles.forEach(p -> System.out.println(p.toString()));
        System.out.printf("Кол-во проектов %d", projectFiles.size());
    }
}
