/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ime.eb.logica.mancalagame;
import alice.tuprolog.*;
import alice.tuprolog.InvalidTheoryException;
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
    private String jogador;
    private String jogAnt;
    
    public PrologBind() throws FileNotFoundException, InvalidTheoryException, IOException, NoSolutionException{
        engine = new Prolog();
        jogador = new String("opponent");
        jogAnt = null;
        
        engine.setTheory(new Theory(new FileInputStream("res/kalah-java.pl")));
        SolveInfo info=engine.solve(new Struct("initialize",new Struct("kalah"),new Var("Position"), new Struct(jogador)));
        posAtual = Term.parse(info.getVarValue("Position").toString());
    }
    
    public ArrayList jogaPlayer(ArrayList moves) throws NoSolutionException, UnknownVarException{
        SolveInfo info;
        ArrayList listaListasPos = new ArrayList<>();
        ArrayList listaPos = new ArrayList<>();
        Struct posStruc = new Struct("board",new Struct(new Var("C1"),new Struct(new Var("C2"),new Struct(new Var("C3"),new Struct(new Var("C4"),new Struct(new Var("C5"),new Struct(new Var("C6"),new Struct())))))),new Var("CM"),new Struct(new Var("P1"),new Struct(new Var("P2"),new Struct(new Var("P3"),new Struct(new Var("P4"),new Struct(new Var("P5"),new Struct(new Var("P6"),new Struct())))))),new Var("PM"));
            
            if(jogador.equals("opponent")){
                if(jogAnt!=null){
                    if(jogAnt.equals("opponent")){
                        info=engine.solve(new Struct("move",new Struct(),posAtual, new Var("Position")));
                        posAtual = Term.parse(info.getVarValue("Position").toString());
                    }
                }
                    
                for(int i=0; i<moves.size(); i++){
                    
                    int move = (int)moves.get(i);  
                    info=engine.solve(new Struct("move",new Struct(),posAtual, posStruc));
                    listaPos = parsePos(info);
                    listaListasPos.add(listaPos);                    
                    info=engine.solve(new Struct("move",new Struct(new Int(move),new Struct()),posAtual, new Var("Position")));
                    posAtual = Term.parse(info.getVarValue("Position").toString());
                    
                }
                //listaPos = (ArrayList)info.getBindingVars();
            }
            else{
                listaListasPos.add("Não é a sua vez");
            }
        jogAnt = "opponent";
        return listaListasPos;
    }
    
    public ArrayList jogaComputer() throws NoSolutionException{
        SolveInfo info;
        Term move;
        ArrayList ListPos = new ArrayList<>();
        ArrayList PosList = new ArrayList<>();
        ArrayList movesList = new ArrayList<>();
        Struct posStruc = new Struct("board",new Struct(new Var("C1"),new Struct(new Var("C2"),new Struct(new Var("C3"),new Struct(new Var("C4"),new Struct(new Var("C5"),new Struct(new Var("C6"),new Struct())))))),new Var("CM"),new Struct(new Var("P1"),new Struct(new Var("P2"),new Struct(new Var("P3"),new Struct(new Var("P4"),new Struct(new Var("P5"),new Struct(new Var("P6"),new Struct())))))),new Var("PM"));
        
        if(jogador.equals("computer")){
            
            info = engine.solve(new Struct("choose_move",posAtual,new Struct(jogador),new Var("Move")));
            move = Term.parse(info.getVarValue("Move").toString());
            String moves = move.toString();
            int i = 1;
            while(i<moves.length()){
                int moveI = Integer.parseInt(moves.substring(i,i+1));
                i = i+2;
                movesList.add(moveI);
            }
                        
            for(int j=0; j<movesList.size(); j++){
                int m = Integer.parseInt(movesList.get(j).toString());
                System.out.println("Move: "+m);
                  
                if(j < movesList.size()-1){
                    
                    info = engine.solve(new Struct("move",new Struct(new Int(m), new Struct()),posAtual, new Var("Position")));
                    posAtual = Term.parse(info.getVarValue("Position").toString());
                    info = engine.solve(new Struct("move",new Struct(),posAtual, posStruc));
                    PosList = parsePos(info);
                    ListPos.add(PosList);
                    info = engine.solve(new Struct("move",new Struct(),posAtual, new Var("Position")));
                    posAtual = Term.parse(info.getVarValue("Position").toString());
                    System.out.println(posAtual);
                }
                else{
                    info = engine.solve(new Struct("move",new Struct(new Int(m), new Struct()),posAtual, new Var("Position")));
                    posAtual = Term.parse(info.getVarValue("Position").toString());
                    info = engine.solve(new Struct("move",new Struct(),posAtual, posStruc));
                    PosList = parsePos(info);
                    ListPos.add(PosList);
                    info = engine.solve(new Struct("move",new Struct(),posAtual, new Var("Position")));
                    System.out.println(info.getVarValue("Position").toString());
                }
                
                
                
            }
            
            
        }
        else{
            //listaPos = new ArrayList<String>();
            ListPos.add("Não é a vez do computador");
        }
        
        return ListPos;
    }
    
    public ArrayList parsePos(SolveInfo pos) throws NoSolutionException{
        ArrayList ListPos = new ArrayList<Integer>();
        
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
    
    public int gameOver() throws NoSolutionException{
        SolveInfo info = engine.solve(new Struct("game_over",posAtual,new Struct(jogador),new Var("Resultado")));
        if(info.isSuccess()){
            String res = info.getVarValue("Resultado").toString();
            if(res.equals("opponent")) return 1;
            else if(res.equals("computer")) return 2;
            else return 0;
        }
        else 
            return -1;
    }
    
    public String nextPlayer() throws NoSolutionException{
        SolveInfo info = engine.solve(new Struct("next_player", new Struct(jogador),new Var("ProxJogador")));
        jogador = info.getVarValue("ProxJogador").toString();
        return jogador;
    }
    
    public static void main(String[] args) throws MalformedGoalException, NoSolutionException, InvalidTheoryException, NoMoreSolutionException {
        // TODO code application logic here
        
        try {        
        
            PrologBind kalah = new PrologBind();
            ArrayList<Integer> moves = new ArrayList<>();
            moves.add(1);
            ArrayList<Integer> moves2 = new ArrayList<>();
            moves2.add(4);
            
            System.out.println(kalah.posAtual.getTerm().toString());
            kalah.jogaPlayer(moves);
            System.out.println(kalah.posAtual.toString());
            
            kalah.jogaPlayer(moves2);
            System.out.println(kalah.posAtual.toString());
            
            kalah.nextPlayer();
            
            ArrayList computadorMoves = kalah.jogaComputer();
            
            System.out.println(kalah.posAtual.toString());
            
            
            
        } catch (Exception ex){
        ex.printStackTrace();
        }
}
    
    
}
