package academy;

import academy.maze.InteractiveMazeManager;
import academy.maze.NotInteractiveMazeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "maze-app",
        version = "1.0",
        mixinStandardHelpOptions = true,
        description = "Maze generator and solver CLI application.",
        subcommands = {Application.GenerateCommand.class, Application.SolveCommand.class})
public class Application implements Runnable {

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "OFF");
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        InteractiveMazeManager.start();
    }

    @Command(name = "generate", description = "Generate a maze with specified algorithm and dimensions.")
    static class GenerateCommand implements Runnable {

        private static final Logger LOGGER = LoggerFactory.getLogger(GenerateCommand.class);

        @Option(
                names = {"--algorithm", "-a"},
                required = true,
                description = "Maze generation algorithm (dfs, prim, binary)")
        private String algorithm;

        @Option(
                names = {"--width", "-w"},
                required = true,
                description = "Width of the maze")
        private int width;

        @Option(
                names = {"--height", "-h"},
                required = true,
                description = "Height of the maze")
        private int height;

        @Option(
                names = {"--output", "-o"},
                required = true,
                description = "Output file for the generated maze")
        private String output;

        @Override
        public void run() {
            LOGGER.info(
                    "Запуск генерации лабиринта: algorithm={}, size={}x{}, output={}",
                    algorithm,
                    width,
                    height,
                    output);
            try {
                NotInteractiveMazeManager.generateMaze(algorithm, width, height, output);
            } catch (Exception e) {
            }
        }
    }

    @Command(name = "solve", description = "Solve a maze with specified algorithm and points.")
    static class SolveCommand implements Runnable {

        private static final Logger LOGGER = LoggerFactory.getLogger(SolveCommand.class);

        @Option(
                names = {"--algorithm", "-a"},
                required = true,
                description = "Maze solving algorithm (astar, dijkstra, bfs)")
        private String algorithm;

        @Option(
                names = {"--file", "-f"},
                required = true,
                description = "Input maze file")
        private String file;

        @Option(
                names = {"--start", "-s"},
                required = true,
                description = "Start coordinates in format x,y")
        private String start;

        @Option(
                names = {"--end", "-e"},
                required = true,
                description = "End coordinates in format x,y")
        private String end;

        @Option(
                names = {"--output", "-o"},
                required = true,
                description = "Output file for the solved maze")
        private String output;

        @Override
        public void run() {
            LOGGER.info(
                    "Solving maze: file={}, algorithm={}, start={}, end={}, output={}",
                    file,
                    algorithm,
                    start,
                    end,
                    output);

            try {
                int[] startCoords = parseCoords(start);
                int[] endCoords = parseCoords(end);
                NotInteractiveMazeManager.solveMaze(algorithm, file, startCoords, endCoords, output);
                LOGGER.info("Лабиринт решен успешно. Сохранено в {}", output);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        private int[] parseCoords(String s) {
            // Проверяем, что строка содержит ровно одну запятую
            if (!s.contains(",")) {
                String errorMessage = "Invalid point format: " + s + ", expected format: x,y";
                throw new IllegalArgumentException(errorMessage);
            }

            String[] parts = s.split(",");
            if (parts.length != 2) {
                String errorMessage = "Invalid point format: " + s + ", expected format: x,y";
                throw new IllegalArgumentException(errorMessage);
            }

            // Дополнительная проверка что оба числа не пустые
            if (parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                String errorMessage = "Invalid point format: " + s + ", expected format: x,y";
                throw new IllegalArgumentException(errorMessage);
            }

            try {
                return new int[] {Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())};
            } catch (NumberFormatException e) {
                String errorMessage = "Invalid point format: " + s + ", coordinates must be integers";
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }
}
