package com.chess.gui;

import com.chess.board.Move;
import com.chess.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
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

    private static final Color PANEL_COLOR = Color.decode("0xFDFE6");
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80);

    private final static String PIECE_ICON_PATH = "art/fancy/";

    public TakenPiecesPanel()
    {
        super(new BorderLayout());
        this.setBackground(Color.decode("0xFDF5E6"));
        this.setBorder(PANEL_BORDER);

        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.northPanel.setBackground(PANEL_COLOR);

        this.southPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel.setBackground(PANEL_COLOR);

        this.add(this.northPanel, BorderLayout.NORTH);
        this.add(this.southPanel, BorderLayout.SOUTH);

        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    public void redo(final MoveLog moveLog) throws IOException
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
                if (takenPiece.getAlliance().isBlack())
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
            final BufferedImage image =
                    ImageIO.read(new File(
                            PIECE_ICON_PATH +
                                    takenPiece.getAlliance().toString().substring(0, 1) +
                                    takenPiece.toString() + ".gif"));
            final ImageIcon icon = new ImageIcon(image);
            final JLabel imageLabel = new JLabel(icon);
            this.southPanel.add(imageLabel);
        }

        for (Piece takenPiece : blackTakenPieces)
        {
            final BufferedImage image =
                    ImageIO.read(new File(
                            PIECE_ICON_PATH +
                                    takenPiece.getAlliance().toString().substring(0, 1) +
                                    takenPiece.toString() + ".gif"));
            final ImageIcon icon = new ImageIcon(image);
            final JLabel imageLabel = new JLabel(icon);
            this.northPanel.add(imageLabel);
        }

        validate();
    }

}
