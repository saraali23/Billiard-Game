package com.mygdx.game.Screens.BallInfo;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CleanLabel {
    private Label label;
    private int waitSeconds;

    public CleanLabel(int waitSeconds, Label label) {
        this.label = label;
        this.waitSeconds = waitSeconds;
    }

    public void startCountdownFromNow() {
        Timer timer = new Timer(waitSeconds * 1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                label.setText("");
            }
        });
        timer.start();
    }
}
