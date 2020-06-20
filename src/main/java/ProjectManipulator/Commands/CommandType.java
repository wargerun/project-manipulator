package ProjectManipulator.Commands;

public enum CommandType {
    VersionProjectScan,
    VersionProjectIncrement,
    ;

    public static final String COMMAND_DELIMITER = " ";

    public static CommandType FromString(String commandTypeStr) {
        switch(commandTypeStr) {
            case "0":
                return CommandType.VersionProjectScan;
            case "1":
                return CommandType.VersionProjectIncrement;
            default:
                throw new IllegalArgumentException("Тип команды \"" + commandTypeStr + "\" не поддерживается!");
        }
    }
}