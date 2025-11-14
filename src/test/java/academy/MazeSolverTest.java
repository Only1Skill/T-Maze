package academy;

import static org.junit.jupiter.api.Assertions.*;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;
import academy.maze.solver.AStarSolver;
import academy.maze.solver.DijkstraSolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Maze Solver Tests")
class MazeSolverTest {

    @Test
    @DisplayName("A* Solver should find optimal path in simple maze")
    void aStarSolver_ShouldFindOptimalPath() {
        // Шаг 1: Создание тестового лабиринта
        CellType[][] grid = createSimpleMaze();
        Maze maze = new Maze(grid);
        AStarSolver solver = new AStarSolver();
        Point start = new Point(1, 1);
        Point end = new Point(3, 3);

        // Шаг 2: Поиск пути
        Path path = solver.solve(maze, start, end);

        // Шаг 3: Проверка результатов
        assertNotNull(path, "Path should not be null");
        assertTrue(path.points().length > 0, "Path should contain points");
        assertEquals(start, path.points()[0], "Path should start at start point");
        assertEquals(end, path.points()[path.points().length - 1], "Path should end at end point");

        // Проверка валидности пути
        assertTrue(isPathValid(path, maze), "Path should be valid (no walls and continuous)");
    }

    @Test
    @DisplayName("Dijkstra Solver should find optimal path")
    void dijkstraSolver_ShouldFindOptimalPath() {
        // Подготовка
        CellType[][] grid = createSimpleMaze();
        Maze maze = new Maze(grid);
        DijkstraSolver solver = new DijkstraSolver();
        Point start = new Point(1, 1);
        Point end = new Point(3, 3);

        // Действие
        Path path = solver.solve(maze, start, end);

        // Проверка
        assertNotNull(path);
        assertTrue(path.points().length > 0);
        assertEquals(start, path.points()[0]);
        assertEquals(end, path.points()[path.points().length - 1]);
    }

    @Test
    @DisplayName("Solver should handle start equals end")
    void solver_ShouldHandleStartEqualsEnd() {
        // Подготовка
        CellType[][] grid = createSimpleMaze();
        Maze maze = new Maze(grid);
        AStarSolver solver = new AStarSolver();
        Point point = new Point(1, 1);

        // Действие
        Path path = solver.solve(maze, point, point);

        // Проверка
        assertNotNull(path);
        assertEquals(1, path.points().length);
        assertEquals(point, path.points()[0]);
    }

    // Вспомогательные методы для создания тестовых лабиринтов
    private CellType[][] createSimpleMaze() {
        return new CellType[][] {
            {CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL},
            {CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL},
            {CellType.WALL, CellType.WALL, CellType.WALL, CellType.EMPTY, CellType.WALL},
            {CellType.WALL, CellType.EMPTY, CellType.EMPTY, CellType.EMPTY, CellType.WALL},
            {CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL, CellType.WALL}
        };
    }

    private boolean isPathValid(Path path, Maze maze) {
        Point[] points = path.points();
        CellType[][] grid = maze.cells();

        // Проверка, что путь не проходит через стены
        for (Point point : points) {
            if (grid[point.y()][point.x()] == CellType.WALL) {
                return false;
            }
        }

        // Проверка непрерывности пути
        for (int i = 1; i < points.length; i++) {
            Point prev = points[i - 1];
            Point curr = points[i];
            int dx = Math.abs(prev.x() - curr.x());
            int dy = Math.abs(prev.y() - curr.y());
            if (dx + dy != 1) { // Должны быть соседними клетками
                return false;
            }
        }

        return true;
    }
}
