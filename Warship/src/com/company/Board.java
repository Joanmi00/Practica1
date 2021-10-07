package com.company;

import Utils.ConsoleColors;
import java.io.*;

public class Board {

    // Constantes del tablero
    public static final int BOARD_DIM = 10;
    public static final int BOARD_BOATS_COUNT = 5;

    // Propiedades de la clase
    private Cell cells[][];
    private Boat boats[];
    private int dimensiones[] = {5, 4, 3, 2, 2};

    private static int cont = 0;
    private static int resu = 0;
    private static boolean salir = false;

    private boolean end_game;

    public Board(){
        // Crea la matriz de celdas del tablero
        cells = new Cell[BOARD_DIM][BOARD_DIM];
        // inicializa la matriz a agua
        for (int i=0; i<BOARD_DIM; i++) {
            for (int j=0; j<BOARD_DIM; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
        end_game=false;
    }

    // comprueba si ha acabado el juego
    private void testEnd(){
        for (Boat boat : boats) {
            if (boat.getBoatState()!=Boat.BOAT_SUNKEN)
                return;
        }
        end_game=true;
    }

    public boolean getEnd_Game(){
        return end_game;
    }

    // Crea los botes y los posiciona
    public void initBoats(){
        boats = new Boat[BOARD_BOATS_COUNT];
        for (int i=0; i<BOARD_BOATS_COUNT; i++){
            boats[i] = new Boat();
            boats[i].setBoat(dimensiones[i], this);
            boats[i].viewCells(); //Muestra por pantalla la posición que ocupan
        }
    }

    //Devuelve el objeto Cell que ocupa una fila y coulmna
    public Cell getCell(int fila, int columna){
        return cells[fila][columna];
    }

    //Devuelve un valor válido dentro del tablero
    public int fitValueToBoard(int value){
        if (value <=0) return 0;
        if (value > BOARD_DIM-1) return BOARD_DIM-1;
        return value;
    }


    //El jugador lanza una bomba sobre el tablero
    public int shot(int fila, int columna){
        System.out.print(ConsoleColors.PURPLE+"---- ");

        //Sacammos el objeto Boat que hay en la celda bombardeada
        Boat boat = cells[fila][columna].getBoat();
        if (boat != null) {
            //Si en la celda hay un barco, llamamos a su método touch (tocado)
            boat.touchBoat(fila, columna);
            testEnd();

        }
        else{ // marco la casilla como disparada
            cells[fila][columna].setFired();
        }
        System.out.print(ConsoleColors.GREEN+" [" +  fila + "], [" + columna + "] --> " +
                cells[fila][columna].getContainsString());
        System.out.println(ConsoleColors.PURPLE+" ----"+ConsoleColors.RESET);

        FileWriter fileW;

        try {
            File ficher = new File("moviments_out.txt");
            fileW = new FileWriter(ficher, true);

            salir = true;
            BufferedWriter buffer = new BufferedWriter(fileW);
            buffer.write(cont + ";" + fila + ";" + columna + ";" + cells[fila][columna].getContains() + "\n");
            cont++;

            buffer.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());

        }

        return cells[fila][columna].getContains();
    }

    // indica si una cel·la ha estat o no disparada, per no repetir la jugada
    public boolean fired(int fila,int columna){
        if (cells[fila][columna].getContains()==Cell.CELL_WATER ||
                cells[fila][columna].getContains()==Cell.CELL_BOAT)
            return false;
        else
            return true;

    }

    // Para mostrar el tablero por pantalla
    public void paint(){
        // Cabecera ...
        System.out.print("      ");
        for (int k=0; k<Board.BOARD_DIM; k++){
            System.out.print(ConsoleColors.BLUE+k + " ");
        }
        System.out.println();
        char c='A';
        for (int i=0; i<Board.BOARD_DIM; i++) {
            System.out.print((ConsoleColors.BLUE+c++) + " <-- " +ConsoleColors.RESET);
            for (int j=0; j<Board.BOARD_DIM; j++) {
                System.out.print(cells[i][j].getContainsString() + " ") ;
            }
            System.out.println(ConsoleColors.BLUE+" -->");
        }

        System.out.print("      ");
        for (int k=0; k<Board.BOARD_DIM; k++){
            System.out.print(ConsoleColors.BLUE+k + " ");
        }
        System.out.println(ConsoleColors.RESET);
    }

    // Para mostrar el tablero por pantalla durante el juego
    // (sin mostrar los barcos)
    public void paintGame(){
        // Cabecera ...
        System.out.print("  <-- ");
        for (int k=0; k<Board.BOARD_DIM; k++){
            System.out.print(k + " ");
        }
        System.out.println(" -->");
        char c='A';
        for (int i=0; i<Board.BOARD_DIM; i++) {
            System.out.print((c++) + " <-- ");
            for (int j=0; j<Board.BOARD_DIM; j++) {
                if (cells[i][j].getContainsString()==Cell.CELL_BOAT_CHAR)
                    System.out.print("_ ") ;
                else
                    System.out.print(cells[i][j].getContainsString() + " ") ;
            }
            System.out.println(" -->");
        }
    }

    public void FichBarcos(){

        boats = new Boat[BOARD_BOATS_COUNT];
        FileReader fileR = null;

        try{

            File file = new File("boat_in.txt");
            fileR = new FileReader(file);
            BufferedReader bufferR = new BufferedReader(fileR);

            while (bufferR.ready()) {
                String linea = bufferR.readLine();
                String[] items = linea.split(";");
                boats[Integer.parseInt(items[0])] = new Boat();
                boats[Integer.parseInt(items[0])].setBoat(Integer.parseInt(items[1]), this, Integer.parseInt(items[0]), Integer.parseInt(items[3]), Integer.parseInt(items[4]),Integer.parseInt(items[2]));
                boats[Integer.parseInt(items[0])].viewCells();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                assert fileR != null;
                fileR.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
