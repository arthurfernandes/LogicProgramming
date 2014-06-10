/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ime.eb.logica.mancalagame;

import alice.tuprolog.*;import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Theory;
import alice.tuprolog.event.OutputEvent;
import alice.tuprolog.event.OutputListener;
import alice.tuprolog.event.SpyEvent;
import alice.tuprolog.event.SpyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author arthurfernandes
 */
public class PrologBind {

    /**
     * @param args the command line arguments
     */
    private Prolog engine;
    private Term posAtual;
    private Integer mancalaComputador[] = new Integer[6];
    private Integer mancalaJogador[] = new Integer[6];
    private String jogador;
    private boolean computerHasNoPiecesLeft = false;
    private ArrayList<Integer> listaPos;
    private int jogAcum;
    
    public PrologBind(int dificuldade) throws FileNotFoundException, InvalidTheoryException, IOException, NoSolutionException{
        engine = new Prolog();
        jogador = new String("oponente");
        jogAcum = 0;
        
        engine.setTheory(new Theory(new FileInputStream("res/kalah.pl")));
        engine.addTheory(new Theory("olhar_a_frente("+dificuldade +")."));
        SolveInfo info=engine.solve(new Struct("inicializar",new Struct("kalah"),new Var("Position"), new Struct(jogador)));
        listaPos = new ArrayList<>();
        posAtual = Term.parse(info.getVarValue("Position").toString());
    }
    
    
    
    public ArrayList jogaPlayer(ArrayList moves) throws NoSolutionException, UnknownVarException{
        SolveInfo info;
        
        ArrayList listaPosicoes = new ArrayList<>();
        Struct posStruc = new Struct("tabuleiro",new Struct(new Var("C1"),new Struct(new Var("C2"),new Struct(new Var("C3"),new Struct(new Var("C4"),new Struct(new Var("C5"),new Struct(new Var("C6"),new Struct())))))),new Var("CM"),new Struct(new Var("P1"),new Struct(new Var("P2"),new Struct(new Var("P3"),new Struct(new Var("P4"),new Struct(new Var("P5"),new Struct(new Var("P6"),new Struct())))))),new Var("PM"));
            
            if(jogador.equals("oponente")){
                if(jogAcum >0){

                        info=engine.solve(new Struct("mover",new Struct(),posAtual, new Var("Position")));
                        posAtual = Term.parse(info.getVarValue("Position").toString());
                    
                }
                    
                for(int i=0; i<moves.size(); i++){
                    
                    int move = (int)moves.get(i);  
                    info=engine.solve(new Struct("mover",new Struct(new Int(move),new Struct()),posAtual, posStruc));
                    listaPos = parsePos(info);
                    listaPosicoes.add(listaPos);                    
                    info=engine.solve(new Struct("mover",new Struct(new Int(move),new Struct()),posAtual, new Var("Position")));
                    posAtual = Term.parse(info.getVarValue("Position").toString());
                    
                    lerPosicoes(listaPosicoes);
                }
                
            }
            else{
                listaPosicoes.add("Não é a sua vez");
            }
        jogAcum++;
        return listaPosicoes;
    }
    
    public ArrayList jogaComputer() throws NoSolutionException{
        SolveInfo info;
        Term move;
        ArrayList listaPosicoes = new ArrayList<>();
        ArrayList movesList = new ArrayList<>();
        Struct posStruc = new Struct("tabuleiro",new Struct(new Var("C1"),new Struct(new Var("C2"),new Struct(new Var("C3"),new Struct(new Var("C4"),new Struct(new Var("C5"),new Struct(new Var("C6"),new Struct())))))),new Var("CM"),new Struct(new Var("P1"),new Struct(new Var("P2"),new Struct(new Var("P3"),new Struct(new Var("P4"),new Struct(new Var("P5"),new Struct(new Var("P6"),new Struct())))))),new Var("PM"));
        
        if(jogador.equals("computador")){
            System.out.println(posAtual);
            info = engine.solve(new Struct("escolher_jogada",posAtual,new Struct(jogador),new Var("Move")));
            try{
            	move = Term.parse(info.getVarValue("Move").toString());
            	String moves = move.toString();
                System.out.println("MOVES:"+moves);
                int i = 1;
                while(i<moves.length()){
                    int moveI = Integer.parseInt(moves.substring(i,i+1));
                    i = i+2;
                    movesList.add(moveI);
                }
            }
            catch(Exception e){
            	Integer moves = 1;
            	if(listaPos.get(0)!=0)
            		moves = 1;
            	else if(listaPos.get(1)!=0)
            		moves = 2;
            	else if(listaPos.get(2)!=0)
            		moves = 3;
            	else if(listaPos.get(3)!=0)
            		moves = 4;
            	else if(listaPos.get(4)!=0)
            		moves = 5;
            	else if(listaPos.get(5)!=0)
            		moves = 6;
            	else{
            		this.computerHasNoPiecesLeft = true;
            		return null;
            	}
            		
            	
            	
            	movesList.add(moves);
            }
            
            
            for(int j=0; j<movesList.size(); j++){
                int m = (int)movesList.get(j);
                System.out.println("Move: "+m);
                    info = engine.solve(new Struct("mover",new Struct(new Int(m), new Struct()),posAtual, new Var("Position")));
                    posAtual = Term.parse(info.getVarValue("Position").toString());
                    info = engine.solve(new Struct("mover",new Struct(),posAtual, posStruc));
                    listaPos = parsePos(info);
                    listaPosicoes.add(listaPos);
                    info = engine.solve(new Struct("mover",new Struct(),posAtual, new Var("Position")));
                    posAtual = Term.parse(info.getVarValue("Position").toString());   
                
                lerPosicoes(listaPosicoes);
            }
    
        }
        else{
            //listaPos = new ArrayList<String>();
            listaPosicoes.add("Não é a vez do computador");
        }
        
        return listaPosicoes;
    }
    
    public ArrayList parsePos(SolveInfo pos) throws NoSolutionException{
        ArrayList ListPos = new ArrayList<>();
        
        ListPos.add(Integer.parseInt(pos.getVarValue("C1").toString()));
        ListPos.add(Integer.parseInt(pos.getVarValue("C2").toString()));
        ListPos.add(Integer.parseInt(pos.getVarValue("C3").toString()));
        ListPos.add(Integer.parseInt(pos.getVarValue("C4").toString()));
        ListPos.add(Integer.parseInt(pos.getVarValue("C5").toString()));
        ListPos.add(Integer.parseInt(pos.getVarValue("C6").toString()));
        ListPos.add(Integer.parseInt(pos.getVarValue("CM").toString()));
        ListPos.add(Integer.parseInt(pos.getVarValue("P1").toString()));
        ListPos.add(Integer.parseInt(pos.getVarValue("P2").toString()));
        ListPos.add(Integer.parseInt(pos.getVarValue("P3").toString()));
        ListPos.add(Integer.parseInt(pos.getVarValue("P4").toString()));
        ListPos.add(Integer.parseInt(pos.getVarValue("P5").toString()));
        ListPos.add(Integer.parseInt(pos.getVarValue("P6").toString()));
        ListPos.add(Integer.parseInt(pos.getVarValue("PM").toString()));
        
        return ListPos;
    }
    
    public void lerPosicoes(ArrayList listaPosicoes){
        for(int i=0; i<listaPosicoes.size(); i++){
            ArrayList posicoes = (ArrayList)listaPosicoes.get(i);
            
            for(int j=0; j<posicoes.size(); j++){
                System.out.print(posicoes.get(j)+",");
            }
            System.out.println();
        }
    }
    
    public int gameOver() throws NoSolutionException{
    	if(this.computerHasNoPiecesLeft){
    		return 2;
    	}
    	
        if(jogador.equals("computador")){
            SolveInfo info = engine.solve(new Struct("fim_de_jogo",posAtual,new Struct(jogador),new Var("Resultado")));
            if(info.isSuccess()){
                String res = info.getVarValue("Resultado").toString();
                if(res.equals("oponente")) return 1;
                else if(res.equals("computador")) return 2;
                else return 0;
            }
            else 
                return -1;
        }
        else{
            SolveInfo info = engine.solve(new Struct("fim_de_jogo",posAtual,new Struct(jogador),new Var("Resultado")));
            if(info.isSuccess()){
                String res = info.getVarValue("Resultado").toString();
                if(res.equals("computador")) return 1;
                else if(res.equals("oponente")) return 2;
                else return 0;
            }
            else 
                return -1;
        }
    }
    
    public String nextPlayer() throws NoSolutionException{
        SolveInfo info = engine.solve(new Struct("proximo_jogador", new Struct(jogador),new Var("ProxJogador")));
        jogador = info.getVarValue("ProxJogador").toString();
        return jogador;
    }
    
    public static void main(String[] args) throws MalformedGoalException, NoSolutionException, InvalidTheoryException, NoMoreSolutionException {
        // TODO code application logic here
        
        try {        
        
            PrologBind kalah = new PrologBind(2);
            ArrayList<Integer> moves = new ArrayList<>();
            moves.add(1);
            ArrayList<Integer> moves2 = new ArrayList<>();
            moves2.add(4);
            ArrayList<Integer> moves3 = new ArrayList<>();
            moves3.add(3);
            ArrayList<Integer> moves4 = new ArrayList<>();
            moves4.add(2);
            
            kalah.jogaPlayer(moves);
            kalah.nextPlayer();
            kalah.jogaComputer();
            kalah.nextPlayer();
            
            kalah.jogaPlayer(moves2);
            //kalah.jogaPlayer(moves3);
            //kalah.jogaPlayer(moves4);
        } catch (Exception ex){
        ex.printStackTrace();
        }
}
    
    
}
