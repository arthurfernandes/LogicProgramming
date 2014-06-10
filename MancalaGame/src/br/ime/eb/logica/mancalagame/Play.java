package br.ime.eb.logica.mancalagame;


import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.UnknownVarException;


public class Play extends BasicGameState{
	
	public enum DifficultLevel {EASY,MEDIUM,HARD};
	
	private static int windowWidth;
	private static int windowHeight;
	private static int offsetMancala_X[] = new int[12];
	private static int offsetMancala_Y[] = new int[12];
	private static int offsetKalah_X[] = new int[2];
	private static int offsetKalah_Y[] = new int[2];
	
	private static int lengthMancala_X;
	private static int lengthMancala_Y;
	private static int lengthKalah_X;
	private static int lengthKalah_Y;
	
	private static int distanceMancala_X;
	private static int distanceMancala_Y;
	
	private static int ballWidth;
	
	private static int messageBox_X;
	private static int messageBox_Y;
	private static DifficultLevel difficultLevel;
	
	private String messageBoxText = "";
	private String messageNumberKalah = "";
	
	private int valuesMancala[] = new int[12];
	private int valuesKalah[] = new int[2];
	
	private enum Action { QUIT,NONE,BACK,COMPUTER,PLAYER };
	private Action action = Action.NONE;
	private boolean isAllowedToPlay = false;
	
	private Image playNow;
	private Image exitGame;
	private Shape ball;
	private Image background;
	private TextField textField;
	
	private Color fontColor;
	private Color computerFontColor;
	private TrueTypeFont awtMancalaFont;
	private TrueTypeFont awtKalahFont;
	private TrueTypeFont awtMessageFont;
	
	private TrueTypeFont textMancalaFont;
	
	private ArrayList listaDePosicoes;
	private ArrayList posicaoComputador;
	private int computadorJogadasRestantes = 0;
	private boolean computadorJogaNovamente = false;
	private boolean isGameOver = false;
	private boolean computerFreed = false;
	private int computerIndex;
	JLayer backgroundSong = null;
	
	PrologBind prologBind = null;
	
	public Play(int state){
		
	}
	
	private int getRelativeRatioWidth(int AbsoluteWidth){
		return (int)(windowWidth*((float) AbsoluteWidth/1005));
	}
	
	private int getRelativeRatioHeight(int AbsoluteHeight){
		return (int)(windowHeight*((float) AbsoluteHeight/487));
		
	}
	
	public void enter(GameContainer container,StateBasedGame sgb){
		
		if(backgroundSong!=null)
			backgroundSong.stopSong();
		backgroundSong = new JLayer();
		backgroundSong.playSong("res/03test.mp3");
		try {
			int dificuldade = 1;
			switch(this.difficultLevel){
			case EASY:
				dificuldade = 1;
				break;
			case MEDIUM:
				dificuldade = 2;
				break;
			case HARD:
				dificuldade = 3;
				break;
				default:
					
			}
			prologBind = new PrologBind(dificuldade);
		} catch ( InvalidTheoryException
				| NoSolutionException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		messageBoxText = "Novo Jogo!";
		isGameOver = false;
		for(int i = 0;i<valuesMancala.length;i++)
			valuesMancala[i] = 6;
		
		this.computadorJogaNovamente = false;
		this.computerFreed = false;
		this.isAllowedToPlay = true;
		valuesKalah[0] = 0;
		valuesKalah[1] = 0;
	
	}
	
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{
		
		windowWidth = MancalaGame.getWindowWidth();
		windowHeight = MancalaGame.getWindowHeight();
		ballWidth = getRelativeRatioWidth(15);
		messageBox_X = getRelativeRatioWidth(565);
		messageBox_Y = getRelativeRatioHeight(400);
		
		offsetMancala_X[0] = getRelativeRatioWidth(167+10);
		offsetMancala_Y[0] = getRelativeRatioHeight(145+10);
		offsetKalah_X[0] = getRelativeRatioWidth(39);
		offsetKalah_Y[0] = getRelativeRatioHeight(160);
		offsetKalah_X[1] = getRelativeRatioWidth(886);
		offsetKalah_Y[1] = getRelativeRatioHeight(57);
		
		lengthMancala_X = getRelativeRatioWidth(100);
		lengthMancala_Y = getRelativeRatioHeight(100);
		
		lengthKalah_X = getRelativeRatioWidth(100);
		lengthKalah_Y = getRelativeRatioHeight(294);
		
		distanceMancala_X = getRelativeRatioWidth(121);
		distanceMancala_Y = getRelativeRatioHeight(135);
		
		for(int i = 1;i<offsetMancala_X.length;i++)
			offsetMancala_X[i] = offsetMancala_X[0]+(distanceMancala_X)*(i%6);
		
		for(int i = 1;i<offsetMancala_Y.length;i++)
			offsetMancala_Y[i] = offsetMancala_Y[0]+(distanceMancala_Y)*(i<6?0:1);
		
		for(int i = 0;i<valuesMancala.length;i++)
			valuesMancala[i] = 6;
		
		valuesKalah[0] = 0;
		valuesKalah[1] = 0;
		
		action = Action.NONE;
		isAllowedToPlay = true;
		fontColor = new Color(0.83f,0.83f,0.83f,0.95f);
		computerFontColor = new Color(0.92f,0.30f,0.32f,0.95f);
		awtMancalaFont = new TrueTypeFont(new Font("Herculanum", Font.BOLD, (int)((float)19/1300*MancalaGame.getWindowWidth())),true);
		
		awtKalahFont = new TrueTypeFont( new Font("Herculanum",Font.BOLD,(int)((float)25/1300*MancalaGame.getWindowWidth())),true);
		awtMessageFont = new TrueTypeFont(new Font("Herculanum",Font.BOLD,(int)((float)24/1300*MancalaGame.getWindowWidth())),true);
		
		ball = new Ellipse(0,0,ballWidth/2,ballWidth/2);
		background = new Image("res/backgroundnovo.png");
		background = background.getScaledCopy(windowWidth,windowHeight);
		/*this.textField = new TextField(gc, this.textMancalaFont, 100, 100, 100, 100);
	    this.textField.setTextColor(Color.white);
	    this.textField.setBackgroundColor(new Color(0,0,0,0));
	    this.textField.setBorderColor(new Color(0,0,0,0));*/
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{
		//g.drawString("Banco Imobiliario", 100, 200);
				
		background.draw();
		
		//Drawing the balls
		
		
		for(int i = 0;i<offsetMancala_X.length;i++){
			g.setColor(new Color(0.14f,0.37f,0.19f,0.7f));
			for(int j = 0;j<valuesMancala[i];j++){
				int littleOffset = (j/16)*4;
				ball.setCenterX(offsetMancala_X[i]+(j%4)*(ballWidth+2)+littleOffset);
				ball.setCenterY(offsetMancala_Y[i]+((j/4)%4)*(ballWidth+2)+littleOffset);
				g.fill(ball);
				g.draw(ball);
			}
			g.setFont(awtMancalaFont);
			g.setColor(fontColor);
			g.drawString(String.valueOf(valuesMancala[i]),offsetMancala_X[i]+lengthMancala_X/6, offsetMancala_Y[i]+lengthMancala_Y-this.getRelativeRatioHeight(30));
		}
		
		
		
		for(int i = 0;i<valuesKalah.length;i++){
			if(i==0)
				g.setColor(new Color(0.6f,0.17f,0.215f,0.65f));
			else
				g.setColor(new Color(0.14f,0.514f,0.60f,0.65f));
			for(int j = 0;j<valuesKalah[i];j++){
				
				ball.setX(offsetKalah_X[i]+(j%4)*(ballWidth+2));
				ball.setY(offsetKalah_Y[i]+((j/4))*(ballWidth+2));
				g.fill(ball);
				g.draw(ball);
			}
			g.setFont(awtKalahFont);
			g.setColor(fontColor);
			g.drawString(String.valueOf(valuesKalah[i]),offsetKalah_X[i]+lengthKalah_X/4, offsetKalah_Y[i]+lengthKalah_Y);
		}
		g.setFont(awtMessageFont);
		g.setColor(fontColor);
		if(!this.isAllowedToPlay)
			g.setColor(computerFontColor);
		g.drawString(this.messageBoxText,this.messageBox_X,this.messageBox_Y);
		
	}
	
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException{
		switch(action){
			case QUIT:
				action = Action.NONE;
				System.exit(0);
				break;
			case BACK:
				action = Action.NONE;
				backgroundSong.stopSong();
				sbg.enterState(MancalaGame.menu);
				break;
			case PLAYER:
				break;
			default:
				
		}
		
		if(this.computadorJogaNovamente && this.computerFreed){
			System.out.println("AQUI");
			Timer timer = new Timer();
			computerFreed = false;
			timer.schedule(new TimerTask(){
				
				@Override
				public void run() {
					
					// TODO Auto-generated method stub
					posicaoComputador = (ArrayList) listaDePosicoes.get(computerIndex);
					for(int j = 0;j<6;j++)
						valuesMancala[5-j] = (int) posicaoComputador.get(j);
					
					valuesKalah[0] = (int)posicaoComputador.get(6);
					
					for(int j = 7;j<13;j++)
						valuesMancala[j-1] = (int) posicaoComputador.get(j);
					
					valuesKalah[1] = (int) posicaoComputador.get(13);
					
					
					computerIndex++;
					computadorJogadasRestantes--;
					if(computadorJogadasRestantes==0){
						computadorJogaNovamente=false;
						messageBoxText = "Sua vez Jogador!";
						try {
							prologBind.nextPlayer();
						} catch (NoSolutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						isAllowedToPlay = true;
						
					}
					else{
						messageBoxText = "Joguei de novo! ";
					}
					
					if(computadorJogadasRestantes == 0){
					try {
						int gameOver = prologBind.gameOver();
						if(gameOver>0){
							isGameOver = true;
							switch(gameOver){
							case 1:
								messageBoxText = "Parabéns você ganhou!";
								break;
							case 2:
								messageBoxText = "Que pena eu ganhei...";
								break;
							case 0:
								messageBoxText = "Empatamos... Jogue Novamente!";
								break;
							}
						}
						else{
							isGameOver = false;
						}
					} catch (NoSolutionException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					}
					
						computerFreed = true;
				};
			}, 3000);
			
		}
	}
	
	
	
	public void keyPressed(int key,char c){
		switch(key){
		case Keyboard.KEY_BACK:
			action = Action.BACK;
			break;
		case Keyboard.KEY_ESCAPE:
			action = Action.QUIT;
			break;
		}
		
		
	}
	
	public void mouseClicked(int button,int posX,int posY,int clickCount){
		float quitX = (posX-this.getRelativeRatioWidth(79))/(float) this.getRelativeRatioWidth(45);
		float quitY = (posY-this.getRelativeRatioHeight(66))/(float) this.getRelativeRatioHeight(45);
		float backX = (posX-this.getRelativeRatioWidth(136))/(float) this.getRelativeRatioWidth(45);
		float backY = (posY-this.getRelativeRatioHeight(66))/(float) this.getRelativeRatioHeight(45);
		//Quit
		if((quitX*quitX + quitY*quitY) < 1)
			action = Action.QUIT;
		
		//Back
		if((backX*backX + backY*backY)<1)
			action = Action.BACK;
		
		//Play

		int posX2 = posX + 15;
		int posY2 = posY + 15;
		for(int i = 6;i<this.offsetMancala_X.length;i++){
			
			int offset_X = offsetMancala_X[i]-this.getRelativeRatioWidth(21);
			int offset_Y = offsetMancala_Y[i]-this.getRelativeRatioHeight(27);
			if(posX2>offset_X && posX2<(offset_X+lengthMancala_X) &&
					(posY2>offset_Y && posY2<(offset_Y+lengthMancala_Y))){
				if(!isAllowedToPlay || isGameOver){
					if(!isGameOver)
						this.messageBoxText = "Espere sua Vez!";
				}
				else{
					if(valuesMancala[i]==0){
						this.messageBoxText = "Jogada Invalida!";
						break;
					}
					else{
						

						try {
							int gameOver = prologBind.gameOver();
							if(gameOver>0){
								isGameOver = true;
								switch(gameOver){
								case 1:
									this.messageBoxText = "Parabéns você ganhou!";
									break;
								case 2:
									this.messageBoxText = "Que pena eu ganhei...";
									break;
								case 0:
									this.messageBoxText = "Empatamos... Jogue Novamente!";
									
								}
							}
							else{
								isGameOver = false;
							}
						} catch (NoSolutionException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						
						ArrayList<Integer> moves = new ArrayList<>();
			            moves.add(i-5);
						try {
							listaDePosicoes = prologBind.jogaPlayer(moves);
							
						} catch (NoSolutionException | UnknownVarException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						ArrayList posicao = (ArrayList) listaDePosicoes.get(0);
						
						int kalahJogadorAntigo = valuesKalah[1];
						int proximoMancala = valuesMancala[5];
						
						for(int j = 0;j<6;j++)
							this.valuesMancala[5-j] = (int) posicao.get(j);
						
						this.valuesKalah[0] = (int)posicao.get(6);
						
						for(int j = 7;j<13;j++)
							this.valuesMancala[j-1] = (int) posicao.get(j);
						
						this.valuesKalah[1] = (int) posicao.get(13);
						
						
						if(((valuesKalah[1]-kalahJogadorAntigo)- (valuesMancala[5]-proximoMancala) ) == 1 && (isGameOver == false)){
							this.messageBoxText = "Que sorte! Jogue Novamente.";
							
						}
						else{
							this.isAllowedToPlay = false;
							if(isGameOver)
								return;
							try {
								
								prologBind.nextPlayer();
							} catch (NoSolutionException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.messageBoxText = "Agora é a minha vez!";
							try {
								int gameOver = prologBind.gameOver();
								if(gameOver>0){
									isGameOver = true;
									switch(gameOver){
									case 1:
										messageBoxText = "Parabéns você ganhou!";
										break;
									case 2:
										messageBoxText = "Que pena eu ganhei...";
										break;
									case 0:
										messageBoxText = "Empatamos... Jogue Novamente!";
										break;
									}
								}
								else{
									isGameOver = false;
								}
							} catch (NoSolutionException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
							try {
								listaDePosicoes = prologBind.jogaComputer();
							} catch (NoSolutionException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						
								
								this.computadorJogadasRestantes = listaDePosicoes.size();
								this.computerIndex=0;
								
								Timer timer = new Timer();
								timer.schedule(new TimerTask(){

									@Override
									public void run() {
										computerFreed = false;
										// TODO Auto-generated method stub
										posicaoComputador = (ArrayList) listaDePosicoes.get(computerIndex);
										if(computadorJogadasRestantes>1)
											computadorJogaNovamente = true;
										else 
											computadorJogaNovamente = false;
										
										System.out.println("NUMERO JOGADAS COMPUTADOR:"+computadorJogadasRestantes);
										
										for(int j = 0;j<6;j++)
											valuesMancala[5-j] = (int) posicaoComputador.get(j);
										
										valuesKalah[0] = (int)posicaoComputador.get(6);
										
										for(int j = 7;j<13;j++)
											valuesMancala[j-1] = (int) posicaoComputador.get(j);
										
										valuesKalah[1] = (int) posicaoComputador.get(13);
										
										computadorJogadasRestantes--;
										System.out.println("Computador Jogou:"+computerIndex);
										computerIndex++;
										if(computadorJogadasRestantes == 0){
										try {
											int gameOver = prologBind.gameOver();
											if(gameOver>0){
												isGameOver = true;
												switch(gameOver){
												case 1:
													messageBoxText = "Parabéns você ganhou!";
													break;
												case 2:
													messageBoxText = "Que pena eu ganhei...";
													break;
												case 0:
													messageBoxText = "Empatamos... Jogue Novamente!";
													break;
												}
											}
											else{
												isGameOver = false;
											}
										} catch (NoSolutionException e2) {
											// TODO Auto-generated catch block
											e2.printStackTrace();
										}
										}
										if(computadorJogadasRestantes==0 && !isGameOver){
											messageBoxText = "Sua vez Jogador!";
											try {
												prologBind.nextPlayer();
											} catch (NoSolutionException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											isAllowedToPlay = true;
										}
										
										
											computerFreed = true;
									};
								}
									
								, 2000);
								
									
								}
							
						}
					}
				}
			}
		}
		
		
	public static void setDifficultLevel(DifficultLevel difficultLevel){
		Play.difficultLevel = difficultLevel;
	}
	
	public static DifficultLevel getDifficultLevel(){
		return Play.difficultLevel;
	}
	
	public int getID(){
		return 1;
	}
}
