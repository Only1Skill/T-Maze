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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class InteractiveMazeManager {
    private static final Scanner input = new Scanner(System.in);
    private static final Random random = new Random();

    public static void start() {
        System.out.println("=== Лабиринт: Генерация и Поиск пути ===");

        while (true) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Создать лабиринт");
            System.out.println("2. Найти путь в лабиринте");
            System.out.println("3. Выход");
            System.out.print("> ");

            int choice = readInt();
            input.nextLine();

            try {
                switch (choice) {
                    case 1 -> createMaze();
                    case 2 -> solveMaze();
                    case 3 -> {
                        System.out.println("До свидания!");
                        input.close();
                        return;
                    }
                    default -> System.out.println("Неверный выбор");
                }
            } catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void createMaze() throws IOException {
        System.out.print("Введите ширину лабиринта: ");
        int width = readInt();
        System.out.print("Введите высоту лабиринта: ");
        int height = readInt();
        input.nextLine();

        System.out.print("Использовать различные покрытия? (1-да, 0-нет): ");
        boolean useCoatings = readInt() == 1;
        input.nextLine();

        System.out.println("Выберите алгоритм генерации:");
        System.out.println("1. Алгоритм Прима");
        System.out.println("2. Поиск в глубину (DFS)");
        System.out.println("3. Бинарное дерево");
        System.out.print("> ");

        int algorithm = readInt();
        input.nextLine();

        Maze maze = generateMaze(algorithm, width, height, useCoatings);

        System.out.println("\nСгенерированный лабиринт:");
        MazeCommands.printMaze(maze);

        System.out.print("Введите имя файла для сохранения: ");
        String filename = input.nextLine().trim();
        if (filename.isEmpty()) filename = "maze.txt";

        Path resourcesDir = Path.of("src", "main", "resources");
        if (!Files.exists(resourcesDir)) {
            Files.createDirectories(resourcesDir);
        }

        Path filePath = resourcesDir.resolve(filename);
        MazeCommands.saveMazeToFile(maze, filePath.toString());
        System.out.println("Лабиринт сохранен в: " + filePath.toAbsolutePath());
    }

    private static Maze generateMaze(int algorithm, int width, int height, boolean useCoatings) {
        return switch (algorithm) {
            case 1 -> new PrimGenerator(useCoatings).generate(width, height);
            case 2 -> new DFSGenerator(useCoatings).generate(width, height);
            case 3 -> new BinaryTreeGenerator(useCoatings).generate(width, height);
            default -> {
                System.out.println("Неизвестный алгоритм, используется Прим");
                yield new PrimGenerator(useCoatings).generate(width, height);
            }
        };
    }

    private static void solveMaze() throws IOException {
        System.out.print("Введите имя файла с лабиринтом: ");
        String filename = input.nextLine().trim();

        Maze maze = MazeCommands.loadMazeFromFile(filename);

        Point start = selectStartPoint(maze);
        Point end = selectEndPoint(maze, start);

        System.out.println("Выберите алгоритм решения:");
        System.out.println("1. A*");
        System.out.println("2. Дейкстра");
        System.out.print("> ");
        int algorithm = readInt();
        input.nextLine();

        System.out.print("Использовать Unicode графику? (1-да, 0-нет): ");
        boolean useUnicode = readInt() == 1;
        input.nextLine();

        CellType[][] grid = maze.cells();
        grid[start.y()][start.x()] = CellType.START;
        grid[end.y()][end.x()] = CellType.END;

        var solution =
                switch (algorithm) {
                    case 1 -> new AStarSolver().solve(maze, start, end);
                    case 2 -> new DijkstraSolver().solve(maze, start, end);
                    default -> throw new IllegalArgumentException("Неизвестный алгоритм");
                };

        System.out.println("\nНайденный путь:");
        MazeCommands.printMazeWithPath(maze, Arrays.asList(solution.points()), useUnicode);

        System.out.print("Сохранить решение? (1-да, 0-нет): ");
        if (readInt() == 1) {
            input.nextLine();
            saveSolution(maze, solution);
        }
    }

    private static Point selectStartPoint(Maze maze) {
        System.out.println("Выберите начальную точку:");
        System.out.println("1. Найти автоматически (O)");
        System.out.println("2. Указать вручную");
        System.out.println("3. Выбрать случайно");
        System.out.print("> ");

        int choice = readInt();
        input.nextLine();

        return switch (choice) {
            case 1 -> MazeCommands.findCellByType(maze, CellType.START);
            case 2 -> readPointFromUser(maze, "начальную");
            case 3 -> findRandomPoint(maze);
            default -> {
                System.out.println("Неверный выбор, используется автоматический поиск");
                yield MazeCommands.findCellByType(maze, CellType.START);
            }
        };
    }

    private static Point selectEndPoint(Maze maze, Point start) {
        System.out.println("Выберите конечную точку:");
        System.out.println("1. Найти автоматически (X)");
        System.out.println("2. Указать вручную");
        System.out.println("3. Выбрать случайно");
        System.out.print("> ");

        int choice = readInt();
        input.nextLine();

        Point end =
                switch (choice) {
                    case 1 -> MazeCommands.findCellByType(maze, CellType.END);
                    case 2 -> readPointFromUser(maze, "конечную");
                    case 3 -> findRandomPoint(maze);
                    default -> {
                        System.out.println("Неверный выбор, используется автоматический поиск");
                        yield MazeCommands.findCellByType(maze, CellType.END);
                    }
                };

        // Убедимся, что начальная и конечная точки разные
        while (end.equals(start)) {
            System.out.println("Конечная точка совпадает с начальной! Выберите другую.");
            end = readPointFromUser(maze, "конечную");
        }

        return end;
    }

    private static Point readPointFromUser(Maze maze, String pointType) {
        while (true) {
            System.out.print("Введите X координату " + pointType + " точки: ");
            int x = readInt();
            System.out.print("Введите Y координату " + pointType + " точки: ");
            int y = readInt();
            input.nextLine();

            Point point = new Point(x, y);
            try {
                return MazeCommands.validateOrFindStartPoint(maze, point);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + " Попробуйте снова.");
            }
        }
    }

    private static Point findRandomPoint(Maze maze) {
        CellType[][] grid = maze.cells();
        List<Point> validPoints = new ArrayList<>();

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] != CellType.WALL) {
                    validPoints.add(new Point(x, y));
                }
            }
        }

        if (validPoints.isEmpty()) {
            throw new IllegalStateException("В лабиринте нет свободных клеток");
        }

        Point point = validPoints.get(random.nextInt(validPoints.size()));
        System.out.println("Случайная точка: (" + point.x() + ", " + point.y() + ")");
        return point;
    }

    private static void saveSolution(Maze maze, academy.maze.dto.Path solution) throws IOException {
        System.out.print("Введите имя файла для решения: ");
        String filename = input.nextLine().trim();
        if (filename.isEmpty()) filename = "solution.txt";

        MazeCommands.saveSolutionToFile(maze, Arrays.asList(solution.points()), filename);
        System.out.println("Решение сохранено в файл: " + filename);
    }

    private static int readInt() {
        while (!input.hasNextInt()) {
            System.out.print("Пожалуйста, введите число: ");
            input.next();
        }
        return input.nextInt();
    }
}
