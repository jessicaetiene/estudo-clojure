;Composiçao de função
;combinar várias funções em uma só, unindo vários comportamentos.

(def transacoes
  [{:valor 33.0 :tipo "despesa" :comentario "Almoço" :moeda "R$" :data "19/11/2016"}
   {:valor 2700.0 :tipo "receita" :comentario "Bico" :moeda "R$" :data "01/12/2016"}
   {:valor 29.0 :tipo "despesa" :comentario "Livro de clojure" :moeda "R$" :data "03/12/2016"}])

(defn valor-sinalizado [transacao]
  (if (= (:tipo transacao) "despesa")
      (str (:moeda transacao) " -" (:valor transacao))
      (str (:moeda transacao) " +" (:valor transacao))))

;Associar valores a nomes - Usando let

(defn valor-sinalizado [transacao]
  (let [moeda (:moeda transacao) 
        valor (:valor transacao)]
    (if (= (:tipo transacao) "despesa")
      (str moeda " -" valor)
      (str moeda " +" valor))))


;  let permite que usemos moeda e valor
;  dentro de todo o escopo que foi criado com (let , até que seu )
;  apareça para encerrá-lo.


;valor default em maps

(defn valor-sinalizado [transacao]
  (let [moeda (:moeda transacao "R$") 
        valor (:valor transacao)]
    (if (= (:tipo transacao) "despesa")
      (str moeda " -" valor)
      (str moeda " +" valor))))

(defn data-valor [transacao]
  (str (:data transacao) " => " (valor-sinalizado transacao)))


;Para converter os valores para uma moeda diferente
;suponha que R$ 1,00 -> ¥ 2,15


;uma nova transação
;cujo valor é convertido para a moeda em questão, e o símbolo
;desta moeda é colocado na transação

(defn transacao-em-yuan [transacao]
  (assoc transacao :valor (* 2.15 (:valor transacao))
                   :moeda "¥"))

;O que o assoc faz???
;assoc vem do inglês associate
;pega o mapa passado (transacao no caso)
;e associa a este mapa os valores que seguem como argumento

(def cotacoes {:yuan {:cotacao 2.15 :simbolo "¥"}})

(defn transacao-em-yuan [transacao]
  (assoc transacao :valor (* (:cotacao (:yuan cotacoes)) (:valor transacao))
                   :moeda (:simbolo (:yuan cotacoes))))


;Para facilitar temos a função get-in
;permite buscar falores aninhados em um mapa de forma mais simples

(defn transacao-em-yuan [transacao]
  (assoc transacao :valor (* (get-in cotacoes [:yuan :cotacao]) (:valor transacao))
                   :moeda (get-in cotacoes [:yuan :simbolo])))


(defn transacao-em-yuan [transacao]
  (let [yuan (:yuan cotacoes)]
    (assoc transacao :valor (* (:cotacao yuan) (:valor transacao))
                   :moeda (:simbolo yuan))))
  
(data-valor (transacao-em-yuan (first transacoes)))

(defn texto-resumo-em-yuan [transacao]
    (data-valor (transacao-em-yuan transacao)))
  
(map texto-resumo-em-yuan transacoes)

;Em clojure, pontos flutuantes e inteiros do tipo BigInt são contagiantes
;o que significa que qualquer operação que envolva um double retornará um double
;o mesmo acontece com BigInt

;Para termos melhor precisão em operações aritméticas precisamos fazer uso do tipo BigDecimal

(def cotacoes {:yuan {:cotacao 2.15M :simbolo "¥"}})

(def transacoes
  [{:valor 33.0M :tipo "despesa" :comentario "Almoço" :moeda "R$" :data "19/11/2016"}
   {:valor 2700.0M :tipo "receita" :comentario "Bico" :moeda "R$" :data "01/12/2016"}
   {:valor 29.0M :tipo "despesa" :comentario "Livro de clojure" :moeda "R$" :data "03/12/2016"}])

(defn texto-resumo-em-yuan [transacao]
  (-> (transacao-em-yuan transacao)
     (data-valor)))

;Quando o propósito de uma função é apenas combinar outdras funções
;passando para uma o resultado de outra
;há um forte indício de que podemos fazer o uso de composição de função

; função comp -> permite construir a composição de duas ou mais funções

(def texto-resumo-em-yuan (comp data-valor transacao-em-yuan))

;(def texto-resumo-em-yuan (comp transacao-em-yuan data-valor))

;defn é a 
;forma curta para (def nome-da-funcao (fn [argumento]
;(<alguma operação aqui>)))

;Partial application -> Aplicação parcial

;Existe um recurso em Clojure chamado de desestruturação, do inglês destructuring

(defn transacao-em-yuan [transacao]
  (let [{yuan :yuan} cotacoes]
      (assoc transacao :valor (* (:cotacao yuan) (:valor transacao))
                       :moeda (:simbolo yuan))))


(defn transacao-em-yuan [transacao]
  (let [{{cotacao :cotacao simbolo :simbolo} :yuan} cotacoes]
      (assoc transacao :valor (* cotacao (:valor transacao))
                       :moeda simbolo)))


(def cotacoes
  {:yuan {:cotacao 2.15M :simbolo "¥"}
   :euro {:cotacao 0.28M :simbolo "€"}})

(defn transacao-em-outra-moeda [moeda transacao]
  (let [{{cotacao :cotacao simbolo :simbolo} moeda} cotacoes]
      (assoc transacao :valor (* cotacao (:valor transacao))
                       :moeda simbolo)))

(transacao-em-outra-moeda :euro (first transacoes))

;A aplicação parcial de uma função significa que
;pegamos uma função que tem vários argumentos e criamos uma
;nova, baseada nela, mas que recebe menos argumentos.

(def transacao-em-euro (partial transacao-em-outra-moeda :euro))
(def transacao-em-yuan (partial transacao-em-outra-moeda :yuan))

(transacao-em-euro (first transacoes))
(transacao-em-yuan (first transacoes))

(clojure.string/join ", " (map texto-resumo-em-yuan transacoes))

;; uma função parcial que juntará elementos em uma string,
;; separando-os por vírgula
(def junta-tudo (partial clojure.string/join ", "))

(junta-tudo (map texto-resumo-em-yuan transacoes))