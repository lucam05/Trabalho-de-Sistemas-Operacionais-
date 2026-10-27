import java.util.concurrent.Semaphore;
import java.util.Random;

// Classe que gerencia a mesa e a sincronização dos fumantes e do agente
class Mesa {
    // Semáforos para cada fumante (inicializados em 0, pois começam bloqueados)
    public final Semaphore fumanteTabaco = new Semaphore(0);
    public final Semaphore fumantePapel = new Semaphore(0);
    public final Semaphore fumanteFosforo = new Semaphore(0);
    
    // Semáforo para o agente (controla a reposição na mesa)
    public final Semaphore agenteSem = new Semaphore(1);
    
    // Exclusão mútua para a mesa
    private final Semaphore mutex = new Semaphore(1);

    public void colocarIngredientes(int sorteio) {
        try {
            agenteSem.acquire(); // Aguarda o fumante terminar para colocar mais
            mutex.acquire();
            
            System.out.println("\n[AGENTE] Colocou ingredientes na mesa:");
            if (sorteio == 0) {
                System.out.println("  -> Papel e Fósforos na mesa. (Falta tabaco -> Acorda Fumante de Tabaco)");
                fumanteTabaco.release();
            } else if (sorteio == 1) {
                System.out.println("  -> Tabaco e Fósforos na mesa. (Falta papel -> Acorda Fumante de Papel)");
                fumantePapel.release();
            } else {
                System.out.println("  -> Tabaco e Papel na mesa. (Falta fósforos -> Acorda Fumante de Fósforos)");
                fumanteFosforo.release();
            }
            
            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Thread do Representante / Agente de Cigarros
class Agente extends Thread {
    private final Mesa mesa;
    private final Random random = new Random();

    public Agente(Mesa mesa) {
        this.mesa = mesa;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 5; i++) { // Realiza 5 ciclos de fornecimento
                int sorteio = random.nextInt(3); // Sorteia quais 2 ingredientes colocar
                mesa.colocarIngredientes(sorteio);
                Thread.sleep(2000); // Intervalo do agente
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Thread dos Fumantes
class Fumante extends Thread {
    private final String ingredienteQuePossui;
    private final Semaphore meuSemafaro;
    private final Mesa mesa;

    public Fumante(String ingredienteQuePossui, Semaphore meuSemafaro, Mesa mesa) {
        this.ingredienteQuePossui = ingredienteQuePossui;
        this.meuSemafaro = meuSemafaro;
        this.mesa = mesa;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 2; i++) { // Cada fumante fuma 2 vezes na simulação
                meuSemafaro.acquire(); // Fica bloqueado até que seus ingredientes estejam na mesa
                
                System.out.println("   [Fumante que tem " + ingredienteQuePossui + "] Pegou os ingredientes, fez o cigarro e foi fumar no fumódromo...");
                Thread.sleep(1500); // Tempo fumando
                System.out.println("   [Fumante que tem " + ingredienteQuePossui + "] Terminou de fumar e avisou o agente.");
                
                mesa.agenteSem.release(); // Libera o agente para colocar novos ingredientes
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Classe Principal com a Main para Execução e Validação
public class MainFumantes {
    public static void main(String[] args) {
        Mesa mesa = new Mesa();

        System.out.println("=== INICIANDO SIMULAÇÃO: O PROBLEMA DOS FUMANTES ===");

        // Criação das threads dos três fumantes e do agente
        Agente agente = new Agente(mesa);
        Fumante f1 = new Fumante("Tabaco", mesa.fumanteTabaco, mesa);
        Fumante f2 = new Fumante("Papel", mesa.fumantePapel, mesa);
        Fumante f3 = new Fumante("Fósforos", mesa.fumanteFosforo, mesa);

        // Inicialização das threads
        agente.start();
        f1.start();
        f2.start();
        f3.start();
    }
}
