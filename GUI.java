import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Stack;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class GUI implements ActionListener {
    public static JTextPane[][] board = new JTextPane[9][9];
    public JButton button;
    public JButton button2;
    public GUI() {
        
        JFrame frame = new JFrame();

        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                board[i][j] = new JTextPane();
                board[i][j].setText("");
                board[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
                board[i][j].setFont(new Font("Arial",Font.BOLD,33));
                StyledDocument doc = board[i][j].getStyledDocument();
                SimpleAttributeSet center = new SimpleAttributeSet();
                StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                doc.setParagraphAttributes(0, doc.getLength(), center, false);
            }
        }


        button = new JButton("Submit");
        button.setBounds(101, 450, 100, 25);
        button.addActionListener(this);
        
        button2 = new JButton("Clear");
        button2.setBounds(280, 450, 100, 25);
        button2.addActionListener(this);

        JPanel panel = new JPanel();
        panel.setBackground(Color.black);
        panel.setBounds(42, 20, 400, 400);
        panel.setLayout(new GridLayout(9, 9));
        

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.add(panel);
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                panel.add(board[i][j]);
            }
        }
        frame.add(button);
        frame.add(button2);
        frame.setTitle("Sudoku Solver");
        frame.setSize(500,550);
        frame.setVisible(true);

        ImageIcon image = new ImageIcon("Test/PencilBlack.png");
        frame.setIconImage(image.getImage());

    }
    
    public class Choice {
        
    	private int row;  
        private int col;  
        private int num;
        
        public Choice() { 
        	int[][] temp = mergedGrid();
        	for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                		if(temp[r][c] == 0) {
                			this.row = r;
                			this.col = c;
                			this.num = 1;
                			break;
                		}
                }
                if(this.num == 1) break;
        	}
        }
        private Choice(int ro, int co, int number) {
        	this.row = ro;
            this.col = co;
            this.num = number;
        }
        
		public Choice nextChoice() {
        	return new Choice(this.row, this.col, this.num + 1);
        }
        
        public boolean unconsideredChoicesExist() {
        	return (this.num < 9);
        }
        
        public boolean couldLeadToSolution() {      
        	int[][] temp = mergedGrid();
        	if (this.row >= 9 || this.row < 0) return false;
        	if (this.col >= 9 || this.col < 0) return false;
        	if (this.num >= 10) return false;
        	
        	for(int i = 0; i < 9; i++) {
        		if(this.num == temp[this.row][i]) return false;
        		if(this.num == temp[i][this.col]) return false;
        	}
        	int rRange = this.row;
        	int cRange = this.col;
        	if((this.row + 1) % 3 != 0) {
                rRange = this.row - ((this.row + 1) % 3) + 3;
        	}
        	if((this.col + 1) % 3 != 0) {
        	    cRange = this.col - ((this.col + 1) % 3) + 3;
        	}
            for (int r = rRange - 2; r <= rRange; r++) {
            	for (int c = cRange - 2; c <= cRange; c++) {
            		if(this.num == temp[r][c]) { 
            			
            			return false;
            		}
            	}
            }
            
        	
            return true;
        }
        
        public String toString() {
        	String str = "";
            	str += this.row + ", " + this.col + ", " + this.num;
            return(str);
        }
    }
     
    int[][] grid;
    Stack<Choice> stack = new Stack<Choice>();
    
    public GUI(int[][] grid) {  
        this.grid = grid;
        stack = new Stack<Choice>();
    }

    public boolean solutionFound() {
    	int[][] temp = mergedGrid();
        
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if(temp[r][c] == 0) {
                	return false;
                }
            }
        }
        return true;
    }
    
    private void printSolution() {
    	int[][] temp = mergedGrid();
    	for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
            	board[r][c].setText("" + temp[r][c]);
            }
    	}
    }
    
    private int[][] mergedGrid() {
        int[][] mergedGrid = new int[9][9];
        
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                mergedGrid[r][c] = grid[r][c];
            }
        }
        
        for (Choice choice : stack) {                          
            mergedGrid[choice.row][choice.col] = choice.num;   
        }                                                      
        
        return mergedGrid;
    }
    
    
    public int solve() {  

        int numSolutions = 0;
        
        Choice choice = new Choice();               

        while (true) {
            
            if (choice.couldLeadToSolution()) {
                stack.push(choice);
                choice = new Choice();              
            } 
            else if (choice.unconsideredChoicesExist()) {
                choice = choice.nextChoice();
            }
            else {        
                if (stack.isEmpty()) break;           
                choice = stack.pop().nextChoice();    
            }   
            
            if (solutionFound()) {    
                printSolution();  
                numSolutions++;
                choice = stack.pop();                 
                choice = choice.nextChoice();         
                break;
            }     
        }
        return numSolutions;
    }

    public void actionPerformed(ActionEvent e) {
        int[][] grid = new int[9][9];
        HashSet<String> set = new HashSet<String>();
        int stop = 0;
        int repeat = 0;
        if(e.getSource() == button){
            for(int i = 0; i < 9; i++){
                for(int j = 0; j < 9; j++){
                    if(board[i][j].getText().equals("")){
                        grid[i][j] = 0;
                    }else if(board[i][j].getText().length() == 1 && board[i][j].getText().charAt(0) >= 49 && board[i][j].getText().charAt(0) <= 57){ 
                        char check = board[i][j].getText().charAt(0);
                        if (!set.add(check + " in row " + i) || !set.add(check + " in column " + j) || !set.add(check + " in block " + i/3 + "-" + j/3)){
                            repeat++;   
                        }
                        grid[i][j] = Integer.parseInt(board[i][j].getText());
                    }else{
                        stop++;
                    }
                }
            }
            if(stop == 0){
                if(repeat == 0){
                    GUI solver = new GUI(grid);
                    int numSolutions = solver.solve();
                    if (numSolutions == 0) System.out.println(System.lineSeparator() + "No solution exists");
                }else{
                    System.out.println("The numbers you entered have invalid placement.");
                }
            }else{
                System.out.println("You entered an invalid value. Only enter numbers from 1 to 9.");
            }
        }
        if(e.getSource() == button2){
            for(int i = 0; i < 9; i++){
                for(int j = 0; j < 9; j++){
                    board[i][j].setText("");
                }
            }
        }
    }

    public static void main(String[] args) {
        new GUI();
    }
}
