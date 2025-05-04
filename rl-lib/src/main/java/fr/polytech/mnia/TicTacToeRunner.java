package fr.polytech.mnia;

import de.prob.statespace.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TicTacToeRunner extends Runner {
    private final Scanner scanner = new Scanner(System.in);
    private final QLearningAgent qAgent = new QLearningAgent();
    private Evironnement env; // environnement ProB

    private int userMark = 0; // 0 ou 1, alterne après chaque partie

    public TicTacToeRunner() throws Exception {
        super("/TicTacToe/tictac.mch");
        this.initialise();
        env = new Evironnement(this); // initialisation de l'environnement
    }

    public void trainAgent() {
        Trainer trainer = new Trainer(qAgent);
        trainer.train(10000);
    }

    public void execSequence() throws Exception {
        trainAgent();

        while (true) {
            printWelcomeMessage();
            playSingleGame();
            // alterner le rôle du joueur pour la prochaine partie
            userMark = 1 - userMark;
            System.out.print("Rejouer ? (y/n) : ");
            String replay = scanner.nextLine();
            if (!replay.equalsIgnoreCase("y")) break;
        }
    }

    private void playSingleGame() throws Exception {
        // remettre l'état initial
        state = env.getState();
        printBoardTemplate();

        boolean userTurn = (userMark == 0);
        while (true) {
            if (userTurn) {
                if (!playerTurn()) continue;
                if (checkGameOver(userMark)) return;
            } else {
                System.out.println("L'IA réfléchit...");
                if (!aiTurn()) return;
                if (checkGameOver(1 - userMark)) return;
            }
            userTurn = !userTurn;
        }
    }

    private boolean playerTurn() throws Exception {
        System.out.print("Votre coup (1-9) : ");
        int pos;
        try {
            pos = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrée invalide, veuillez saisir un nombre entre 1 et 9.");
            return false;
        }

        Transition userMove = findTransition(pos);
        if (userMove == null) {
            System.out.println("Coup invalide, veuillez réessayer.");
            return false;
        }
        state = userMove.getDestination().explore();
        printBoard();
        return true;
    }

    private boolean aiTurn() throws Exception {
        int best = chooseBestMove();
        if (best < 1 || best > 9) {
            System.out.println("Match nul !");
            return false;
        }
        Transition aiMove = findTransition(best);
        state = aiMove.getDestination().explore();
        System.out.println("IA joue en " + best);
        printBoard();
        return true;
    }

    private boolean checkGameOver(int player) throws Exception {
        if ("TRUE".equals(state.eval("win(" + player + ")").toString())) {
            if (player == userMark) System.out.println("Félicitations, vous avez gagné !");
            else System.out.println("L'IA a gagné !");
            return true;
        }
        if (state.getOutTransitions().isEmpty()) {
            System.out.println("Match nul !");
            return true;
        }
        return false;
    }

    private int chooseBestMove() throws Exception {
        int[][] board = extractBoard();
        List<Integer> legalMoves = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            int r = i / 3, c = i % 3;
            if (board[r][c] == -1) legalMoves.add(i);
        }
        return qAgent.chooseAction(encodeBoard(board), legalMoves) + 1;
    }

    private String encodeBoard(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board)
            for (int cell : row)
                sb.append(cell == -1 ? "X" : cell);
        return sb.toString();
    }

    private int[][] extractBoard() throws Exception {
        int[][] board = new int[3][3];
        for (int[] row : board) java.util.Arrays.fill(row, -1);

        String input = state.eval("square").toString().replaceAll("[^0-9↦,]", "");
        if (!input.isEmpty()) {
            for (String e : input.split(",")) {
                String[] parts = e.split("↦");
                int r = Integer.parseInt(parts[0]) - 1;
                int c = Integer.parseInt(parts[1]) - 1;
                int v = Integer.parseInt(parts[2]);
                board[r][c] = v;
            }
        }
        return board;
    }

    private Transition findTransition(int pos) {
        int r = (pos - 1) / 3 + 1;
        int c = (pos - 1) % 3 + 1;
        for (Transition t : state.getOutTransitions()) {
            String pred = t.getParameterPredicate();
            if (pred.contains("xx = " + r) && pred.contains("yy = " + c)) {
                return t;
            }
        }
        return null;
    }

    private void printBoard() throws Exception {
        String[][] board = new String[][]{{" ", " ", " "}, {" ", " ", " "}, {" ", " ", " "}};
        String input = state.eval("square").toString().replaceAll("[^0-9↦,]", "");

        for (String entry : input.split(",")) {
            if (!entry.contains("↦")) continue;
            String[] parts = entry.split("↦");
            int row = Integer.parseInt(parts[0]) - 1;
            int col = Integer.parseInt(parts[1]) - 1;
            board[row][col] = parts[2];
        }

        System.out.println();
        for (int i = 0; i < 3; i++) {
            System.out.println(" " + board[i][0] + " | " + board[i][1] + " | " + board[i][2]);
            if (i < 2) System.out.println("---+---+---");
        }
        System.out.println();
    }

    private void printBoardTemplate() {
        System.out.println("Positions :");
        System.out.println(" 1 | 2 | 3");
        System.out.println("---+---+---");
        System.out.println(" 4 | 5 | 6");
        System.out.println("---+---+---");
        System.out.println(" 7 | 8 | 9");
        System.out.println();
    }

    private void printWelcomeMessage() {
        System.out.println("\nBienvenue au Tic Tac Toe !");
        System.out.println(userMark == 0 ? "Vous êtes '0', l'IA est '1'." : "Vous êtes '1', l'IA est '0'.");
    }
}
