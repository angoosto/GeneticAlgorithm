package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import main.Component;
import main.ComponentJoint;

public class ComponentJointTests {
	
	ComponentJoint cj = new ComponentJoint();
	
	@Test
	public void testList() {
		Component c1 = null;
		Component c2 = null;
		List<Component> expected = new ArrayList<Component>();
		expected.add(c1); expected.add(c2);
		List<Component> actual = cj.getComponentList();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRandom() {
		Component c1 = new Component();
		c1.width = 1f;
		c1.height = 1f;
		Component c2 = new Component(c1);
		ComponentJoint randomCJ = ComponentJoint.randomJoint(c1, c2);
		assertTrue(randomCJ.getPercentOne() > 0f && randomCJ.getPercentOne() < 1f);
		assertTrue(randomCJ.getPercentTwo() > 0f && randomCJ.getPercentTwo() < 1f);
		assertTrue(randomCJ.getPointA() > 0f && randomCJ.getPointA() < 1f);
		assertTrue(randomCJ.getPointB() > (-0.5f + randomCJ.getPointA()) && randomCJ.getPointB() < (0.5f + randomCJ.getPointA()));
	}
	
	@Test
	public void testCheck1() {
		float expected = 1;
		float actual = cj.checkBetweenZeroAndOne(5f);
		assertEquals(expected, actual, 0f);
	}
	
	@Test
	public void testCheck2() {
		float expected = 0f;
		float actual = cj.checkBetweenZeroAndOne(-0.5f);
		assertEquals(expected, actual, 0f);
	}
	
	@Test
	public void testCheck3() {
		float expected = 0.5f;
		float actual = cj.checkBetweenZeroAndOne(0.5f);
		assertEquals(expected, actual, 0f);
	}

}
