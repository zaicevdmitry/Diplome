package by.zaicev.app.utils.wifi;

/**
 * Координаты точки.
 */
public class Point {

    /**
     * Координата по оси Х.
     */
    private int x;

    /**
     * Координата по оси У.
     */
    private int y;

    public Point() {
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
