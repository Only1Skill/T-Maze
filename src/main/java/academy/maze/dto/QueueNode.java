package academy.maze.dto;

/**
 * Узел для приоритетной очереди в алгоритмах поиска пути.
 *
 * @param point точка в лабиринте
 * @param priority приоритет точки
 */
public record QueueNode(Point point, int priority) implements Comparable<QueueNode> {
    @Override
    public int compareTo(QueueNode other) {
        return Integer.compare(this.priority, other.priority);
    }
}
