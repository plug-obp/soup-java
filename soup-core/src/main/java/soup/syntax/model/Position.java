package soup.syntax.model;

import java.util.Objects;

public class Position {
    public static final Position ZERO = new Position(Cursor.ZERO, Cursor.ZERO);

    public Cursor start;
    public Cursor stop;

    public Position(Cursor start, Cursor stop) {
        this.start = start;
        this.stop = stop;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, stop);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return Objects.equals(start, position.start) && Objects.equals(stop, position.stop);
    }

    @Override
    public String toString() {
        return "Position{" +
                "start=" + start +
                ", stop=" + stop +
                '}';
    }
}