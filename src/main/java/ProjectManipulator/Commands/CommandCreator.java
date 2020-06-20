package ProjectManipulator.Commands;

import ProjectManipulator.Commands.Imp.VersionProjectIncrement;
import ProjectManipulator.Commands.Imp.VersionProjectScanCommand;

public class CommandCreator {
    public static ICommand CreateCommand(CommandType commandType) throws Exception {
        switch(commandType) {
            case VersionProjectScan:
                return new VersionProjectScanCommand();
            case VersionProjectIncrement:
                return new VersionProjectIncrement();
            default:
                throw new Exception("Команда " + commandType + " не поддерживается");
        }
    }
}
