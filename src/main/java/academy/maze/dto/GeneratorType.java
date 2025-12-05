package academy.maze.dto;

import java.util.Arrays;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum GeneratorType {
    PRIM,
    DFS,
    BINARY;

    public static GeneratorType find(String name) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный алгоритм генерации: " + name));
    }
}
