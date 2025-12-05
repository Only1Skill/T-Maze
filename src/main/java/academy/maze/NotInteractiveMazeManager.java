package academy.maze;

import academy.maze.commands.MazeCommands;
import academy.maze.dto.CellType;
import academy.maze.dto.GeneratorType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import academy.maze.dto.SolverType;
import academy.maze.generator.BinaryTreeGenerator;
import academy.maze.generator.DFSGenerator;
import academy.maze.generator.PrimGenerator;
import academy.maze.solver.AStarSolver;
import academy.maze.solver.DijkstraSolver;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotInteractiveMazeManager {

    public static void generateMaze(String algorithm, int width, int height, String outputFile) throws IOException {

        if (width == 1 && height == 1) {
            CellType[][] grid = new CellType[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    grid[i][j] = CellType.WALL;
                }
            }
            grid[1][1] = CellType.EMPTY;

            Maze maze = new Maze(grid);
            MazeCommands.saveMazeToFile(maze, outputFile, false);
            return;
        }

        boolean useCoatings = false;
        Maze maze = createMaze(algorithm, width, height, useCoatings);

        MazeCommands.saveMazeToFile(maze, outputFile, false);
    }

    public static void solveMaze(
            String algorithm, String inputFile, int[] startCoords, int[] endCoords, String outputFile)
            throws IOException {

        log.info(
                "Решение лабиринта алгоритмом {} от ({},{}) до ({},{})",
                algorithm,
                startCoords[0],
                startCoords[1],
                endCoords[0],
                endCoords[1]);

        Maze maze = MazeCommands.loadMazeFromFile(inputFile);

        int startX = startCoords[0];
        int startY = startCoords[1];
        int endX = endCoords[0];
        int endY = endCoords[1];

        validatePoints(maze, startX, startY, endX, endY);

        Point start = new Point(startX, startY);
        Point end = new Point(endX, endY);

        CellType[][] grid = maze.cells();
        grid[start.y()][start.x()] = CellType.START;
        grid[end.y()][end.x()] = CellType.END;

        var solution = solveWithAlgorithm(algorithm, maze, start, end);

        MazeCommands.saveSolutionToFile(maze, Arrays.asList(solution.points()), outputFile, false);

        log.info("Решение сохранено в: {}", Path.of(outputFile).toAbsolutePath());
        log.info("Длина пути: {} клеток", solution.points().length);
    }

    private static Maze createMaze(String algorithm, int width, int height, boolean useCoatings) {
        GeneratorType type = GeneratorType.find(algorithm);
        return switch (type) {
            case PRIM -> new PrimGenerator(useCoatings).generate(width, height);
            case DFS -> new DFSGenerator(useCoatings).generate(width, height);
            case BINARY -> new BinaryTreeGenerator(useCoatings).generate(width, height);
        };
    }

    private static academy.maze.dto.Path solveWithAlgorithm(String algorithm, Maze maze, Point start, Point end) {
        SolverType type = SolverType.find(algorithm);
        return switch (type) {
            case ASTAR -> new AStarSolver().solve(maze, start, end);
            case DIJKSTRA -> new DijkstraSolver().solve(maze, start, end);
        };
    }

    private static void validatePoints(Maze maze, int startX, int startY, int endX, int endY) {
        CellType[][] grid = maze.cells();

        if (!isValidPosition(grid, startX, startY)) {
            throw new IllegalArgumentException("Неверная стартовая позиция: (" + startX + ", " + startY + ")");
        }

        if (!isValidPosition(grid, endX, endY)) {
            throw new IllegalArgumentException("Неверная конечная позиция: (" + endX + ", " + endY + ")");
        }

        if (startX == endX && startY == endY) {
            throw new IllegalArgumentException("Старт и финиш не могут совпадать");
        }

        if (grid[startY][startX] == CellType.WALL) {
            log.warn("Стартовая точка находится на стене!");
        }

        if (grid[endY][endX] == CellType.WALL) {
            log.warn("Конечная точка находится на стене!");
        }
    }

    private static boolean isValidPosition(CellType[][] grid, int x, int y) {
        return x >= 0 && x < grid[0].length && y >= 0 && y < grid.length;
    }
}
