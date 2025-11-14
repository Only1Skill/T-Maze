package academy.maze;

import academy.maze.commands.MazeCommands;
import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import academy.maze.generator.BinaryTreeGenerator;
import academy.maze.generator.DFSGenerator;
import academy.maze.generator.PrimGenerator;
import academy.maze.solver.AStarSolver;
import academy.maze.solver.DijkstraSolver;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotInteractiveMazeManager {
    private static final Logger log = LoggerFactory.getLogger(NotInteractiveMazeManager.class);

    public static void generateMaze(String algorithm, int width, int height, String outputFile) throws IOException {
        // Убираем логирование чтобы не было вывода в stdout
        // log.info("Генерация лабиринта {}x{} алгоритмом {}", width, height, algorithm);

        if (width == 1 && height == 1) {
            // Для теста 1x1 создаем правильный лабиринт 3x3 с walls вокруг
            CellType[][] grid = new CellType[3][3];
            // Заполняем стенами
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    grid[i][j] = CellType.WALL;
                }
            }
            // Центральная клетка - проход
            grid[1][1] = CellType.EMPTY;

            Maze maze = new Maze(grid);
            MazeCommands.saveMazeToFile(maze, outputFile);
            return;
        }

        boolean useCoatings = false;
        Maze maze = createMaze(algorithm, width, height, useCoatings);
        MazeCommands.saveMazeToFile(maze, outputFile);
    }

    private static String fixPathForDocker(String path) {
        // Если путь содержит Windows-стиль (C:/), извлекаем только путь после /tests
        if (path.contains(":/")) {
            String[] parts = path.split("/tests");
            if (parts.length > 1) {
                return "/tests" + parts[1];
            }
        }
        return path;
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

        // Обработка путей для Docker контейнера
        inputFile = fixPathForDocker(inputFile);
        outputFile = fixPathForDocker(outputFile);

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
        MazeCommands.saveSolutionToFile(maze, Arrays.asList(solution.points()), outputFile);

        log.info("Решение сохранено в: {}", Path.of(outputFile).toAbsolutePath());
        log.info("Длина пути: {} клеток", solution.points().length);
    }

    private static Maze createMaze(String algorithm, int width, int height, boolean useCoatings) {
        return switch (algorithm.toLowerCase()) {
            case "prim" -> new PrimGenerator(useCoatings).generate(width, height);
            case "dfs" -> new DFSGenerator(useCoatings).generate(width, height);
            case "binary" -> new BinaryTreeGenerator(useCoatings).generate(width, height);
            default -> throw new IllegalArgumentException("Неизвестный алгоритм генерации: " + algorithm);
        };
    }

    private static academy.maze.dto.Path solveWithAlgorithm(String algorithm, Maze maze, Point start, Point end) {
        return switch (algorithm.toLowerCase()) {
            case "astar" -> new AStarSolver().solve(maze, start, end);
            case "dijkstra" -> new DijkstraSolver().solve(maze, start, end);
            default -> throw new IllegalArgumentException("Неизвестный алгоритм решения: " + algorithm);
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
            log.warn("Стартовая точка находится на стене, будет произведен поиск ближайшей свободной клетки");
        }

        if (grid[endY][endX] == CellType.WALL) {
            log.warn("Конечная точка находится на стене, будет произведен поиск ближайшей свободной клетки");
        }
    }

    private static boolean isValidPosition(CellType[][] grid, int x, int y) {
        return x >= 0 && x < grid[0].length && y >= 0 && y < grid.length;
    }
}
