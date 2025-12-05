package academy.maze.dto;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Тип ячейки в лабиринте. WALL - стена, PATH - свободная ячейка. */
@Getter
@AllArgsConstructor
public enum CellType {
    WALL('#', "▓"),
    START('O', "●"),
    END('X', "★"),
    PATH('.', "•"),
    GRASS('G', "G"),
    SAND('N', "N"),
    WATER('W', "W"),
    EMPTY(' ', "░");

    private final char symbol;
    private final String unicode;

    public static CellType fromChar(char symbol) {
        return Arrays.stream(values())
                .filter(type -> type.symbol == symbol)
                .findFirst()
                .orElse(EMPTY);
    }
}
