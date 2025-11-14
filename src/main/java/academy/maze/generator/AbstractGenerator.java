package academy.maze.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class AbstractGenerator implements Generator {

    protected final Random random = new Random();
    protected final boolean useCoatings;
    static final int[][] DIRECTIONS = new int[][]{{0, 2}, {0, -2}, {2, 0}, {-2, 0}};
    private final List<CellType> COVERAGE_TYPES = List.of(CellType.GRASS, CellType.SAND, CellType.WATER);

    protected AbstractGenerator(boolean useCoatings) {
        this.useCoatings = useCoatings;
    }

    protected int randomOdd(int bound) {
        if (bound <= 1) {
            return 0;
        }
        int max = (bound - 1) / 2;
        if (max <= 0) {
            return 0;
        }
        return random.nextInt(0, max) * 2 + 1;
    }

    protected List<Point> findNeighbours(Point from, int width, int height, CellType[][] maze) {
        List<Point> neighbours = new ArrayList<>();
        for (int[] dir : DIRECTIONS) {
            int nx = from.x() + dir[0];
            int ny = from.y() + dir[1];
            if (nx > 0 && nx < width - 1 && ny > 0 && ny < height - 1 && maze[ny][nx] == CellType.WALL) {
                neighbours.add(new Point(nx, ny));
            }
        }
        return neighbours;
    }

    protected CellType[][] createClearMaze(int width, int height, boolean empty) {
        if (empty) {
            return createEmptyMazeWithWalls(width, height);
        } else {
            return createAllWallsMaze(width, height);
        }
    }

    private CellType[][] createAllWallsMaze(int width, int height) {
        CellType[][] maze = new CellType[height][width];
        for (CellType[] row : maze) {
            Arrays.fill(row, CellType.WALL);
        }
        return maze;
    }

    private CellType[][] createEmptyMazeWithWalls(int width, int height) {
        CellType[][] maze = new CellType[height][width];
        for (CellType[] row : maze) {
            Arrays.fill(row, CellType.EMPTY);
        }
        for (int i = 0; i < width; i++) {
            maze[0][i] = CellType.WALL;
            maze[height - 1][i] = CellType.WALL;
        }
        for (int i = 0; i < height; i++) {
            maze[i][0] = CellType.WALL;
            maze[i][width - 1] = CellType.WALL;
        }

        return maze;
    }

    protected CellType getRandomCoverage(boolean useCoatings) {
        return useCoatings ? COVERAGE_TYPES.get(random.nextInt(COVERAGE_TYPES.size())) : CellType.EMPTY;
    }
}
