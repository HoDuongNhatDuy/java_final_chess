package com.chess.gui;

import javax.swing.*;


/**
 * Created by Danopie on 6/18/2017.
 */
public class GameButton extends JButton {
    enum Type{
        UNDO(Resource.UNDO_BTN_PATH),
        NEW_SINGLE(Resource.NEW_SINGLE_BTN_PATH),
        NEW_MULTI(Resource.NEW_MULTI_BTN_PATH);

        private String path;
        Type(String path){
            this.path = path;
        }
        public String getPath(){
            return path;
        }

    }
    private ImageIcon pressed;
    private ImageIcon normal;
    private Type type;
    public GameButton(Type type){
        super();
        this.type = type;
        pressed = new ImageIcon(type.getPath()+"pressed.png");
        normal = new ImageIcon(type.getPath()+"normal.png");
        setIcon(normal);
        setPressedIcon(pressed);
        setContentAreaFilled(false);
        setBorder(null);
    }
}
