package com.chess.gui;

import com.chess.board.Board;
import com.chess.board.Move;
import com.chess.pieces.Rook;

import javax.print.attribute.standard.MediaSize;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

import static com.chess.gui.Table.*;

/**
 * Created by hoduo on 6/10/2017.
 */
public class GameHistoryPanel extends JPanel
{
    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(150, 400);

    GameHistoryPanel()
    {
        this.setLayout(new BorderLayout());

        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);

        // Align text center
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);

        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board, final MoveLog moveHistory)
    {
        int currentRow = 0;
        this.model.clear();
        for (final Move move : moveHistory.getMoves())
        {
            final String moveText = move.toString();

            if (move.getMovedPiece().getAlliance().isWhite())
            {
                this.model.setValueAt(moveText, currentRow, 0);
            }
            else if (move.getMovedPiece().getAlliance().isBlack())
            {
                this.model.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }

        if (moveHistory.getMoves().size() > 0)
        {
            final Move lastMove = moveHistory.getMoves().get(moveHistory.size() - 1);
            final String moveText = lastMove.toString();

            if (lastMove.getMovedPiece().getAlliance().isWhite())
            {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);
            }
            else if (lastMove.getMovedPiece().getAlliance().isBlack())
            {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow - 1, 1);
            }
        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private String calculateCheckAndCheckMateHash(final Board board)
    {
        if (board.getCurrentPlayer().isInCheckMate())
            return "#";
        if (board.getCurrentPlayer().isInCheck())
            return "+";
        return "";
    }

    private static class DataModel extends DefaultTableModel
    {
        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        DataModel()
        {
            values = new ArrayList<>();
        }

        public void clear()
        {
            values.clear();
        }

        @Override
        public int getRowCount()
        {
            if (values == null)
                return 0;
            return values.size();
        }

        @Override
        public int getColumnCount()
        {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int column)
        {
            final Row currentRow = this.values.get(row);

            if (column == 0)
                return currentRow.getWhiteMove();
            else if (column == 1)
                return currentRow.getBlackMove();

            return null;
        }

        @Override
        public void setValueAt(final Object object, final int row, final int column)
        {
            final Row currentRow;

            if (this.values.size() <= row)
            {
                currentRow = new Row();
                this.values.add(currentRow);
            }
            else
            {
                currentRow = this.values.get(row);
            }

            if (column == 0)
            {
                currentRow.setWhiteMove((String)object);
                fireTableRowsInserted(row, row);
            }
            else if (column == 1)
            {
                currentRow.setBlackMove((String)object);
                fireTableCellUpdated(row, column);
            }
        }


        @Override
        public Class<?> getColumnClass(final int column)
        {
            return Move.class;
        }

        @Override
        public String getColumnName(final int column)
        {
            return NAMES[column];
        }
    }

    private static class Row
    {
        private String whiteMove;
        private String blackMove;

        Row()
        {

        }

        public String getWhiteMove()
        {
            return whiteMove;
        }

        public void setWhiteMove(String whiteMove)
        {
            this.whiteMove = whiteMove;
        }

        public String getBlackMove()
        {
            return blackMove;
        }

        public void setBlackMove(String blackMove)
        {
            this.blackMove = blackMove;
        }
    }
}
