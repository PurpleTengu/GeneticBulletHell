package actualgame;

import java.awt.Color;
import java.awt.Font;

import framework.Game;
import framework.TopFrame;

public class Runner  {
    
    public static void main(String[] args){
    	Runner r = new Runner();
    	Game g = makeTheGame();
    	TopFrame t = new TopFrame(g,800,600);
    	g.start();
    }
    
	public static Game makeTheGame(){
		Game m = new Menu(
				new String[] {
						"Start",
						"Start B",
						"Start C",
						"Start D",
						"Start With Default"},
				new Game[] {
						new TouhouGame(new BossSeed(System.currentTimeMillis())),
						new TouhouGame(new BossSeed(System.currentTimeMillis())),
						new TouhouGame(new BossSeed(System.currentTimeMillis())),
						new TouhouGame(new BossSeed(System.currentTimeMillis())),
						new TouhouGame(new BossSeed())},
				new Color(100,150,30),
				Font.decode("123123-bold-60")
			);
		return m;
	}
    
}