package main;

import org.jbox2d.common.Vec2;

/*
 * This class deals with the translation of world to screen co-ordinates
 */

public class View {
    private final Graphic graphic;
    private Position<Integer> initialPosition = new Position<Integer>(0, 0);
    private Position<Integer> userDrag = new Position<Integer>(0, 0);
    private static final double pixelsPerMetre = 250;
    private final double scroll = 0.2;
    private double initialZoom = 0.15;
    private float ballZoom = 18f;

    public View(Graphic graphic) {
        this.graphic = graphic;
    }

    public Position<Integer> convert(Position<Float> toConvert) {
        Position<Integer> position = new Position<Integer>();

        position.x = (int) ((toConvert.x * pixelsPerMetre - initialPosition.x) * initialZoom);
        position.y = (int) ((toConvert.y * pixelsPerMetre - initialPosition.y) * initialZoom);

        position.x += graphic.getWidth() / 2;
        position.y += graphic.getHeight() / 2;

        return position;
    }

    public Position<Integer> convert(Vec2 toConvert) {
        Position<Float> position = new Position<Float>();
        position.x = toConvert.x;
        position.y = toConvert.y;
        return convert(position);
    }

    public Position<Integer> convertRelative(Vec2 toConvert) {
        Position<Integer> point = new Position<Integer>();

        point.x = (int) (toConvert.x * pixelsPerMetre * initialZoom);
        point.y = (int) (toConvert.y * pixelsPerMetre * initialZoom);

        return point;
    }

    public float convertRelative(float a) {
        return (float) (a * initialZoom);
    }

    public int convertRelative(int a) {
        return (int) (a * initialZoom);
    }

    public void initialDragging(int x, int y) {
        userDrag = new Position<Integer>(x, y);
    }

    public void continueDragging(int x, int y) {
        initialPosition.x -= (int) ((x - userDrag.x) / initialZoom);
        initialPosition.y -= (int) ((y - userDrag.y) / initialZoom);

        userDrag.x = x;
        userDrag.y = y;
    }

    public void userScroll(int userInput) {
        float newZoom = (float) (initialZoom - userInput * scroll * initialZoom);
        if (newZoom > 0.02 && newZoom < 1) {
            initialZoom = newZoom;
            ballZoom = newZoom * 130;
        }
    }
    
    public float getZoom() {
    	return ballZoom;
    }

}
