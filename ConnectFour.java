import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;

/**
 * The main class, contains the main method and the whole connect four program.
 */
public class ConnectFour {
    /**
     * The number of columns in the board.
     */
    private static final int COLUMNS = 7;

    /**
     * The number of rows in the board.
     */
    private static final int ROWS = 6;

    /**
     * The game board which houses all the player moves.
     */
    private static int[][] board = new int[ROWS][COLUMNS];

    /**
     * The current player.
     */
    private static int player = 1;

    private static Color defaultLabelColor;

    /**
     * The menu item that saves the game.
     */
    private static JMenuItem saveMenuItem = new JMenuItem("Save");

    /**
     * The menu item that loads a save.
     */
    private static JMenuItem loadMenuItem = new JMenuItem("Load");

    /**
     * The menu item that resets the game.
     */
    private static JMenuItem resetMenuItem = new JMenuItem("Reset");

    /**
     * Shows the current player and their color.
     */
    private static JLabel currentPlayerLabel = new JLabel("PLAYER 1's TURN");

    /**
     * The column buttons.
     */
    private static JButton[] buttons = new JButton[COLUMNS];

    /**
     * The individual slots in the board (represented by a color changing JLabel).
     */
    private static JLabel[][] gridLabels = new JLabel[ROWS][COLUMNS];

    /**
     * The JPanel which houses both the gridLabels and the column buttons.
     */
    private static JPanel gamePanel = new JPanel(new BorderLayout());

    /**
     * The size of each of the slots.
     */
    private static final Dimension BUTTON_DIMENSION = new Dimension(75, 35);

    /**
     * The action listener for each of the menu bar items.
     */
    private static ActionListener menuListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == loadMenuItem) {
                loadSave();
            } else if (actionEvent.getSource() == saveMenuItem) {
                serializeGame();
            } else {
                resetGame();
            }
        }
    };

    /**
     * The action listener applied to the buttons at the top of each column.
     */
    private static ActionListener buttonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            for (int i = 0; i < COLUMNS; i++) {
                if (actionEvent.getSource() == buttons[i]) {
                    play(i);
                    int res = checkForWin();
                    System.out.println(res);
                    if (res != 0) {
                        JOptionPane.showMessageDialog(gamePanel, (res == -1 ? "TIE, NO ONE WINS" : ("PLAYER " + res + " WINS.")) + "THE GAME WILL RESET", "Connect Four", JOptionPane.INFORMATION_MESSAGE, null);
                        resetGame();
                    }
                }
            }
        }
    };

    /**
     * The action listener applied to each of the connect four "slots" (labels).
     */
    private static MouseListener gridListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            int column = ((JLabel) mouseEvent.getSource()).getName().charAt(0) - '0';
            play(column);
            int res = checkForWin();
            if (res != 0) {
                JOptionPane.showMessageDialog(gamePanel, (res == -1 ? "TIE, NO ONE WINS" : ("PLAYER " + res + " WINS.")) + "THE GAME WILL RESET", "Connect Four", JOptionPane.INFORMATION_MESSAGE, null);
                resetGame();
            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            //not interested
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            //not interested
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            //not interested
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            //not interested
        }
    };


    /**
     * Resets the game.
     */
    private static void resetGame() {
        board = new int[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                gridLabels[i][j].setBackground(defaultLabelColor);
            }
        }
        currentPlayerLabel.setText("PLAYER 1's TURN");
        currentPlayerLabel.setBackground(Color.RED);
        player = 1;
    }

    /**
     * Sets up many of the Swing components, and makes the main window visible.
     */
    private static void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLUMNS));
        JFrame mainWindow = new JFrame("Connect Four Test");

        JMenu fileMenu = new JMenu("File");
        JMenuBar menuBar = new JMenuBar();

        JPanel buttonPanel = new JPanel(new GridLayout(1, COLUMNS));

        gamePanel.setOpaque(false);

        for (int i = 0; i < COLUMNS; i++) {
            buttons[i] = new JButton(Integer.toString(i + 1));
            buttons[i].addActionListener(buttonListener);
            buttons[i].setSize(BUTTON_DIMENSION);
            buttons[i].setPreferredSize(BUTTON_DIMENSION);
            buttons[i].setMaximumSize(BUTTON_DIMENSION);
            buttonPanel.add(buttons[i]);
        }

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                gridLabels[i][j] = new JLabel();
                gridLabels[i][j].setName(Integer.toString(j));
                gridLabels[i][j].setBorder(new LineBorder(Color.BLACK, 1));
                gridLabels[i][j].addMouseListener(gridListener);
                gridLabels[i][j].setOpaque(true);
                gridLabels[i][j].setEnabled(true);
                gridPanel.add(gridLabels[i][j]);
            }
        }
        defaultLabelColor = gridLabels[0][0].getBackground();

        gamePanel.add(buttonPanel, BorderLayout.NORTH);
        gamePanel.add(gridPanel, BorderLayout.CENTER);

        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.add(resetMenuItem);
        menuBar.add(fileMenu);

        loadMenuItem.addActionListener(menuListener);
        loadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        saveMenuItem.addActionListener(menuListener);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        resetMenuItem.addActionListener(menuListener);
        resetMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));

        menuBar.setPreferredSize(new Dimension(300, 20));

        JPanel topDisplay = new JPanel(new BorderLayout());
        topDisplay.setOpaque(false);

        currentPlayerLabel.setOpaque(true);
        currentPlayerLabel.setBackground(Color.RED);

        topDisplay.add(menuBar, BorderLayout.WEST);
        topDisplay.add(currentPlayerLabel, BorderLayout.EAST);
        mainPanel.add(topDisplay, BorderLayout.NORTH);
        mainPanel.add(gamePanel, BorderLayout.CENTER);

        mainWindow.add(mainPanel);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setSize(new Dimension(500, 500));
        mainWindow.setVisible(true);
    }

    /**
     * Makes a play at the given column.
     *
     * @param column the column to place the current player's piece
     */
    private static void play(int column) {
        for (int i = ROWS - 1; i >= 0; i--) {
            if (board[i][column] == 0) {
                board[i][column] = player;
                gridLabels[i][column].setBackground(player == 1 ? Color.RED : Color.YELLOW);
                player = (player == 1) ? 2 : 1;
                currentPlayerLabel.setText("PLAYER " + player + "'s TURN");
                currentPlayerLabel.setBackground(player == 1 ? Color.RED : Color.YELLOW);

                return;
            }
        }
    }

    /**
     * Saves the current game using object serialization.
     */
    private static void serializeGame() {
        try (FileOutputStream fileOutputStream = new FileOutputStream("connectFourSave.ser");
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {

            //writes object
            objectOutputStream.writeObject(board);
        } catch (FileNotFoundException nfe) {
            System.err.println("Necessary file not found.");
            java.awt.Toolkit.getDefaultToolkit().beep();
        } catch (IOException ioe) {
            System.err.println("Unexpected I/O Error. Keeping previous save.");
        }
    }

    /**
     * Loads a save file.
     */
    private static void loadSave() {
        try (FileInputStream fileInputStream = new FileInputStream("connectFourSave.ser");
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            handleLoad(objectInputStream.readObject());
        } catch (FileNotFoundException nfe) {
            System.err.println("Necessary file not found. Keeping defaults...");
            java.awt.Toolkit.getDefaultToolkit().beep();
        } catch (EOFException eof) {
            //silent
        } catch (IOException ioe) {
            System.err.println("Unexpected I/O Error. Keeping defaults...");
            java.awt.Toolkit.getDefaultToolkit().beep();
        } catch (ClassNotFoundException e) {
            System.err.println("Couldn't properly parse necessary file. Keeping defaults...");
        }
    }

    /**
     * Ensure the game resumes from a save correctly.
     *
     * @param boardObject the object (int[][]) representing the game board
     */
    private static void handleLoad(Object boardObject) {
        board = (int[][]) boardObject;

        int oneCount = 0;
        int twoCount = 0;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                gridLabels[i][j].setBackground(defaultLabelColor);
                if (board[i][j] == 1) {
                    oneCount++;
                    gridLabels[i][j].setBackground(Color.RED);
                } else if (board[i][j] == 2) {
                    twoCount++;
                    gridLabels[i][j].setBackground(Color.YELLOW);
                }
            }
        }

        player = (oneCount > twoCount) ? 2 : 1;
        currentPlayerLabel.setText("PLAYER " + player + "'s TURN");
        currentPlayerLabel.setBackground(player == 1 ? Color.RED : Color.YELLOW);
    }

    private static void printGrid() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Returns the value of a position on the game board.
     *
     * @param y the y-position of the board
     * @param x the x-position of the board
     * @return the value of the board at pos x,y (either 0, 1, or 2)
     */
    private static int pieceOnBoard(int y, int x) {
        return (y < 0 || x < 0 || y >= 6 || x >= COLUMNS) ? 0 : board[y][x];
    }

    /**
     * Checks the board for four-in-a-row.
     *
     * @return -1 for a tie, 0 for nothing, 1 or 2 for the winning player
     */
    private static int checkForWin() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                if (pieceOnBoard(y, x) != 0 && pieceOnBoard(y, x) == pieceOnBoard(y, x + 1)
                        && pieceOnBoard(y, x) == pieceOnBoard(y, x + 2)
                        && pieceOnBoard(y, x) == pieceOnBoard(y, x + 3)) {
                    return pieceOnBoard(y, x);
                }
            }
        }

        //Vertical check
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                if (pieceOnBoard(y, x) != 0 && pieceOnBoard(y, x) == pieceOnBoard(y + 1, x)
                        && pieceOnBoard(y, x) == pieceOnBoard(y + 2, x)
                        && pieceOnBoard(y, x) == pieceOnBoard(y + 3, x)) {
                    return pieceOnBoard(y, x);
                }
            }
        }

        //Diagonal check
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                //diagonal search
                for (int d = -1; d <= 1; d += 2)
                    if (pieceOnBoard(y, x) != 0 && pieceOnBoard(y, x) == pieceOnBoard(y + d, x + 1)
                            && pieceOnBoard(y, x) == pieceOnBoard(y + 2 * d, x + 2)
                            && pieceOnBoard(y, x) == pieceOnBoard(y + 3 * d, x + 3)) {

                        return pieceOnBoard(y, x);
                    }
            }
        }

        //None
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                if (pieceOnBoard(y, x) == 0) return 0;
            }
        }

        //tie
        return -1;
    }

    /**
     * The main method, launches the GUI.
     *
     * @param args the input arguments (unused)
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            System.err.println("ERROR SETTING LOOK AND FEEL");
        }

        //Creates GUI on Event Dispatching Thread
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                //starts GUI
                initComponents();
            }
        });
    }
}
