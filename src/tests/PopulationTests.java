package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import main.Population;
import main.Individual;
import main.Result;

public class PopulationTests {

	
	
	@Test
	public void testNormalize() {
		List<Result> results = new ArrayList<Result>();
		Individual individual = new Individual();
		
		Result r1 = new Result(individual, 5f);
		Result r2 = new Result(individual, 10f);
		
		results.add(r1);
		results.add(r2);
		
		float unit = 2.0f;
		
		Population.normalize(results, unit);
		
		assertEquals(r1.score / unit, results.get(0).normalised, 0f);
		assertEquals(r2.score / unit, results.get(1).normalised, 0f);
	}
	
	@Test
	public void testSum() {
		List<Result> results = new ArrayList<Result>();
		Individual individual = new Individual();
		
		Result r1 = new Result(individual, 10f);
		Result r2 = new Result(individual, 4.5f);
		Result r3 = new Result(individual, 0.3f);
		Result r4 = new Result(individual, -4f);
		
		results.add(r1);
		results.add(r2);
		results.add(r3);
		results.add(r4);
		
		float expected = 14.8f;
		float actual = Population.sum(results);
		
		assertEquals(expected, actual, 0f);
		
	}

}
