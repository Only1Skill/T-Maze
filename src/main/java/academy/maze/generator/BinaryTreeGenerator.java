package academy.maze.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;

public class BinaryTreeGenerator extends AbstractGenerator {

    public BinaryTreeGenerator(boolean useCoatings) {
        super(useCoatings);
    }

    @Override
    public Maze generate(int width, int height) {
        CellType[][] maze = createClearMaze(width, height, true);

        for (int y = 1; y < height - 1; y += 2) {
            for (int x = 1; x < width - 1; x += 2) {
                carveCell(maze, x, y);
                createPassage(maze, x, y, width, height);
            }
        }

        return new Maze(maze);
    }

    private void carveCell(CellType[][] maze, int x, int y) {
        maze[y][x] = getRandomCoverage(useCoatings);
    }

    private void createPassage(CellType[][] maze, int x, int y, int width, int height) {
        boolean canGoRight = x < width - 3;
        boolean canGoDown = y < height - 3;

        if (canGoRight && canGoDown) {
            if (random.nextBoolean()) {
                maze[y][x + 1] = getRandomCoverage(useCoatings);
            } else {
                maze[y + 1][x] = getRandomCoverage(useCoatings);
            }
        } else if (canGoRight) {
            maze[y][x + 1] = getRandomCoverage(useCoatings);
        } else if (canGoDown) {
            maze[y + 1][x] = getRandomCoverage(useCoatings);
        }
    }
}
