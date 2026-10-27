# Trabalho-de-Sistemas-Operacionais-

---

# Documentação — Trabalho Prático 1: Comunicação e Sincronização de Processos e Threads

**Disciplina:** Sistemas Operacionais

**Curso:** Engenharia de Software — PUC Minas

---

## 🛠️ Tecnologias e Conceitos Utilizados

* **Linguagem:** Java (JDK 8 ou superior)
* **Concorrência:** Programação multithread nativa (`Thread`)
* **Sincronização:** Semáforos da biblioteca `java.util.concurrent.Semaphore`
* **Prevenção de Deadlocks e Condições de Corrida:** Exclusão mútua em regiões críticas com semáforos e sem o uso de espera ocupada (*busy wait*).

---

## 💈 Letra b) Problema do Barbeiro Dorminhoco

### 1. Descrição do Problema

O problema consiste em simular a operação de uma barbearia com **1 barbeiro**, **1 cadeira de atendimento** e **$N$ cadeiras na sala de espera**.

* Se não houver clientes, o barbeiro dorme.


* Quando chega um cliente e o barbeiro está dormindo, o cliente o acorda e senta-se na cadeira de corte.


* Se chegam outros clientes enquanto o barbeiro está ocupado:
* Sentam em uma das cadeiras vagos na sala de espera.


* Se todas as cadeiras da sala de espera estiverem ocupadas, o cliente vai embora sem cortar o cabelo.





### 2. Modelagem e Estrutura

* **Threads:**
* `Barbeiro`: Executa em loop infinito, bloqueando-se para dormir quando não há clientes e acordando para atender quando recebe a sinalização.


* `Cliente`: Tenta entrar na barbearia e verifica se há espaço livre na sala de espera.




* **Recursos Compartilhados e Região Crítica:**
* `waiting`: Variável inteira que armazena a quantidade atual de clientes na sala de espera. A leitura e modificação dessa variável é uma **região crítica** e deve ser protegida por exclusão mútua.




* **Mecanismos de Sincronização (Semáforos):**
* `mutex` (`Semaphore(1)`): Garante acesso exclusivo à variável compartilhada `waiting`.


* `customers` (`Semaphore(0)`): Conta quantos clientes estão aguardando na barbearia. Bloqueia o barbeiro até que um cliente chegue.


* `barbers` (`Semaphore(0)`): Indica a disponibilidade do barbeiro para atender um cliente que está na sala de espera.





---

## 📚 Letra c) Problema dos Leitores e Escritores

### 1. Descrição do Problema

Várias threads (tarefas concorrentes) tentam acessar uma área de memória compartilhada (um vetor de inteiros $M$) para realizar leituras ou escritas:

* **Leitores:** Podem ler o vetor simultaneamente em paralelo, pois leituras concorrentes não alteram os dados e não causam conflito.


* **Escritores:** Precisam de acesso **exclusivo** ao vetor $M$, pois alterações simultâneas (ou durante leituras) geram condições de corrida (*race conditions*) e corrupção de dados.



### 2. Modelagem e Estrutura

* **Threads:**
* `Leitor`: Acessa dados no vetor $M$ em paralelo com outros leitores.
* `Escritor`: Altera valores em posições do vetor $M$ com acesso exclusivo.


* **Recursos Compartilhados e Região Crítica:**
* `M`: Vetor de inteiros compartilhado (`int[] M = {3, 7, 1, 9}`).


* `readCount`: Variável de controle do número de leitores ativos na área compartilhada.


* **Mecanismos de Sincronização (Semáforos):**
* `mutex` (`Semaphore(1)`): Protege a atualização do contador de leitores (`readCount`).
* `db` (`Semaphore(1)`): Garante exclusão mútua para escrita no vetor $M$. O primeiro leitor a chegar adquire o semáforo `db` para impedir que escritores modifiquem os dados enquanto houver leituras ativas, e o último leitor a sair o libera.

---

## 🚬 Letra d) Problema dos Fumantes

### 1. Descrição do Problema

O problema clássico de sincronização dos fumantes envolve **três threads fumantes** e **uma thread representante (agente)**.

* Cada fumante possui estoques infinitos de apenas **um** ingrediente específico necessário para fazer e fumar cigarros (um tem apenas tabaco, outro tem apenas papel e o terceiro tem apenas fósforos).


* Para fumar, são necessários **todos os três ingredientes** (tabaco, papel e fósforos).


* O agente possui um estoque infinito dos três materiais, escolhe aleatoriamente dois ingredientes diferentes, coloca-os na mesa e desbloqueia o fumante que possui o ingrediente restante.


* O fumante selecionado remove os ingredientes da mesa, faz o cigarro, fuma por um tempo simulado no fumódromo e, ao terminar, sinaliza para o agente que o ciclo pode recomeçar.



### 2. Modelagem e Estrutura

* **Threads:**
* `Agente`: Executa ciclos onde sorteia dois ingredientes distintos e os disponibiliza na mesa, aguardando o término do consumo para reiniciar o ciclo.


* `Fumante`: Três threads distintas (Tabaco, Papel e Fósforos) que permanecem bloqueadas até que o agente coloque na mesa os dois ingredientes que faltam para cada uma.




* **Recursos Compartilhados e Região Crítica:**
* A mesa de compartilhamento de ingredientes controlada por exclusão mútua.


* **Mecanismos de Sincronização (Semáforos):**
* `agenteSem` (`Semaphore(1)`): Controla o acesso do agente para colocar novos ingredientes, garantindo que ele só coloque novos itens após o fumante atual terminar de fumar.
* `fumanteTabaco`, `fumantePapel`, `fumanteFosforo` (`Semaphore(0)`): Semáforos individuais para cada fumante, mantendo-os bloqueados até que os ingredientes correspondentes estejam disponíveis na mesa.


* `mutex` (`Semaphore(1)`): Garante exclusão mútua ao manipular os recursos na mesa.



---

## 🍜 Letra e) Problema dos Filósofos

### 1. Descrição do Problema

Cinco filósofos estão sentados em uma mesa redonda e alternam suas vidas entre **meditar** e **comer**. Entre cada par de filósofos vizinhos há **um hashi (palito)**, totalizando **5 hashis compartilhados**.

* Para comer, o filósofo $f_i$ precisa pegar o hashi à sua direita ($p_i$) e o hashi à sua esquerda ($p_{i+1}$), um de cada vez.


* Como os hashis são compartilhados, **dois filósofos vizinhos não podem comer ao mesmo tempo**.


* Se todos os filósofos pegarem o hashi da direita ao mesmo tempo, todos ficam esperando o da esquerda para sempre, gerando um **deadlock**.



### 2. Modelagem e Estrutura

* **Threads:**
* `Filosofo`: Cinco threads que executam ciclos de meditar → ficar com fome → pegar os hashis → comer → devolver os hashis.


* **Recursos Compartilhados e Região Crítica:**
* `hashis[]`: Vetor com os 5 hashis da mesa. Cada hashi é disputado pelos dois filósofos vizinhos a ele.


* `comendo[]`: Tabela de estados dos filósofos, usada para validar que dois vizinhos nunca comem juntos. Sua leitura e escrita é uma **região crítica**.


* **Mecanismos de Sincronização (Semáforos):**
* `hashis[i]` (`Semaphore(1)`): Um semáforo binário por hashi. Garante que cada hashi esteja na mão de apenas um filósofo por vez.


* `garcom` (`Semaphore(N-1)`): Permite que no máximo **4 dos 5 filósofos** tentem pegar hashis ao mesmo tempo.


* `mutex` (`Semaphore(1)`): Garante exclusão mútua ao atualizar a tabela de estados `comendo[]`.



### 3. Estratégia para Evitar Deadlock

Com o semáforo `garcom` limitando a disputa a $N-1$ filósofos, sempre sobra pelo menos um filósofo que consegue pegar os **dois** hashis, comer e liberá-los para os demais. Assim, a espera circular (condição necessária para o deadlock) nunca se forma.

---

## 🍲 Letra f) O Jantar dos Selvagens

### 1. Descrição do Problema

Uma tribo de selvagens janta ao redor de um caldeirão que comporta **$N$ porções**.

* Quando um selvagem quer comer, ele se serve de uma porção do caldeirão.


* Se o caldeirão estiver vazio, o selvagem **acorda o cozinheiro** e espera que ele encha o caldeirão novamente antes de se servir.


* Depois de encher o caldeirão, o cozinheiro volta a dormir.


* **Restrições:** selvagens não podem se servir ao mesmo tempo (mas podem comer ao mesmo tempo), não podem se servir com o caldeirão vazio, e o cozinheiro só pode encher o caldeirão quando ele estiver vazio.



### 2. Modelagem e Estrutura

* **Threads:**
* `Selvagem`: Várias threads que se servem do caldeirão e depois comem por um tempo aleatório (o ato de comer fica fora da região crítica, permitindo que vários comam em paralelo).


* `Cozinheiro`: Executa em loop infinito, dormindo até ser acordado e enchendo o caldeirão com $N$ porções.


* **Recursos Compartilhados e Região Crítica:**
* `porcoes`: Variável inteira com a quantidade atual de porções no caldeirão. Verificar se está vazio e retirar uma porção é a **região crítica**.


* **Mecanismos de Sincronização (Semáforos):**
* `mutex` (`Semaphore(1)`): Garante que apenas um selvagem se sirva por vez.


* `caldeiraoVazio` (`Semaphore(0)`): Mantém o cozinheiro dormindo até que um selvagem encontre o caldeirão vazio e o acorde.


* `caldeiraoCheio` (`Semaphore(0)`): Bloqueia o selvagem que acordou o cozinheiro até que o caldeirão esteja cheio novamente.



### 3. Estratégia para Evitar Deadlock

O selvagem que encontra o caldeirão vazio continua com o `mutex` enquanto espera o cozinheiro. Isso não causa deadlock porque o cozinheiro **não precisa do `mutex`** para encher o caldeirão; ele depende apenas de `caldeiraoVazio`. Ao mesmo tempo, isso impede que outros selvagens tentem se servir (ou acordem o cozinheiro de novo) enquanto o caldeirão está sendo enchido.

---

## 🤖 Letra g) Problema dos Três Robôs

### 1. Descrição do Problema

Três robôs (**Bart**, **Lisa** e **Maggie**), cada um controlado por sua própria thread, devem se mover sempre na sequência:

**Bart → Lisa → Maggie → Lisa → Bart → Lisa → Maggie → ...**

* Apenas **um robô se move por vez**, e o movimento é indicado pela chamada `move()`.


* A solução não pode usar **espera ocupada** (*busy wait*).



### 2. Modelagem e Estrutura

* **Threads:**
* `Bart`: Espera a sua vez, se move e passa a vez para Lisa.


* `Lisa`: Se move **duas vezes por ciclo**. Na primeira (vindo de Bart) passa a vez para Maggie; na segunda (vindo de Maggie) passa a vez para Bart.


* `Maggie`: Espera a sua vez, se move e passa a vez para Lisa.


* **Recursos Compartilhados e Região Crítica:**
* A "vez" de se mover, controlada pelos semáforos.


* `historico`: Lista com a sequência real dos movimentos, usada para validação. A inserção na lista é uma **região crítica**.


* **Mecanismos de Sincronização (Semáforos):**
* `semBart` (`Semaphore(1)`): Começa em **1** porque Bart é o primeiro a se mover.


* `semLisa` (`Semaphore(0)`) e `semMaggie` (`Semaphore(0)`): Começam em **0**, mantendo Lisa e Maggie bloqueadas até receberem a vez.


* `mutex` (`Semaphore(1)`): Protege o acesso à lista `historico`.



### 3. Estratégia para Evitar Deadlock e Busy Wait

A cada instante, **exatamente um** dos semáforos `semBart`, `semLisa` e `semMaggie` tem valor 1, e quem se move sempre libera o próximo da sequência. Assim, o "bastão" passa de robô em robô e nunca se perde. As threads ficam bloqueadas em `acquire()` (sem consumir CPU) enquanto não é a sua vez. As threads são iniciadas fora de ordem de propósito (Maggie, Lisa, Bart) para mostrar que a sequência depende apenas dos semáforos.

---

### **Instruções para Teste e Execução**

1. Acesse qualquer compilador Java online (ex.: [OnlineGDB](https://www.onlinegdb.com/online_java_compiler) ou [JDoodle](https://www.jdoodle.com/online-java-compiler)).
2. Cole o código correspondente no editor.
3. Execute e verifique a saída no console:
* **Barbeiro Dorminhoco:** Observe os clientes lotando as cadeiras, o barbeiro acordando/dormindo e os clientes sendo dispensados quando as cadeiras estão ocupadas.


* **Leitores e Escritores:** Observe múltiplos leitores lendo sem bloquear uns aos outros e os escritores exigindo bloqueio exclusivo para modificação de dados.


* **Fumantes:** Acompanhe o agente sorteando os pares de ingredientes e o fumante correspondente pegando os itens, fumando e liberando o agente para o próximo ciclo.


* **Filósofos:** Observe os filósofos pegando e devolvendo os hashis sem que dois vizinhos comam juntos. Ao final, a seção `VALIDAÇÃO` mostra quantas vezes cada um comeu e confirma que não houve deadlock.


* **Selvagens:** Observe os selvagens se servindo um de cada vez e o cozinheiro sendo acordado somente quando o caldeirão esvazia. A seção `VALIDAÇÃO` confere o total de porções servidas e o número de vezes que o caldeirão foi enchido.


* **Três Robôs:** Observe os movimentos numerados. A seção `VALIDAÇÃO` compara a sequência obtida com o padrão Bart → Lisa → Maggie → Lisa.




> **Observação:** Os programas das letras e, f e g possuem a classe principal com nomes `MainFilosofos`, `MainSelvagens` e `MainRobos`. Para compilar localmente, salve o arquivo com o nome da classe (ex.: `MainFilosofos.java`) e execute `javac MainFilosofos.java && java MainFilosofos`.
