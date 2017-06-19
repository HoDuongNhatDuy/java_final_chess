package com.chess.gui;

import com.chess.Alliance;
import com.chess.Coordinate;
import com.chess.board.Board;
import com.chess.board.Move;
import com.chess.board.Move.NullMove;
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
    private final TurnSignPanel myTurnSignPanel;
    private final TurnSignPanel opponentTurnSignPanel;
    private final ToolPanel toolPanel;
    private final MoveLog moveLog;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovePiece;

    //private static Dimension OUTER_FRAME_DIMENSION = new Dimension(950, 900);
    private static Dimension OUTER_FRAME_DIMENSION = new Dimension(960, 850);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private final static Dimension TURN_SIGN_PANEL_DIMENSION = new Dimension(400, 5);

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

    Partner partner = null;
    AIPlayer bot;
    boolean isWaitingForLAN = false;

    InitMultiplayerThread initMultiplayerThread;
    SolveLANMoveThread solveLANMoveThread;

    final GameHistory gameHistory;

    private Table() throws IOException {
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setLayout(new BorderLayout());

        final JMenuBar tableMenuBar = populateMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);

        vsType = VsType.HUMAN;
        isFlipped = false;

        this.chessBoard = Board.createStandardBoard(Alliance.WHITE);

        gameHistory = new GameHistory();
        gameHistory.add(chessBoard, new NullMove());

        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.myTurnSignPanel = new TurnSignPanel();
        this.opponentTurnSignPanel = new TurnSignPanel();
        this.toolPanel = new ToolPanel();
        this.moveLog = new MoveLog();

        this.myTurnSignPanel.setPreferredSize(TURN_SIGN_PANEL_DIMENSION);
        this.opponentTurnSignPanel.setPreferredSize(TURN_SIGN_PANEL_DIMENSION);

        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);

        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.add(toolPanel,BorderLayout.NORTH);

        setTurnSign(chessBoard.getCurrentPlayer().getAlliance());

        this.gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        });

        this.gameFrame.setResizable(false);

        this.gameFrame.setVisible(true);
    }

    public static Table get() {
        return INSTANCE;
    }

    public void show() {
        updateBoard();
    }

    public void undo(){
        GameHistory.GameState state = gameHistory.getLastState();

        if (state != null){
            chessBoard = state.getBoard();
            gameHistory.undo();
            moveLog.undo();
            setTurnSign(chessBoard.getCurrentPlayer().getAlliance());
            updateBoard();
        }
    }

    private JMenuBar populateMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());

        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("Game");

        final JMenuItem undo = new JMenuItem("Undo");
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });

        fileMenu.add(undo);

        fileMenu.addSeparator();

        groupVsMenuItem = new ButtonGroup();

        vsHuman = new JRadioButtonMenuItem("Free Control");
        vsHuman.setSelected(true);
        groupVsMenuItem.add(vsHuman);
        fileMenu.add(vsHuman);

        vsLan = new JRadioButtonMenuItem("Multiplayer");
        vsLan.setSelected(true);
        groupVsMenuItem.add(vsLan);
        fileMenu.add(vsLan);

        vsAI = new JRadioButtonMenuItem("Singleplayer");
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

                    initMultiplayerThread = new InitMultiplayerThread();
                    initMultiplayerThread.getThread().start();
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

    class InitMultiplayerThread implements Runnable{
        Thread thread;

        public InitMultiplayerThread() {
            this.thread = new Thread(this,"Find partner thread");
        }

        public Thread getThread() {
            return thread;
        }

        @Override
        public void run() {
            isWaitingForLAN = true;
            System.out.println("Start find partner");

            if (partner != null){ // change from vsLAN
                partner.sendGaveUpMessage();
                try {
                    partner.destroy();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                partner = null;
            }

            try {
                partner = new Partner();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Find partner complete");
            isWaitingForLAN = false;

            chessBoard = Board.createStandardBoard(partner.getAlliance().getOpposite());
            setTurnSign(chessBoard.getCurrentPlayer().getAlliance());

            updateBoard();

            if (partner.getAlliance().isWhite()) // wait for white turn
            {
                isFlipped = true;
            }

            solveLANMoveThread = new SolveLANMoveThread();
            solveLANMoveThread.getThread().start();
        }
    }

    class SolveLANMoveThread implements Runnable {
        Thread thread;

        public SolveLANMoveThread() {
            this.thread = new Thread(this, "Solve LAN move thread");
        }

        public Thread getThread() {
            return thread;
        }

        @Override
        public void run() {
            while (partner != null){
                Coordinate[] moves = partner.getMoveCoordinate();

                if (moves == null) { // opponent has gone!!
                    JOptionPane.showMessageDialog(gameFrame, "Your opponent has left");
                    vsHuman.setSelected(true);

                    return;
                }

                if (chessBoard.getCurrentPlayer().getAlliance().isBlack()){
                    Coordinate from = moves[0];
                    Coordinate to = moves[1];

                    final Move move = Move.MoveFactory.createMove(chessBoard, from, to);
                    final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
                    if (transition.getMoveStatus().isDone()) {
                        chessBoard = transition.getTransitionBoard();
                        setTurnSign(chessBoard.getCurrentPlayer().getAlliance());

                        moveLog.add(move);
                    }

                    if (chessBoard.getCurrentPlayer().isInCheckMate() || chessBoard.getCurrentPlayer().isInStaleMate()) {

                        if (!isFlipped)
                            JOptionPane.showMessageDialog(gameFrame, chessBoard.getCurrentPlayer().getOpponent().toString() + " won");
                        else
                            JOptionPane.showMessageDialog(gameFrame, chessBoard.getCurrentPlayer().toString() + " won");

                        try {
                            restart();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                        return;
                    }
                    isWaitingForLAN = false;
                    updateBoard();
                }
            }
        }
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

    private class ToolPanel extends JPanel{
        private JPanel logoPanel;
        private JPanel buttonPanel;
        ToolPanel(){
            super(new BorderLayout());
            logoPanel = new JPanel(){
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D painter = (Graphics2D)g;
                    painter.drawImage(Resource.MAIN_LOGO,350,5,logoPanel);
                }
            };
            buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            add(logoPanel,BorderLayout.CENTER);
            add(buttonPanel,BorderLayout.EAST);
            logoPanel.setOpaque(false);
            buttonPanel.setOpaque(false);

            GameButton undoBtn = new GameButton(GameButton.Type.UNDO);
            undoBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    undo();
                }
            });
            GameButton newSingleBtn = new GameButton(GameButton.Type.NEW_SINGLE);
            newSingleBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    vsType = VsType.AI;
                    try {
                        restart();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
            GameButton newMultiBtn = new GameButton(GameButton.Type.NEW_MULTI);
            newMultiBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    vsType = VsType.LAN;

                    initMultiplayerThread = new InitMultiplayerThread();
                    initMultiplayerThread.getThread().start();
                }
            });
            buttonPanel.add(newSingleBtn);
            buttonPanel.add(newMultiBtn);
            buttonPanel.add(undoBtn);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D painter = (Graphics2D)g;
            painter.drawImage(Resource.BACKGROUND,0,0,this);
        }


    }

    private class BoardPanel extends JPanel {

        private JLayeredPane layeredPane;
        private JPanel legendPanel;
        private ChessPanel chessPanel;

        BoardPanel() throws IOException {
            super(new BorderLayout());
            legendPanel = new JPanel(){
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D painter = (Graphics2D)g;
                    painter.drawImage(Resource.LEGEND,0,0,this);
                }
            };
            chessPanel = new ChessPanel(this);
            layeredPane = new JLayeredPane();
            layeredPane.add(legendPanel,JLayeredPane.DEFAULT_LAYER);
            layeredPane.add(chessPanel,JLayeredPane.PALETTE_LAYER);
            this.add(layeredPane,BorderLayout.CENTER);

            this.add(myTurnSignPanel,BorderLayout.SOUTH);
            this.add(opponentTurnSignPanel,BorderLayout.NORTH);

            legendPanel.setBounds(0,0,800,800);
            chessPanel.setBounds(33,33,584,672);

            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board) throws IOException {
            chessPanel.drawBoard(board);
        }
    }

    private class ChessPanel extends JPanel{
        private final List<TilePanel> boardTiles;

        ChessPanel(final BoardPanel boardPanel) throws IOException{
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();

            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    final TilePanel tilePanel = new TilePanel(boardPanel, new Coordinate(x, 7 - y));
                    this.boardTiles.add(tilePanel);
                    add(tilePanel);
                }
            }
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

        public void undo(){
            this.moves.remove(this.moves.size() - 1);
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
                    if ((vsType.isVsLan() && chessBoard.getCurrentPlayer().getAlliance().isWhite() && !isWaitingForLAN) ||
                            vsType.isVsAI() && chessBoard.getCurrentPlayer().getAlliance().isWhite() ||
                            vsType.isVsHuman()) {
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
                                    setTurnSign(chessBoard.getCurrentPlayer().getAlliance());

                                    gameHistory.add(chessBoard, move);
                                    moveLog.add(move);

                                    if (vsType.isVsLan()) {
                                        partner.sendMoveCoordinate(sourceTile.getCoordinate(), destinationTile.getCoordinate());
                                    }
                                }
                                sourceTile = null;
                                humanMovePiece = null;
                                destinationTile = null;

                                if (vsType.isVsLan() && transition.getMoveStatus().isDone()) {
                                    isWaitingForLAN = true;
                                }

                                if (chessBoard.getCurrentPlayer().isInCheckMate() || chessBoard.getCurrentPlayer().isInStaleMate()) {
                                    if (!isFlipped)
                                        JOptionPane.showMessageDialog(gameFrame, chessBoard.getCurrentPlayer().getOpponent().toString() + " won");
                                    else
                                        JOptionPane.showMessageDialog(gameFrame, chessBoard.getCurrentPlayer().toString() + " won");

                                    try {
                                        restart();
                                        isWaitingForLAN = false;
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }

                                }
                                updateBoard();
                            }
                        }
                    }
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                    if (vsType.isVsAI() && chessBoard.getCurrentPlayer().getAlliance().isBlack()) {
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

                final BufferedImage image = ImageIO.read(new File(Resource.PIECE_ICON_PATH + alliance.toString().substring(0, 1) + piece.toString() + ".gif"));

                add(new JLabel(new ImageIcon(image)));
            }
        }

        private void highLightLegalMoves(final Board board) throws IOException {
            for (final Move move : pieceLegalMoves(board)) {
                if (move.getDestinationCoordinate().equals(this.coordinate)) {
                    setBorder(BorderFactory.createLineBorder(Resource.HIGHLIGHT_COLOR, 3));
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

        private void assignColor() throws IOException {
            //Do nothing
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D painter = (Graphics2D)g;
            if ((this.coordinate.getX() + this.coordinate.getY()) % 2 == 0){
                painter.drawImage(Resource.TILE_LIGHT,0,0,this);
            }
            else{
                painter.drawImage(Resource.TILE_DARK,0,0,this);
            }
        }
    }

    private void solveAIMove() {
        System.out.println("AI Move");

        /*Coordinate[] moves = bot.getMoveCoordinate(chessBoard.getCurrentPlayer().getLegalMoves()
                , chessBoard.getCurrentPlayer().getOpponent().getLegalMoves());

        Coordinate from = moves[0];
        Coordinate to = moves[1];

        final Move move = Move.MoveFactory.createMove(chessBoard, from, to);
        final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
        if (transition.getMoveStatus().isDone()) {
            chessBoard = transition.getTransitionBoard();
            setTurnSign(chessBoard.getCurrentPlayer().getAlliance());

            moveLog.add(move);
        }*/

        MoveTransition transition = null;
        Move move = null;

        Coordinate[] moves = bot.getMoveCoordinate(chessBoard);

        Coordinate from = moves[0];
        Coordinate to = moves[1];

        move = Move.MoveFactory.createMove(chessBoard, from, to);

        transition = chessBoard.getCurrentPlayer().makeMove(move);

        chessBoard = transition.getTransitionBoard();
        gameHistory.add(chessBoard, move);

        setTurnSign(chessBoard.getCurrentPlayer().getAlliance());

        moveLog.add(move);

        if (chessBoard.getCurrentPlayer().isInCheckMate() || chessBoard.getCurrentPlayer().isInStaleMate()) {
            if (!isFlipped)
                JOptionPane.showMessageDialog(gameFrame, chessBoard.getCurrentPlayer().getOpponent().toString() + " won");
            else
                JOptionPane.showMessageDialog(gameFrame, chessBoard.getCurrentPlayer().toString() + " won");

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
            chessBoard = Board.createStandardBoard(partner.getAlliance().getOpposite());
            setTurnSign(chessBoard.getCurrentPlayer().getAlliance());

            updateBoard();
        } else if (vsType.isVsHuman()) {
            chessBoard = Board.createStandardBoard(Alliance.WHITE);
            setTurnSign(chessBoard.getCurrentPlayer().getAlliance());

            if (partner != null){ // change from vsLAN
                partner.sendGaveUpMessage();
                partner.destroy();
                partner = null;
            }

            updateBoard();
        } else if (vsType.isVsAI()){
            bot = new AIPlayer();
            chessBoard = Board.createStandardBoard(Alliance.WHITE);
            setTurnSign(chessBoard.getCurrentPlayer().getAlliance());

            updateBoard();
        }

        gameHistory.reset();
        gameHistory.add(chessBoard, new NullMove());

        System.out.println(chessBoard);
    }

    void setTurnSign(Alliance alliance){
        if (alliance.isWhite()) {
            myTurnSignPanel.enableSign(true);
            opponentTurnSignPanel.enableSign(false);
        }
        else {
            opponentTurnSignPanel.enableSign(true);
            myTurnSignPanel.enableSign(false);
        }
    }

    private class TurnSignPanel extends JPanel{
        private boolean signEnabled = false;

        public void enableSign(boolean signEnabled){
            this.signEnabled = signEnabled;
            validate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D painter = (Graphics2D)g;
            if(signEnabled){
                setBackground(Resource.HIGHLIGHT_COLOR);
            } else {
                painter.drawImage(Resource.BACKGROUND,0,0,this);
            }
        }
    }

}

