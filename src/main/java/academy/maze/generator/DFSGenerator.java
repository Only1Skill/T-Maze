package academy.maze.generator;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Point;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class DFSGenerator extends AbstractGenerator {

    public DFSGenerator(boolean useCoatings) {
        super(useCoatings);
    }

    @Override
    public Maze generate(int width, int height) {
        CellType[][] maze = createClearMaze(width, height, false);
        Deque<Point> stack = new ArrayDeque<>();
        Point start = getRandomStartPoint(width, height);
        setCell(maze, start, getRandomCoverage(useCoatings));
        stack.push(start);
        while (!stack.isEmpty()) {
            Point current = stack.peek();
            Point next = findRandomUnvisitedNeighbor(current, maze, width, height);
            if (next != null) {
                carvePath(current, next, maze, useCoatings);
                stack.push(next);
            } else {
                stack.pop();
            }
        }
        return new Maze(maze);
    }

    private Point getRandomStartPoint(int width, int height) {
        if (width <= 1 && height <= 1) {
            return new Point(0, 0);
        }
        if (width <= 1) {
            return new Point(0, randomOdd(height));
        }
        if (height <= 1) {
            return new Point(randomOdd(width), 0);
        }
        return new Point(randomOdd(width), randomOdd(height));
    }

    private Point findRandomUnvisitedNeighbor(Point current, CellType[][] maze, int width, int height) {
        List<Point> neighbors = findNeighbours(current, width, height, maze);
        return neighbors.isEmpty() ? null : neighbors.get(random.nextInt(neighbors.size()));
    }

    private void carvePath(Point from, Point to, CellType[][] maze, boolean useCoatings) {
        setCell(maze, to, getRandomCoverage(useCoatings));
        Point between = new Point((from.x() + to.x()) / 2, (from.y() + to.y()) / 2);
        setCell(maze, between, getRandomCoverage(useCoatings));
    }

    private void setCell(CellType[][] maze, Point point, CellType type) {
        maze[point.y()][point.x()] = type;
    }
}
