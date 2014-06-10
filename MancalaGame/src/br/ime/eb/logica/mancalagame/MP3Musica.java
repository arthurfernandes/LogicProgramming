package br.ime.eb.logica.mancalagame;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MP3Musica extends Thread {
	 
	// OBJETO PARA O ARQUIVO MP3 A SER TOCADO
	private File mp3;
	private Player player;
	// OBJETO PLAYER DA BIBLIOTECA JLAYER QUE TOCA O ARQUIVO MP3

	/**
	 * CONSTRUTOR RECEBE O OBJETO FILE REFERECIANDO O ARQUIVO MP3 A SER
	 * TOCADO E ATRIBUI AO ATRIBUTO DA CLASS
	 *
	 * @param mp3
	 */
	public void tocar(File mp3) {
		this.mp3 = mp3;
	}
	public void resumeSong(){
		try {
			this.player.play();
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * ===============================================================
	 * ======================================METODO RUN QUE TOCA O MP3
	 * ===============================================================
	 */
	public void run() {
		try {
			FileInputStream fis = new FileInputStream(mp3);
			BufferedInputStream bis = new BufferedInputStream(fis);

			player = new Player(bis);
			System.out.println("Tocando Musica!");

			player.play();
			System.out.println("Terminado Musica!");

		} catch (Exception e) {
			System.out.println("Problema ao tocar Musica" + mp3);
			e.printStackTrace();
		}
	}
	
	public void parar(){
		this.stop();
	}
}