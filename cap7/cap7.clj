;Pureza e imutabilidade

;Pureza -> Lidar com funções que não têm efeito colateral quando são aplicadas

;Uma função
;que não tem efeitos colaterais permite que sua aplicação, desde que
;com um mesmo argumento, seja sempre previsível. Isto é
;conhecido como transparência referencial.


(def de-para [{:de "a" :para "4"}
              {:de "e" :para "3"} 
              {:de "i" :para "1"}
              {:de "o" :para "0"}])

(defn escrita-hacker [texto dicionario]
    (if (empty? dicionario)
        texto
        (let [conversao (first dicionario)]
            (escrita-hacker (clojure.string/replace texto (:de conversao) (:para conversao))  (rest dicionario)))))

(escrita-hacker "alameda" de-para)


;Uma função pura não faz operações que mudam o estado de
;alguma coisa. Exemplos de mudança de estado são:
;  -> Alterar valores dentro de um mapa;
;  -> Alterar uma String;
;  -> Escrever em um arquivo;
;  -> Imprimir algo na tela; e
;  -> Inserir um registro no banco de dados.


; E também são consideradas impuras as funções que, para gerar algum resultado, executam processos de ler um arquivo, consultar
; um banco de dados ou obter um recurso da internet. Mesmo sendo processos de leitura, por saírem de dentro do domínio do
; programa, não geram resultados previsíveis, pois o arquivo, banco de dados ou API que você vai ler, pode ter mudado entre uma
; leitura e outra.

;=================================================Desfazendo uma impureza========================================================

(def cotacoes
  {:yuan {:cotacao 2.15M :simbolo "¥"}
   :euro {:cotacao 0.28M :simbolo "€"}})

(defn transacao-em-outra-moeda [moeda transacao]
  (let [{{cotacao :cotacao simbolo :simbolo} moeda} cotacoes] ; a definição de cotações acontece fora da função
      (assoc transacao :valor (* cotacao (:valor transacao))
                       :moeda simbolo)))


; Para resolver isso, podemos fazer com que transacao-em-outra-moeda seja a
; aplicação parcial de uma outra função que recebe a tabela de
; conversão padrão como argumento

(defn transacao-convertida [cotacoes moeda transacao]
  (let [{{cotacao :cotacao simbolo :simbolo} moeda} cotacoes] ; a definição de cotações acontece fora da função
      (assoc transacao :valor (* cotacao (:valor transacao))
                       :moeda simbolo)))

(def transacao-em-outra-moeda (partial transacao-convertida cotacoes))

(def transacao-em-euro (partial transacao-em-outra-moeda :euro))
(def transacao-em-yuan (partial transacao-em-outra-moeda :yuan))

(transacao-em-euro (first transacoes))
(transacao-em-yuan (first transacoes))



;aridade múltipla ==> quando uma função se comporta de forma diferente de acordo com a quantidade de argumentos que recebe

(defn transacao-em-outra-moeda 
    ([cotacoes moeda transacao]
    (let [{{cotacao :cotacao simbolo :simbolo} moeda} cotacoes] 
        (assoc transacao :valor (* cotacao (:valor transacao))
                       :moeda simbolo)))
    ([moeda transacao]
        (transacao-em-outra-moeda cotacoes moeda transacao)))


;Imutabilidade ==> 

; Mudanças de estado são bem raras em Clojure. Isso porque a linguagem força o uso de estruturas de dados que não são
; modificáveis. Um mapa em Clojure não é editável. Uma String não é concatenável. Isso faz com que sejam imutáveis. Só para deixar
; claro: imutável é algo que não muda, de jeito nenhum, nem com reza braba! Nada muda em um dado depois que ele é criado. Estas
; estruturas de dados imutáveis são conhecidas como estruturas de dados permanentes.

;Quando vemos um novo par de chave e valor sendo incluído em um mapa, o que acontece é que o mapa existente é
;reaproveitado como base para o novo mapa, com os novos valores.


; Quais são as vantagens da imutabilidade?

;  + nós só precisamos nos preocupar com a validação das informações no momento em que eles são criados.
;  + Quando há a possibilidade de mutação, dois dados que são iguais agora podem não ser iguais no futuro.
;  + Compartilhar dados que não mudam é fácil, ao ponto de que podemos aproveitar dados antigos em estruturas que representam novos dados.

(def membros-fundadores 
    (list "Argentina" "Brasil" "Paraguai" "Uruguai"))

(def membros-plenos (cons "Venezuela" membros-fundadores)) ;cons adiciona um elemento a uma coleção - venezuela é incluída na primeira posição da lista

(rest membros-plenos) ; rest é uma função que pega uma coleção, ignora o primeiro elemento e retorna uma nova coleção contendo o resto

(identical? (rest membros-plenos) membros-fundadores) ; compara 2 elementos para saber se são os mesmos objetos.


;Átomos e mutações
;Clojure cuida para que as mutações ocorram de forma atômica.
;algumas vezes podemos precisar manter a referência e mudar o valor do seu conteúdo.
;atom = é uma das formas que a linguagem provê para o gerenciamento de estado.

(def registros (atom ()))
; criamos registros que referencia um atomo que contém lista vazia


; Isto acontece porque o que temos é, de fato, o que compõe um
; átomo. Se quisermos obter o valor atual do estado do átomo,
; precisamos fazer uso de um novo elemento, o símbolo @

;swap! - Para incluir novos elementos nesta lista (o ! sugere que há uma mudança de estado por acontecer)
;-> precisa de duas informações para fazer a mutação 1 - O átomo alvo, 2 - uma função - junto com seus argumentos que será aplicada aos valores que o átomo contém

(swap! registros conj {:valor 29M :tipo "despesa" :comentario "Livro de Clojure" :moeda "R$" :data "03/12/2016"})
(swap! registros conj {:valor 2700M :tipo "receita" :comentario "Bico" :moeda "R$" :data "01/12/2016"})

;; abstraindo inclusão de dados no átomo
(defn registrar [transacao]
    (swap! registros conj transacao))

;; fazendo uso desta absração
(registrar {:valor 33M :tipo "despesa" :comentario "Almoço" :moeda "R$" :data "19/11/2016"})
;; ({:valor 33M, :tipo "despesa", :comentario "Almoço",  :moeda "R$", :data "19/11/2016"}
;; {:valor 2700M, :tipo "receita", :comentario "Bico", :moeda "R$", :data "01/12/2016"}
;; {:valor 29M, :tipo "despesa", :comentario "Livro de Clojure", :moeda "R$", :data "03/12/2016"})

(registrar {:valor 45M :tipo "despesa" :comentario "Jogo no Steam" :moeda "R$" :data "26/12/2016"})

;Abstraíndo a leitura de dados do átomo

(def transacoes @registros)

;RECURSÃO

;           Uma solução recursiva deve:
;               1. iniciar o saldo com zero;
;               2. encerrar o processo quando não existirem mais elementos sobre os quais iterar, e retornar o saldo;
;               3. para cada iteração, aumentar ou diminuir o saldo com o valor da transação; e
;               4. reiniciar o processo, desta vez com o saldo sendo o novo valor calculado na etapa anterior 
;                  e as transações sem a transação utilizada na etapa anterior.


(defn despesa? [transacao]
    (= (:tipo transacao) "despesa"))

(defn saldo-acumulado [acumulado transacoes]
    (if-let [transacao (first transacoes)] ;é uma macro que funciona como let , só é executado se o elemento entre colchetes existir (na verdade, se for qualquer coisa diferente de nil e false )
        (saldo-acumulado (if (despesa? transacao)
                            (- acumulado (:valor transacao))
                            (+ acumulado (:valor transacao)))
                            (rest transacoes))
        acumulado))

(saldo-acumulado 0 transacoes)
(saldo-acumulado 0 (take 2 transacoes))

(defn calcular [acumulado transacao]
    (let [valor (:valor transacao)]
        (if (despesa? transacao)
            (+ acumulado valor)
            (- acumulado valor))))


(defn saldo-acumulado [acumulado transacoes]
    (if-let [transacao (first transacoes)] ;if-let espera 1 ou 2 blocos
        (do  ;consideradas aplicadas dentro de um bloco só.
            (prn "começou saldo-acumulado. Saldo até agora:" acumulado)  ;prn é uma função que recebe Strings e as imprime na tela.
            (saldo-acumulado (calcular acumulado transacao) (rest transacoes)))
        (do
            (prn "Processo encerrado. Saldo final:" acumulado)
            acumulado)))

;; relembrando valor-sinalizado
(defn valor-sinalizado [transacao]
    (let [moeda (:moeda transacao "R$")
          valor (:valor transacao)]
        (if (despesa? transacao)
            (str moeda " -" valor)
            (str moeda " +" valor))))


(defn saldo-acumulado [acumulado transacoes]
    (prn "Começou saldo-acumulado. Saldo até agora:" acumulado)
    (if-let [transacao (first transacoes)] ;if-let espera 1 ou 2 blocos
        (do  ;consideradas aplicadas dentro de um bloco só.
            (prn "Valor da transação atual:" (valor-sinalizado transacao))
            (prn "Quantidade de transações restantes:" (count (rest transacoes)))
            (prn ) 
            (saldo-acumulado (calcular acumulado transacao) (rest transacoes)))
            
        (do
            (prn "Processo encerrado. Saldo final:" acumulado)
            acumulado)))


(defn saldo-acumulado [acumulado transacoes]
    (if-let [transacao (first transacoes)] ;if-let espera 1 ou 2 blocos
            (saldo-acumulado (calcular acumulado transacao) (rest transacoes))
            acumulado))


(defn saldo 
    ;; caso a função receba só um argumento
    ([transacoes]
    (saldo-acumulado 0 transacoes))
    
    ;; caso a função receba dois argumentos
    ([acumulado transacoes]
     (if-let [transaco (first transacoes)]
       (saldo  (calcular acumulado transacao) (rest transacoes))
       acumulado)))

(saldo transacoes)
;; 2593M
(saldo 0 transacoes)
;; 2593M


;Tail Call Optimization e Tail Recursion Elimination
    ;; ** Quando uma função é invocada, um conjunto de dados é alocado na memória do seu computador.
    ;; ** para cada função, são guardadas as variáveis locais à função, os parâmetros que lhe foram passados e o
    ;;    endereço de memória para o qual o programa deve retornar
    ;;    quando a função acabar.


;; Tail Call Optimization (TCO) é uma técnica que busca
;; otimizar a alocação de dados na pilha de execução quando a última
;; instrução em uma função é a aplicação de uma outra função.

;; nova versão de saldo, com tail recursion
(def saldo
    ([transacoes] (saldo 0 transacoes))
    ([acumulado transacoes]
        (if (empty? transacoes)
            acumulado
            (saldo (calcular acumulado (first transacao)) 
                   (rest transacoes)))))


recur -> de uma forma especial de Clojure que faz a tail recursion elimination
(def saldo
    ([transacoes] (saldo 0 transacoes))
    ([acumulado transacoes]
        (if (empty? transacoes)
            acumulado
            (recur (calcular acumulado (first transacao)) 
                   (rest transacoes)))))

;; recur é usada para indicar que um loop recursivo vai começar e o compilador pode realizar suas otimizações.