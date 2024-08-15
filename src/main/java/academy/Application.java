package academy;

import academy.maze.Generator;
import academy.maze.MazeReader;
import academy.maze.MazeRenderer;
import academy.maze.Solver;
import academy.maze.dto.Point;
import academy.maze.impl.AStarSolver;
import academy.maze.impl.DfsMazeGenerator;
import academy.maze.impl.DijkstraSolver;
import academy.maze.impl.PrimMazeGenerator;
import java.io.File;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.*;

@Command(
        name = "maze-app",
        mixinStandardHelpOptions = true,
        version = "maze-app 1.0",
        description = "Maze generator and solver CLI application.",
        subcommands = {
            Application.GenerateCommand.class,
            Application.SolveCommand.class,
        })
public class Application implements Runnable {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    @Command(name = "generate", description = "Generate a maze with specified algorithm and dimensions.")
    static class GenerateCommand implements Callable<Integer> {
        @Option(
                names = {"-a", "--algorithm"},
                required = true,
                description = "Algorithm: dfs or prim")
        String algorithm;

        @Option(
                names = {"-w", "--width"},
                required = true,
                description = "Maze width")
        int width;

        @Option(
                names = {"-h", "--height"},
                required = true,
                description = "Maze height")
        int height;

        @Option(
                names = {"-o", "--output"},
                description = "Output file (optional, prints to console if absent)")
        File outputFile;

        @Override
        public Integer call() throws Exception {
            Generator generator;
            switch (algorithm.toLowerCase()) {
                case "dfs":
                    generator = new DfsMazeGenerator();
                    break;
                case "prim":
                case "kruskal":
                    generator = new PrimMazeGenerator();
                    break;
                default:
                    System.err.println("Unknown algorithm: " + algorithm);
                    return 1;
            }
            var maze = generator.generate(width, height);
            if (outputFile == null) {
                MazeRenderer.printToConsole(maze, null, null, null);
            } else {
                MazeRenderer.saveToFile(maze, null, null, null, outputFile.getPath());
            }
            return 0;
        }
    }

    @Command(name = "solve", description = "Solve a maze with specified algorithm and points.")
    static class SolveCommand implements Callable<Integer> {
        @Option(
                names = {"-a", "--algorithm"},
                required = true,
                description = "Algorithm: astar or dijkstra")
        String algorithm;

        @Option(
                names = {"-f", "--file"},
                required = true,
                description = "Input maze file")
        File inputFile;

        @Option(
                names = {"-s", "--start"},
                required = true,
                description = "Start point x,y")
        String startPointStr;

        @Option(
                names = {"-e", "--end"},
                required = true,
                description = "End point x,y")
        String endPointStr;

        @Option(
                names = {"-o", "--output"},
                description = "Output file (optional, prints to console if absent)")
        File outputFile;

        @Override
        public Integer call() throws Exception {
            try {
                Solver solver;
                switch (algorithm.toLowerCase()) {
                    case "astar":
                        solver = new AStarSolver();
                        break;
                    case "dijkstra":
                        solver = new DijkstraSolver();
                        break;
                    default:
                        System.err.println("Unknown algorithm: " + algorithm);
                        return 1;
                }
                var maze = MazeReader.readFromFile(inputFile.getPath());
                Point start = parsePoint(startPointStr);
                Point end = parsePoint(endPointStr);
                var path = solver.solve(maze, start, end);

                if (outputFile == null) {
                    MazeRenderer.printToConsole(maze, start, end, path);
                } else {
                    MazeRenderer.saveToFile(maze, start, end, path, outputFile.getPath());
                }
                return 0;
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                return 128;
            }
        }
    }

    private static Point parsePoint(String s) {
        String[] parts = s.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid point format: " + s + ", expected format: x,y");
        }
        try {
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());
            return new Point(x, y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in point: " + s);
        }
    }
}
