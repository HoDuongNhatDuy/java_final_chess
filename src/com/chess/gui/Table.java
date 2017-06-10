package com.chess.gui;

import com.chess.Coordinate;
import com.chess.board.Board;
import com.chess.board.Move;
import com.chess.board.Tile;
import com.chess.pieces.Piece;
import com.chess.player.MoveTransition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
public class Table
{
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovePiece;

    private static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    private final static Color DARK_TILE_COLOR = new Color(107, 55, 5);
    private final static Color LIGHT_TILE_COLOR = new Color(255, 252, 210);

    private final static String PIECE_ICON_PATH = "art/fancy/";

    public Table () throws IOException
    {
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setLayout(new BorderLayout());

        final JMenuBar tableMenuBar = populateMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);

        this.chessBoard = Board.createStandardBoard();

        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);

        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);

        this.gameFrame.setVisible(true);
    }

    private JMenuBar populateMenuBar()
    {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());

        return tableMenuBar;
    }

    private JMenu createFileMenu()
    {
        final JMenu fileMenu = new JMenu("File");

        // load pgn
        final JMenuItem openPGN = new JMenuItem("Load PGN File");

        openPGN.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Open pgn file");
            }
        });

        fileMenu.add(openPGN);

        // Exit
        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private class BoardPanel extends JPanel
    {
        private final List<TilePanel> boardTiles;

        BoardPanel() throws IOException
        {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();

            for (int y = 0; y < 8; y++)
            {
                for (int x = 0; x < 8; x++)
                {
                    final TilePanel tilePanel = new TilePanel(this, new Coordinate(x, 7 - y));
                    this.boardTiles.add(tilePanel);
                    add(tilePanel);
                }
            }

            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board) throws IOException
        {
            removeAll();
            for (final TilePanel tilePanel : boardTiles)
            {
                tilePanel.drawTile(board);
                add(tilePanel);
            }

            validate();
            repaint();
        }
    }

    public static class MoveLog
    {
        private final List<Move> moves;

        public MoveLog()
        {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves()
        {
            return moves;
        }

        public void add(final Move move)
        {
            moves.add(move);
        }

        public int size()
        {
            return moves.size();
        }

        public void clear()
        {
            moves.clear();
        }

        public Move remove(final int index)
        {
            return moves.remove(index);
        }

        public boolean remove(final Move move)
        {
            return moves.remove(move);
        }
    }

    private class TilePanel extends JPanel
    {
        private final Coordinate coordinate;

        TilePanel(final BoardPanel boardPanel, final Coordinate coordinate) throws IOException
        {
            super(new GridLayout());
            this.coordinate = coordinate;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener()
            {
                @Override
                public void mouseClicked(final MouseEvent e)
                {
                    if (isRightMouseButton(e))
                    {
                        System.out.println("Right click");

                        sourceTile = null;
                        humanMovePiece = null;
                        destinationTile = null;
                    }
                    else if (isLeftMouseButton(e))
                    {
                        if (sourceTile == null)     // first click
                        {
                            System.out.println("Left first click");

                            sourceTile = chessBoard.getTile(coordinate);
                            humanMovePiece = sourceTile.getPiece();
                            if (humanMovePiece == null)
                                sourceTile = null;
                        }
                        else    // second click
                        {
                            System.out.println("Left second click");

                            destinationTile = chessBoard.getTile(coordinate);
                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getCoordinate(), destinationTile.getCoordinate());
                            final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone())
                            {
                                chessBoard = transition.getTransitionBoard();
                            }
                            sourceTile = null;
                            humanMovePiece = null;
                            destinationTile = null;
                        }
                    }
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                boardPanel.drawBoard(chessBoard);
                            } catch (IOException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void mousePressed(final MouseEvent e)
                {

                }

                @Override
                public void mouseReleased(final MouseEvent e)
                {

                }

                @Override
                public void mouseEntered(final MouseEvent e)
                {

                }

                @Override
                public void mouseExited(final MouseEvent e)
                {

                }
            });

            validate();
        }

        public void drawTile(final Board board) throws IOException
        {
            setBorder(BorderFactory.createEmptyBorder());
            assignColor();
            assignTilePieceIcon(board);
            highLightLegalMoves(board);

            validate();
            repaint();
        }

        private void assignTilePieceIcon(final Board board) throws IOException
        {
            this.removeAll();

            if (board.getTile(this.coordinate).isOccupied())
            {
                final BufferedImage image =
                        ImageIO.read(new File(
                                PIECE_ICON_PATH +
                                        board.getTile(this.coordinate).getPiece().getAlliance().toString().substring(0, 1) +
                                        board.getTile(this.coordinate).getPiece().toString() + ".gif"));


                add(new JLabel(new ImageIcon(image)));
            }
        }

        private void highLightLegalMoves(final Board board) throws IOException
        {
            for (final Move move : pieceLegalMoves(board))
            {
                if (move.getDestinationCoordinate().equals(this.coordinate))
                {
                    //add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                    setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                }
            }
        }

        private Collection<Move> pieceLegalMoves(Board board)
        {
            if (humanMovePiece != null && humanMovePiece.getAlliance() == board.getCurrentPlayer().getAlliance())
            {
                return humanMovePiece.calculateLegalMoves(board);
            }

            return Collections.emptyList();
        }

        private void assignColor()
        {
            if ((this.coordinate.getX() + this.coordinate.getY()) % 2 == 0 )
                setBackground(LIGHT_TILE_COLOR);
            else
                setBackground(DARK_TILE_COLOR);
        }
    }
}
