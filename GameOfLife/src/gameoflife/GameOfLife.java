
package gameoflife;

import java.io.*;
import java.util.Scanner;
import java.awt.*; //needed for graphics
import javax.swing.*; //needed for graphics
import static javax.swing.JFrame.EXIT_ON_CLOSE; //needed for graphics
import java.util.Random;

public class GameOfLife extends JFrame {

    //FIELDS
    int rowNum = 100;
    int colNum = 100;
    
    boolean alive[][] = new boolean[rowNum][colNum]; //Create 2-d array to store data
    boolean aliveNext[][] = new boolean[rowNum][colNum]; //Create 2-d array to store data
    
    int numGenerations = 1000;
    int currGeneration = 1;
    
    String fileName = "initialCells.txt";
    int width = 800; //width of the grid in pixels
    int height = 800; //height of the grid in pixels
    int borderWidth = 50;

    int cellSizeX = width/rowNum; //cell size depends on the width of the grid and the total cell number
    int cellSizeY = height/colNum;
    
    int labelX = width / 2;
    int labelY = borderWidth;
 
    int numRandomCells = 700;
    
    //METHODS
    
    //Creates the first generation of cells
    public void plantFirstGeneration() throws IOException {
        makeEveryoneDead();
        plantFromFile( fileName );
        //plantRandomly();
       
    }
    
    //Sets all cells to dead
    public void makeEveryoneDead() {
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                alive[i][j] = false;
            }
        }
    }

    
    //reads the first generations' alive cells from a file
    public void plantFromFile(String fileName) throws IOException {

        FileReader f = new FileReader(fileName);
        Scanner s = new Scanner(f);

        int row, col;
        
        while (s.hasNext()){
            row = s.nextInt();
            col = s.nextInt();
            alive[row][col] = true;
            
        }

    }
        //Plants random cells
        public void plantRandomly() throws IOException {
        Random generator = new Random();
        int row, col;
        for (int i = 0; i < numRandomCells; i++){
            row = generator.nextInt(rowNum);
            col = generator.nextInt(colNum);
            alive[row][col] = true;
        }
    }
    
    //Applies the rules of The Game of Life to set the true-false values of the aliveNext[][] array,
    //based on the current values in the alive[][] array
    public void computeNextGeneration() {
        int i, j;
        int count;
        
        for ( i = 0; i < rowNum; i++) {
            for ( j = 0; j < colNum; j++) {
                aliveNext[i][j] = alive[i][j];
            }
            
        }
        for (i = 0; i<rowNum; i++){
            for (j = 0; j < colNum; j++) {
                count = countLivingNeighbors(i, j);
                if (alive[i][j] == true){
                    if (count < 3 || count > 4){
                        aliveNext[i][j]=false;
                    }
                }
                else if (alive[i][j] == false) {
                    if (count == 3 ){
                        aliveNext[i][j]=true;
                    }
                }
            }
        }
    }

    
    //Overwrites the current generation's 2-D array with the values from the next generation's 2-D array
    public void plantNextGeneration() {
        
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0;  j< colNum; j++) {
                alive[i][j] = aliveNext[i][j];
                
            }
            
        }
        currGeneration = currGeneration + 1;
    }

    
    //Counts the number of living cells adjacent to cell (i, j)
    public int countLivingNeighbors(int i, int j) {
        int count = 0;
        int a, b, c, d;
        if (i == 0) { //Counts different neighbors differently if the cell is on the edge or in a corner
            a = 0;
            b = 1;
        } 
        else if (i == rowNum - 1) {
            a = -1;
            b = 0;
        } 
        else {
            a = -1;
            b = 1;
        }
        
        if (j == 0) {
            c = 0;
            d = 1;
        }
        else if (j == colNum - 1){
            c = -1;
            d = 0;
        }
        else {
            c = -1;
            d = 1;
        }
        for (int k = a; k <= b; k++) {
            for (int l = c; l <= d; l++){
                if (alive[i+k][j+l] == true){
                    count ++;
                }
                               
            }
  
        }
         return count; 
    }


    //Displays the statistics at the top of the screen
    void drawLabel(Graphics g, int state) {
        g.setColor(new Color(232, 250, 195));
        g.fillRect(0, 0, width, borderWidth);
        g.setColor(new Color(82, 183, 250));
        g.drawString("Generation: " + state, labelX, labelY);
    }

        //Makes the pause between generations
    public static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } 
        catch (Exception e) {}
    }

    //Draws the current generation of living cells on the screen
    public void paint(Graphics g) {
        int x, y, i, j;
        
        x = borderWidth;
        y = cellSizeY/2 + borderWidth; 
        
        drawLabel(g, currGeneration);
        
        //Color livingCell = new Color(160, 252, 131);
        for (i = 0; i < rowNum; i++) {
            //Fill this in
            
            for (j = 0; j < colNum; j++) {
                if (alive[i][j] == true) {
                    g.setColor( new Color(160, 252, 131) );
                } 
                else {
                    g.setColor( new Color(232, 250, 195) );
                }
                        
                
                g.fillRect(x, y, cellSizeX, cellSizeY);

                g.setColor( Color.white );
                g.drawRect(x, y, cellSizeX, cellSizeY);
                x += cellSizeX;                
               //Fill this in
            }
            
            x = borderWidth;
            y += cellSizeY;
            
        }
    }


    //Sets up the JFrame screen
    public void initializeWindow() {
        setTitle("Game of Life Simulator");
        setSize(height+100, width+100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(new Color(232, 250, 195));
        setVisible(true); //calls paint() for the first time
    }
    
    
    //Main algorithm
    public static void main(String args[]) throws IOException {

        GameOfLife currGame = new GameOfLife();
        currGame.plantFirstGeneration();
        currGame.initializeWindow();
        sleep(1000);
         //Sets the initial generation of living cells, either by reading from a file or creating them algorithmically

        while (true) {
            currGame.computeNextGeneration();
            currGame.plantNextGeneration();
            currGame.repaint();
            sleep(50); 
        }
    } //end of main()
    
} //end of class
