package academy.maze;

import academy.maze.dto.CellType;
import academy.maze.dto.Maze;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MazeReader {

    // Считывает лабиринт из файла (ожидается обводка #, пробелы и # внутри)
    public static Maze readFromFile(String filename) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filename));

        if (lines.size() < 3) {
            throw new IOException("Invalid maze file: too few lines");
        }

        // Т.к. есть рамка, убираем 1-ю и последнюю строки и колонки
        int height = lines.size() - 2;
        int width = lines.getFirst().length() - 2;

        CellType[][] cells = new CellType[height][width];

        for (int y = 0; y < height; y++) {
            String line = lines.get(y + 1);
            if (line.length() < width + 2) {
                throw new IOException("Invalid maze file: line length too short at line " + (y + 2));
            }
            for (int x = 0; x < width; x++) {
                char ch = line.charAt(x + 1);
                switch (ch) {
                    case '#':
                        cells[y][x] = CellType.WALL;
                        break;
                    case ' ':
                        cells[y][x] = CellType.PATH;
                        break;
                    case 'O': // Старт и конец можно обрабатывать отдельно, здесь ставим PATH
                    case 'X':
                    case '.':
                        cells[y][x] = CellType.PATH;
                        break;
                    default:
                        throw new IOException("Unexpected char '" + ch + "' in maze at (" + x + "," + y + ")");
                }
            }
        }
        return new Maze(cells);
    }
}
