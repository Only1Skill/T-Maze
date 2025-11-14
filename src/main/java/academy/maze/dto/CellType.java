package academy.maze.dto;

import lombok.Getter;

/**
 * Тип ячейки в лабиринте. WALL - стена, PATH - свободная ячейка.
 */
@Getter
public enum CellType {
    START('O'),
    END('X'),
    WALL('#'),
    PATH('.'),
    GRASS('G'),
    SAND('N'),
    WATER('W'),
    EMPTY(' ');

    private final char symbol;

    CellType(char symbol) {
        this.symbol = symbol;
    }

}
