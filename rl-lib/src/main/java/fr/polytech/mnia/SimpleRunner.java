package fr.polytech.mnia;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import de.prob.statespace.Transition;

public class SimpleRunner extends Runner {
    double epsilon = 0.2; // Exploration rate
    Evironnement env;
    List<Transition> choices;
    int iterations = 1000;

    public SimpleRunner() throws Exception {
        super("/Simple/SimpleRL.mch");
        this.initialise();
        env = new Evironnement(this);
        choices = env.getActions();
    }

    @Override
    public void execSequence() throws Exception {
        int choice;
        double reward;
        Transition t;

        Double[] results = new Double[choices.size()];
        int[] TimesChoosen = new int[choices.size()];
        Double[] totalReward = new Double[choices.size()];

        // Initialize results and times chosen
        for (int i = 0; i < choices.size(); i++) {
            results[i] = 0.0;
            TimesChoosen[i] = 0;
            totalReward[i] = 0.0;
        }

        // Save the history of times chosen and results for each action
        int[][] timesChosenHistory = new int[iterations][choices.size()];
        double[][] resultsHistory = new double[iterations][choices.size()];

        for (int i = 0; i < iterations; i++) {
            choice = choose(results);
            t = choices.get(choice);
            env.runAction(t);

            if (env.getState().getStateRep().equals("( res=OK )")) {
                reward = 1.0;
            } else {
                reward = 0.0;
            }

            totalReward[choice] += reward;
            TimesChoosen[choice]++;
            results[choice] = totalReward[choice] / TimesChoosen[choice];

            // Save the current state of times chosen and results
            for (int j = 0; j < choices.size(); j++) {
                timesChosenHistory[i][j] = TimesChoosen[j];
                resultsHistory[i][j] = results[j];
            }
        }

        // Save results to CSV
        saveResultsToCSV("results_history.csv", choices, timesChosenHistory, resultsHistory);

        for (int i = 0; i < results.length; i++) {
            System.out.println("\nfor " + choices.get(i).getParameterPredicate());
            System.out.println("Choosen : " + TimesChoosen[i] + " times");
            System.out.println("Expected result : " + results[i]);
        }
    }

    public int choose(Double[] results) {
        Random random = new Random();
        double alea = random.nextDouble();
        int choice;
        if (epsilon < alea) {
            choice = random.nextInt(choices.size());
        } else {
            choice = optimal(results);
        }
        return choice;
    }

    public int optimal(Double[] list) {
        if (list.length == 0) {
            return 0;
        }
        int index = 0;
        for (int i = 1; i < list.length; i++) {
            if (list[i - 1] < list[i]) {
                index = i;
            }
        }
        return index;
    }

    private void saveResultsToCSV(String fileName, List<Transition> choices, int[][] timesChosenHistory, double[][] resultsHistory) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write header for times chosen
            writer.append("Iteration");
            for (Transition choice : choices) {
                writer.append(",").append(choice.getParameterPredicate()).append("_TimesChosen");
            }
            writer.append("\n");
    
            // Write data for times chosen
            for (int i = 0; i < timesChosenHistory.length; i++) {
                writer.append(String.valueOf(i));
                for (int value : timesChosenHistory[i]) {
                    writer.append(",").append(String.valueOf(value));
                }
                writer.append("\n");
            }
    
            // Write header for results
            writer.append("\nIteration");
            for (Transition choice : choices) {
                writer.append(",").append(choice.getParameterPredicate()).append("_Result");
            }
            writer.append("\n");
    
            // Write data for results
            for (int i = 0; i < resultsHistory.length; i++) {
                writer.append(String.valueOf(i));
                for (double value : resultsHistory[i]) {
                    writer.append(",").append(String.valueOf(value));
                }
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
