package academy.maze.dto;

public record PathNode(Point position, int weight) implements Comparable<PathNode> {

    @Override
    public int compareTo(PathNode other) {
        return Integer.compare(this.weight, other.weight);
    }
}
