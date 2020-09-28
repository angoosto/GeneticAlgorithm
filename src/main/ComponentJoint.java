package main;

import java.util.ArrayList;
import java.util.List;

/*
 * This class deals with with joining the different body parts to one another
 */
public class ComponentJoint {
    public static final float maximumAngularVelocity = 0.2f;

    private float pointA;
    private float pointB;

    // initial angular velocity for the component joint
    private float initialAngularVelocity;

    private float percentOne;
    private float percentTwo;

    public Component componentOne;
    public Component componentTwo;

    public ComponentJoint() {
    }

    /**
     * Creates a copy from one ComponentJoint, but without the components attached to it
     * 
     * @param copy
     *            the ComponentJoint to copy from.
     */
    public ComponentJoint(ComponentJoint copy) {
        pointA = copy.pointA;
        pointB = copy.pointB;
        initialAngularVelocity = copy.initialAngularVelocity;
        percentOne = copy.percentOne;
        percentTwo = copy.percentTwo;
    }

    /**
     * 
     * @return list of components attached to the joint
     */
    
    public List<Component> getComponentList() {
        ArrayList<Component> componentList = new ArrayList<Component>();
        componentList.add(componentOne);
        componentList.add(componentTwo);
        return componentList;
    }

    /**
     * 
     * @param first the first component to be attached
     * @param second the second component to be attached
     * @return a random joint joining the two components
     */
    
    public static ComponentJoint randomJoint(Component first, Component second) {
        ComponentJoint componentJoint = new ComponentJoint();

        componentJoint.initialAngularVelocity = Randoms.random(maximumAngularVelocity,
                -maximumAngularVelocity);

        componentJoint.setPointA(Randoms.random(0f, 1f));
        componentJoint.setPointB(componentJoint.pointA + Randoms.random(-0.5f, 0.5f));

        componentJoint.percentOne = Randoms.random(0f, 1f);
        componentJoint.percentTwo = Randoms.random(0f, 1f);

        componentJoint.componentOne = first;
        componentJoint.componentTwo = second;

        return componentJoint;
    }

    /**
     * Helper method for setters, makes sure the argument is between 1 and 0
     * @param a the number to check
     * @return if a is above 1 it returns 1 and if below 0 it returns 0
     */
    
    public float checkBetweenZeroAndOne(float a) {
        if (a > 1) {
            return 1;
        } else if (a < 0) {
            return 0;
        }

        return a;
    }

    public float getPointA() {
        return pointA;
    }

    public float getPointB() {
        return pointB;
    }

    public void setPointA(float pointA) {
        pointA = checkBetweenZeroAndOne(pointA);
        this.pointA = pointA;
    }

    public void changePointA(float change) {
        setPointA(pointA + change);
    }

    public void setPointB(float pointB) {
        pointB = checkBetweenZeroAndOne(pointB);
        this.pointB = pointB;
    }

    public void changePointB(float change) {
        setPointB(pointB + change);
    }

    public float getInitialAngularVelocity() {
        return initialAngularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        if (angularVelocity > maximumAngularVelocity) {
            angularVelocity = maximumAngularVelocity;
        } else if (angularVelocity < -maximumAngularVelocity) {
            angularVelocity = -maximumAngularVelocity;
        }

        this.initialAngularVelocity = angularVelocity;
    }

    public void changeAngularVelocity(float change) {
        setAngularVelocity(initialAngularVelocity + change);
    }

    public float getPercentOne() {
        return percentOne;
    }

    public void setPercentOne(float percentOne) {
        if (percentOne < 0) {
            percentOne = 0;
        } else if (percentOne > 1) {
            percentOne = 1;
        }

        this.percentOne = percentOne;
    }

    public void changePercentOne(float change) {
        setPercentOne(percentOne + change);
    }

    public float getPercentTwo() {
        return percentTwo;
    }

    public void setPercentTwo(float percentTwo) {
        if (percentTwo < 0) {
            percentTwo = 0;
        } else if (percentTwo > 1) {
            percentTwo = 1;
        }
        this.percentTwo = percentTwo;
    }

    public void changePercentTwo(float change) {
        setPercentTwo(percentTwo + change);
    }

    @Override
    public String toString() {
        return "ComponentJoint pointA=" + pointA + ", pointB=" + pointB
                + ", initialAngularVelocity=" + initialAngularVelocity + ", percentOne="
                + percentOne + ", percentTwo=" + percentTwo + ", componentOne="
                + componentOne + ", componentTwo=" + componentTwo;
    }
}
