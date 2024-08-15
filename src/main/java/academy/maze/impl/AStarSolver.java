package academy.maze.impl;

import academy.maze.Solver;
import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class AStarSolver implements Solver {

    private static class Node implements Comparable<Node> {
        Point points;
        int g; // стоимость от start до текущей
        int f; // g + эвристика
        Node parent;

        Node(Point points, int g, int f, Node parent) {
            this.points = points;
            this.g = g;
            this.f = f;
            this.parent = parent;
        }

        @SuppressFBWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f, other.f);
        }
    }

    private int heuristic(Point a, Point b) {
        // Манхэттенское расстояние
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    @Override
    public Path solve(Maze maze, Point start, Point end) {
        int height = maze.cells().length;
        int width = maze.cells()[0].length;

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<Point, Integer> gScore = new HashMap<>();
        Map<Point, Node> allNodes = new HashMap<>();
        Set<Point> closedSet = new HashSet<>();

        Node startNode = new Node(start, 0, heuristic(start, end), null);
        openSet.add(startNode);
        gScore.put(start, 0);
        allNodes.put(start, startNode);

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.points.equals(end)) {
                // восстановление пути
                List<Point> path = new ArrayList<>();
                Node cur = current;
                while (cur != null) {
                    path.add(cur.points);
                    cur = cur.parent;
                }
                Collections.reverse(path);
                return new Path(path.toArray(new Point[0]));
            }

            closedSet.add(current.points);

            for (int i = 0; i < 4; i++) {
                int nx = current.points.x() + dx[i];
                int ny = current.points.y() + dy[i];

                if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                if (maze.cells()[ny][nx] != CellType.PATH) continue;

                Point neighbor = new Point(nx, ny);
                if (closedSet.contains(neighbor)) continue;

                int tentativeG = current.g + 1;
                int neighborG = gScore.getOrDefault(neighbor, Integer.MAX_VALUE);

                if (tentativeG < neighborG) {
                    gScore.put(neighbor, tentativeG);
                    int f = tentativeG + heuristic(neighbor, end);

                    Node neighborNode = allNodes.getOrDefault(neighbor, new Node(neighbor, tentativeG, f, current));
                    neighborNode.g = tentativeG;
                    neighborNode.f = f;
                    neighborNode.parent = current;
                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                    allNodes.put(neighbor, neighborNode);
                }
            }
        }

        // Путь не найден
        return new Path(new Point[0]);
    }
}
