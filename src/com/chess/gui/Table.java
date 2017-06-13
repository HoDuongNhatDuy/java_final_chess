package com.chess.gui;

import com.chess.Alliance;
import com.chess.Coordinate;
import com.chess.board.Board;
import com.chess.board.Move;
import com.chess.board.Tile;
import com.chess.network.Partner;
import com.chess.pieces.Piece;
import com.chess.player.AIPlayer;
import com.chess.player.MoveTransition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * Created by hoduo on 6/10/2017.
 */
public class Table {
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private Board chessBoard;

    private VsType vsType;
    private boolean isFlipped;

    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final MoveLog moveLog;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovePiece;

    private static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    private final static Color DARK_TILE_COLOR = new Color(107, 55, 5);
    private final static Color LIGHT_TILE_COLOR = new Color(255, 252, 210);

    private final static String PIECE_ICON_PATH = "art/fancy/";

    private static Table INSTANCE = null;

    static {
        try {
            INSTANCE = new Table();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ButtonGroup groupVsMenuItem;
    JRadioButtonMenuItem vsHuman;
    JRadioButtonMenuItem vsLan;
    JRadioButtonMenuItem vsAI;

    Partner partner;
    AIPlayer bot;
    boolean isWaitingForLAN = false;

    private Table() throws IOException {
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setLayout(new BorderLayout());

        final JMenuBar tableMenuBar = populateMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);

        vsType = VsType.HUMAN;
        isFlipped = false;

        this.chessBoard = Board.createStandardBoard(Alliance.WHITE);

        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.moveLog = new MoveLog();

        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);

        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);

        this.gameFrame.setVisible(true);
    }

    public static Table get() {
        return INSTANCE;
    }

    public void show() {
        updateBoard();
    }

    private JMenuBar populateMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());

        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        groupVsMenuItem = new ButtonGroup();

        vsHuman = new JRadioButtonMenuItem("Human");
        vsHuman.setSelected(true);
        groupVsMenuItem.add(vsHuman);
        fileMenu.add(vsHuman);

        vsLan = new JRadioButtonMenuItem("LAN");
        vsLan.setSelected(true);
        groupVsMenuItem.add(vsLan);
        fileMenu.add(vsLan);

        vsAI = new JRadioButtonMenuItem("AI");
        vsAI.setSelected(true);
        groupVsMenuItem.add(vsAI);
        fileMenu.add(vsAI);

        vsHuman.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    vsType = VsType.HUMAN;
                    try {
                        isFlipped = false;
                        restart();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        vsLan.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    vsType = VsType.LAN;
                    try {
                        restart();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        vsAI.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    vsType = VsType.AI;
                    try {
                        restart();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        fileMenu.addSeparator();

        // Exit
        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    enum VsType {
        HUMAN {
            @Override
            public boolean isVsHuman() {
                return true;
            }

            @Override
            public boolean isVsLan() {
                return false;
            }

            @Override
            public boolean isVsAI() {
                return false;
            }
        },
        LAN {
            @Override
            public boolean isVsHuman() {
                return false;
            }

            @Override
            public boolean isVsLan() {
                return true;
            }

            @Override
            public boolean isVsAI() {
                return false;
            }
        },
        AI {
            @Override
            public boolean isVsHuman() {
                return false;
            }

            @Override
            public boolean isVsLan() {
                return false;
            }

            @Override
            public boolean isVsAI() {
                return true;
            }
        };

        public abstract boolean isVsHuman();

        public abstract boolean isVsLan();

        public abstract boolean isVsAI();
    }

    private class BoardPanel extends JPanel {
        private final List<TilePanel> boardTiles;

        BoardPanel() throws IOException {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();

            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    final TilePanel tilePanel = new TilePanel(this, new Coordinate(x, 7 - y));
                    this.boardTiles.add(tilePanel);
                    add(tilePanel);
                }
            }

            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board) throws IOException {
            removeAll();
            for (final TilePanel tilePanel : boardTiles) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }

            validate();
            repaint();
        }
    }

    public static class MoveLog {
        private final List<Move> moves;

        public MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return moves;
        }

        public void add(final Move move) {
            moves.add(move);
        }

        public int size() {
            return moves.size();
        }

        public void clear() {
            moves.clear();
        }

        public Move remove(final int index) {
            return moves.remove(index);
        }

        public boolean remove(final Move move) {
            return moves.remove(move);
        }
    }

    private class TilePanel extends JPanel {
        private final Coordinate coordinate;

        TilePanel(final BoardPanel boardPanel, final Coordinate coordinate) throws IOException {
            super(new GridLayout());
            this.coordinate = coordinate;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                    if (isRightMouseButton(e)) {
                        System.out.println("Right click");

                        sourceTile = null;
                        humanMovePiece = null;
                        destinationTile = null;

                        updateBoard();
                    } else if (isLeftMouseButton(e)) {
                        if (sourceTile == null)     // first click
                        {
                            System.out.println("Left first click");

                            sourceTile = chessBoard.getTile(coordinate);
                            humanMovePiece = sourceTile.getPiece();
                            if (humanMovePiece == null)
                                sourceTile = null;

                            updateBoard();
                        } else    // second click
                        {
                            System.out.println("Left second click");

                            destinationTile = chessBoard.getTile(coordinate);
                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getCoordinate(), destinationTile.getCoordinate());
                            final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                moveLog.add(move);

                                if (vsType.isVsLan()) {
                                    partner.sendMoveCoordinate(sourceTile.getCoordinate(), destinationTile.getCoordinate());
                                }
                            }
                            sourceTile = null;
                            humanMovePiece = null;
                            destinationTile = null;

                            if (chessBoard.getCurrentPlayer().isInCheckMate() || chessBoard.getCurrentPlayer().isInStaleMate()) {
                                JOptionPane.showMessageDialog(gameFrame, chessBoard.getCurrentPlayer().getOpponent().toString() + " won");

                                try {
                                    restart();
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }

                            }
                            updateBoard();

                            if (vsType.isVsLan() && transition.getMoveStatus().isDone()) {
                                isWaitingForLAN = true;
                            }
                        }
                    }
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                    if (vsType.isVsLan() && isWaitingForLAN) {
                        SolveLANMove();
                        isWaitingForLAN = false;
                    } else if (vsType.isVsAI() && chessBoard.getCurrentPlayer().getAlliance().isBlack()) {
                        solveAIMove();
                    }
                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });

            validate();
        }

        public void drawTile(final Board board) throws IOException {
            setBorder(BorderFactory.createEmptyBorder());
            assignColor();
            assignTilePieceIcon(board);
            highLightLegalMoves(board);

            validate();
            repaint();
        }

        private void assignTilePieceIcon(final Board board) throws IOException {
            this.removeAll();

            if (board.getTile(this.coordinate).isOccupied()) {
                Piece piece = board.getTile(this.coordinate).getPiece();
                Alliance alliance = piece.getAlliance();

                if (vsType.isVsLan() && partner != null && partner.getAlliance().isWhite()) {
                    alliance = alliance.getOpposite();
                }

                final BufferedImage image = ImageIO.read(new File(PIECE_ICON_PATH + alliance.toString().substring(0, 1) + piece.toString() + ".gif"));

                add(new JLabel(new ImageIcon(image)));
            }
        }

        private void highLightLegalMoves(final Board board) throws IOException {
            for (final Move move : pieceLegalMoves(board)) {
                if (move.getDestinationCoordinate().equals(this.coordinate)) {
                    //add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                    setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                }
            }
        }

        private Collection<Move> pieceLegalMoves(Board board) {
            if (humanMovePiece != null && humanMovePiece.getAlliance() == board.getCurrentPlayer().getAlliance()) {
                Collection<Move> allLegalMoves = board.getCurrentPlayer().getLegalMoves();
                Collection<Move> currentLegalMoves = new ArrayList<>();

                for (Move move : allLegalMoves) {
                    if (move.getMovedPiece().equals(humanMovePiece))
                        currentLegalMoves.add(move);
                }
                return currentLegalMoves;
            }

            return Collections.emptyList();
        }

        private void assignColor() {
            if ((this.coordinate.getX() + this.coordinate.getY()) % 2 == 0)
                setBackground(LIGHT_TILE_COLOR);
            else
                setBackground(DARK_TILE_COLOR);
        }
    }

    private void SolveLANMove() {
        System.out.println("LAN move");

        Coordinate[] moves = partner.getMoveCoordinate();

        Coordinate from = moves[0];
        Coordinate to = moves[1];

        final Move move = Move.MoveFactory.createMove(chessBoard, from, to);
        final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
        if (transition.getMoveStatus().isDone()) {
            chessBoard = transition.getTransitionBoard();
            moveLog.add(move);
        }

        if (chessBoard.getCurrentPlayer().isInCheckMate() || chessBoard.getCurrentPlayer().isInStaleMate()) {
            JOptionPane.showMessageDialog(gameFrame, chessBoard.getCurrentPlayer().getOpponent().toString() + " won");

            try {
                restart();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        updateBoard();
    }

    private void solveAIMove() {
        System.out.println("AI Move");

        Coordinate[] moves = bot.getMoveCoordinate(chessBoard.getCurrentPlayer().getLegalMoves()
                , chessBoard.getCurrentPlayer().getOpponent().getLegalMoves());

        Coordinate from = moves[0];
        Coordinate to = moves[1];

        final Move move = Move.MoveFactory.createMove(chessBoard, from, to);
        final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
        if (transition.getMoveStatus().isDone()) {
            chessBoard = transition.getTransitionBoard();
            moveLog.add(move);
        }

        if (chessBoard.getCurrentPlayer().isInCheckMate() || chessBoard.getCurrentPlayer().isInStaleMate()) {
            JOptionPane.showMessageDialog(gameFrame, chessBoard.getCurrentPlayer().getOpponent().toString() + " won");

            try {
                restart();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        updateBoard();

    }

    void updateBoard() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    gameHistoryPanel.redo(chessBoard, moveLog);
                    takenPiecesPanel.redo(moveLog, isFlipped);

                    boardPanel.drawBoard(chessBoard);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void restart() throws Exception {
        moveLog.clear();
        if (vsType.isVsLan()) {
            partner = new Partner();
            chessBoard = Board.createStandardBoard(partner.getAlliance().getOpposite());
            updateBoard();

            if (partner.getAlliance().isWhite()) // wait for white turn
            {
                this.isFlipped = true;
                SolveLANMove();
            }
        } else if (vsType.isVsHuman()) {
            chessBoard = Board.createStandardBoard(Alliance.WHITE);
            updateBoard();
        } else if (vsType.isVsAI()){
            bot = new AIPlayer();
            chessBoard = Board.createStandardBoard(Alliance.WHITE);
            updateBoard();
        }

        System.out.println(chessBoard);
    }
}

