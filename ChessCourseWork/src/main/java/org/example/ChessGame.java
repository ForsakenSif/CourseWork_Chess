package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

abstract class Figure implements Serializable {
    char symbol;
    boolean isWhite;

    public Figure(char symbol, boolean isWhite) {
        this.symbol = symbol;
        this.isWhite = isWhite;
    }

    public abstract boolean isMoveValid(int startX, int startY, int endX, int endY, GameBoard board);

    public char getSymbol() {
        return symbol;
    }

    public boolean isWhite() {
        return isWhite;
    }
}

class Pawn extends Figure {
    public Pawn(boolean isWhite) {
        super(isWhite ? '♙' : '♟', isWhite);
    }

    @Override
    public boolean isMoveValid(int startX, int startY, int endX, int endY, GameBoard board) {
        // Направление движения в зависимости от цвета (для белых +1, для черных -1)
        int direction = isWhite ? 1 : -1;
        System.out.println("endX: " + endX);
        System.out.println("endY: " + endY);
        System.out.println("startX: " + startX);
        System.out.println("startY: " + startY);
        System.out.println("direction: " + direction);
        System.out.println("board.getPiece(endX, endY): " + board.getPiece(endX, endY));

        // Если пешка двигается по вертикали
        if (startY == endY) {

            // 1. Ход на одну клетку вперед
            if (endX == startX - direction && board.getPiece(endX, endY) == null) {
                System.out.println("Ход на одну клетку вперед.");
                return true;
            }

            // 2. Ход на две клетки вперед (только с начальной позиции)
            if (startX == (isWhite ? 6 : 1) && endX == startX - 2 * direction) {
                // Проверяем, что обе клетки на пути пустые
                if (board.getPiece(endX, endY) == null && board.getPiece(startX - direction, startY) == null) {
                    System.out.println("Ход на две клетки вперед.");
                    return true;
                }
            }
        }

        // Если пешка захватывает по диагонали
        if ((isWhite ? startX - endX == 1 : startX - endX == -1 ) && (endY == startY - direction || endY == startY + direction) && board.getPiece(endX, endY) != null) {
            Figure target = board.getPiece(endX, endY);

            if (target.isWhite() != isWhite) { // Захват вражеской фигуры
            System.out.println("Захват по диагонали.");
            return true;
            }
        }

        // Если ход не соответствует ни одному из правил
        System.out.println("Неверный ход для пешки.");
        return false;
    }
}
class Knight extends Figure {
    public Knight(boolean isWhite) {
        super(isWhite ? '♘' : '♞', isWhite);
    }

    @Override
    public boolean isMoveValid(int startX, int startY, int endX, int endY, GameBoard board) {
        // Проверяем движение на "букву Г"
        if (Math.abs(startX - endX) == 2 && Math.abs(startY - endY) == 1 || Math.abs(startX - endX) == 1 && Math.abs(startY - endY) == 2) {
            // Конь не может попасть на клетку, занятую своей фигурой
            if (board.getPiece(endX, endY) == null || board.getPiece(endX, endY).isWhite() != isWhite) {
                System.out.println("Ход коня.");
                return true;
            }
        }
        System.out.println("Неверный ход для коня.");
        return false;
    }
}

class Bishop extends Figure {
    public Bishop(boolean isWhite) {
        super(isWhite ? '♗' : '♝', isWhite);
    }

    @Override
    public boolean isMoveValid(int startX, int startY, int endX, int endY, GameBoard board) {
        // Проверяем движение по диагонали
        if (Math.abs(startX - endX) == Math.abs(startY - endY)) {
            int dx = Integer.signum(endX - startX);
            int dy = Integer.signum(endY - startY);

            // Проверяем, что на пути нет фигур
            int x = startX + dx;
            int y = startY + dy;
            while (x != endX || y != endY) {
                if (board.getPiece(x, y) != null) {
                    System.out.println("На пути стоит фигура.");
                    return false;
                }
                x += dx;
                y += dy;
            }

            // Проверяем, что целевая клетка пуста или там фигура противника
            if (board.getPiece(endX, endY) == null || board.getPiece(endX, endY).isWhite() != isWhite) {
                System.out.println("Ход слона.");
                return true;
            }
        }
        System.out.println("Неверный ход для слона.");
        return false;
    }
}

class King extends Figure {
    public King(boolean isWhite) {
        super(isWhite ? '♔' : '♚', isWhite);
    }

    @Override
    public boolean isMoveValid(int startX, int startY, int endX, int endY, GameBoard board) {
        // Король может двигаться на одну клетку в любом направлении
        if (Math.abs(startX - endX) <= 1 && Math.abs(startY - endY) <= 1) {
            // Проверяем, не стоит ли на целевой клетке фигура того же цвета
            if (board.getPiece(endX, endY) == null || board.getPiece(endX, endY).isWhite() != isWhite) {
                System.out.println("Ход короля.");
                return true;
            }
        }
        System.out.println("Неверный ход для короля.");
        return false;
    }
}


class Queen extends Figure {
    public Queen(boolean isWhite) {
        super(isWhite ? '♕' : '♛', isWhite);
    }

    @Override
    public boolean isMoveValid(int startX, int startY, int endX, int endY, GameBoard board) {
        // Проверяем, что движение по вертикали, горизонтали или диагонали
        if (startX == endX || startY == endY || Math.abs(startX - endX) == Math.abs(startY - endY)) {
            int dx = Integer.signum(endX - startX);
            int dy = Integer.signum(endY - startY);

            // Проверяем, что на пути нет фигур
            int x = startX + dx;
            int y = startY + dy;
            while (x != endX || y != endY) {
                if (board.getPiece(x, y) != null) {
                    System.out.println("На пути стоит фигура.");
                    return false;
                }
                x += dx;
                y += dy;
            }

            // Проверяем, что целевая клетка пуста или там фигура противника
            if (board.getPiece(endX, endY) == null || board.getPiece(endX, endY).isWhite() != isWhite) {
                System.out.println("Ход ферзя.");
                return true;
            }
        }
        System.out.println("Неверный ход для ферзя.");
        return false;
    }
}










class Rook extends Figure {
    public Rook(boolean isWhite) {
        super(isWhite ? '♖' : '♜', isWhite);
    }

    public boolean isMoveValid(int startX, int startY, int endX, int endY, GameBoard board) {
        if (startX == endX) {
            for (int i = Math.min(startY, endY) + 1; i < Math.max(startY, endY); i++) {
                if (board.getPiece(startX, i) != null) {
                    return false;
                }
            }
            return true;
        }
        if (startY == endY) {
            for (int i = Math.min(startX, endX) + 1; i < Math.max(startX, endX); i++) {
                if (board.getPiece(i, startY) != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}

class GameBoard implements Serializable{

    public void copyFrom(GameBoard other) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.setPiece(i, j, other.getPiece(i, j));
            }
        }
    }

    private final Figure[][] board = new Figure[8][8];

    public GameBoard() {
        // Инициализация фигур на доске
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(false);
            board[6][i] = new Pawn(true);
        }
        board[0][0] = board[0][7] = new Rook(false);
        board[0][1] = board[0][6] = new Knight(false);
        board[0][2] = board[0][5] = new Bishop(false);
        board[0][3] = new Queen(false);
        board[0][4] = new King(false);

        board[7][0] = board[7][7] = new Rook(true);
        board[7][1] = board[7][6] = new Knight(true);
        board[7][2] = board[7][5] = new Bishop(true);
        board[7][3] = new Queen(true);
        board[7][4] = new King(true);
    }

    public Figure getPiece(int x, int y) {
        return board[x][y];
    }

    public void setPiece(int x, int y, Figure figure) {
        board[x][y] = figure;
    }

    public boolean isMoveValid(int startX, int startY, int endX, int endY, Figure piece) {
        return piece.isMoveValid(startX, startY, endX, endY, this);
    }

    public void displayBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    System.out.print(board[i][j].getSymbol() + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }
}

public class ChessGame {
    private static final GameBoard board = new GameBoard();
    private static boolean gameOver = false; // Флаг окончания игры
    private static boolean whiteTurn = true;

    private static int selectedX = -1;
    private static int selectedY = -1;
    private static JLabel turnLabel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chess Game");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 850);  // Размер окна

        // Панель с шахматным полем
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 8));  // Расположим клетки в сетке 8x8
        JButton[][] buttons = new JButton[8][8];

        // Метка, отображающая чей ход
        turnLabel = new JLabel("Ход белых", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(turnLabel, BorderLayout.NORTH); // Добавляем метку сверху

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Serif", Font.BOLD, 45));  // Устанавливаем шрифт для букв
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.DARK_GRAY);  // Цвет клеток
                buttons[i][j].setPreferredSize(new Dimension(70, 70));  // Размер клетки
                final int x = i, y = j;
                buttons[i][j].addActionListener(e -> onClick(x, y, buttons));
                panel.add(buttons[i][j]);
            }
        }

        frame.add(panel);
        frame.setVisible(true);
        // Панель для кнопок сохранения и загрузки
        JPanel controlPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить игру");
        JButton loadButton = new JButton("Загрузить игру");

        saveButton.addActionListener(e -> saveGame());
        loadButton.addActionListener(e -> loadGame(buttons));

        controlPanel.add(saveButton);
        controlPanel.add(loadButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH); // Добавляем панель с кнопками внизу
        frame.setVisible(true);

        // Инициализируем отображение поля
        updateBoardDisplay(buttons);
    }

    private static void onClick(int x, int y, JButton[][] buttons) {
        if (gameOver) return; // Если игра окончена, не выполняем дальнейшие действия
        Figure figure = board.getPiece(x, y);

        // Первый клик - выбираем фигуру
        if (selectedX == -1 && selectedY == -1) {
            if (figure != null && figure.isWhite() == whiteTurn) {
                selectedX = x;
                selectedY = y;
                System.out.println("Выбрана фигура: " + figure.getSymbol() + " на позиции " + x + "," + y);
            } else {
                System.out.println("Неверный выбор фигуры");
            }

        }

        // Второй клик - выполняем ход
        else {
            if (figure == null || figure.isWhite() != whiteTurn) {
                if (board.isMoveValid(selectedX, selectedY, x, y, board.getPiece(selectedX, selectedY))) {
                    // Проверяем, если захватывается король
                    Figure target = board.getPiece(x, y);
                    if (target != null && target instanceof King) {
                        gameOver = true; // Устанавливаем флаг окончания игры
                        String winner = whiteTurn ? "Белые" : "Черные";
                        JOptionPane.showMessageDialog(null, winner + " победили! Король захвачен.");
                        return; }
                    board.setPiece(x, y, board.getPiece(selectedX, selectedY));
                    board.setPiece(selectedX, selectedY, null);
                    // Проверяем на шах для короля следующего игрока
                    if (isKingInCheck(!whiteTurn)) {
                        String checkMessage = whiteTurn ? "Шах черным!" : "Шах белым!";
                        JOptionPane.showMessageDialog(null, checkMessage);
                    }
                    whiteTurn = !whiteTurn;  // Переключаем ход

                    updateBoardDisplay(buttons);

                    // Обновляем текст метки для текущего хода
                    turnLabel.setText(whiteTurn ? "Ход белых" : "Ход черных");


                    System.out.println("Ход выполнен");
                } else {
                    System.out.println("Неверный ход");
                }
            }
            // Сбрасываем выбор
            selectedX = -1;
            selectedY = -1;


        }


    }


    private static boolean isKingInCheck(boolean isWhiteKing) {
        // Найдем короля нужного цвета на доске
        int kingX = -1, kingY = -1;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Figure piece = board.getPiece(i, j);
                if (piece instanceof King && piece.isWhite() == isWhiteKing) {
                    kingX = i;
                    kingY = j;
                    break;
                }
            }
        }
        if (kingX == -1 || kingY == -1) return false; // Король не найден

        // Проверяем, угрожают ли какие-либо фигуры соперника позиции короля
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Figure piece = board.getPiece(i, j);
                if (piece != null && piece.isWhite() != isWhiteKing) {
                    if (piece.isMoveValid(i, j, kingX, kingY, board)) {
                        return true; // Есть угроза королю
                    }
                }
            }
        }
        return false;
    }

    // Метод для сохранения игры в файл
    private static void saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("chess_game.sav"))) {
            out.writeObject(board);
            out.writeBoolean(whiteTurn);
            JOptionPane.showMessageDialog(null, "Игра сохранена.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка при сохранении игры.");
            e.printStackTrace();
        }
    }

    // Метод для загрузки игры из файла
    private static void loadGame(JButton[][] buttons) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("chess_game.sav"))) {
            GameBoard loadedBoard = (GameBoard) in.readObject();
            boolean loadedWhiteTurn = in.readBoolean();

            // Восстанавливаем состояние
            board.copyFrom(loadedBoard); // копируем состояние доски
            whiteTurn = loadedWhiteTurn;
            gameOver = false; // сбрасываем флаг окончания игры
            turnLabel.setText(whiteTurn ? "Ход белых" : "Ход черных");

            updateBoardDisplay(buttons);
            JOptionPane.showMessageDialog(null, "Игра загружена.");
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Ошибка при загрузке игры.");
            e.printStackTrace();
        }
    }
    private static void updateBoardDisplay(JButton[][] buttons) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Figure figure = board.getPiece(i, j);
                if (figure != null) {
                    buttons[i][j].setText(String.valueOf(figure.getSymbol()));  // Устанавливаем символ фигуры
                } else {
                    buttons[i][j].setText("");  // Если клетки пустые, ставим пусто
                }
            }
        }
    }
}
