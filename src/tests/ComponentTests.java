package tests;

import static org.junit.Assert.*;

import org.jbox2d.common.Vec2;
import org.junit.Test;

import main.Component;

public class ComponentTests {

	Component c = new Component();
	
	@Test
	public void anchorTest1() {
		c.setWidth(1f);
		c.setHeight(1.5f);
		Vec2 expected = new Vec2(-20.5f, -0.75f);
		Vec2 actual = c.getAnchor(5f);
		assertEquals(expected, actual);
	}
	
	@Test
	public void anchorTest2() {
		c.setWidth(0f);
		c.setHeight(0f);
		Vec2 expected = new Vec2(0f, 0f);
		Vec2 actual = c.getAnchor(5f);
		assertEquals(expected, actual);
	}
	
	@Test
	public void anchorTest3() {
		c.setWidth(0.5f);
		c.setHeight(0.9f);
		Vec2 expected = new Vec2(-137.45f, -0.45f);
		Vec2 actual = c.getAnchor(50);
		assertEquals(expected, actual);
	}
	
	@Test
	public void stringTest1() {
		c.setWidth(1f);
		c.setHeight(1f);
		c.setNumber(1);
		String expected = "Component: width = 1.0, height = 1.0, idm = 1";
		String actual = c.toString();
		assertEquals(expected, actual);
	}

}
