
package contamination.simulation;

import java.io.*;
import java.util.Scanner;
import java.awt.*; //needed for graphics
import javax.swing.*; //needed for graphics
import static javax.swing.JFrame.EXIT_ON_CLOSE; //needed for graphics
import java.util.Random;


public class ContaminationSimulation extends JFrame {
    //FIELDS
    int rowNum = 20;
    int colNum = 20;
   
    Color[][] colour = new Color[rowNum][colNum]; //Using colour instead of booleans to store different cells
    Color[][] colourNext = new Color[rowNum][colNum];
    
    int numGenerations = 1000;
    int currGeneration = 1;
    
    Color level1 = new Color (212, 250, 57);
    Color level2 = new Color (167, 250, 57);
    Color level3 = new Color (113, 253, 108);
    Color contaminated1 = new Color (250, 170, 172);
    Color contaminated2 = new Color (253, 108, 128);
    Color mutation  = new Color (108, 152, 253);
    Color[] CellColor = {level1, level2, level3, contaminated1, contaminated2, mutation};
   
    String fileName = "initialCells.txt";
    int width = 600; //width of the window in pixels
    int height = 600;
    int borderWidth = 50;

    int cellSizeX = width/rowNum; 
    int cellSizeY = height/colNum; 
    
    int labelX = width / 2;
    int labelY = borderWidth;
 
    int numRandomCells = 20;
    
    Color background = Color.white;
    
    Random generator = new Random();
    
    //METHODS
    public void plantFirstGeneration() throws IOException {
        makeEveryoneDead();
        plantFromFile( fileName );
        //plantRandomly();
       
    }

    
    //Sets all cells to dead
    public void makeEveryoneDead() {
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                colour[i][j] = background;
            }
        }
    }

    //reads the first generations' alive cells from a file
    public void plantFromFile(String fileName) throws IOException {
        FileReader f = new FileReader(fileName);
        Scanner s = new Scanner(f);

        int row, col;
        int color;
        
        while (s.hasNext()){
            row = s.nextInt();
            col = s.nextInt();
            color = s.nextInt();
            colour[row][col] = CellColor[color];
            
        }

    }
    
    // plants the first generation randomly
    public void plantRandomly() throws IOException {
        int row, col, cellColor;
        for (int i = 0; i < numRandomCells; i++){
            row = generator.nextInt(rowNum);
            col = generator.nextInt(colNum);
            cellColor = generator.nextInt(CellColor.length-2);
            colour[row][col] = CellColor[cellColor];
        }
    }
    
    
    //Applies the rules of Contamination to set the true-false values of the colourNext[][] array,
    //based on the current values in the colour[][] array
    public void computeNextGeneration() {
        int i, j;
        int countLivingCell, countInfected, countMutation, countStage1;
        
        for ( i = 0; i < rowNum; i++) {
            for ( j = 0; j < colNum; j++) {
                colourNext[i][j] = colour[i][j];
            }
        }
        
        for (i = 0; i<rowNum; i++){
            for (j = 0; j < colNum; j++) {
                countLivingCell = countLivingNeighbors(i, j)[0];
                countInfected = countLivingNeighbors(i, j)[1];
                countMutation = countLivingNeighbors(i, j)[2];
                countStage1 = countLivingNeighbors(i, j)[3];
                if (colour[i][j] == background){ //If the cell is initially dead, it may come to life because of reproduction
                   reproduction(countLivingCell, countInfected, countStage1, countMutation, i ,j);
                }
                else{ //If the cell is initially alive 
                    infection(countInfected, i, j); //healthy cells may be infected
                    deterioration(i, j); //infected cell may deteriorate, die or mutate
                }
            }
        }
    }
    
    // Determines the colour of the new born cell
    public void reproduction(int countLivingCell, int countInfected, int countStage1, int countMutation, int i, int j) {

        if (countLivingCell == 2 ){ // Exactly two cells can reproduce a new cell
            if (countMutation >= 1){ // If one of the parent cell is blue, the offspring will also be blue
                colourNext[i][j] = CellColor[5];
            }
            else if (countInfected == 0){ // Else if both parent cells are green, the offspring will also be green
                colourNext[i][j] = CellColor[generator.nextInt(3)];
            }
            else if (countLivingCell == countStage1){ // Else if both parent cells are pink, the offspring will also be pink or be blue
                int chance2 = generator.nextInt(100);
                if (chance2 == 0 & currGeneration >= 100){ 
                    mutation(i ,j);
                }
                else {
                    colourNext[i][j] = CellColor[3];
                }
            }
            else{ // If one parent cell is green and one is red, the offspring can either be green or red
                colourNext[i][j] = CellColor[generator.nextInt(5)];
            }
        }

    }
    
    //Determines if a cell will be infected in the next generation
    public void infection(int countInfected, int i, int j){
        int chance1 = 10;
        if (countInfected >= 1){ //If a healthy cell has at least pink/red cell surrounding it, it may be infected
             //Cells with different colours of green have different possibilities of being infected
            if (colour[i][j] == CellColor[0]){
                chance1 = generator.nextInt(1);
            }
            else if (colour[i][j] == CellColor[1]){
                chance1 = generator.nextInt(2);
            }
            else if (colour[i][j] == CellColor[2]){
                chance1 = generator.nextInt(5);
            }
        }
        if (chance1 == 0){ //Change the cell colour in the next generation based on the result determined
            colourNext[i][j] = CellColor[3];
        }
    } 
    
    // Determines if a cell will deteriorate in the next generation
    public void deterioration(int i, int j){
        int chance2 = generator.nextInt(3);
        
        if (colour[i][j] == CellColor[3]){ // If a cell is pink, it has a chance of turning red
            if (chance2 == 0){
                colourNext[i][j] = CellColor[4];
            }
        }
        else if (colour[i][j] == CellColor[4]){// If a cell is red, it dies in the next generation
            colourNext[i][j] = background;
        }        
    }
    
    // Determines if a cell will be mutated
    public void mutation(int i, int j){
  
        colourNext[i][j] = CellColor[5];
            
    }
    
    //Overwrites the current generation's 2-D array with the values from the next generation's 2-D array
    public void plantNextGeneration() {
        
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0;  j< colNum; j++) {
                colour[i][j] = colourNext[i][j];
                
            }
            
        }
        currGeneration = currGeneration + 1;
    }

    
    //Counts the number of living cells adjacent to cell (i, j)
    public int[] countLivingNeighbors(int i, int j) {
        int countLiveCell = 0;
        int countStage1 = 0;
        int countInfected = 0;
        int countMutation = 0;
        int a, b, c, d;
        if (i == 0) {
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
                if (colour[i+k][j+l] != background){ //Counts all of the living cells 
                    countLiveCell ++;
                }
                if (colour[i+k][j+l] == CellColor[3]){ //Counts all of the pink cells
                    countStage1 ++;
                }
                if (colour[i+k][j+l] == CellColor[3] || colour[i+k][j+l] == CellColor[4]){ //Counts all of the pink and red cells
                    countInfected ++;
                }
                if (colour[i+k][j+l] == CellColor[5] ){ //Counts all of the blue cells
                    countMutation ++;
                }
                               
            }
  
        }
        int[] count = {countLiveCell, countInfected, countMutation, countStage1};
        return count; 
    }


    //Makes the pause between generations
    public static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } 
        catch (Exception e) {}
    }

    
    //Displays the statistics at the top of the screen
    void drawLabel(Graphics g, int state) {
        g.setColor(background);
        g.fillRect(0, 0, width, borderWidth);
        g.setColor(new Color (245, 205, 49));
        g.drawString("Generation: " + state, labelX, labelY);
    }

    
    //Draws the current generation of living cells on the screen
    @Override
    public void paint(Graphics g) {
        int x, y, i, j;
        
        x = borderWidth;
        y = cellSizeY/2 + borderWidth; 
        
        drawLabel(g, currGeneration);
        
        //Color livingCell = new Color(160, 252, 131);
        for (i = 0; i < rowNum; i++) {
            //Fill this in
            
            for (j = 0; j < colNum; j++) {

                g.setColor( colour[i][j] );

                        
                
                g.fillRect(x, y, cellSizeX, cellSizeY);

                g.setColor( new Color(245, 205, 49));
                g.drawRect(x, y, cellSizeX, cellSizeY);
                x += cellSizeX;                
               //Fill this in
            }
            
            x = borderWidth;
            y += cellSizeY;
            //Fill this in
        }
    }

    //Sets up the JFrame screen
    public void initializeWindow() {
        setTitle("Game of Life Simulator");
        setSize(height+100, width+100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(background);
        setVisible(true); //calls paint() for the first time
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        ContaminationSimulation currGame = new ContaminationSimulation();
        currGame.plantFirstGeneration();
        currGame.initializeWindow();
        sleep(1000);
         //Sets the initial generation of living cells, either by reading from a file or creating them algorithmically

        for (int i = 1; i <= currGame.numGenerations; i++) {
            currGame.computeNextGeneration();
            currGame.plantNextGeneration();
            currGame.repaint();
            sleep(200);
        }
        
    }

}