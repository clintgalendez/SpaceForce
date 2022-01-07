import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class spaceForce {
    static List<String> boardPlayer = new ArrayList<>();
    static List<String> boardEnemy = new ArrayList<>();
    static List<Integer> playerPos = new ArrayList<>();
    static List<Integer> cpuPos = new ArrayList<>();
    
    static Scanner userScanner = new Scanner(System.in);
    static String userInput = "";
    static String playerName = "";

    static int PlayerChosenPlace = 0;
    static int userCol;
    static int userRow;
    static int cpuCol;
    static int cpuRow;
    static int CPUchosenPlace;
    static int shipStatusPlayer = 20;
    static int shipStatusCPU = 20;
    static int currentScore = 0;
    static int comboScore = 1;

    static boolean isPlayerTurn = true;

    public static void updateBoard(List<String> board, boolean sceneGame) {
        
        if (isPlayerTurn & sceneGame) {
            System.out.println(("                     (PLAYER TURN)"));
        } else if (sceneGame) {
            System.out.println(("                      (ENEMY TURN)"));
        } 

        //Print out the column guide
        System.out.println(("   | 0 || 1 || 2 || 3 || 4 || 5 || 6 || 7 || 8 || 9 |"));

        int j = 0;
        for (int i = 0; i < 10; i++) {
            System.out.print(i+"  ");
            for (int k = 0; k < 10 && j < board.size(); k++, j++) {
                System.out.print("| "+board.get(j)+" |");
            }

            System.out.println();
        }
        System.out.println("-----------------------------------------------------");
        return;
    }

    public static void inputInitialValueToBoard() {
        boardPlayer.clear();
        boardEnemy.clear();

        for (int i = 0; i < 100; i++) {
            boardPlayer.add(" ");
            boardEnemy.add(" ");
        } 
    }

    public static void inputCheck() {
        System.out.print("Please Input Coloumn: ");
        do {
            while (true) {
                try {
                    userCol = Integer.parseInt(userScanner.nextLine());
                    break;
                } catch (NumberFormatException nfe) {
                    System.out.print("The Input is not a number please try again: ");
                }
            }          
            
            if (userCol > 9 | userCol < 0) System.out.print("The Input is not an available move please input the coloumn again: ");
        } while (userCol > 9 | userCol < 0);

        System.out.print("Please Input Row: ");
        do {
            while (true) {
                try {
                    userRow = Integer.parseInt(userScanner.nextLine());
                    break;
                } catch (NumberFormatException nfe) {
                    System.out.print("The Input is not a number please try again: ");
                }
            }          
            
            if (userRow > 9 | userRow < 0) System.out.print("The Input is not an available move please input the row again: ");
        } while (userRow > 9 | userRow < 0);
        
        PlayerChosenPlace = Integer.parseInt(Integer.toString(userRow) + Integer.toString(userCol));
        

        while (PlayerChosenPlace >= boardPlayer.size() || boardPlayer.get(PlayerChosenPlace).equals("@") || boardPlayer.get(PlayerChosenPlace).equals("X") || boardPlayer.get(PlayerChosenPlace).equals("-")) {
            System.out.println("Error! the inputted move is not in the board or already taken, please input again: ");
            inputCheck();
        }
    }

    public static void cpuMove() {
        Random randomPlace = new Random();

        cpuCol = randomPlace.nextInt(9);
        cpuRow = randomPlace.nextInt(9);

        CPUchosenPlace = Integer.parseInt(Integer.toString(cpuRow) + Integer.toString(cpuCol));
        while (cpuPos.contains(CPUchosenPlace) || boardEnemy.get(CPUchosenPlace).equals("X") || boardEnemy.get(CPUchosenPlace).equals("-")) {
            cpuMove();
        }     
    }

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else {
                System.out.print("\033\143");
            }
        } catch (IOException | InterruptedException ex) {}
    }

    public static void gamePlan() throws FileNotFoundException {
        File instructions = new File("instructions.txt");
        clearConsole();
        readFile(instructions);
        nextDial();
        inputInitialValueToBoard();

        //Player Planning for Ship Placement
        for (int i = 1; i < 21; i++) {
            clearConsole();
            updateBoard(boardPlayer, false);
            System.out.println("Where will you put ship #" + i +"?");

            inputCheck();
            boardPlayer.set(PlayerChosenPlace, "@");
            playerPos.add(PlayerChosenPlace);
        }

        //CPU Planning for Ship Placement
        for (int i = 1; i < 21; i++) {
            cpuMove();
            cpuPos.add(CPUchosenPlace);
        }
        mainGame();
    }
    
    public static void gameScore(boolean defeatEnemyShip, boolean changeScore) {
        //System.out.println("For Debugging Purposes <enemy>: " + Arrays.toString(cpuPos.toArray()));
        //System.out.println("For Debugging Purposes <Player>: " + Arrays.toString(playerPos.toArray()));
        if (defeatEnemyShip && changeScore) {
            System.out.println("Current Score: "+ currentScore + " (+"+ 10 +" x"+comboScore+")");
            currentScore += 10 * comboScore;
            comboScore++;
        } else if (changeScore){
            System.out.println("Current Score: "+ currentScore + " (-"+ 2 +")");
            comboScore = 1;
            currentScore -= 4;
        } else {
            System.out.println("Current Score: " + currentScore);
        }
        System.out.println("Available Ship for Player: " + shipStatusPlayer);
        System.out.println("Available Ship for CPU: " + shipStatusCPU);
    }

    public static void mainGame() throws FileNotFoundException {
        File enemyTauntFile = new File("enemyTaunt.txt");
        File animationFile = new File("animation.txt");
        File good = new File("winEnding.txt");
        File bad = new File("drawEnd.txt");
        File draw = new File("badEnding.txt");
        int move = 0;
        inputInitialValueToBoard();

        while (checkWinner().equals("")) {
            if (isPlayerTurn) {
                move++;
                clearConsole();
                showMessageBox("");
                updateBoard(boardPlayer, true);
                gameScore(false, false);
                inputCheck();
                clearConsole();

                showMessageBox("");
                moveCheck(boardPlayer, cpuPos, PlayerChosenPlace);
                System.out.println("You bombed, Coloumn: "+userCol+" And Row: "+userRow);
                waitTime(3);
            } else {
                clearConsole();
                readFile(animationFile);
                waitTime(2);

                clearConsole();

                cpuMove();
                showMessageBox(chooseRandomLine(enemyTauntFile));
                moveCheck(boardEnemy, playerPos, CPUchosenPlace);

                System.out.println("The enemy bombed, Coloumn: "+cpuCol+" And Row: "+cpuRow);
                waitTime(10);
            }
            isPlayerTurn = !isPlayerTurn;
        }

        if (checkWinner().equals("player")) {
            clearConsole();
            readFile(good);
            System.out.println("You've won! with total points of: " + (currentScore - move));
        } else if (checkWinner().equals("enemy")) {
            clearConsole();
            for (int i = 0; i < boardEnemy.size(); i++) {
                if (boardEnemy.get(i).equals("")) {
                    currentScore--;
                }
            }
            readFile(bad);
            System.out.println("Sorry, You lost! With the total points of: " + (currentScore - move));
        } else {
            readFile(draw);
            System.out.println("It's a draw! Score:" + currentScore + 5);
        }
    }
    
    public static void waitTime(int time) {
        try {
            Thread.sleep(time*1000);
        }
        catch(InterruptedException ex) {
            System.out.println("For some odd reason, timer is broken. Closing...");
            Thread.currentThread().interrupt();
        }
    }

    public static void destroyEnemySurrounding(List<String> board, List<Integer> posStorage, int index) {
        int east = index + 1;
        int southEast = index + 11;
        int south = index + 10;
        int SouthWest = index + 9;
        int west = index - 1;
        int northWest = index - 11;
        int north = index - 10;
        int northEast = index - 9;

        for (int i = 0; i < 8; i++) {
            switch(i) {
                case 0:
                    if (east < board.size() & east > 0) {
                        if (!board.get(east).equals("-") & !board.get(east).equals("X") & !posStorage.contains(east)) {
                            board.set((east), "-");
                        }
                    }
                    break;
                case 1:
                    if (southEast < board.size() & southEast > 0) {
                        if (!board.get(southEast).equals("-") & !board.get(southEast).equals("X") & !posStorage.contains(southEast)) {
                            board.set((southEast), "-");
                        }
                    }
                    break;
                case 2:
                    if (south < board.size() & south > 0) {
                        if (!board.get(south).equals("-") & !board.get(south).equals("X") & !posStorage.contains(south)) {
                            board.set((south), "-");
                        }
                    }
                    break;
                case 3:
                    if (SouthWest < board.size() & SouthWest > 0) {
                        if (!board.get(SouthWest).equals("-") & !board.get(SouthWest).equals("X") & !posStorage.contains(SouthWest)) {
                            board.set((SouthWest), "-");
                        }
                    }
                    break;
                case 4:
                    if (west < board.size() & west > 0) {
                        if (!board.get(west).equals("-") & !board.get(west).equals("X") & !posStorage.contains(west)) {
                            board.set((west), "-");
                        }
                    }
                    break;
                case 5:
                    if (northWest < board.size() & northWest > 0) {
                        if (!board.get(northWest).equals("-") & !board.get(northWest).equals("X") & !posStorage.contains(northWest)) {
                            board.set((northWest), "-");
                        }
                    }
                    break;
                case 6:
                    if (north < board.size() & north > 0) {
                        if (!board.get(north).equals("-") & !board.get(north).equals("X") & !posStorage.contains(north)) {
                            board.set((north), "-");
                        }
                    }
                    break;
                case 7:
                    if (northEast < board.size() & northEast > 0) {
                        if (!board.get(northEast).equals("-") & !board.get(northEast).equals("X") & !posStorage.contains(northEast)) {
                            board.set((northEast), "-");
                        }
                    }
                    break;
            }
        }
        return;
    }

    public static void moveCheck(List<String> board, List<Integer> posStorage, int index) {
        if (posStorage.contains(index)) {
            board.set(index, "X");
            destroyEnemySurrounding(board, posStorage, index);
            if (isPlayerTurn == true) {
                shipStatusCPU--;
                updateBoard(board, true);
                gameScore(true, true);
            } else {
                shipStatusPlayer--;
                updateBoard(board, true);
                gameScore(false, true);
            }
            posStorage.remove(posStorage.indexOf(index));
        } else {
            board.set(index, "-");
            updateBoard(board, true);
            gameScore(false, false);
        }
    }

    public static void readFile(File filetoRead) throws FileNotFoundException {
        if (filetoRead.exists() && !filetoRead.isDirectory()) {
            try (Scanner instFile = new Scanner(filetoRead)) {
                while (instFile.hasNextLine()) {
                    System.out.println(instFile.nextLine());
                }
            }
        } else {
            System.out.println("Error! Text file, not found. Please include to download the text file and put it on the same folder as the java program.");
            return;
        }
    }

    public static String chooseRandomLine(File enemyTaunt) throws FileNotFoundException
    {
       String result = null;
       Random rand = new Random();
       int n = 0;
       for(Scanner sc = new Scanner(enemyTaunt); sc.hasNext();)
       {
            n++;
            String line = sc.nextLine();
            if (rand.nextInt(n) == 0) result = line;         
       }
  
       return result;      
    }

    public static void showMessageBox(String showText) throws FileNotFoundException {
        System.out.println("**********************Incoming Message*******************\r\n");
        System.out.print(showText+"\r\n");
        System.out.println("*********************************************************");
    }

    public static String checkWinner() {
        if (shipStatusCPU <= 0) {
            return "player";
        } else if(shipStatusPlayer <= 0) {
            return "enemy";
        } else if(shipStatusCPU <= 0 & shipStatusPlayer <= 0) {
            return "draw";
        }
        return "";
    }

    public static void nextDial() {
        System.out.print("Type 'next' to proceed: ");
        while (!userScanner.nextLine().equals("next")) {
            System.out.print("Invalid command, please try again: ");
        }
    }

    public static void cutScene() throws FileNotFoundException {
        clearConsole();
        File scene1 = new File("scene1.txt");
        File scene2 = new File("scene2.txt");

        readFile(scene1);
        nextDial();

        clearConsole();
        if (scene2.exists() && !scene2.isDirectory()) {
            try (Scanner instFile = new Scanner(scene2)) {
                while (instFile.hasNextLine()) {
                    String tempSentence = instFile.nextLine();  
                    if (tempSentence.contains(".!")) {
                        System.out.println();
                        continue;
                    }
                    if (tempSentence.contains("name")) {
                        tempSentence = tempSentence.replace("name", playerName+",");
                    }

                    if (tempSentence.contains("You:")) {
                        tempSentence = tempSentence.replace("You:", playerName+":");
                    } 
                    System.out.println(tempSentence);
                    waitTime(5);
                }
            }
        } else {
            System.out.println("Error! Text file, not found. Please include to download the text file and put it on the same folder as the java program.");
            return;
        }
        nextDial();
        gamePlan();
    }

    public static void getName() {
        System.out.print("Greetings! please insert the name of your Space Ranger to begin: ");
        playerName = userScanner.nextLine();
        while (playerName.equals("")) {
            System.out.print("The name is empty, please try again: ");
            playerName = userScanner.nextLine();
        }
        System.out.print("Logging in... ");
        waitTime(2);
        System.out.println("Granted! ");
        waitTime(1);
    }

    public static void main(String[] args) throws FileNotFoundException {
        clearConsole();

        File background = new File("background.txt");
        File credits = new File("credits.txt");

        readFile(background);

        System.out.print("Type here: ");
        String word = userScanner.nextLine().toLowerCase();
        if (word.equals("start")) {
            getName();
            System.out.println("type 'yes' to see the cutscene, type anything to skip");
            System.out.print("Type here: ");
            if (userScanner.nextLine().equals("yes")) {
                cutScene();
            } else {
                gamePlan();
            }

        } else if (word.equals("credits")) {
            clearConsole();

            readFile(credits);

            System.out.print("Type here: ");
            if (userScanner.nextLine().toLowerCase().equals("return")) {
                main(args);
            }
        }
    }
}