package com.chess.gui;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * Created by Danopie on 6/19/2017.
 */
public class Resource {
    public final static String RESOURCE_PATH = "art/wood/";
    public static final String PIECE_ICON_PATH = RESOURCE_PATH + "pieces/" ;

    public static final Image BACKGROUND = Toolkit.getDefaultToolkit().getImage(RESOURCE_PATH + "glass_background.jpg");
    public static final Image SIDE_BACKGROUND = Toolkit.getDefaultToolkit().getImage(RESOURCE_PATH + "glass_background.jpg");
    public static final Image TAKEN_PIECES_LOGO = Toolkit.getDefaultToolkit().getImage(RESOURCE_PATH + "taken_pieces.png");
    public static final Image MAIN_LOGO = Toolkit.getDefaultToolkit().getImage( RESOURCE_PATH + "logo.png");
    public static final Image LEGEND = Toolkit.getDefaultToolkit().getImage(RESOURCE_PATH + "legend.jpg");
    public static final Image TILE_DARK = Toolkit.getDefaultToolkit().getImage(RESOURCE_PATH +"tile_dark.gif");
    public static final Image TILE_LIGHT =Toolkit.getDefaultToolkit().getImage(RESOURCE_PATH +"tile_light.gif");


    public static final String UNDO_BTN_PATH = RESOURCE_PATH +"button/undo/";
    public static final String NEW_SINGLE_BTN_PATH = RESOURCE_PATH +"button/new_single/";
    public static final String NEW_MULTI_BTN_PATH = RESOURCE_PATH +"button/new_multi/";


    public static final Font PANEL_FONT = new Font("Tahoma",Font.PLAIN,16);
    public static final Border PANEL_BORDER = new EtchedBorder(EtchedBorder.LOWERED);
    public static final int TABLE_ROW_HEIGHT = 40;

    public static final Color HIGHLIGHT_COLOR = Color.decode("#45ed83");
}
