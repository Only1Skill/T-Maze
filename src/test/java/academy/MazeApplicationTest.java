package academy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

@DisplayName("Maze Application Comprehensive Tests")
public class MazeApplicationTest {

    @TempDir
    protected Path tempDir;
    protected Path testMazeFile;
    protected Path testSolutionFile;

    @BeforeEach
    void setUp() {
        testMazeFile = tempDir.resolve("test_maze.txt");
        testSolutionFile = tempDir.resolve("test_solution.txt");
    }
}
