; Preguiça - A capacidade de só fazer alguma coisa quando for realmente a hora de fazê-la.

;Teste da Preguiça

;; mas se o processamento for preguiçoso, nada deveria aparecer
;; e retorna uma string
(defn teste-da-Preguica []
    (prn "Não deveria aparecer nada aqui")
    "nada")

(defn um-oi [a b]
    "oi")

(um-oi (teste-da-Preguica) (teste-da-Preguica)) ;Não é preguiçosa

;LISTAS , SEQUÊNCIAS E COLEÇÕES EM CLOJURE
;LISTA = Uma lista é uma estrutura de dados que guarda diversos valores, podendo ser repetidos e com tipos distintosQ
;COLEÇÃO = Uma coleção agrega vários valores (listas, mapas, conjuntos)
;SEQUÊNCIA = é um tipo de coleção na qual que seus dados são acessados de forma linear.

;; uma lista de transações
(def transacoes
[{:valor 33.0 :tipo "despesa" :comentario "Almoço"
:moeda "R$" :data "19/11/2016"}
{:valor 2700.0 :tipo "receita" :comentario "Bico"
:moeda "R$" :data "01/12/2016"}
{:valor 29.0 :tipo "despesa" :comentario "Livro de Clojure"
:moeda "R$" :data "03/12/2016"}
{:valor 45M :tipo "despesa" :comentario "Jogo no Steam"
:moeda "R$" :data "26/12/2016"}])

;; uma função que nos diz se uma transação é uma despesa
(defn despesa? [transacao]
(= (:tipo transacao) "despesa"))

(filter despesa? transacoes)

(class (filter despesa? transacoes))

;; vamos associar a sequência de despesas a um nome
(def despesas (filter despesa? transacoes))

despesas
;filter só é aplicada quando `despesas` é avaliada

;; vamos pegar o valor e a moeda de uma transação
;; e informar que estamos fazendo isso
(defn valor-sinalizado [transacao]
    (prn "Pegando o valor e a moeda da transação:" transacao)
    (let [moeda (:moeda transacao "R$")
          valor (:valor transacao)]
          (if (= (:tipo transacao) "despesa")
            (str moeda " -" valor)
            (str moeda " +" valor))))

(def transacao-aleatoria {:valor 9.0})

(valor-sinalizado transacao-aleatoria)

(def valores (map valor-sinalizado transacoes))
(class (map valor-sinalizado transacoes))