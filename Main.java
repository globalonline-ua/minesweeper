package minesweeper;

import java.util.*;

public class Main {

    static int[] gameField;
    static int mines;
    static int SIZE = 9;
    static int[][] gameMatrix;
    static int gameFieldSize;
    static Map<Integer, Character> gameChars;
    enum Msg {
        USR_TURN("Set/unset mines marks or claim a cell as free (ex.: 1 1 free/mine): "),
        USR_FIRST("How many mines do you want on the field? "),
        USR_WIN("Congratulations! You found all the mines!"),
        USR_LOSE("You stepped on a mine and failed!"),
        USR_TURN_NUM("There is a number here!");

        final String label;
        Msg(String label) {
            this.label = label;
        }

        public String getLbl() {
            return this.label;
        }
    }

    static {
        gameChars = new HashMap<>();
        gameChars.put(0, '.');
        gameChars.put(9, '.');
        gameChars.put(10, '*');
        gameChars.put(11, '*');
        gameChars.put(20, '/');
        gameChars.put(21, 'X');
        for (int i = 1; i < 9; i++) {
            gameChars.put(i, (char) (i + '0'));
        }
        for (int i = 91; i < 99; i++) {
            gameChars.put(i, '.');
        }
        for (int i = 81; i < 89; i++) {
            gameChars.put(i, '*');
        }
    }

    public static void newField(int size, int mines) {
        gameFieldSize = size * size;
        gameField = new int[gameFieldSize];
        gameMatrix = new int[size][size];
        Arrays.fill(gameField, 0);
        Random random = new Random();
        int rndMines = mines > 0 ? mines : random.nextInt(gameFieldSize);
        for (int i = 0; i < rndMines; i++) {
            int rndField = random.nextInt(gameFieldSize);
            while (gameField[rndField] == 9) {
                rndField = random.nextInt(gameFieldSize);
            }
            gameField[rndField] = 9;
        }
        int countSize = 0;
        for (int i = 0; i < gameMatrix.length; i++) {
            for (int j = 0; j < gameMatrix[i].length; j++) {
                gameMatrix[i][j] = gameField[countSize];
                countSize++;
            }
        }
    }


    static void fillTurn(boolean[][] visited, int r, int c) {
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) {
            return;
        }
        if (visited[r][c]) {
            return;
        }
        visited[r][c] = true;

        if (gameMatrix[r][c] == 0) {
            gameMatrix[r][c] = 20;
        }

        if (gameMatrix[r][c] == 10) {
            gameMatrix[r][c] = 20;
            //return;
        }

        if (gameMatrix[r][c] > 80 && gameMatrix[r][c] < 89) {
            gameMatrix[r][c] -= 80;
            //return;
        }
        if (gameMatrix[r][c] > 90 && gameMatrix[r][c] < 99) {
            gameMatrix[r][c] -= 90;
            return;
        }
        if (gameMatrix[r][c] == 9 || gameMatrix[r][c] == 11) {
            return;
        }

        for (int k = -1; k < 2; k++) {
            for (int l = -1; l < 2; l++) {
                try {
                    fillTurn(visited, r + k, c + l);
                } catch (Exception ignored) {
                }
            }
        }

    }

    public static void createNumbers() {
        for (int i = 0; i < gameMatrix.length; i++) {
            for (int j = 0; j < gameMatrix[i].length; j++) {
               int countM = 0;
               if (gameMatrix[i][j] != 9) {
                   for (int k = -1; k < 2; k++) {
                       for (int l = -1; l < 2; l++) {
                           try {
                               if (gameMatrix[i + k][j + l] == 9) {
                                   countM++;
                               }
                           } catch (IndexOutOfBoundsException ignored) {
                           }
                       }
                   }
                   if (countM > 0) {
                       gameMatrix[i][j] = countM + 90;
                   }
               }
            }
        }
    }

    public static void revealMines() {
        for (int i = 0; i < gameMatrix.length; i++) {
            for (int j = 0; j < gameMatrix[i].length; j++) {
                if (gameMatrix[i][j] == 9 || gameMatrix[i][j] == 11) {
                    gameMatrix[i][j] = 21;
                }
            }
        }
    }

    public static void printField(int[][] gameMatrix) {
        StringBuilder result = new StringBuilder();
        result.append(" |123456789|\n").append("-|---------|\n");
        int cntF = 0;
        for (int[] intArr : gameMatrix) {
            cntF++;
            result.append(cntF).append("|");
            for (int i : intArr) {
                result.append(gameChars.get(i));
            }
            result.append("|\n");
        }
        result.append("-|---------|");
        System.out.println(result);
    }

    public static boolean checkWin() {
        for (int[] intArr : gameMatrix) {
            for (int i : intArr) {
                if (i == 9) {
                    return false;
                }
                if (i == 10) {
                    return false;
                }
                if (i > 80 && i < 89) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkWinFull() {
        for (int[] intArr : gameMatrix) {
            for (int i : intArr) {
                if (i == 0 || i == 10 || (i > 80 && i < 89) || (i > 90 && i < 99)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getInput() {
        Scanner scanner;
        String str;

        while(true) {
            try {
                System.out.print(Msg.USR_TURN.getLbl());
                scanner = new Scanner(System.in);
                String goToStr = scanner.nextLine();
                int x = Integer.parseInt(goToStr.split(" ")[0]) - 1;
                int y = Integer.parseInt(goToStr.split(" ")[1]) - 1;
                String mark = goToStr.split(" ")[2];
                str = goToStr;
                break;
            } catch (Exception ignored) {
            }
        }

        return str;
    }
    public static boolean usrTurn(int num) {
        String goToStr = getInput();
        int x = Integer.parseInt(goToStr.split(" ")[0]) - 1;
        int y = Integer.parseInt(goToStr.split(" ")[1]) - 1;
        String mark = goToStr.split(" ")[2];
        boolean setC = false;

        // check user's first turn (always free of mine)
        if (num == 1) {
            while (true) {
                if (gameMatrix[y][x] == 9 && mark.contains("free")) {
                    newField(SIZE, mines);
                } else {
                    break;
                }
            }
            createNumbers();
        }

        while (!setC) {

            // turn
            switch (mark) {
                case "free":
                    if (gameMatrix[y][x] == 0) {
                        gameMatrix[y][x] = 20;
                        setC = true;
                    } else if (gameMatrix[y][x] == 9) {
                        revealMines();
                        return false;
                    } else if (gameMatrix[y][x] == 11) {
                        revealMines();
                        return false;
                    } else if (gameMatrix[y][x] == 10) {
                        gameMatrix[y][x] = 20;
                        setC = true;
                    } else if (gameMatrix[y][x] > 0 && gameMatrix[y][x] < 9) {
                        setC = true;
                    } else if (gameMatrix[y][x] > 90 && gameMatrix[y][x] < 99) {
                        gameMatrix[y][x] -= 90;
                        setC = true;
                    } else {
                        setC = true;
                    }
                    fillTurn(new boolean[SIZE][SIZE], y, x);

                case "mine":
                    if (gameMatrix[y][x] == 0) {
                        gameMatrix[y][x] = 10;
                        setC = true;
                    } else if (gameMatrix[y][x] == 9) {
                        gameMatrix[y][x] = 11;
                        setC = true;
                    } else if (gameMatrix[y][x] == 11) {
                        gameMatrix[y][x] = 9;
                        setC = true;
                    } else if (gameMatrix[y][x] == 10) {
                        gameMatrix[y][x] = 0;
                        setC = true;
                    } else if (gameMatrix[y][x] > 0 && gameMatrix[y][x] < 9) {
                        setC = true;
                    } else if (gameMatrix[y][x] > 90 && gameMatrix[y][x] < 99) {
                        gameMatrix[y][x] -= 10;
                        setC = true;
                    } else if (gameMatrix[y][x] > 80 && gameMatrix[y][x] < 89) {
                        gameMatrix[y][x] += 10;
                        setC = true;
                    } else {
                        setC = true;
                    }

            }
        }


        return true;
    }

    public static void main(String[] args) {
        Scanner scanner;
        while (true) {
            try {
                scanner = new Scanner(System.in);
                System.out.print(Msg.USR_FIRST.getLbl());
                mines = scanner.nextInt();
                break;
            } catch (Exception ignore) {
            }
        }
        newField(SIZE, mines);
        printField(gameMatrix);
        usrTurn(1);

        printField(gameMatrix);
        while (!checkWin() && !checkWinFull()) {
            if(usrTurn(2)) {
                printField(gameMatrix);
            } else {
                // ending with lose
                printField(gameMatrix);
                System.out.println(Msg.USR_LOSE.getLbl());
                return;
            }
        }
        // ending with win
        System.out.println(Msg.USR_WIN.getLbl());
    }
}
