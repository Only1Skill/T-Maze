package academy;

import static org.junit.jupiter.api.Assertions.*;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import academy.maze.generator.DFSGenerator;
import academy.maze.solver.AStarSolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Error Handling and Edge Cases Tests")
class ErrorHandlingTest {

    @Test
    @DisplayName("Should throw exception for negative dimensions")
    void generator_ShouldThrowExceptionForNegativeDimensions() {
        // Подготовка
        DFSGenerator generator = new DFSGenerator(false);

        // Действие и Проверка - используем лямбду для отложенного выполнения
        assertThrows(
                NegativeArraySizeException.class,
                () -> {
                    generator.generate(-1, 10);
                },
                "Should throw exception for negative width");

        assertThrows(
                NegativeArraySizeException.class,
                () -> {
                    generator.generate(10, -1);
                },
                "Should throw exception for negative height");
    }

    @Test
    @DisplayName("Solver should throw exception for invalid start point")
    void solver_ShouldThrowExceptionForInvalidStartPoint() {
        // Подготовка
        CellType[][] grid = createSimpleMaze();
        Maze maze = new Maze(grid);
        AStarSolver solver = new AStarSolver();
        Point invalidStart = new Point(0, 0); // Позиция стены
        Point end = new Point(3, 3);

        // Действие и Проверка
        assertThrows(
                RuntimeException.class,
                () -> {
                    solver.solve(maze, invalidStart, end);
                },
                "Should throw exception when start point is on wall");
    }

    @Test
    @DisplayName("Solver should throw exception when no path exists")
    void solver_ShouldThrowExceptionWhenNoPathExists() {
        // Подготовка - лабиринт без пути
        CellType[][] grid = createImpossibleMaze();
        Maze maze = new Maze(grid);
        AStarSolver solver = new AStarSolver();
        Point start = new Point(1, 1);
        Point end = new Point(3, 3);

        // Действие и Проверка
        assertThrows(
                RuntimeException.class,
                () -> {
                    solver.solve(maze, start, end);
                },
                "Should throw exception when no path exists");
    }

    @Test
    @DisplayName("Should handle out of bounds coordinates gracefully")
    void solver_ShouldHandleOutOfBoundsCoordinates() {
        // Подготовка
        CellType[][] grid = createSimpleMaze();
        Maze maze = new Maze(grid);
        AStarSolver solver = new AStarSolver();
        Point outOfBounds = new Point(100, 100); // За пределами лабиринта
        Point validPoint = new Point(1, 1);

        // Действие и Проверка
        assertThrows(
                RuntimeException.class,
                () -> {
                    solver.solve(maze, outOfBounds, validPoint);
                },
                "Should throw exception for out of bounds coordinates");
    }

    // Вспомогательные методы
    private CellType[][] createSimpleMaze() {
        return new CellType[][] {
            {CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL},
            {CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL},
            {CellType.WALL, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL},
            {CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL},
            {CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL}
        };
    }

    private CellType[][] createImpossibleMaze() {
        return new CellType[][] {
            {CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL},
            {CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL},
            {CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL},
            {CellType.WALL, CellType.EMPTY, CellType.WALL, CellType.EMPTY, CellType.WALL},
            {CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL}
        };
    }
}
