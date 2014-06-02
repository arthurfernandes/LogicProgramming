package br.ime.eb.logica.mancalagame;

import br.ime.eb.logica.mancalagame.Menu;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

public class MancalaGame extends StateBasedGame{

	public static final String gamename = "Mancala";
	private static final int windowWidth = 1200;
	private static final int windowHeight = windowWidth*487/1005;
	public static final int menu = 0;
	public static final int play = 1;
	
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
			appgc = new AppGameContainer(new MancalaGame("Mancala"));
			appgc.setShowFPS(false);
			appgc.setDisplayMode(MancalaGame.getWindowWidth(), MancalaGame.getWindowHeight(), false);
			appgc.start();
		}catch(SlickException e){
			e.printStackTrace();
		}
		
	}
}
