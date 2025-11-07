package academy.maze.commands;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MazeCommands {

    public static Maze loadMazeFromFile(String filePath) throws IOException {
        // Обработка пути для Docker контейнера
        if (filePath.contains(":/")) {
            String[] parts = filePath.split("/tests");
            if (parts.length > 1) {
                filePath = "/tests" + parts[1];
            }
        }

        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            // Пробуем альтернативные пути...
            Path[] alternativePaths = {
                Path.of("src", "main", "resources", filePath),
                Path.of(filePath.replaceFirst("^/", "")),
                Path.of("tests", filePath),
                Path.of("/tests", filePath)
            };

            for (Path altPath : alternativePaths) {
                if (Files.exists(altPath)) {
                    path = altPath;
                    break;
                }
            }

            if (!Files.exists(path)) {
                throw new IOException("Файл лабиринта не найден: " + filePath);
            }
        }

        List<String> lines = Files.readAllLines(path);
        int height = lines.size();
        int width = lines.get(0).length();

        CellType[][] grid = new CellType[height][width];

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                grid[y][x] = charToCellType(line.charAt(x));
            }
        }

        return new Maze(grid);
    }

    public static void saveMazeToFile(Maze maze, String filename) throws IOException {
        // Исправляем путь для Docker
        String fixedFilename = fixPathForDocker(filename);
        Path path = Path.of(fixedFilename);

        // Создаем директорию если нужно
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (CellType[] row : maze.cells()) {
                for (CellType cell : row) {
                    writer.write(cell.getSymbol());
                }
                writer.newLine();
            }
        }
        // Убрали вывод в консоль
    }

    private static String fixPathForDocker(String path) {
        // Если путь содержит Windows-стиль, извлекаем только Linux-путь
        if (path.contains(":/")) {
            // Ищем "/tests" в пути
            int testsIndex = path.indexOf("/tests");
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

        System.out.println("Начальная точка находится на стене. Ищем ближайшую свободную клетку...");

        return findNearestPassableCell(maze, suggestedStart, "начальной точки");
    }

    public static Point validateOrFindEndPoint(Maze maze, Point suggestedEnd) {
        CellType[][] grid = maze.cells();
        int width = grid[0].length;
        int height = grid.length;

        int x = suggestedEnd.x();
        int y = suggestedEnd.y();

        if (x < 0 || y < 0 || x >= width || y >= height) {
            throw new IllegalArgumentException("Конечная точка вне границ лабиринта");
        }

        if (grid[y][x] != CellType.WALL) {
            return suggestedEnd;
        }

        System.out.println("Конечная точка находится на стене. Ищем ближайшую свободную клетку...");

        return findNearestPassableCell(maze, suggestedEnd, "конечной точки");
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
        CellType[][] grid = maze.cells();
        for (CellType[] row : grid) {
            for (CellType cell : row) {
                char symbol = cell.getSymbol();
                if (useUnicode) {
                    System.out.print(convertToUnicode(symbol));
                } else {
                    System.out.print(symbol);
                }
            }
            System.out.println();
        }
    }

    public static void printMazeWithPath(Maze maze, List<Point> path, boolean useUnicode) {
        CellType[][] grid = copyGrid(maze.cells());

        for (Point point : path) {
            if (grid[point.y()][point.x()] != CellType.START && grid[point.y()][point.x()] != CellType.END) {
                grid[point.y()][point.x()] = CellType.PATH;
            }
        }

        for (CellType[] row : grid) {
            for (CellType cell : row) {
                char symbol = cell.getSymbol();
                if (useUnicode) {
                    System.out.print(convertToUnicode(symbol));
                } else {
                    System.out.print(symbol);
                }
            }
            System.out.println();
        }
    }

    public static String convertToUnicode(char symbol) {
        return switch (symbol) {
            case '#' -> "▓"; // стена
            case ' ' -> "░"; // путь
            case 'S' -> "●"; // старт
            case 'E' -> "★"; // финиш
            case '.' -> "•"; // найденный путь
            default -> String.valueOf(symbol);
        };
    }

    private static CellType charToCellType(char c) {
        return switch (c) {
            case '#' -> CellType.WALL;
            case 'S', 'O' -> CellType.START;
            case 'E', 'X' -> CellType.END;
            case '.' -> CellType.PATH;
            case 'G' -> CellType.GRASS;
            case 'N' -> CellType.SAND;
            case 'W' -> CellType.WATER;
            default -> CellType.EMPTY;
        };
    }

    private static CellType[][] copyGrid(CellType[][] original) {
        CellType[][] copy = new CellType[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }

    public static void saveSolutionToFile(Maze maze, List<Point> path, String filename) throws IOException {
        CellType[][] grid = copyGrid(maze.cells());

        // Отмечаем путь в лабиринте
        for (Point point : path) {
            if (grid[point.y()][point.x()] != CellType.START && grid[point.y()][point.x()] != CellType.END) {
                grid[point.y()][point.x()] = CellType.PATH;
            }
        }

        Path outputPath = Path.of(filename);
        Path parent = outputPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            for (CellType[] row : grid) {
                for (CellType cell : row) {
                    writer.write(cell.getSymbol());
                }
                writer.newLine();
            }
        }
    }
}
