package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJoint;

/*
 * This class creates all the physics bodies and adds them to the world
 */

public class Simulator implements Callable<List<Result>> {

	private boolean expire = true;

	private List<Individual> individuals;
	private List<Result> results = new ArrayList<Result>();
	private ArrayList<Creature> creatures = new ArrayList<Creature>();

	private Configuration c = new Configuration();

	private float timeStep;
	private float radius = 1f;

	private int velocityIterations = 6;
	private int positionIterations = 2;
	private int groundLength = 1000;
	private float distanceToBall = 6f;

	private static float gravity = 9.8f;
	private World world;

	public Simulator() {

	}

	/**
	 * @param individuals list of all the individuals that should be evaluated by this simulator
	 */
	public Simulator(List<Individual> individuals, float gravity,float timeStep) {
		if (individuals.isEmpty()) { 
			throw new IllegalArgumentException("Simulator has 0 individuals");
		}

		this.individuals = individuals;	
		Simulator.gravity = gravity;
		this.timeStep = timeStep;
	}

	public void setFilters(Filter filter) {
		Creature c1 = creatures.get(0);
		Creature c2 = creatures.get(1);

		c1.getFilter().categoryBits = 4;
		c2.getFilter().categoryBits = 5;
		c1.getFilter().maskBits = 1 | 3 | 5;
		c2.getFilter().maskBits = 2 | 3 | 4;
	}

	private void setGround() {
		BodyDef bd = new BodyDef();
		bd.position.set(Math.max(-groundLength / 2, -10), 0f);
		bd.type = BodyType.STATIC;

		ChainShape shape = new ChainShape();

		ArrayList<Vec2> groundVertices = new ArrayList<Vec2>();

		for (int i = 0; i < groundLength / 5; ++i) {
			Vec2 segment = new Vec2((float) 5 * i, 0f);
			groundVertices.add(segment);
		}

		Vec2[] spec = {};

		shape.createChain(groundVertices.toArray(spec), groundVertices.size());

		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		Filter filter = new Filter();
		//		filter.groupIndex = 1;
		fd.filter = filter;
		fd.friction = 0.3f;
		fd.restitution = 0.5f;

		Body body = world.createBody(bd);
		body.createFixture(fd);
		body.setUserData(new Ground());

	}

	private void setBall1() {
		BodyDef bd = new BodyDef();
		bd.position.set(distanceToBall, -3f);
		bd.linearDamping = 0.5f;
		bd.type = BodyType.DYNAMIC;

		CircleShape cs = new CircleShape();
		cs.setRadius(radius);

		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		Filter filter = new Filter();
		filter.groupIndex = -1;
		filter.categoryBits = 1;
		filter.maskBits = 4;
		filter.maskBits = 3;
		fd.filter = filter;
		fd.density = (0.5f);
		fd.friction = (1.5f);
		fd.restitution = (0.5f);

		Body b = world.createBody(bd);
		b.createFixture(fd);
		b.setUserData(new Ball1UserData());
	}

	private void setBall2() {
		BodyDef bd = new BodyDef();
		bd.position.set(distanceToBall, -6f);
		bd.linearDamping = 0.5f;
		bd.type = BodyType.DYNAMIC;

		CircleShape cs = new CircleShape();
		cs.setRadius(radius);

		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		Filter filter = new Filter();
		filter.groupIndex = -1;
		filter.categoryBits = 2;
		filter.maskBits = 5;
		filter.maskBits = 3;
		fd.filter = filter;
		fd.density = (0.5f);
		fd.friction = (1.5f);
		fd.restitution = (0.5f);

		Body b = world.createBody(bd);
		b.createFixture(fd);
		b.setUserData(new Ball2UserData());
	}

	private void setCreatures() {
		for (Individual individual : individuals) {
			creatures.add(new Creature(individual, world));
		}
	}

	public void setGravity(String gravity) {
		switch (gravity) {
		case "Mercury": Simulator.gravity = 3.7f;
		world.setGravity(new Vec2(0, 3.7f));
		break;
		case "Venus": Simulator.gravity = 8.9f;
		world.setGravity(new Vec2(0, 8.9f));
		break;
		case "Earth": Simulator.gravity = 9.8f;
		world.setGravity(new Vec2(0, 9.8f));
		break;
		case "Mars": Simulator.gravity = 3.7f;
		world.setGravity(new Vec2(0, 3.7f));
		break;
		case "Jupiter": Simulator.gravity = 24.9f;
		world.setGravity(new Vec2(0, 24.9f));
		break;
		case "Saturn": Simulator.gravity = 10.4f;
		world.setGravity(new Vec2(0, 10.4f));
		break;
		case "Uranus": Simulator.gravity = 8.9f;
		world.setGravity(new Vec2(0, 8.9f));
		break;
		case "Neptune": Simulator.gravity = 11.2f;
		world.setGravity(new Vec2(0, 11.2f));
		break;
		case "Pluto": Simulator.gravity = 0.6f;
		world.setGravity(new Vec2(0, 0.6f));
		break;
		}
	}

	/**
	 * Creates the JBox2D environment, to which it adds the physics bodies
	 */
	public void setup() {
		Vec2 gravityVec2 = new Vec2(0f, gravity);
		world = new World(gravityVec2);
		world.setAllowSleep(true);

		setGround();
		setBall2();

		//		System.out.println(c.load);
		if(c.getLoad().equals("f")) {
			setBall1();
		}

		setCreatures();
	}

	/**
	 * Advances the simulator forward
	 */
	public synchronized void update() {
		world.step(timeStep, velocityIterations, positionIterations);

		for (RevoluteJoint joint = (RevoluteJoint) world.getJointList(); joint != null; joint = (RevoluteJoint) joint
				.getNext()) {
			if (joint.getJointAngle() <= joint.getLowerLimit()
					|| joint.getJointAngle() >= joint.getUpperLimit()) {
				joint.setMotorSpeed((float) (-joint.getMotorSpeed()));
			}
		}
	}

	/**
	 * Searches for finished individuals and removes them from the simulator
	 */
	public synchronized void removeFinished() {
		if (!expire) {
			return;
		}

		ArrayList<Creature> creaturesToRemove = new ArrayList<Creature>();
		ArrayList<Individual> individualsToRemove = new ArrayList<Individual>();

		for (Creature creature : creatures) {
			if (creature.isFinished(timeStep)) {
				results.add(creature.removeFromWorld());
				creaturesToRemove.add(creature);
				individualsToRemove.add(creature.getIndividual());
			}
		}

		creatures.removeAll(creaturesToRemove);
		individuals.removeAll(individualsToRemove);
	}

	public List<Result> simulate() {
		setup();

		while (!creatures.isEmpty()) {
			update();
			removeFinished();
		}

		return results;
	}

	public List<Result> call() {
		return simulate();
	}

	public boolean isExpire() {
		return expire;
	}

	public void setExpire(boolean expire) {
		this.expire = expire;
	}

	public synchronized World getWorld() {
		return world;
	}

	public void addIndividual(Individual toAdd) {
		individuals.add(toAdd);
	}

	public List<Individual> getIndividuals() {
		return individuals;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setTimeStep(float timeStep) {
		this.timeStep = timeStep;
	}

	public float getTimeStep() {
		return timeStep;
	}

	public static float getGravity() {
		return gravity;
	}

	public void setGravity(float gravity) {
		Simulator.gravity = gravity;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getDistanceToBall() {
		return distanceToBall;
	}

	public ArrayList<Creature> getCreatures() {
		return creatures;
	}

}
