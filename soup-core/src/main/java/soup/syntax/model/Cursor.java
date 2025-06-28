package soup.syntax.model;


import java.util.Objects;

public class Cursor {
    public static final Cursor ZERO = new Cursor(0, 0);
    public int line;
    public int column;

    public Cursor(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, column);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cursor)) return false;
        Cursor cursor = (Cursor) o;
        return line == cursor.line && column == cursor.column;
    }

    @Override
    public String toString() {
        return "Cursor{" +
                "line=" + line +
                ", column=" + column +
                '}';
    }
}