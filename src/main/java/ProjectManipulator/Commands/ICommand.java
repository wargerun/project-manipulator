package ProjectManipulator.Commands;


public interface ICommand {
    CommandType CurrentCommandType();
    void LoadParameters(String param) throws Exception;
    void Execute() throws Exception;
    void Display();
}
