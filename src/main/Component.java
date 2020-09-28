/**
 * 
 */
package main;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;

/*
 *	The component forms the body part of the creature
 */
public class Component {
    
    public static final float minimumWidth = 0.5f;
    public static final float maximumWidth = 2f;
    public static final float minimumHeight = 0.5f;
    public static final float maximumHeight = 2f;

    private static int id = 10000;

    public int number = -1;
    
    public float width;
    public float height;

    //List of joints connected to this component
    public ArrayList<ComponentJoint> componentJoints = new ArrayList<ComponentJoint>();

    /**
     * Default constructor.
     */
    public Component() {
        if (number == -1) {
            number = id++;
        }
    }

    /**
     * Constructor creating a copy
     * 
     * @param copy
     *            - component to copy from
     */
    public Component(Component copy) {
        this();
        width = copy.width;
        height = copy.height;
    }

    /**
     * Creates a random component within maximum and minimum bounds for height and width
     * @return a random component
     */
    public static Component random() {
        Component component = new Component();
        component.width = Randoms.random(Component.minimumWidth, Component.maximumWidth);
        component.height = Randoms.random(Component.minimumHeight, Component.maximumHeight);

        return component;
    }

    /**
     * Gets the anchor of a component
     * @param percent Either percentOne or percentTwo of component joint
     * @return the anchor
     */
    public Vec2 getAnchor(float percent) {
        float x = 0;
        float y = 0;

        float o = 2 * width + 2 * height;
        float d = percent * o;

        if (d < height) {
            x = 0;
            y = d;
        } else if (d < height + width) {
            x = d - height;
            y = height;
        } else if (d < 2 * height + width) {
            x = width;
            y = height - (d - height - width);
        } else {
            x = width - (d - height * 2 - width);
            y = 0;
        }

        x -= width / 2;
        y -= height / 2;

        return new Vec2(x, y);
    }

    public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int i) {
		this.number = i;
	}

	@Override
    public String toString() {
        return "Component: width = " + width + ", height = " + height + ", idm = " + number;
    }

}
