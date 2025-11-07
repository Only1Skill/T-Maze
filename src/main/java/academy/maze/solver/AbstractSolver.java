package academy.maze.solver;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;
import java.util.*;

public abstract class AbstractSolver implements Solver {

    private static final int[][] MOVES = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    protected List<Point> getAdjacentPoints(Point pos, Maze maze) {
        List<Point> result = new ArrayList<>();
        CellType[][] grid = maze.cells();
        int rows = grid.length;
        int cols = grid[0].length;

        for (int[] move : MOVES) {
            int newX = pos.x() + move[0];
            int newY = pos.y() + move[1];

            if (newX >= 0 && newX < cols && newY >= 0 && newY < rows) {
                CellType cell = grid[newY][newX];
                if (cell != CellType.WALL) {
                    result.add(new Point(newX, newY));
                }
            }
        }
        return result;
    }

    protected Path buildPath(Point destination, Map<Point, Point> predecessors) {
        if (!predecessors.containsKey(destination)) {
            throw new RuntimeException("Решение не найдено");
        }

        List<Point> points = new ArrayList<>();
        Point step = destination;

        while (step != null) {
            points.add(step);
            step = predecessors.get(step);
        }

        Collections.reverse(points);
        return new Path(points.toArray(new Point[0]));
    }

    private void checkIfAlreadySolved(int x, int y, Maze maze) {
        CellType cell = maze.cells()[y][x];
        if (cell == CellType.PATH || cell == CellType.START || cell == CellType.END) {
            throw new RuntimeException("В лабиринте уже есть решение");
        }
    }
}
