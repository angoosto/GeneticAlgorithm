/**
 * 
 */
package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Filter;

import com.google.gson.Gson;

/*
 * This class performs the main genetic algorithm and starts the running of the application. 
 */
public class CreatureEvolution {
	private Window window;

	private Configuration c;
	private int populations = 0;
	private Result bestResult;
	Simulator presentation;
	private Gson gson = new Gson();

	/**
	 * Empty constructor
	 */

	public CreatureEvolution() {

	}

	/**
	 * Constructor which initialises some settings
	 * @param config The configuration of settings
	 */

	public CreatureEvolution(Configuration config) {
		c = config;

		if (c.getConcurrentsimulators() < 1) {
			throw new IllegalArgumentException("There must be at least one concurrent simulator");
		}

		Population.setWorkers(c.getConcurrentsimulators());
		Population.setGravity(c.getGravity());
		Population.setTimeStep(c.getTimeStep());
		Creature.setCreatureMilliseconds(c.getCreatureSeconds());
		Individual.setMutationChance(c.getMutationChance());
	}

	private boolean finishedPopulations() {
		if(populations == c.getNumberOfPopulations()) return true;
		return false;
	}

	/**
	 * The main genetic algorithm which takes a sample of creatures and pushes them to be graphically rendered
	 */

	private Population geneticAlgorithm() {

		Population population = new Population(c.getCreaturesPerPopulation());

		do {
			if(!c.isGUI()) {
				System.out.print("Population no. " + (populations + 1) + "... ");
			} else {
				window.appendString("Population no. " + (populations + 1) + " ");
			}

			long started = System.currentTimeMillis();
			population.computeAll();

			if (!c.isGUI()) {
				int waitMillis = 100;

				while (!population.isFinished()) {
					try {
						Thread.sleep(waitMillis);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				long wait = c.getPause() * 1000;
				int FPS = 60;
				float waitTime = 1000 / FPS;

				presentation = population.getSample();
				presentation.setExpire(false);
				presentation.setTimeStep(waitTime / 1000);
				presentation.setup();

				Filter filter1 = new Filter();
				filter1.categoryBits = 4;
				filter1.maskBits = 1;
				filter1.maskBits = 3;

				Filter filter2 = new Filter();
				filter2.categoryBits = 5;
				filter2.maskBits = 2;
				filter2.maskBits = 3;

				for(Body body : presentation.getCreatures().get(0).getBodies()) {
					body.getFixtureList().setFilterData(filter1);
				}

				for(Body body : presentation.getCreatures().get(1).getBodies()) {
					body.getFixtureList().setFilterData(filter2);
				}

				window.setSimulator(presentation);

				while (!population.isFinished()
						|| started + wait > System.currentTimeMillis()) {
					presentation.update();
					window.updateDisplay();

					try {
						Thread.sleep((int) waitTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			population.recordResults();

			populations++;
			bestResult = population.getBestResult();
			population = population.evolve();

			String populationInfo = "finished in " + (System.currentTimeMillis() - started) / 1000 +
					" seconds with a high score of " + bestResult + "\n";
			if(c.isGUI()) {
				window.appendString(populationInfo);
			} else {
				System.out.print(populationInfo);
			}
		} while (!finishedPopulations());

		return population;

	}

	public void saveBestIndividual(Individual bestIndividual) {
		File saved = new File("SavedIndividuals");

		if(!saved.exists()) {
			saved.mkdirs();
		}

		File savedIndividual = new File("SavedIndividuals/" + c.getSave());

		try {
			PrintWriter pw = new PrintWriter(savedIndividual);
			pw.write(gson.toJson(bestIndividual));
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private String removeSpaces(String input) {
		return input.replaceAll("(\\p{L1})(\\p{Lu})", "$1 $2");
	}

	private void load() {
		FileReader load;
		
		window.appendString(removeSpaces(c.getLoad()));
		
		try {
			load = new FileReader(new File("SavedIndividuals/" + c.getLoad()));
			Individual individualToLoad = new Individual();
			try {
				individualToLoad = gson.fromJson(load, Individual.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Map<Integer, Component> loadMap = new HashMap<Integer, Component>();

			for(ComponentJoint cj : individualToLoad.componentJoints) {
				if(loadMap.containsKey(cj.componentOne.getNumber())) {
					cj.componentOne = loadMap.get(cj.componentOne.getNumber());
				} else {
					loadMap.put(cj.componentOne.getNumber(), cj.componentOne);
				}

				if(loadMap.containsKey(cj.componentTwo.getNumber())) {
					cj.componentTwo = loadMap.get(cj.componentTwo.getNumber());
				} else {
					loadMap.put(cj.componentTwo.getNumber(), cj.componentTwo);
				}
			}

			List<Individual> individuals = new ArrayList<Individual>();
			individuals.add(individualToLoad);

			Simulator presentation = new Simulator(individuals, c.getGravity(), 0.01f);
			presentation.setExpire(false);
			presentation.setup();

			window.setSimulator(presentation);
			//			Creature creatureToLoad = presentation.getCreatures().get(0);

			while(true) {
				presentation.update();
				window.updateDisplay();

				try {
					Thread.sleep(1000/60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void checkLoad() {
		if(!c.getLoad().equals("f")) {
			load();
		}
	}

	private void checkGui() {
		String mode;
		if(c.getLoad().equals("f")) {
			mode = "simulation";
		} else {
			mode = "load";
		}
		if (c.isGUI()) {
			window = new Window(c.getWindowWidth(), c.getWindowHeight(), mode);
		}
	}

	public void start() {
		checkGui();
		checkLoad();
		Population p = geneticAlgorithm();
		if(!c.getSave().equals("f")) {
			saveBestIndividual(p.getPreviousBestIndividual());
		}
	}
	
	public Result getBestResult() {
		return bestResult;
	}
}
