package atouhougame.gamescreens;

import java.awt.event.ActionEvent;

import atouhougame.TGlobal;
import framework.Game;
import framework.Global;
import framework.Keys;
import framework.ParagraphText;
import framework.SwitchGameEvent;
import framework.Text;

public class TextForwardScreen extends Game{
	
	public TextForwardScreen(String title, String[] minitext){
		super();
		
		this.bkgColor = TGlobal.greyBack;
	
		
		int height = TGlobal.fbig.getSize()+5;
		this.add(
				new Text(
						title,
						TGlobal.textTrans,TGlobal.fbig,
						30,
						height
				));
		height+=TGlobal.fsmall.getSize()+15;
		this.add(
				new ParagraphText(
						minitext,
						TGlobal.textLight,TGlobal.fsmall,
						45,
						height,
						8
				));
		this.add(new Text(
			"(Press any button go to the main menu)",
			TGlobal.textTrans,TGlobal.fsmall,
			45,
			Global.height-TGlobal.fsmall.getSize()-16
		));
	}
	
	@Override
	public void update(){
		super.update();
		if (Keys.anyKeyPressed()){
			TGlobal.sound_menu_escape.play();
			Keys.clearpressedButtons();
			switchGame(new SwitchGameEvent(this,ActionEvent.ACTION_PERFORMED,TGlobal.mainMenu,endGameDelay));
		}
	}
}
