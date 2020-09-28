/**
 * 
 */
package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/*
 * This class creates an individual creature and contains methods to mutate an individual
 */

public class Individual {
    private static final int minShapes = 2;
    private static final int maxShapes = 10;
    private static float mutationChance = 0.2f;

    public static void setMutationChance(float mutationChance) {
        Individual.mutationChance = mutationChance;
    }

    public ArrayList<ComponentJoint> componentJoints = new ArrayList<ComponentJoint>();

    /**
     * Default constructor
     */
    public Individual() {

    }

    /**
     * Copy constructor
     * 
     * @param copy
     *            the individual to copy from
     */
    public Individual(Individual copy) {
        HashMap<Integer, Component> used = new HashMap<Integer, Component>();

        for (ComponentJoint componentJoint : copy.componentJoints) {
            // copy numerical values but not components
            ComponentJoint my = new ComponentJoint(componentJoint);

            if (used.containsKey(componentJoint.componentOne.number)) {
                my.componentOne = used.get(componentJoint.componentOne.number);
            } else {
                my.componentOne = new Component(componentJoint.componentOne);
                used.put(componentJoint.componentOne.number, my.componentOne);
            }

            if (used.containsKey(componentJoint.componentTwo.number)) {
                my.componentTwo = used.get(componentJoint.componentTwo.number);
            } else {
                my.componentTwo = new Component(componentJoint.componentTwo);
                used.put(componentJoint.componentTwo.number, my.componentTwo);
            }

            componentJoints.add(my);
        }
    }

    /**
     * Represents a single mutation
     */
    public static float mutation(float from, float to) {
        if (Randoms.random(0f, 1f) < mutationChance) {
            return Randoms.random(from, to);
        }

        return 0f;
    }

    private List<Component> getComponentList() {
        HashSet<Component> componentSet = new HashSet<Component>();

        for (ComponentJoint componentJoint : componentJoints) {
            componentSet.addAll(componentJoint.getComponentList());
        }

        ArrayList<Component> componentList = new ArrayList<Component>(componentSet);

        return componentList;
    }

    /**
     * @return a new mutated copy of the individual
     */
    public Individual mutate() {
        // create a copy individual
        Individual mutated = new Individual(this);

        for (ComponentJoint componentJoint : mutated.componentJoints) {
            componentJoint.changeAngularVelocity(mutation(-0.05f, 0.05f));
            componentJoint.changePointA(mutation(-0.05f, 0.05f));
            componentJoint.changePointB(mutation(-0.05f, 0.05f));
            componentJoint.changePercentOne(mutation(-5f, 5f));
            componentJoint.changePercentTwo(mutation(-5f, 5f));
        }

        List<Component> componentList = mutated.getComponentList();

        for (Component component : componentList) {
            float widthToMutate = mutation(-0.05f, 0.05f);
            float heightToMutate = mutation(-0.05f, 0.05f);

            if (component.width + widthToMutate >= Component.minimumWidth
                    && component.width + widthToMutate <= Component.maximumWidth) {
                component.width += widthToMutate;
            }

            if (component.height + heightToMutate >= Component.minimumHeight
                    && component.height + heightToMutate <= Component.maximumHeight) {
                component.height += heightToMutate;
            }
        }

        if (mutationChance / 10 > Randoms.random(0f, 1f)
                && componentList.size() > 2) {
            mutated.removeRandomComponent();
        }

        if (mutationChance / 10 > Randoms.random(0f, 1f)
                && componentList.size() < Individual.maxShapes) {
            mutated.addRandomComponent();
        }

        return mutated;
    }

    private Component getRandomExistingComponent() {
        ComponentJoint connector = componentJoints.get(Randoms.random(0,
                componentJoints.size()));
        return Randoms.random() < 0.5 ? connector.componentOne : connector.componentTwo;
    }

    private void addRandomComponent() {
        Component newComponent = Component.random();
        Component toConnect = getRandomExistingComponent();

        ComponentJoint connectingJoint = ComponentJoint.randomJoint(newComponent, toConnect);

        componentJoints.add(connectingJoint);
    }

    private void removeRandomComponent() {
        Component toRemove = getRandomExistingComponent();

        if (toRemove.componentJoints.size() != 1) {
            return;
        }

        componentJoints.remove(toRemove.componentJoints.get(0));
    }

    public static Individual random() {
        Individual individual = new Individual();

        // initial random component pair
        Component componentOne = Component.random();
        Component componentTwo = Component.random();
        ComponentJoint initialComponentJoint = ComponentJoint.randomJoint(componentOne, componentTwo);
        individual.componentJoints.add(initialComponentJoint);

        for (int i = 0; i < Randoms.random(minShapes, maxShapes - 2); ++i) {
            individual.addRandomComponent();
        }

        return individual;
    }

    public static List<Individual> random(int n) {
        ArrayList<Individual> individuals = new ArrayList<Individual>();

        for (int i = 0; i < n; ++i) {
            individuals.add(Individual.random());
        }

        return individuals;
    }

    @Override
    public String toString() {
        return "Individual [joints=" + componentJoints + "]";
    }

}
