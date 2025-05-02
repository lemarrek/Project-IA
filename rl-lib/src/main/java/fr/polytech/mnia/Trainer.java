package fr.polytech.mnia;

import java.util.*;

public class Trainer {

    private QLearningAgent agent;

    public Trainer(QLearningAgent agent) {
        this.agent = agent;
    }

    public void train(int episodes) {
        for (int ep = 0; ep < episodes; ep++) {
            int[] board = new int[9];
            Arrays.fill(board, -1);
            boolean done = false;

            while (!done) {
                // Joueur 0 (random)
                List<Integer> moves0 = getLegalMoves(board);
                if (moves0.isEmpty()) break;
                int m0 = moves0.get(new Random().nextInt(moves0.size()));
                board[m0] = 0;
                if (checkWin(board, 0)) break;

                // Joueur 1 (agent)
                List<Integer> moves1 = getLegalMoves(board);
                if (moves1.isEmpty()) break;
                String state = encode(board);
                int m1 = agent.chooseAction(state, moves1);
                board[m1] = 1;
                String nextState = encode(board);

                double reward = 0;
                boolean win = checkWin(board, 1);
                if (win) reward = 1;
                else if (getLegalMoves(board).isEmpty()) reward = 0.5;

                agent.update(state, m1, nextState, reward, getLegalMoves(board));
                if (win || reward > 0) break;
            }
        }
    }

    private List<Integer> getLegalMoves(int[] b) {
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < b.length; i++) if (b[i] == -1) l.add(i);
        return l;
    }

    private String encode(int[] board) {
        StringBuilder sb = new StringBuilder();
        for (int v : board) sb.append(v == -1 ? "X" : v);
        return sb.toString();
    }

    private boolean checkWin(int[] b, int p) {
        int[][] w = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
        };
        for (int[] line : w) {
            if (b[line[0]] == p && b[line[1]] == p && b[line[2]] == p) return true;
        }
        return false;
    }
}
