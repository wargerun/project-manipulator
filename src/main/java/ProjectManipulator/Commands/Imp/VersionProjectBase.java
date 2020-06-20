package ProjectManipulator.Commands.Imp;

import ProjectManipulator.Commands.CommandType;
import ProjectManipulator.Commands.ICommand;
import ProjectManipulator.Helpers.VersionProjectType;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class VersionProjectBase implements ICommand {

    protected VersionProjectType versionProjectType = VersionProjectType.Default;
    protected static final String EXTENSION_CS_PROJECT = ".csproj";
    protected static final String EXTENSION_SOLUTION = ".sln";

    protected void ThrowIfCommandTypeInvalid(String commandTypeStr) {
        CommandType commandType = CommandType.FromString(commandTypeStr);

        if (commandType != CurrentCommandType()){
            throw new IllegalArgumentException(String.format("Текущая команда %s не соответствует действительности, ожидалось %s", CurrentCommandType(), commandType));
        }
    }

    protected File getFileSolution(String text) throws Exception {
        String solutionFileStr = trySelectViaRegexpFrom(text, "solution=\\\"(.*?)\\\"");

        if (solutionFileStr == null) {
            throw new Exception("Путь до солюшена не найден в строке: '" + text + "'");
        }

        File solutionFile = new File(solutionFileStr);

        if (solutionFile.isDirectory()) {
            String fileName = solutionFile.getName() + EXTENSION_SOLUTION;
            solutionFile = new File(solutionFile, fileName);
        }

        if (!solutionFile.exists()){
            ThrowReasonAsFileNotExist(solutionFileStr);
        }

        if (!solutionFile.getPath().endsWith(EXTENSION_SOLUTION)){
            throw new InvalidPathException(solutionFileStr, "Некорректное расширение файла");
        }

        return solutionFile;
    }

    protected String trySelectViaRegexpFrom(String userInput, String patternStr){
        return trySelectViaRegexpFrom(userInput, patternStr, 1);
    }

    protected String trySelectViaRegexpFrom(String userInput, String patternStr, Integer groupId) {
        // Create matcher on file
        String result = null;
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(userInput);

        if (matcher.find()) {
            // Get the matching string
            result = matcher.group(groupId);
        }

        return result;
    }

    protected static void ThrowReasonAsFileNotExist(String file){
        throw new InvalidPathException(file, "Файл не существует");
    }
}
