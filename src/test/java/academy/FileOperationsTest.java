package academy;

import static org.junit.jupiter.api.Assertions.*;

import academy.maze.NotInteractiveMazeManager;
import academy.maze.commands.MazeCommands;
import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import academy.maze.generator.DFSGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("File Operations Integration Tests")
class FileOperationsTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Should save and load maze correctly")
    void shouldSaveAndLoadMazeCorrectly() throws IOException {
        // Шаг 1: Подготовка
        Path mazeFile = tempDir.resolve("test_maze.txt");
        DFSGenerator generator = new DFSGenerator(false);
        Maze originalMaze = generator.generate(10, 10);

        // Шаг 2: Сохранение и загрузка
        MazeCommands.saveMazeToFile(originalMaze, mazeFile.toString());
        Maze loadedMaze = MazeCommands.loadMazeFromFile(mazeFile.toString());

        // Шаг 3: Проверка
        assertNotNull(loadedMaze, "Loaded maze should not be null");
        assertEquals(originalMaze.cells().length, loadedMaze.cells().length, "Height should match after loading");
        assertEquals(originalMaze.cells()[0].length, loadedMaze.cells()[0].length, "Width should match after loading");

        // Детальная проверка содержимого
        verifyMazeContentMatches(originalMaze, loadedMaze);
    }

    @Test
    @DisplayName("Should throw exception for non-existent file")
    void shouldThrowExceptionForNonExistentFile() {
        assertThrows(
                IOException.class,
                () -> {
                    MazeCommands.loadMazeFromFile("non_existent_file.txt");
                },
                "Should throw IOException for non-existent file");
    }

    @Test
    @DisplayName("Should save solution with path correctly")
    void shouldSaveSolutionWithPathCorrectly() throws IOException {
        // Подготовка
        Path solutionFile = tempDir.resolve("test_solution.txt");
        CellType[][] grid = createSimpleMaze();

        // Устанавливаем начальную и конечную точки в лабиринте
        grid[1][1] = CellType.START; // Начальная точка O
        grid[3][3] = CellType.END; // Конечная точка X

        Maze maze = new Maze(grid);
        List<Point> path = List.of(new Point(1, 1), new Point(2, 1), new Point(3, 1), new Point(3, 2), new Point(3, 3));

        // Действие
        MazeCommands.saveSolutionToFile(maze, path, solutionFile.toString());

        // Проверка
        assertTrue(Files.exists(solutionFile), "Solution file should be created");
        String content = Files.readString(solutionFile);

        // Для отладки выведем содержимое файла
        System.out.println("File content:");
        System.out.println(content);

        assertTrue(content.contains("O"), "Solution should contain start point marker");
        assertTrue(content.contains("X"), "Solution should contain end point marker");
        assertTrue(content.contains("."), "Solution should contain path markers");
    }

    @Test
    @DisplayName("NotInteractiveMazeManager should generate and solve maze")
    void notInteractiveManager_ShouldGenerateAndSolveMaze() throws IOException {
        // Подготовка
        Path mazeFile = tempDir.resolve("generated_maze.txt");
        Path solutionFile = tempDir.resolve("generated_solution.txt");

        // Действие 1: Генерация лабиринта
        NotInteractiveMazeManager.generateMaze("dfs", 15, 15, mazeFile.toString());

        // Проверка 1
        assertTrue(Files.exists(mazeFile), "Maze file should be created");

        // Действие 2: Решение лабиринта
        int[] start = {1, 1};
        int[] end = {13, 13};
        NotInteractiveMazeManager.solveMaze("astar", mazeFile.toString(), start, end, solutionFile.toString());

        // Проверка 2
        assertTrue(Files.exists(solutionFile), "Solution file should be created");
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

    private void verifyMazeContentMatches(Maze original, Maze loaded) {
        CellType[][] originalCells = original.cells();
        CellType[][] loadedCells = loaded.cells();

        for (int y = 0; y < originalCells.length; y++) {
            for (int x = 0; x < originalCells[y].length; x++) {
                assertEquals(
                        originalCells[y][x],
                        loadedCells[y][x],
                        String.format("Cell at (%d, %d) should match after save/load", x, y));
            }
        }
    }
}
