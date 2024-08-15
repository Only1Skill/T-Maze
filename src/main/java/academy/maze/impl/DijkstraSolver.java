package academy.maze.impl;

import academy.maze.Solver;
import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import academy.maze.dto.Path;
import academy.maze.dto.Point;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class DijkstraSolver implements Solver {

    // Вспомогательный класс для очереди с приоритетом
    private static class Node implements Comparable<Node> {
        Point point;
        int dist;

        Node(Point point, int dist) {
            this.point = point;
            this.dist = dist;
        }

        @SuppressFBWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
        public int compareTo(Node other) {
            return Integer.compare(this.dist, other.dist);
        }
    }

    @Override
    public Path solve(Maze maze, Point start, Point end) {
        int height = maze.cells().length;
        int width = maze.cells()[0].length;
        int[][] dist = new int[height][width];
        Point[][] prev = new Point[height][width];

        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);

        dist[start.y()][start.x()] = 0;
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(start, 0));

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (!pq.isEmpty()) {
            Node node = pq.poll();
            Point cur = node.point;
            int curDist = node.dist;

            if (cur.equals(end)) break;
            if (curDist > dist[cur.y()][cur.x()]) continue;

            for (int dir = 0; dir < 4; dir++) {
                int nx = cur.x() + dx[dir];
                int ny = cur.y() + dy[dir];

                if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                if (maze.cells()[ny][nx] != CellType.PATH) continue;

                int alt = dist[cur.y()][cur.x()] + 1;
                if (alt < dist[ny][nx]) {
                    dist[ny][nx] = alt;
                    prev[ny][nx] = cur;
                    pq.add(new Node(new Point(nx, ny), alt));
                }
            }
        }

        // Восстановление пути
        LinkedList<Point> path = new LinkedList<>();
        Point cur = end;
        if (prev[cur.y()][cur.x()] != null || cur.equals(start)) {
            while (cur != null) {
                path.addFirst(cur);
                cur = prev[cur.y()][cur.x()];
            }
        }

        return new Path(path.toArray(new Point[0]));
    }
}
