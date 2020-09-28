package main;

/*
 * This is a utility class which allows the creation of positional vectors of any numeric data type
 */

public class Position<T extends Number> {
    public T x;
    public T y;

    public Position() {

    }

    public Position(T x, T y) {
        this.x = x;
        this.y = y;
    }
    
    public boolean equals(Position<T> other) {
    		if(this.x==other.x && this.y==other.y) {
    			return true;
    		} else {
    			return false;
    		}
    }

    @Override
    public String toString() {
        return "(" + x.toString() + ", " + y.toString() + ")";
    }
    
}
