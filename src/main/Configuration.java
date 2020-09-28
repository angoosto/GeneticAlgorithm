/**
 * 
 */
package main;

import java.io.IOException;
import java.util.Properties;

/*
 * This class deals with the settings for the application. It reads in the default configuration properties from a properties file.
 */
public class Configuration {
    private int numberOfPopulations;
    private int creaturesPerPopulation;
    private float mutationChance;
    private int creatureSeconds;
    private int concurrentSimulators;
    private float gravity;
    private boolean GUI;
    private int windowWidth;
    private int windowHeight;
    private int pause;
    private double satisfactory;
    private String save;
    private String load;
    private float timeStep;

    public Configuration() {
        applyProperties(getDefaultProperties());
    }

    public Configuration(Properties p) {
        applyProperties(p);
    }

    private void applyProperties(Properties p) {
        creaturesPerPopulation = Integer.parseInt(p.getProperty("creaturesPerPopulation"));
        creatureSeconds = Integer.parseInt(p.getProperty("creatureSeconds"));
        concurrentSimulators = Integer.parseInt(p.getProperty("concurrentSimulators"));
        numberOfPopulations = Integer.parseInt(p.getProperty("numberOfPopulations"));
        satisfactory = Double.parseDouble(p.getProperty("satisfactory"));
        windowWidth = Integer.parseInt(p.getProperty("windowWidth"));
        windowHeight = Integer.parseInt(p.getProperty("windowHeight"));
        gravity = Float.parseFloat(p.getProperty("gravity"));
        timeStep = Float.parseFloat(p.getProperty("timeStep"));
        GUI = Boolean.parseBoolean(p.getProperty("GUI"));
        pause = Integer.parseInt(p.getProperty("pause"));
        mutationChance = Float.parseFloat(p.getProperty("mutationChance"));
        save = p.getProperty("save");
        load = p.getProperty("load");

        if (numberOfPopulations == -200 && satisfactory == -200) {
            numberOfPopulations = 100;
        }
    }

    private static Properties getDefaultProperties() {
        Properties defaultProperties = new Properties();
        try {
            defaultProperties.load(Configuration.class
                    .getResourceAsStream("/configuration.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't find default configuration file");
        }
        return defaultProperties;
    }

	/**
	 * @return the numberOfPopulations
	 */
	public int getNumberOfPopulations() {
		return numberOfPopulations;
	}

	/**
	 * @return the creaturesPerPopulation
	 */
	public int getCreaturesPerPopulation() {
		return creaturesPerPopulation;
	}

	/**
	 * @return the creatureSeconds
	 */
	public int getCreatureSeconds() {
		return creatureSeconds;
	}

	/**
	 * @return the concurrentSimulators
	 */
	public int getConcurrentsimulators() {
		return concurrentSimulators;
	}

	/**
	 * @return the windowWidth
	 */
	public int getWindowWidth() {
		return windowWidth;
	}

	/**
	 * @return the windowHeight
	 */
	public int getWindowHeight() {
		return windowHeight;
	}

	/**
	 * @return the gravity
	 */
	public float getGravity() {
		return gravity;
	}

	/**
	 * @return the timeStep
	 */
	public float getTimeStep() {
		return timeStep;
	}

	/**
	 * @return the gUI
	 */
	public boolean isGUI() {
		return GUI;
	}

	/**
	 * @return the pause
	 */
	public int getPause() {
		return pause;
	}

	/**
	 * @return the mutationChance
	 */
	public float getMutationChance() {
		return mutationChance;
	}

	/**
	 * @return the satisfactory
	 */
	public double getSatisfactory() {
		return satisfactory;
	}

	/**
	 * @return the save
	 */
	public String getSave() {
		return save;
	}

	/**
	 * @return the load
	 */
	public String getLoad() {
		return load;
	}

}
