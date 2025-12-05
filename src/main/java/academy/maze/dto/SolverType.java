package academy.maze.dto;

import java.util.Arrays;

public enum SolverType {
    ASTAR,
    DIJKSTRA;

    public static SolverType find(String name) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный алгоритм решения: " + name));
    }
}
