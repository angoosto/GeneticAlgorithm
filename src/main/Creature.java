package main;

import java.util.ArrayList;
import java.util.HashMap;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

/*
 * This class sets up the creature itself using components and componentJoints 
 */
public class Creature {
    private World world;
    private Individual individual;
    private HashMap<Component, Body> componentToBody = new HashMap<Component, Body>();
    private ArrayList<Body> bodies = new ArrayList<Body>();
    private ArrayList<Joint> joints = new ArrayList<Joint>();

//    private int maskBits;
//    private int categoryBits;
    
    private float maximumDistance = -1000;
    private float timer;
    private float restTimer;
    private Filter filter = new Filter();

    private static long creatureSeconds;
    private static long restSeconds;

    public static void setCreatureMilliseconds(long creatureSeconds) {
        Creature.creatureSeconds = creatureSeconds;
        Creature.restSeconds = Math.min(10, creatureSeconds / 2);
    }
    
    /**
     * Empty constructor
     */
    public Creature() {
    		
    }
    
    /**
     * Constructor for a creature
     * @param individual the individual to match the creature
     * @param world the world in which the creature will be created
     */
    
    public Creature(Individual individual, World world) {
        this.world = world;
        this.individual = individual;

        for (ComponentJoint componentJoint : individual.componentJoints) {
        	
            if (!componentToBody.containsKey(componentJoint.componentOne)) {
                Body bodyOne = setComponent(componentJoint.componentOne);
                bodies.add(bodyOne);
                componentToBody.put(componentJoint.componentOne, bodyOne);
            }

            if (!componentToBody.containsKey(componentJoint.componentTwo)) {
                Body bodyTwo = setComponent(componentJoint.componentTwo);
                bodies.add(bodyTwo);
                componentToBody.put(componentJoint.componentTwo, bodyTwo);
            }

            joints.add(setJoint(componentJoint));
//            setBall();
        }
    }

    private Body setComponent(Component component) {
        BodyDef bd = new BodyDef();
        bd.position.set(0f, -5f);
        bd.angle = 0f;
        bd.type = BodyType.DYNAMIC;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(component.width / 2, component.height / 2);

        FixtureDef fd = new FixtureDef();
//        filter.groupIndex = -2;
        filter.maskBits = 3;
        fd.filter = filter;
        fd.shape = shape;
        fd.density = 0.5f;
        fd.friction = 0.3f;
        fd.restitution = 0.5f;

        Body body = world.createBody(bd);
        body.createFixture(fd);

        ComponentUserData userData = new ComponentUserData();
        userData.individual = individual;
        body.setUserData(userData);

        return body;
    }

    private Joint setJoint(ComponentJoint componentJoint) {
        Body bodyOne = componentToBody.get(componentJoint.componentOne);
        Body bodyTwo = componentToBody.get(componentJoint.componentTwo);

        RevoluteJointDef joint = new RevoluteJointDef();
        joint.bodyA = bodyOne;
        joint.bodyB = bodyTwo;

        joint.localAnchorA = componentJoint.componentOne.getAnchor(componentJoint
                .getPercentOne());
        joint.localAnchorB = componentJoint.componentTwo.getAnchor(componentJoint
                .getPercentTwo());

        joint.lowerAngle = Math.min(componentJoint.getPointA(),
                componentJoint.getPointB());
        joint.upperAngle = Math.max(componentJoint.getPointA(),
                componentJoint.getPointB());

        joint.maxMotorTorque = 70.0f;
        joint.motorSpeed = Circles.circle(componentJoint.getInitialAngularVelocity());

        joint.enableMotor = true;
        joint.enableLimit = true;

        return world.createJoint(joint);
    }

    public Individual getIndividual() {
        return individual;
    }

    public Result removeFromWorld() {
        for (Body body : bodies) {
            world.destroyBody(body);
        }

        for (Joint joint : joints) {
            world.destroyJoint(joint);
        }

        return new Result(individual, getDistance());
    }

    public boolean isFinished(float time) {
        timer += time;

        if (timer > creatureSeconds) {
            return true;
        }

        float distance = getDistance();

        if (distance > maximumDistance) {
            maximumDistance = distance;
            restTimer = 0;
        } else {
            restTimer += time;
        }

        if (restTimer >= restSeconds) {
            return true;
        }

        return false;
    }

    public float getDistance() {
        return getAveragePosition().x;
    }

    public Position<Float> getAveragePosition() {
        Position<Float> position = new Position<Float>(0f, 0f);

        for (Body body : bodies) {
            position.x += body.getPosition().x;
            position.y += body.getPosition().y;
        }

        position.x /= bodies.size();
        position.y /= bodies.size();

        return position;
    }

    public ArrayList<Body> getBodies() {
        return bodies;
    }

    public void setBodies(ArrayList<Body> bodies) {
        this.bodies = bodies;
    }

    public ArrayList<Joint> getJoints() {
        return joints;
    }

    public void setJoints(ArrayList<Joint> joints) {
        this.joints = joints;
    }
    
    public Filter getFilter() {
    	return filter;
    }

}
