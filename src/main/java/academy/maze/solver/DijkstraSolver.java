package academy.maze.solver;

import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;

public class DijkstraSolver extends PriorityQueueSolver {

    @Override
    public Path solve(Maze maze, Point start, Point end) {
        return findShortestPath(maze, start, end);
    }

    @Override
    int computePriority(int distance, Point current, Point target) {
        return distance;
    }
}
