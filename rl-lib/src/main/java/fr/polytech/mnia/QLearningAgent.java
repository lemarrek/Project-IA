package fr.polytech.mnia;

import java.util.*;

public class QLearningAgent {
    private Map<String, double[]> qTable = new HashMap<>();
    private double alpha = 0.1;
    private double gamma = 0.9;
    private double epsilon = 0.2;
    private Random random = new Random();

    public int chooseAction(String state, List<Integer> legalMoves) {
        ensureState(state, legalMoves);

        if (random.nextDouble() < epsilon) {
            return legalMoves.get(random.nextInt(legalMoves.size()));
        } else {
            double[] qValues = qTable.get(state);
            int bestAction = -1;
            double bestValue = Double.NEGATIVE_INFINITY;
            for (int a : legalMoves) {
                if (qValues[a] > bestValue) {
                    bestValue = qValues[a];
                    bestAction = a;
                }
            }
            return bestAction;
        }
    }

    public void update(String state, int action, String nextState, double reward, List<Integer> nextMoves) {
        ensureState(state, nextMoves);
        ensureState(nextState, nextMoves);

        double[] qValues = qTable.get(state);
        double[] qNext = qTable.get(nextState);

        double maxNext = nextMoves.isEmpty() ? 0 : Arrays.stream(qNext).max().getAsDouble();
        qValues[action] += alpha * (reward + gamma * maxNext - qValues[action]);
    }

    private void ensureState(String state, List<Integer> moves) {
        qTable.computeIfAbsent(state, k -> new double[9]); // 9 positions possibles
    }
}
