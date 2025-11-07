package academy.maze.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import java.util.ArrayList;
import java.util.List;

public class PrimGenerator extends AbstractGenerator {

    public PrimGenerator(boolean useCoatings) {
        super(useCoatings);
    }

    @Override
    public Maze generate(int width, int height) {
        CellType[][] maze = createClearMaze(width, height, false);
        Point start = new Point(randomOdd(width), randomOdd(height));
        maze[start.y()][start.x()] = getRandomCoverage(useCoatings);
        List<Point> frontier = new ArrayList<>(findNeighbours(start, width, height, maze));
        while (!frontier.isEmpty()) {
            Point current = frontier.remove(random.nextInt(frontier.size()));
            List<Point> carvedNeighbors = getCarvedNeighbors(current, maze, width, height);
            if (!carvedNeighbors.isEmpty()) {
                Point connectedNeighbor = carvedNeighbors.get(random.nextInt(carvedNeighbors.size()));
                connectCells(current, connectedNeighbor, maze);
            }
            addToFrontier(current, frontier, maze, width, height);
        }
        return new Maze(maze);
    }

    private List<Point> getCarvedNeighbors(Point point, CellType[][] maze, int width, int height) {
        List<Point> carved = new ArrayList<>();
        for (int[] dir : DIRECTIONS) {
            int nx = point.x() + dir[0];
            int ny = point.y() + dir[1];
            if (isValidCell(nx, ny, width, height) && maze[ny][nx] != CellType.WALL) {
                carved.add(new Point(nx, ny));
            }
        }
        return carved;
    }

    private void connectCells(Point cell1, Point cell2, CellType[][] maze) {
        int wallX = (cell1.x() + cell2.x()) / 2;
        int wallY = (cell1.y() + cell2.y()) / 2;
        maze[wallY][wallX] = getRandomCoverage(useCoatings);
        maze[cell1.y()][cell1.x()] = getRandomCoverage(useCoatings);
    }

    private void addToFrontier(Point point, List<Point> frontier, CellType[][] maze, int width, int height) {
        for (int[] dir : DIRECTIONS) {
            int nx = point.x() + dir[0];
            int ny = point.y() + dir[1];
            Point neighbor = new Point(nx, ny);
            if (isValidCell(nx, ny, width, height) && maze[ny][nx] == CellType.WALL && !frontier.contains(neighbor)) {
                frontier.add(neighbor);
            }
        }
    }

    private boolean isValidCell(int x, int y, int width, int height) {
        return x > 0 && x < width - 1 && y > 0 && y < height - 1;
    }
}
