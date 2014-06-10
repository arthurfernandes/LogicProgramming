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
	private JLayer song = null;
	private enum Action { QUIT,PLAY,NONE};
	private Action action;
	private Image background;
	private Image difficultArrow;
	
	
	public Menu(int state){

	}
	
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{
		windowWidth = MancalaGame.getWindowWidth();
		windowHeight = MancalaGame.getWindowHeight();
		action = Action.NONE;
		playButtonOffset_X = this.getRelativeRatioWidth(377);
		playButtonOffset_Y = this.getRelativeRatioWidth(229);
		playButtonLength_X = this.getRelativeRatioWidth(272);
		playButtonLength_Y = this.getRelativeRatioWidth(93);
		background = new Image("res/backgroundmenu.png");
		difficultArrow = new Image("res/difficultarrow2.png");
		difficultArrow = difficultArrow.getScaledCopy((int)((float)64/1005*MancalaGame.getWindowWidth()), (int)((float)33/487*MancalaGame.getWindowHeight()));
		background = background.getScaledCopy(MancalaGame.getWindowWidth(),MancalaGame.getWindowHeight());
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{
		background.draw();
		int i = 0;
		switch(Play.getDifficultLevel()){
		case EASY:
			i = 0;
			break;
		case MEDIUM:
			i = 1;
			break;
		case HARD:
			i = 2;
			break;
		}
		difficultArrow.draw(this.getRelativeRatioWidth(388),this.getRelativeRatioHeight(344+i*47));
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
				song.stopSong();
				System.exit(0);
				break;
			case PLAY:
				action = Action.NONE;
				song.stopSong();
				sbg.enterState(MancalaGame.play);
				break;
			default:
				
		}
		
		
	}
	
	public void enter(GameContainer conteiner,StateBasedGame sbg){
		if(song!=null)
			song.stopSong();
		song = new JLayer();
		song.playSong("res/04test.mp3");
		Play.setDifficultLevel(Play.DifficultLevel.MEDIUM);
	}
	
	public void keyPressed(int key,char c){
		Play.DifficultLevel dificuldade;
		switch(key){
		case Keyboard.KEY_RETURN:
			action = Action.PLAY;
			break;
		case Keyboard.KEY_ESCAPE:
			action = Action.QUIT;
			break;
		case Keyboard.KEY_DOWN:
			dificuldade = (Play.getDifficultLevel() != Play.DifficultLevel.HARD) ? ((Play.getDifficultLevel() != Play.DifficultLevel.MEDIUM )? Play.DifficultLevel.MEDIUM : Play.DifficultLevel.HARD ) : Play.getDifficultLevel();
			Play.setDifficultLevel(dificuldade);
			break;
		case Keyboard.KEY_UP:
			dificuldade = (Play.getDifficultLevel() != Play.DifficultLevel.EASY) ? ((Play.getDifficultLevel() != Play.DifficultLevel.MEDIUM )? Play.DifficultLevel.MEDIUM : Play.DifficultLevel.EASY ) : Play.getDifficultLevel();
			Play.setDifficultLevel(dificuldade);
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
		
		//Easy
		if(((posX < getRelativeRatioWidth(550))&& (posX>getRelativeRatioWidth(460))) &&(( posY < getRelativeRatioWidth(370))&& (posY> getRelativeRatioWidth(340)))){
			Play.setDifficultLevel(Play.DifficultLevel.EASY);
		}
		
		//Medium
		if(((posX < getRelativeRatioWidth(550))&& (posX>getRelativeRatioWidth(460))) &&(( posY < getRelativeRatioWidth(420))&& (posY> getRelativeRatioWidth(391)))){
			Play.setDifficultLevel(Play.DifficultLevel.MEDIUM);
		}
		//Hard
		if(((posX < getRelativeRatioWidth(550))&& (posX>getRelativeRatioWidth(460))) &&(( posY < getRelativeRatioWidth(470))&& (posY> getRelativeRatioWidth(438)))){
			Play.setDifficultLevel(Play.DifficultLevel.HARD);
		}
		
	}
	public int getID(){
		return 0;
	}
}
