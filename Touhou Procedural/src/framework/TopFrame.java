package framework;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import framework.Game;
import framework.Keys;

public class TopFrame extends JFrame implements ActionListener{
	
	public static Game game;
	
    public TopFrame(Game startGame, int width, int height) {
    	
    	this.game=startGame;
    	this.addKeyListener(new TAdapter());
    	this.getContentPane().add(game);
		this.pack();
        setTitle("Procedural Touhou");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(width, height);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(true);
    }
    
    private class TAdapter extends KeyAdapter {

        public void keyReleased(KeyEvent e) {
            Keys.keyReleased(e);
        }

        public void keyPressed(KeyEvent e) {
            Keys.keyPressed(e);
        }
    }

	@Override
	public void actionPerformed(ActionEvent evt) {System.out.println(evt.getActionCommand());}
    
	public void actionPerformed(SwitchGameEvent evt){
		this.game.stop();
		this.getContentPane().removeAll();
		
		this.game=evt.targetGame; //TODO fix the god damn evt. apparently DispatchEvent doesn't do what I think it does.
		
		this.getContentPane().add(game);
		this.getContentPane().doLayout();
		
		System.out.println("switching game to "+game);
		this.game.start();
	}
	
}