package academy.maze.impl;

import academy.maze.Generator;
import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrimMazeGenerator implements Generator {

    private static final Random random = new Random();

    @Override
    public Maze generate(int width, int height) {
        @SuppressWarnings("CPD-START")
        // Размеры массива с учетом стен (каждая клетка окружена стенами)
        int mazeWidth = width * 2 + 1;
        int mazeHeight = height * 2 + 1;

        CellType[][] cells = new CellType[mazeHeight][mazeWidth];

        // Инициализация всех клеток как стены
        for (int y = 0; y < mazeHeight; y++) {
            for (int x = 0; x < mazeWidth; x++) {
                cells[y][x] = CellType.WALL;
            }
        }

        // Начинаем с произвольной ячейки внутри
        int startX = 2 * random.nextInt(width) + 1;
        int startY = 2 * random.nextInt(height) + 1;
        cells[startY][startX] = CellType.PATH;
        @SuppressWarnings("CPD-END")

        // Список граней (крайних стен), которые окружают проходы, которые мы уже "вырезали"
        List<int[]> frontier = new ArrayList<>();

        // Добавляем соседние стены выбранной клетки в frontier
        addFrontier(startX, startY, cells, frontier, mazeWidth, mazeHeight);

        while (!frontier.isEmpty()) {
            // Выбираем случайную стену frontier
            int idx = random.nextInt(frontier.size());
            int[] wall = frontier.remove(idx);
            int wx = wall[0];
            int wy = wall[1];

            // Находим соседнюю клетку, которая ещё не посещена (сделана как путь)
            int[] opposite = findOppositeCell(wx, wy, cells, mazeWidth, mazeHeight);

            if (opposite != null) {
                int ox = opposite[0];
                int oy = opposite[1];
                if (cells[oy][ox] == CellType.WALL) {
                    // "Вырезаем" стену и соседнюю клетку
                    cells[wy][wx] = CellType.PATH;
                    cells[oy][ox] = CellType.PATH;

                    // Добавляем новые frontier стены от новой клетки
                    addFrontier(ox, oy, cells, frontier, mazeWidth, mazeHeight);
                }
            }
        }

        return new Maze(cells);
    }

    private void addFrontier(int x, int y, CellType[][] cells, List<int[]> frontier, int mazeWidth, int mazeHeight) {
        // Проверяем 4 направления (двухклеточные шаги), добавляя стены, которые граничат с ещё не посещёнными клетками
        int[][] directions = {
            {2, 0},
            {-2, 0},
            {0, 2},
            {0, -2}
        };
        for (int[] d : directions) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (nx > 0 && nx < mazeWidth && ny > 0 && ny < mazeHeight && cells[ny][nx] == CellType.WALL) {
                int wallX = x + d[0] / 2;
                int wallY = y + d[1] / 2;
                frontier.add(new int[] {wallX, wallY});
            }
        }
    }

    private int[] findOppositeCell(int wallX, int wallY, CellType[][] cells, int mazeWidth, int mazeHeight) {
        // Находим клетку напротив стены из двух сторон, проверяем в какой клетке уже проход
        // и возвращаем другую (не посещённую) клетку
        int[][] directions = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
        };
        for (int[] d : directions) {
            int cx1 = wallX + d[0];
            int cy1 = wallY + d[1];
            int cx2 = wallX - d[0];
            int cy2 = wallY - d[1];

            if (cx1 >= 0
                    && cx1 < mazeWidth
                    && cy1 >= 0
                    && cy1 < mazeHeight
                    && cx2 >= 0
                    && cx2 < mazeWidth
                    && cy2 >= 0
                    && cy2 < mazeHeight) {
                if (cells[cy1][cx1] == CellType.PATH && cells[cy2][cx2] == CellType.WALL) {
                    return new int[] {cx2, cy2};
                }
                if (cells[cy2][cx2] == CellType.PATH && cells[cy1][cx1] == CellType.WALL) {
                    return new int[] {cx1, cy1};
                }
            }
        }
        return null;
    }
}
