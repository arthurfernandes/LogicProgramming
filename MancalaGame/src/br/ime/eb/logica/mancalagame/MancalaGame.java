package br.ime.eb.logica.mancalagame;

import br.ime.eb.logica.mancalagame.Menu;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

public class MancalaGame extends StateBasedGame{

	public static final String gamename = "Mankala";
	private static int windowWidth = 1400;
	private static int windowHeight = windowWidth*487/1005;
	public static final int menu = 0;
	public static final int play = 1;
	
	public static void setWindowWidth(int windowWidth){
		MancalaGame.windowWidth = windowWidth;
		windowHeight = windowWidth*487/1005;
	}
	
	public MancalaGame(String gamename){
		super(gamename);
		this.addState(new Menu(menu));
		this.addState(new Play(play));
	}
	
	public static int getWindowWidth(){
		return windowWidth;
	}
	

	public static int getWindowHeight(){
		return windowHeight;
	}
	
	public void initStatesList(GameContainer gc) throws SlickException{
		this.getState(menu).init(gc, this);
		this.getState(play).init(gc, this);
		this.enterState(menu);
	}
	

	public static void main(String[] args) {
		AppGameContainer appgc;
		try{
			appgc = new AppGameContainer(new MancalaGame("Mankala"));
			appgc.setShowFPS(false);
			MancalaGame.setWindowWidth(appgc.getScreenWidth()-20);
			appgc.setDisplayMode(MancalaGame.getWindowWidth(), MancalaGame.getWindowHeight(), false);
			appgc.start();
		}catch(SlickException e){
			e.printStackTrace();
		}
		
	}
}
