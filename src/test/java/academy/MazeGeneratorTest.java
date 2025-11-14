package academy;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.generator.BinaryTreeGenerator;
import academy.maze.generator.DFSGenerator;
import academy.maze.generator.PrimGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Maze Generator Tests")
class MazeGeneratorTest {

    @Test
    @DisplayName("DFS Generator should create valid maze with correct dimensions")
    void dfsGenerator_ShouldCreateValidMaze() {
        // Шаг 1: Подготовка данных
        DFSGenerator generator = new DFSGenerator(false);
        int width = 15;
        int height = 15;

        // Шаг 2: Выполнение действия
        Maze maze = generator.generate(width, height);

        // Шаг 3: Проверка результатов
        assertNotNull(maze, "Generated maze should not be null");
        assertNotNull(maze.cells(), "Maze cells should not be null");
        assertEquals(height, maze.cells().length, "Maze height should match");
        assertEquals(width, maze.cells()[0].length, "Maze width should match");

        // Проверка границ лабиринта
        assertTrue(hasValidBorders(maze.cells(), width, height),
                   "Maze should have valid wall borders");
    }

    @Test
    @DisplayName("Prim Generator should create valid maze")
    void primGenerator_ShouldCreateValidMaze() {
        // Подготовка
        PrimGenerator generator = new PrimGenerator(false);
        int width = 20;
        int height = 20;

        // Действие
        Maze maze = generator.generate(width, height);

        // Проверка
        assertNotNull(maze);
        assertEquals(height, maze.cells().length);
        assertEquals(width, maze.cells()[0].length);
    }

    @Test
    @DisplayName("Binary Tree Generator should create valid maze")
    void binaryTreeGenerator_ShouldCreateValidMaze() {
        // Подготовка
        BinaryTreeGenerator generator = new BinaryTreeGenerator(false);
        int width = 10;
        int height = 10;

        // Действие
        Maze maze = generator.generate(width, height);

        // Проверка
        assertNotNull(maze);
        assertEquals(height, maze.cells().length);
        assertEquals(width, maze.cells()[0].length);
    }

    @Test
    @DisplayName("Generator with coatings should include different cell types")
    void generatorWithCoatings_ShouldIncludeDifferentCellTypes() {
        // Подготовка - с покрытиями
        DFSGenerator generator = new DFSGenerator(true);
        int width = 15;
        int height = 15;

        // Действие
        Maze maze = generator.generate(width, height);

        // Проверка
        assertNotNull(maze);
        boolean hasSpecialCells = hasSpecialCoverageCells(maze.cells());
        assertTrue(hasSpecialCells, "Maze should contain special coverage cells when coatings are enabled");
    }

    // Вспомогательные методы
    private boolean hasValidBorders(CellType[][] maze, int width, int height) {
        // Проверка верхней и нижней границ
        for (int x = 0; x < width; x++) {
            if (maze[0][x] != CellType.WALL || maze[height - 1][x] != CellType.WALL) {
                return false;
            }
        }

        // Проверка левой и правой границ
        for (int y = 0; y < height; y++) {
            if (maze[y][0] != CellType.WALL || maze[y][width - 1] != CellType.WALL) {
                return false;
            }
        }

        return true;
    }

    private boolean hasSpecialCoverageCells(CellType[][] cells) {
        for (CellType[] row : cells) {
            for (CellType cell : row) {
                if (cell == CellType.GRASS || cell == CellType.SAND || cell == CellType.WATER) {
                    return true;
                }
            }
        }
        return false;
    }
}
