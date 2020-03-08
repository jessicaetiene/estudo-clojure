
;;Função que cria um resumo de uma transacao
(defn resumo [transacao]
    (select-keys transacao [:valor :tipo :data]))

; as transções
(def transacoes
[
    {:valor 33.0 :tipo "despesa" :comentario "Almoço" :data "19/11/2016"}
    {:valor 2700.0 :tipo "receita" :comentario "Bico" :data "01/12/2016"}
    {:valor 29.0 :tipo "despesa" :comentario "Livro de clojure" :data "03/12/2016"}
])

;map para resumo das transações
(map resumo transacoes)


;Função que verifica se uma transação é despesa
;Verificando o valor para a chave tipo
(defn despesa? [transacao]
    (= (:tipo transacao) "despesa"))

(filter despesa? transacoes)

;;Função que retorna só o valor de uma transacao
(defn so-valor [transacao]
    (:valor transacao))

;Pegamos só o valor das transacao do tipo despesa
(map so-valor (filter despesa? transacoes))

;soma dos valores das despesas
(reduce + (map so-valor (filter despesa? transacoes)))


;FUNÇÃO ANONIMA

;verifica se o valor da transacao é maior que 100
(defn valor-grande? [transacao]
    (> (:valor transacao) 100))

(filter valor-grande? transacoes)

;Existe outra forma especial (como def ) para criar uma
;função: fn . Só que é uma função sem nome, que ninguém
;consegue chamar:

;; uma função que não recebe nenhum argumento e não faz nada
(fn [])


;; uma função que recebe um argumento, nome, e retorna uma
;; mensagem carinhosa.
(fn [nome] (str "Olá, " nome "!"))


;; ({:valor 2700.0, :tipo "receita",
;;
:comentario "Bico", :data "01/12/2016"})
(filter (fn [transacao] (> (:valor transacao) 100)), transacoes)


;como escrever função anonima de forma curta
(filter #(> (:valor %)) transacoes)

#() -> forma curta da função anonima == (fn [])

;não temos mais um argumento com o nome - mas o filter vai percorrer uma coleção e passar cada elemento como argumento

;cada elemento da coleção vira argumento da função anônima

;mas não precisamos dar um nome para ele... podemos substituir por %

;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
;+ E SE MINHA FUNÇÃO ANÔNIMA RECEBER MAIS DE UM ARGUMENTO ? +
;+ Você poderá utilizar %1 , %2 , %3 , daí em diante, para  +
;+ referenciar os argumentos na ordem em que eles são       +
;+ passados.                                                +
;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++



(reduce + (map #(:valor %) (filter #(=(:tipo %) "despesa") transacoes)))


;Lendo código clojure de outra forma

(so-valor (first transacoes))
;33.0

;como fica esse código com macro - TRHEAD FIRST*** não há nada de concorrência aqui
; pensa num fluxo - e o primeiro significa que o resultado de uma linha é usado como o primeiro argumento da função seguinte

(-> transacoes
    first
    (so-valor))

(-> (first transacoes)
    (so-valor))


;existe o Thread last
;Ela é útil nos casos em que precisamos passar o resultado da
; aplicação de uma função como o **último argumento da função**

(->> transacoes
     (filter #(=(:tipo %) "despesa"))
     (map #(:valor %))
     (reduce +))

(->> (filter despesa? transacoes)
     (map so-valor)    
     (reduce +))