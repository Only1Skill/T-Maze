package academy.maze.solver;

import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;

public class AStarSolver extends PriorityQueueSolver {

    @Override
    public Path solve(Maze maze, Point start, Point end) {
        return findShortestPath(maze, start, end);
    }

    @Override
    int computePriority(int distance, Point current, Point target) {
        return distance + guessDistance(current, target);
    }

    private int guessDistance(Point a, Point b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
