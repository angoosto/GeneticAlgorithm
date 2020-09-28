package tests;

import static org.junit.Assert.*;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.junit.Test;

import main.Creature;
import main.Position;

public class CreatureTests {
	
	World world = new World(new Vec2(0f, -9.8f));
	Creature c = new Creature();
	
	@Test
	public void testPosition() {
		
		BodyDef bd1 = new BodyDef();
		BodyDef bd2 = new BodyDef();
		
		bd1.position = new Vec2(4f, 4f);
		bd2.position = new Vec2(0f, 0f);
		
		Body b1 = world.createBody(bd1);
		Body b2 = world.createBody(bd2);
		
		c.getBodies().add(b1);
		c.getBodies().add(b2);
		
		Position<Float> expected = new Position<Float>(2.0f, 2.0f);
		Position<Float> actual = c.getAveragePosition();
		
		assertEquals(expected.x, actual.x);
		assertEquals(expected.y, actual.y);
	}

}
