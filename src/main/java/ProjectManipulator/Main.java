package ProjectManipulator;


import ProjectManipulator.Commands.CommandCreator;
import ProjectManipulator.Commands.CommandType;
import ProjectManipulator.Commands.ICommand;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {

        try{
            System.out.println("Выберите команду и передайте необходимые параметры, через ';':");
            System.out.println("0 solution=\"путь к файлу солюшена (.sln) файла\" - Получить версии всех проектов в солюшене .Net");
            System.out.println("0 solution=\"путь к файлу солюшена (.sln) файла\" projects=\"список проектов через ','\" - Получить версии проектов из списка в солюшене .Net");
            System.out.println("0 solution=\"путь к файлу солюшена (.sln) файла\" [AUTO];[путь к папке гита (.git)];[oldBranch];[newBranch] - Получить версии измененных проектов из двух веток  .Net");
            System.out.println();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String userInput = bufferedReader.readLine();

            final String[] splitCommands = userInput.split(CommandType.COMMAND_DELIMITER);
            CommandType commandType = CommandType.FromString(splitCommands[0]);
            PrintUserInput(commandType, splitCommands);

            long startTime = System.currentTimeMillis();
            ICommand command = CommandCreator.CreateCommand(commandType);
            command.LoadParameters(userInput);
            command.Execute();
            command.Display();
            long endTime = System.currentTimeMillis();
            System.out.println("Общее время выполнения команды: " + (endTime-startTime) + "ms");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void PrintUserInput(CommandType commandType, String[] splitUserInput) {
        for (int index = 0; index < splitUserInput.length; index++) {
            if (index == 0) {
                System.out.printf("Команда выбрана: %s (%s)\r\n", splitUserInput[index], commandType);
            }
            else {
                System.out.println(splitUserInput[index]);
            }
        }
    }
}
