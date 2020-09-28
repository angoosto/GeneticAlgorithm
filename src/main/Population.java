/**
 * 
 */
package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * This class creates a population of individuals.  
 */

public class Population {
    private static ThreadPoolExecutor workers;
    private static int concurrentSimulators;

    public static volatile int populationNumber = 0;

    public static void setWorkers(int parallelSimulators) {
        Population.concurrentSimulators = parallelSimulators;

        workers = new ThreadPoolExecutor(parallelSimulators,
                parallelSimulators, 100L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        workers.setThreadFactory(new SimulatorThreadFactory(
                Thread.MAX_PRIORITY, false));
    }

    private int numberOfSimulators;
    private List<Individual> individuals;
    private List<Result> results = new ArrayList<Result>();
    private List<Simulator> simulators;
    private List<Future<List<Result>>> futureSimulatorResultList = new ArrayList<Future<List<Result>>>();
    private Individual previousBestIndividual;
    private Individual currentBestIndividual;

    private static float gravity;
    private static float timeStep;
    private final static double random = 0.05;
    private final static double best = 0.1;

    public static float getGravity() {
        return gravity;
    }

    public static void setGravity(float gravity) {
        Population.gravity = gravity;
    }

    public static float getTimeStep() {
        return timeStep;
    }

    public static void setTimeStep(float timeStep) {
        Population.timeStep = timeStep;
    }

    /**
     * population from a list of individuals
     * 
     * @param creatureSeconds
     * @param individuals
     */
    public Population(List<Individual> individuals,
            Individual previousBestIndividual) {
        this.individuals = individuals;
        this.previousBestIndividual = previousBestIndividual;
        populationNumber++;
    }

    /**
     * Random population
     * @param numIndividuals The size of the population
     */
    public Population(int numIndividuals) {
        this(Individual.random(numIndividuals), null);
    }

    /**
     * Returns true if all results have been recorded
     * @return
     */
    public boolean isFinished() {
        for (Future<List<Result>> futureSimulatorResult : futureSimulatorResultList) {
            if (!futureSimulatorResult.isDone()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Records all results of creatures that have been simulated
     * @return List of results
     */
    
    public ArrayList<Result> recordResults() {
        for (Future<List<Result>> futuresimulatorResult : futureSimulatorResultList) {
            if (!futuresimulatorResult.isDone()) {
                throw new IllegalStateException(
                        "Not all simulators have finished.");
            }

            try {
                List<Result> simulatorResults = futuresimulatorResult.get();
                results.addAll(simulatorResults);
            } catch (Exception e) {
            	e.printStackTrace();
                throw new IllegalStateException("simulators could not finish");
            }
        }

        if (results.size() != individuals.size()) {
            throw new IllegalStateException(
                    "Not all simulators have finished.");
        }

        Collections.sort(results);

        currentBestIndividual = new Individual(results.get(0).individual);

        return new ArrayList<Result>(results);
    }

    public static void normalize(List<Result> results, float unit) {
        for (Result result : results) {
            result.normalised = result.score / unit;
        }
    }

    public static float sum(List<Result> results) {
        float sum = 0;

        for (Result result : results) {
            sum += result.score;
        }

        return sum;
    }

    /**
     * Evolves the population based on score
     * @return the next (evolved) population to be simulated
     */
    public Population evolve() {
        normalize(results, sum(results));

        List<Individual> individuals = new ArrayList<Individual>();

        int useBest = (int) (best * results.size());
        int useRandom = Math.abs((int) (random * (results.size() - useBest)));

        for (int i = 0; i < useBest; ++i) {
            individuals.add(results.get(i).individual.mutate());
        }

        for (int i = 0; i < useRandom; ++i) {
            individuals.add(Individual.random());
        }

        while (individuals.size() != results.size()) {
            float sum = 0;
            float r = Randoms.random();

            for (Result result : results) {
                sum += result.normalised;

                if (sum >= r) {
                    individuals.add(result.individual.mutate());
                    break;
                }
            }
        }

        return new Population(individuals, currentBestIndividual);
    }

    
    /**
     * Method to return 2 creatures to be graphically simulated. The previous best one and a random one
     * @return
     */
    
    public Simulator getSample() {
        List<Individual> presentationIndividuals = new ArrayList<Individual>();
        
        presentationIndividuals.add(getPreviousBestIndividual());
        presentationIndividuals.add(individuals.get(Randoms.random(0,individuals.size())));

        return new Simulator(presentationIndividuals, gravity, timeStep);
    }

    public Individual getPreviousBestIndividual() {
//    	System.out.println("pbi");
        if (previousBestIndividual != null) {
            return previousBestIndividual;
        } else {
            return individuals.get(0);
        }
    }

    public Individual getMyBestIndividual() {
        if (currentBestIndividual != null) {
            return currentBestIndividual;
        } else {
            return getPreviousBestIndividual();
        }
    }

    public Result getBestResult() {
        return results.get(0);
    }

    /**
     * Divides all creatures into a number of simulators. These simulators are then computed
     * 
     * @param numsimulators the number of simulators in which to divide the creatures
     * @return a list of simulators to compute
     */
    public ArrayList<Simulator> getSimulators(int numsimulators) {
        int numIndividuals = individuals.size();

        if (numsimulators > numIndividuals) {
            throw new IllegalArgumentException("The number of simulators must be lower than the number of individuals.");
        }

        int creaturesPersimulator = numIndividuals / numsimulators;
        ArrayList<Simulator> simulators = new ArrayList<Simulator>();

        for (int i = 0; i < numsimulators; ++i) {
            List<Individual> taken = new ArrayList<Individual>(
                    individuals.subList(i * creaturesPersimulator, i
                            * creaturesPersimulator + creaturesPersimulator));
            simulators.add(new Simulator(taken, gravity, timeStep));
        }

        int creaturesLeft = numIndividuals % numsimulators;

        for (int i = 0; i < creaturesLeft; i++) {
            simulators.get(i).addIndividual(
                    individuals.get(numIndividuals - i - 1));
        }

        return simulators;
    }
    
    public void computeAll() {
        simulators = getSimulators(concurrentSimulators);

        for (Simulator simulator : simulators) {
            futureSimulatorResultList.add(workers.submit(simulator));
        }
    }

    /**
     * When a simulator is finished this method is called in order to return the results
     * @param results some simulator's results
     */
    public void submitResults(List<Result> results) {
        this.results.addAll(results);
    }

    public int numThreads() {
        return workers.getActiveCount();
    }

    public int getNumSimulators() {
        return numberOfSimulators;
    }

    public void setNumSimulators(int numSimulators) {
        this.numberOfSimulators = numSimulators;
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }

    public void setIndividuals(ArrayList<Individual> individuals) {
        this.individuals = individuals;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

}
