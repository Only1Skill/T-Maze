package academy.maze;

import static java.nio.file.Files.newOutputStream;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;
import java.io.IOException;
import java.io.PrintStream;

public class MazeRenderer {
    // Метод для отрисовки лабиринта с указанием пути и начальной/конечной точек
    public static void render(Maze maze, Point start, Point end, Path path, PrintStream out) {
        CellType[][] cells = maze.cells();
        int height = cells.length;
        int width = cells[0].length;

        // Создаем буфер символов
        char[][] display = new char[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                display[y][x] = (cells[y][x] == CellType.WALL) ? '#' : ' ';
            }
        }

        // Отмечаем путь точками '.', кроме start и end
        if (path != null && start != null && end != null) {
            for (Point point : path.points()) {
                if (!point.equals(start) && !point.equals(end)) {
                    display[point.y()][point.x()] = '.';
                }
            }
            // Отмечаем старт и конец
            display[start.y()][start.x()] = 'O';
            display[end.y()][end.x()] = 'X';
        }

        // Обводим лабиринт рамкой #
        int frameWidth = width + 2;
        String border = "#".repeat(frameWidth);
        out.println(border);
        for (int y = 0; y < height; y++) {
            out.print("#");
            for (int x = 0; x < width; x++) {
                out.print(display[y][x]);
            }
            out.println("#");
        }
        out.println(border);
    }

    // Сохраняет лабиринт с путем в файл
    public static void saveToFile(Maze maze, Point start, Point end, Path path, String filename) throws IOException {
        try (PrintStream out = new PrintStream(newOutputStream(java.nio.file.Path.of(filename)))) {
            render(maze, start, end, path, out);
        }
    }

    // Печатает лабиринт с путем в консоль
    public static void printToConsole(Maze maze, Point start, Point end, Path path) {
        render(maze, start, end, path, System.out);
    }
}
