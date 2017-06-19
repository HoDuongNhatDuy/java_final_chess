package com.chess.gui;

import com.chess.Alliance;
import com.chess.board.Move;
import com.chess.pieces.Piece;
import com.sun.org.apache.regexp.internal.RE;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.chess.gui.Table.*;

/**
 * Created by hoduo on 6/10/2017.
 */
public class TakenPiecesPanel extends JPanel
{
    private final JPanel northPanel;
    private final JPanel southPanel;

    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(100, 700);

    public TakenPiecesPanel()
    {
        super(new BorderLayout());
        this.setBorder(Resource.PANEL_BORDER);
        JPanel titlePanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D painter = (Graphics2D)g;
                painter.drawImage(Resource.TAKEN_PIECES_LOGO,0,10,this);
            }
        };
        titlePanel.setPreferredSize(new Dimension(100,70));
        titlePanel.setOpaque(false);



        this.northPanel = new JPanel(new GridLayout(8, 1));
        this.northPanel.setBorder(Resource.PANEL_BORDER);
        this.northPanel.setOpaque(false);

        this.southPanel = new JPanel(new GridLayout(8, 1));
        this.southPanel.setBorder(Resource.PANEL_BORDER);
        this.southPanel.setOpaque(false);


        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.Y_AXIS));
        topPanel.add(titlePanel);
        topPanel.add(northPanel);
        topPanel.setOpaque(false);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(this.southPanel, BorderLayout.SOUTH);


        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D painter = (Graphics2D)g;

        painter.drawImage(Resource.SIDE_BACKGROUND,0,0,this);
    }

    public void redo(final MoveLog moveLog, boolean isFlipped) throws IOException
    {
        southPanel.removeAll();
        northPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for (final Move move : moveLog.getMoves())
        {
            if (move.isAttacked())
            {
                final Piece takenPiece = move.getAttackedPiece();
                if (takenPiece.getAlliance().isWhite())
                {
                    blackTakenPieces.add(takenPiece);
                }
                else
                {
                    whiteTakenPieces.add(takenPiece);
                }
            }
        }

        Collections.sort(whiteTakenPieces, new Comparator<Piece>()
        {
            @Override
            public int compare(Piece o1, Piece o2)
            {
                return Integer.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        Collections.sort(blackTakenPieces, new Comparator<Piece>()
        {
            @Override
            public int compare(Piece o1, Piece o2)
            {
                return Integer.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        for (Piece takenPiece : whiteTakenPieces)
        {
            Alliance alliance = takenPiece.getAlliance();
            if (isFlipped)
                alliance = alliance.getOpposite();

            final BufferedImage image =
                    ImageIO.read(new File(
                            Resource.PIECE_ICON_PATH +
                                    alliance.toString().substring(0, 1) +
                                    takenPiece.toString() + ".gif"));

            Image newimg = image.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
            final ImageIcon newIcon = new ImageIcon(newimg);  // transform it back

            final JLabel imageLabel = new JLabel(newIcon);
            this.southPanel.add(imageLabel);
        }

        for (Piece takenPiece : blackTakenPieces)
        {
            Alliance alliance = takenPiece.getAlliance();
            if (isFlipped)
                alliance = alliance.getOpposite();

            final BufferedImage image =
                    ImageIO.read(new File(
                            Resource.PIECE_ICON_PATH +
                                    alliance.toString().substring(0, 1) +
                                    takenPiece.toString() + ".gif"));

            Image newimg = image.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
            final ImageIcon newIcon = new ImageIcon(newimg);  // transform it back

            final JLabel imageLabel = new JLabel(newIcon);
            this.northPanel.add(imageLabel);
        }

        validate();
    }

}
