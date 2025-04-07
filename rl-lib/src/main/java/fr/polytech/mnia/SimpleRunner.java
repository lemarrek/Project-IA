package fr.polytech.mnia;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import de.prob.statespace.Transition;

public class SimpleRunner extends Runner {
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
        System.out.println("\n=== Epsilon Greedy ===");
        eGreedy();
        System.out.println("\n=== Bandit Gradient ===");
        bGradient();
    }

    public void eGreedy() throws IOException {
        int choice;
        double reward;
        Transition t;

        Double[] results = new Double[choices.size()];
        int[] TimesChoosen = new int[choices.size()];
        Double[] totalReward = new Double[choices.size()];

        int[][] timesChosenHistory = new int[iterations][choices.size()];

        for (int i = 0; i < choices.size(); i++) {
            results[i] = 0.0;
            TimesChoosen[i] = 0;
            totalReward[i] = 0.0;
        }

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

            // Save the current state of times chosen
            for (int j = 0; j < choices.size(); j++) {
                timesChosenHistory[i][j] = TimesChoosen[j];
            }
        }

        saveResultsToCSV("egreedy_history.csv", choices, timesChosenHistory);

        for (int i = 0; i < results.length; i++) {
            System.out.println("\nfor " + choices.get(i).getParameterPredicate());
            System.out.println("Choosen : " + TimesChoosen[i] + " times");
            System.out.println("Expected result : " + results[i]);
        }
    }

    public int choose(Double[] results) {
        double epsilon = 0.2;
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

    public void bGradient() throws IOException {
        int choice;
        double reward;
        double alpha = 0.01;
        Transition t;

        Double[] results = new Double[choices.size()];
        int[] TimesChoosen = new int[choices.size()];
        double[][] probabilitiesHistory = new double[iterations][choices.size()];

        for (int i = 0; i < choices.size(); i++) {
            results[i] = 1.0 / results.length;
            TimesChoosen[i] = 0;
        }

        for (int i = 0; i < iterations; i++) {
            choice = chooseGradient(results);
            t = choices.get(choice);
            env.runAction(t);

            if (env.getState().getStateRep().equals("( res=OK )")) {
                reward = 1.0;
            } else {
                reward = 0.0;
            }

            TimesChoosen[choice]++;
            for (int j = 0; j < results.length; j++) {
                if (j == choice) {
                    results[choice] = results[choice] + alpha * reward * (1 - results[choice]);
                } else {
                    results[j] = results[j] - alpha * reward * results[j];
                }
            }

            // Normalisation des probabilités
            double sum = 0.0;
            for (int j = 0; j < results.length; j++) {
                sum += results[j];
            }
            for (int j = 0; j < results.length; j++) {
                results[j] /= sum;
             }

            // Save the current state of probabilities
            for (int j = 0; j < choices.size(); j++) {
                probabilitiesHistory[i][j] = results[j];
            }
        }

        saveProbabilitiesToCSV("bgradient_history.csv", choices, probabilitiesHistory);

        for (int i = 0; i < results.length; i++) {
            System.out.println("\nfor " + choices.get(i).getParameterPredicate());
            System.out.println("Choosen : " + TimesChoosen[i] + " times");
            System.out.println("Probability : " + results[i]);
        }
    }

    public int chooseGradient(Double[] results) {
        double epsilon = 0.3;
        Random random = new Random();
        double alea = random.nextDouble();
        double e = random.nextDouble();
        double cumulativeProbability = 0.0;

        if (e < epsilon) {
            int r = random.nextInt() % results.length;
            if (r < 0) {
                r = -r;
            }
            return r;
        }
        for (int i = 0; i < results.length; i++) {
            cumulativeProbability += results[i];
            if (alea < cumulativeProbability) {
                return i;
            }
        }
        return results.length - 1;
    }

    private void saveResultsToCSV(String fileName, List<Transition> choices, int[][] timesChosenHistory) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveProbabilitiesToCSV(String fileName, List<Transition> choices, double[][] probabilitiesHistory) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write header for probabilities
            writer.append("Iteration");
            for (Transition choice : choices) {
                writer.append(",").append(choice.getParameterPredicate()).append("_Probability");
            }
            writer.append("\n");

            // Write data for probabilities
            for (int i = 0; i < probabilitiesHistory.length; i++) {
                writer.append(String.valueOf(i));
                for (double value : probabilitiesHistory[i]) {
                    writer.append(",").append(String.valueOf(value));
                }
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
