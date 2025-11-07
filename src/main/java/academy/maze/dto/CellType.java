package academy.maze.dto;

/** Тип ячейки в лабиринте. WALL - стена, PATH - свободная ячейка. */
public enum CellType {
    START('S'),
    END('E'),
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

    public char getSymbol() {
        return symbol;
    }
}
