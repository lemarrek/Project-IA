package fr.polytech.mnia;

import de.prob.statespace.Transition;
import java.util.Scanner;

public class TicTacToeRunner extends Runner {
    private Scanner scanner = new Scanner(System.in);

    public TicTacToeRunner() throws Exception {
        super("/TicTacToe/tictac.mch");
        this.initialise();
    }

    public void execSequence() throws Exception {
        System.out.println("Bienvenue au Tic Tac Toe !\n");
        System.out.println("Règles :");
        System.out.println("- Vous jouez contre une IA.");
        System.out.println("- Vous êtes '0', l'IA est '1'.");
        System.out.println("- Pour jouer, entrez un chiffre de 1 à 9 correspondant à une position sur la grille :\n");
        System.out.println("  1 | 2 | 3");
        System.out.println(" ---+---+---");
        System.out.println("  4 | 5 | 6");
        System.out.println(" ---+---+---");
        System.out.println("  7 | 8 | 9");
        System.out.println("\nBonne chance !");

        while (true) {
            // Coup de l'utilisateur (joueur 0)
            System.out.print("Votre coup (1-9) : ");
            int pos;
            try {
                pos = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide, veuillez saisir un nombre entre 1 et 9.");
                continue;
            }
            Transition userMove = findTransition(pos);
            if (userMove == null) {
                System.out.println("Coup invalide, veuillez réessayer.");
                continue;
            }
            state = userMove.getDestination().explore();
            prettyPrintTicTacToe();

            if ("TRUE".equals(state.eval("win(0)").toString())) {
                System.out.println("Félicitations, vous avez gagné !");
                break;
            }
            if (state.getOutTransitions().isEmpty()) {
                System.out.println("Match nul !");
                break;
            }

            // Coup de l'IA (joueur 1)
            System.out.println("L'IA réfléchit...");
            int best = chooseBestMove();
            if (best < 1 || best > 9) {
                System.out.println("Match nul !");
                break;
            }
            Transition aiMove = findTransition(best);
            state = aiMove.getDestination().explore();
            System.out.println("IA joue en " + best);
            prettyPrintTicTacToe();

            if ("TRUE".equals(state.eval("win(1)").toString())) {
                System.out.println("L'IA a gagné !");
                break;
            }
            if (state.getOutTransitions().isEmpty()) {
                System.out.println("Match nul !");
                break;
            }
        }
    }

    //convertit etat proB, en tableau de tableau de int
    private int[][] extractBoard() throws Exception {
        int[][] board = new int[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = -1;
        String input = state.eval("square").toString().replaceAll("[^0-9↦,]", "");
        if (!input.isEmpty()) {
            for (String e : input.split(",")) {
                String[] p = e.split("↦");
                int r = Integer.parseInt(p[0]) - 1;
                int c = Integer.parseInt(p[1]) - 1;
                int v = Integer.parseInt(p[2]);
                board[r][c] = v;
            }
        }
        return board;
    }

    //utilise algo minimax pour trouver meilleur coup
    private int chooseBestMove() throws Exception {
        int[][] board = extractBoard();
        int bestScore = Integer.MIN_VALUE;
        int move = -1;
        for (int pos = 0; pos < 9; pos++) {
            int r = pos / 3, c = pos % 3;
            if (board[r][c] == -1) {
                board[r][c] = 1;
                int score = minimax(board, false);
                board[r][c] = -1;
                if (score > bestScore) {
                    bestScore = score;
                    move = pos + 1;
                }
            }
        }
        return move;
    }

    //cherche a maximiser recompense, explore tous les coups possible
    private int minimax(int[][] b, boolean isMax) {
        int eval = evaluate(b);
        if (eval != 2) return eval;
        int best = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int pos = 0; pos < 9; pos++) {
            int r = pos / 3, c = pos % 3;
            if (b[r][c] == -1) {
                b[r][c] = isMax ? 1 : 0;
                int val = minimax(b, !isMax);
                b[r][c] = -1;
                best = isMax ? Math.max(best, val) : Math.min(best, val);
            }
        }
        return best;
    }

    //+1 si IA, -1 si user, 0 nul, 2 si rien
    private int evaluate(int[][] b) {
        for (int i = 0; i < 3; i++) {
            if (b[i][0] == b[i][1] && b[i][1] == b[i][2] && b[i][0] != -1)
                return b[i][0] == 1 ? +1 : -1;
            if (b[0][i] == b[1][i] && b[1][i] == b[2][i] && b[0][i] != -1)
                return b[0][i] == 1 ? +1 : -1;
        }
        if (b[0][0] == b[1][1] && b[1][1] == b[2][2] && b[0][0] != -1)
            return b[0][0] == 1 ? +1 : -1;
        if (b[0][2] == b[1][1] && b[1][1] == b[2][0] && b[0][2] != -1)
            return b[0][2] == 1 ? +1 : -1;
        for (int[] row : b)
            for (int cell : row)
                if (cell == -1) return 2;
        return 0;
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

    private void prettyPrintTicTacToe(){
        String input = state.eval("square").toString() ;

        String[][] board = {{" ", " ", " "}, {" ", " ", " "}, {" ", " ", " "}};

        input = input.replaceAll("[^0-9↦,]", ""); 
        String[] entries = input.split(",");

        for (String entry : entries) {
            String[] parts = entry.split("↦");
            int row = Integer.parseInt(parts[0]) - 1;
            int col = Integer.parseInt(parts[1]) - 1;
            String value = parts[2];
            board[row][col] = value;
        }

        System.out.print("\n");
        for (int i = 0; i < 3; i++) {
            System.out.println(" " + board[i][0] + " | " + board[i][1] + " | " + board[i][2]);
            if (i < 2) System.out.println("---+---+---");
        }
        System.out.print("\n");
    }
}
