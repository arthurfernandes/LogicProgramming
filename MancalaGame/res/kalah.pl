% Autor:
% Data: 10/06/2014

 /* Estrutura do jogo  */

     jogar(Jogo) :-
        inicializar(Jogo,Posicao,Jogador),
        exibir_jogo(Posicao,Jogador),
        jogar(Posicao,Jogador,Resultado).

     jogar(Posicao,Jogador,Resultado) :-
        fim_de_jogo(Posicao,Jogador,Resultado), !, anunciar(Resultado).
     jogar(Posicao,Jogador,Resultado) :-
        escolher_jogada(Posicao,Jogador,Jogada),
        mover(Jogada,Posicao,Posicao1),
        exibir_jogo(Posicao1,Jogador),
        proximo_jogador(Jogador,Jogador1),
        !,
        jogar(Posicao1,Jogador1,Resultado).

 /* Escolhendo uma jogada por minimax  */

     escolher_jogada(Posicao,computador,Jogada) :-
        olhar_a_frente(Profundidade),
        minimax(Profundidade,Posicao,1,Jogada,Valor),
        nl, write(Jogada), nl.
     escolher_jogada(Posicao,oponente,Jogada) :-
        nl, writeln(['por favor faca uma jogada']), read(Jogada), legal(Jogada).

     avaliar_e_escolher([Jogada|Jogadas],Posicao,Profundidade,MaxMin,Registro,Melhor):-
        mover(Jogada,Posicao,Posicao1),
        minimax(Profundidade,Posicao1,MaxMin,JogadaX,Valor),
        atualizar(Jogada,Valor,Registro,Registro1),
        avaliar_e_escolher(Jogadas,Posicao,Profundidade,MaxMin,Registro1,Melhor).
     avaliar_e_escolher([],Posicao,Profundidade,MaxMin,Registro,Registro).
     
     minimax(0,Posicao,MaxMin,Jogada,Valor):-
        valor(Posicao,V),
        Valor is V*MaxMin.
     minimax(Profundidade,Posicao,MaxMin,Jogada,Valor):-
        Profundidade > 0,
        findall(M,mover(Posicao,M),Jogadas),
        Profundidade1 is Profundidade - 1,
        MinMax is -MaxMin,
        avaliar_e_escolher(Jogadas,Posicao,Profundidade1,MinMax,(nil,-100000),(Jogada,Valor)).

     atualizar(Jogada,Valor,(Jogada1,Valor1),(Jogada1,Valor1)):-
        Valor =< Valor1.
     atualizar(Jogada,Valor,(Jogada1,Valor1),(Jogada,Valor)):-
        Valor > Valor1.

     mover(Tabuleiro,[M|Ms]) :-
        member(M,[1,2,3,4,5,6]),
        sementes_no_mankala(M,Tabuleiro,N),
        estender_jogada(N,M,Tabuleiro,Ms).
     mover(tabuleiro([0,0,0,0,0,0],K,Ys,L),[]).

     sementes_no_mankala(M,tabuleiro(Hs,K,Ys,L),Sementes) :-
        nesimo_membro(M,Hs,Sementes), Sementes > 0.

     estender_jogada(Sementes,M,Tabuleiro,[]) :-
        Sementes =\= (7-M) mod 13, !.
     estender_jogada(Sementes,M,Tabuleiro,Ms) :-
        Sementes =:= (7-M) mod 13, !,
        distribuir_sementes(Sementes,M,Tabuleiro,Tabuleiro1),
        mover(Tabuleiro1,Ms).

/*  Executando uma jogada  */

     mover([N|Ns],Tabuleiro,TabuleiroFinal) :-
       sementes_no_mankala(N,Tabuleiro,Sementes),
       distribuir_sementes(Sementes,N,Tabuleiro,Tabuleiro1),
       mover(Ns,Tabuleiro1,TabuleiroFinal).
     mover([],Tabuleiro1,Tabuleiro2) :-
        trocar(Tabuleiro1,Tabuleiro2).

distribuir_sementes(Sementes,Mankala,Tabuleiro,TabuleiroFinal) :-
   distribuir_meus_mankalas(Sementes,Mankala,Tabuleiro,Tabuleiro1,Sementes1),
   distribuir_seus_mankalas(Sementes1,Tabuleiro1,Tabuleiro2,TabuleiroFinal).

distribuir_meus_mankalas(Sementes,N,tabuleiro(Hs,K,Ys,L),tabuleiro(Hs1,K1,Ys,L),Sementes1) :-
  Sementes > 7-N, !,
  pegar_e_distribuir(N,Sementes,Hs,Hs1),
  K1 is K+1, Sementes1 is Sementes+N-7.
distribuir_meus_mankalas(Sementes,N,tabuleiro(Hs,K,Ys,L),Tabuleiro,0) :-
  pegar_e_distribuir(N,Sementes,Hs,Hs1),
  checar_captura(N,Sementes,Hs1,Hs2,Ys,Ys1,Pecas),
  atualizar_kalah(Pecas,N,Sementes,K,K1),
  checar_se_acabou(tabuleiro(Hs2,K1,Ys1,L),Tabuleiro).

checar_captura(N,Sementes,Hs,Hs1,Ys,Ys1,Pecas) :-
  UltimoMankala is N+Sementes,
  nesimo_membro(UltimoMankala,Hs,1),
  MankalaOposto is 7-UltimoMankala,
  nesimo_membro(MankalaOposto,Ys,Y),
  Y > 0, !,
  n_substituir(MankalaOposto,Ys,0,Ys1),
  n_substituir(UltimoMankala,Hs,0,Hs1),
  Pecas is Y+1.
checar_captura(N,Sementes,Hs,Hs,Ys,Ys,0) :- !.

checar_se_acabou(tabuleiro(Hs,K,Ys,L),tabuleiro(Hs,K,Hs,L1)) :-
  zero(Hs), !, sumlist(Ys,YsSum), L1 is L+YsSum.
checar_se_acabou(tabuleiro(Hs,K,Ys,L),tabuleiro(Ys,K1,Ys,L)) :-
  zero(Ys), !, sumlist(Hs,HsSum), K1 is K+HsSum.
checar_se_acabou(Tabuleiro,Tabuleiro) :- !.

atualizar_kalah(0,Sementes,N,K,K) :- Sementes < 7-N, !.
atualizar_kalah(0,Sementes,N,K,K1) :- Sementes =:= 7-N, !, K1 is K+1.
atualizar_kalah(Pecas,Sementes,N,K,K1) :- Pecas > 0, !, K1 is K+Pecas.

distribuir_seus_mankalas(0,Tabuleiro,Tabuleiro,Tabuleiro) :- !.
distribuir_seus_mankalas(Sementes,tabuleiro(Hs,K,Ys,L),tabuleiro(Hs,K,Ys1,L),tabuleiro(Hs,K,Ys1,L)) :-
  1 =< Sementes, Sementes =< 6,
  nao_zero(Hs), !,
  distribuir(Sementes,Ys,Ys1).
distribuir_seus_mankalas(Sementes,tabuleiro(Hs,K,Ys,L),tabuleiro(Hs,K,Ys1,L),Tabuleiro) :-
  Sementes > 6, !,
  distribuir(6,Ys,Ys1),
  Sementes1 is Sementes-6,
  distribuir_sementes(Sementes1,0,tabuleiro(Hs,K,Ys1,L),Tabuleiro).
distribuir_seus_mankalas(Sementes,tabuleiro(Hs,K,Ys,L),tabuleiro(Hs,K,Hs,L1),tabuleiro(Hs,K,Hs,L1)) :-
  zero(Hs), !, sumlist(Ys,YsSum), L1 is Sementes+YsSum+L.

/*  Distribuicao de sementes (baixo nivel) */

pegar_e_distribuir(0,N,Hs,Hs1) :-
  !, distribuir(N,Hs,Hs1).
pegar_e_distribuir(1,N,[H|Hs],[0|Hs1]) :-
  !, distribuir(N,Hs,Hs1).
pegar_e_distribuir(K,N,[H|Hs],[H|Hs1]) :-
  K > 1, !, K1 is K-1, pegar_e_distribuir(K1,N,Hs,Hs1).

     distribuir(0,Hs,Hs) :- !.
     distribuir(N,[H|Hs],[H1|Hs1]) :-
        N > 0, !, N1 is N-1, H1 is H+1, distribuir(N1,Hs,Hs1).
     distribuir(N,[],[]) :- !.

/*   Precicados de avaliacao */

     valor(tabuleiro(H,K,Y,L),Valor) :- Valor is K-L.

/*  Testando se o jogo acabou */

     fim_de_jogo(tabuleiro(0,N,0,N),Jogador,empate) :-
        pecas(K), N =:= 6*K, !.
     fim_de_jogo(tabuleiro(H,K,Y,L),Jogador,Jogador) :-
        pecas(N), K > 6*N, !.
     fim_de_jogo(tabuleiro(H,K,Y,L),Jogador,Oponente) :-
        pecas(N), L > 6*N, proximo_jogador(Jogador,Oponente).

     anunciar(oponente) :- writeln(['Voce ganhou! Parabens!']).
     anunciar(computador) :- writeln(['Eu ganhei.']).
     anunciar(empate) :- writeln(['O jogo empatou.']).

/*  Utilitarios variados do jogo */

        nesimo_membro(N,[H|Hs],K) :-
            N > 1, !, N1 is N - 1, nesimo_membro(N1,Hs,K).
        nesimo_membro(1,[H|Hs],H).

n_substituir(1,[X|Xs],Y,[Y|Xs]) :- !.
n_substituir(N,[X|Xs],Y,[X|Xs1]) :-
  N > 1, !, N1 is N-1, n_substituir(N1,Xs,Y,Xs1).

     proximo_jogador(computador,oponente).
     proximo_jogador(oponente,computador).

     legal([N|Ns]) :-  0 < N, N < 7, legal(Ns).
     legal([]).

     trocar(tabuleiro(Hs,K,Ys,L),tabuleiro(Ys,L,Hs,K)).

     exibir_jogo(Posicao,computador) :-
        mostrar(Posicao).
     exibir_jogo(Posicao,oponente) :-
        trocar(Posicao,Posicao1), mostrar(Posicao1).

     mostrar(tabuleiro(H,K,Y,L)) :-
        reverse(H,HR),  escrever_sementes(HR), escrever_halahs(K,L), escrever_sementes(Y).

     escrever_sementes(H) :-
        nl, tab(5), exibir_mankalas(H).

     exibir_mankalas([H|Hs]) :-
        escrever_pilha(H), exibir_mankalas(Hs).
     exibir_mankalas([]) :- nl.

        escrever_pilha(N) :- N < 10, write(N), tab(4).
        escrever_pilha(N) :- N >= 10, write(N), tab(3).

     escrever_halahs(K,L) :-
        write(K), tab(34), write(L), nl.

     zero([0,0,0,0,0,0]).

     nao_zero(Hs) :- Hs \== [0,0,0,0,0,0].

/*  Inicializando */

     inicializar(kalah,tabuleiro([N,N,N,N,N,N],0,[N,N,N,N,N,N],0),oponente) :-
        pecas(N).

     pecas(6).