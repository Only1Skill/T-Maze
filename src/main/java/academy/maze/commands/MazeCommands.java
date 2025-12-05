package academy.maze.commands;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MazeCommands {

    public static final String TESTS = "/tests";

    private static final List<Path> SEARCH_ROOTS =
            List.of(Path.of(""), Path.of("src", "main", "resources"), Path.of("tests"), Path.of(TESTS));

    public static Maze loadMazeFromFile(String filePath) throws IOException {
        Path path = findFile(filePath);
        List<String> lines = Files.readAllLines(path);
        if (lines.isEmpty()) {
            throw new IOException("Файл пуст: " + path);
        }
        int height = lines.size();
        int width = lines.getFirst().length();

        CellType[][] grid = new CellType[height][width];

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                if (x < line.length()) {
                    grid[y][x] = CellType.fromChar(line.charAt(x));
                } else {
                    grid[y][x] = CellType.EMPTY;
                }
            }
        }
        return new Maze(grid);
    }

    private static Path findFile(String rawPath) throws IOException {
        String fileName = rawPath;
        if (fileName.contains(":/")) {
            String[] parts = fileName.split(TESTS);
            if (parts.length > 1) {
                fileName = TESTS + parts[1];
            }
        }

        String relativeName = fileName.replaceFirst("^[\\\\/]", "");

        Path directPath = Path.of(fileName);
        if (Files.exists(directPath)) {
            return directPath;
        }

        for (Path root : SEARCH_ROOTS) {
            Path candidate = root.resolve(relativeName);
            if (Files.exists(candidate)) {
                return candidate;
            }
        }

        throw new IOException("Файл лабиринта не найден: " + rawPath + " (искали в: " + SEARCH_ROOTS + ")");
    }

    public static void saveMazeToFile(Maze maze, String filename, boolean useUnicode) throws IOException {
        writeGridToFile(maze.cells(), filename, useUnicode);
    }

    private static String fixPathForDocker(String path) {
        if (path.contains(":/")) {
            int testsIndex = path.indexOf(TESTS);
            if (testsIndex >= 0) {
                return path.substring(testsIndex);
            }
        }
        return path;
    }

    public static Point findCellByType(Maze maze, CellType targetType) {
        CellType[][] grid = maze.cells();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == targetType) {
                    return new Point(x, y);
                }
            }
        }
        throw new IllegalArgumentException("Клетка типа " + targetType + " не найдена в лабиринте");
    }

    public static Point validateOrFindStartPoint(Maze maze, Point suggestedStart) {
        CellType[][] grid = maze.cells();
        int width = grid[0].length;
        int height = grid.length;

        int x = suggestedStart.x();
        int y = suggestedStart.y();

        if (x < 0 || y < 0 || x >= width || y >= height) {
            throw new IllegalArgumentException("Начальная точка вне границ лабиринта");
        }

        if (grid[y][x] != CellType.WALL) {
            return suggestedStart;
        }

        log.info("Начальная точка находится на стене. Ищем ближайшую свободную клетку...");

        return findNearestPassableCell(maze, suggestedStart, "начальной точки");
    }

    private static Point findNearestPassableCell(Maze maze, Point start, String pointName) {
        CellType[][] grid = maze.cells();
        int width = grid[0].length;
        int height = grid.length;

        boolean[][] visited = new boolean[height][width];
        Queue<Point> queue = new LinkedList<>();
        queue.offer(start);
        visited[start.y()][start.x()] = true;

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            for (int[] dir : directions) {
                int nx = current.x() + dir[0];
                int ny = current.y() + dir[1];

                if (nx >= 0 && nx < width && ny >= 0 && ny < height && !visited[ny][nx]) {
                    if (grid[ny][nx] != CellType.WALL) {
                        System.out.println(pointName + " перемещена в (" + nx + ", " + ny + ")");
                        return new Point(nx, ny);
                    }
                    visited[ny][nx] = true;
                    queue.offer(new Point(nx, ny));
                }
            }
        }

        throw new IllegalStateException("Не удалось найти свободную клетку для " + pointName);
    }

    public static void printMaze(Maze maze) {
        printMaze(maze, false);
    }

    public static void printMaze(Maze maze, boolean useUnicode) {
        printGrid(maze.cells(), useUnicode);
    }

    public static void printMazeWithPath(Maze maze, List<Point> path, boolean useUnicode) {
        CellType[][] gridWithPath = createGridWithPath(maze, path);
        printGrid(gridWithPath, useUnicode);
    }

    public static void printGrid(CellType[][] grid, boolean useUnicode) {
        for (CellType[] row : grid) {
            for (CellType cell : row) {
                if (useUnicode) {
                    System.out.print(cell.getUnicode());
                } else {
                    System.out.print(cell.getSymbol());
                }
            }
            System.out.println();
        }
    }

    private static CellType[][] copyGrid(CellType[][] original) {
        CellType[][] copy = new CellType[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }

    public static void saveSolutionToFile(Maze maze, List<Point> path, String filename, boolean useUnicode)
            throws IOException {
        CellType[][] grid = createGridWithPath(maze, path);
        writeGridToFile(grid, filename, useUnicode);
    }

    private static void writeGridToFile(CellType[][] grid, String filename, boolean useUnicode) throws IOException {
        String fixedFilename = fixPathForDocker(filename);
        Path path = Path.of(fixedFilename);

        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (CellType[] row : grid) {
                for (CellType cell : row) {
                    if (useUnicode) {
                        writer.write(cell.getUnicode()); // Запишет ▓, ░
                    } else {
                        writer.write(cell.getSymbol()); // Запишет #, ' '
                    }
                }
                writer.newLine();
            }
        }
    }

    private static CellType[][] createGridWithPath(Maze maze, List<Point> path) {
        CellType[][] grid = copyGrid(maze.cells());

        for (Point point : path) {
            CellType currentType = grid[point.y()][point.x()];
            if (currentType != CellType.START && currentType != CellType.END) {
                grid[point.y()][point.x()] = CellType.PATH;
            }
        }
        return grid;
    }
}
