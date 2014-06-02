package br.ime.eb.logica.mancalagame;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Menu extends BasicGameState{
	private int windowWidth = MancalaGame.getWindowWidth();
	private int windowHeight = MancalaGame.getWindowHeight();
	private int playButtonOffset_X;
	private int playButtonOffset_Y;
	private int playButtonLength_X;
	private int playButtonLength_Y;
	//private String entranceMusic = "res/01test.mp3";
	private enum Action { QUIT,PLAY,NONE};
	private Action action;
	private Image background;
	
	public Menu(int state){

		JLayer song = new JLayer();
		song.main(null);
	}
	
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{
		
		
		action = Action.NONE;
		playButtonOffset_X = this.getRelativeRatioWidth(377);
		playButtonOffset_Y = this.getRelativeRatioWidth(229);
		playButtonLength_X = this.getRelativeRatioWidth(272);
		playButtonLength_Y = this.getRelativeRatioWidth(93);
		background = new Image("res/background0.png");
		background = background.getScaledCopy(MancalaGame.getWindowWidth(),MancalaGame.getWindowHeight());
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{
		background.draw();
	}
	
	private int getRelativeRatioWidth(int AbsoluteWidth){
		return (int)(windowWidth*((float) AbsoluteWidth/1005));
	}
	
	private int getRelativeRatioHeight(int AbsoluteHeight){
		return (int)(windowHeight*((float) AbsoluteHeight/487));
	}
	
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException{
		switch(action){
			case QUIT:
				action = Action.NONE;
				System.exit(0);
				break;
			case PLAY:
				action = Action.NONE;
				sbg.enterState(MancalaGame.play);
				break;
			default:
				
		}
		
		
	}
	
	public void keyPressed(int key,char c){
		switch(key){
		case Keyboard.KEY_RETURN:
			action = Action.PLAY;
			break;
		case Keyboard.KEY_ESCAPE:
			action = Action.QUIT;
			break;
		}
	}
	
	public void mouseClicked(int button,int posX,int posY,int clickCount){
		float quitX = (posX-this.getRelativeRatioWidth(79))/(float) this.getRelativeRatioWidth(45);
		float quitY = (posY-this.getRelativeRatioHeight(66))/(float) this.getRelativeRatioHeight(45);
		//Quit
		if((quitX*quitX + quitY*quitY) < 1)
			action = Action.QUIT;
		
		//Play
		if((posX>playButtonOffset_X && posX<(playButtonOffset_X+playButtonLength_X)) && (posY>playButtonOffset_Y && posY<(playButtonOffset_Y+playButtonLength_Y))){
			action = Action.PLAY;
		}
		
		
		
	}
	public int getID(){
		return 0;
	}
}
