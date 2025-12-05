package academy.maze.solver;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.PathNode;
import academy.maze.dto.Point;
import java.util.*;

public abstract class PriorityQueueSolver extends AbstractSolver {

    protected Path findShortestPath(Maze maze, Point begin, Point goal) {
        ensureMazeHasNoPath(maze);
        Queue<PathNode> queue = new PriorityQueue<>();
        Map<Point, Point> previous = new HashMap<>();
        Map<Point, Integer> distances = new HashMap<>();

        queue.add(new PathNode(begin, 0));
        previous.put(begin, null);
        distances.put(begin, 0);

        while (!queue.isEmpty()) {
            PathNode node = queue.poll();
            Point current = node.position();

            if (current.equals(goal)) {
                break;
            }

            for (Point next : getAdjacentPoints(current, maze)) {
                int newDistance = distances.get(current) + 1;

                if (!distances.containsKey(next) || newDistance < distances.get(next)) {
                    distances.put(next, newDistance);
                    int priority = computePriority(newDistance, next, goal);
                    queue.add(new PathNode(next, priority));
                    previous.put(next, current);
                }
            }
        }

        return buildPath(goal, previous);
    }

    private void ensureMazeHasNoPath(Maze maze) {
        for (CellType[] row : maze.cells()) {
            for (CellType c : row) {
                if (c == CellType.PATH) {
                    throw new IllegalStateException("В лабиринте уже есть решение (найдены клетки PATH)");
                }
            }
        }
    }

    abstract int computePriority(int distance, Point current, Point target);
}
