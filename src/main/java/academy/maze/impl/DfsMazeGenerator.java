package academy.maze.impl;

import academy.maze.Generator;
import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DfsMazeGenerator implements Generator {

    private static final Random random = new Random();

    @Override
    public Maze generate(int width, int height) {
        int mazeWidth = width * 2 + 1;
        int mazeHeight = height * 2 + 1;

        CellType[][] cells = new CellType[mazeHeight][mazeWidth];

        // Заполняем весь массив стенами
        for (int y = 0; y < mazeHeight; y++) {
            for (int x = 0; x < mazeWidth; x++) {
                cells[y][x] = CellType.WALL;
            }
        }

        // Запускаем рекурсивный DFS с произвольной стартовой клетки
        int startX = 2 * random.nextInt(width) + 1;
        int startY = 2 * random.nextInt(height) + 1;

        carvePassagesFrom(startX, startY, cells, mazeWidth, mazeHeight);

        return new Maze(cells);
    }

    private void carvePassagesFrom(int cx, int cy, CellType[][] cells, int mazeWidth, int mazeHeight) {
        int[][] directions = {
            {2, 0},
            {-2, 0},
            {0, 2},
            {0, -2}
        };

        List<int[]> dirs = Arrays.asList(directions);
        Collections.shuffle(dirs, random);

        cells[cy][cx] = CellType.PATH;

        for (int[] dir : dirs) {
            int nx = cx + dir[0];
            int ny = cy + dir[1];

            if (nx > 0 && nx < mazeWidth && ny > 0 && ny < mazeHeight && cells[ny][nx] == CellType.WALL) {
                // Уберем стену между текущей клеткой и новой клеткой
                cells[cy + dir[1] / 2][cx + dir[0] / 2] = CellType.PATH;
                carvePassagesFrom(nx, ny, cells, mazeWidth, mazeHeight);
            }
        }
    }
}
