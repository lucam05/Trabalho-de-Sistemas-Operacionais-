import java.util.concurrent.Semaphore;
import java.util.Random;

class MesaJantar {
    private final int numFilosofos;
    private final Semaphore[] hashis;
    private final Semaphore garcom;
    private final Semaphore mutex = new Semaphore(1);
    private final boolean[] comendo;
    private int violacoes = 0;

    public MesaJantar(int numFilosofos) {
        this.numFilosofos = numFilosofos;
        this.hashis = new Semaphore[numFilosofos];
        for (int i = 0; i < numFilosofos; i++) {
            hashis[i] = new Semaphore(1);
        }
        this.garcom = new Semaphore(numFilosofos - 1);
        this.comendo = new boolean[numFilosofos];
    }

    public void pegarHashis(int id) {
        int direita = id;
        int esquerda = (id + 1) % numFilosofos;
        try {
            garcom.acquire();
            hashis[direita].acquire();
            System.out.println("   [Filósofo " + id + "] Pegou o hashi p" + direita + " (direita).");
            hashis[esquerda].acquire();
            System.out.println("   [Filósofo " + id + "] Pegou o hashi p" + esquerda + " (esquerda).");

            mutex.acquire();
            int vizinhoDireita = (id + numFilosofos - 1) % numFilosofos;
            int vizinhoEsquerda = (id + 1) % numFilosofos;
            if (comendo[vizinhoDireita] || comendo[vizinhoEsquerda]) {
                violacoes++;
            }
            comendo[id] = true;
            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void devolverHashis(int id) {
        int direita = id;
        int esquerda = (id + 1) % numFilosofos;
        try {
            mutex.acquire();
            comendo[id] = false;
            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        hashis[esquerda].release();
        hashis[direita].release();
        System.out.println("   [Filósofo " + id + "] Devolveu os hashis p" + direita + " e p" + esquerda + ".");
        garcom.release();
    }

    public int getViolacoes() {
        return violacoes;
    }
}

class Filosofo extends Thread {
    private final int id;
    private final int refeicoes;
    private final MesaJantar mesa;
    private final Random random = new Random();
    private int vezesQueComeu = 0;

    public Filosofo(int id, int refeicoes, MesaJantar mesa) {
        this.id = id;
        this.refeicoes = refeicoes;
        this.mesa = mesa;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < refeicoes; i++) {
                System.out.println("[Filósofo " + id + "] Meditando...");
                Thread.sleep(random.nextInt(500));

                System.out.println("[Filósofo " + id + "] Está com fome.");
                mesa.pegarHashis(id);

                System.out.println(">>> [Filósofo " + id + "] COMENDO (refeição " + (i + 1) + ")");
                Thread.sleep(300 + random.nextInt(500));
                vezesQueComeu++;

                mesa.devolverHashis(id);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getVezesQueComeu() {
        return vezesQueComeu;
    }
}

public class MainFilosofos {
    public static void main(String[] args) throws InterruptedException {
        int numFilosofos = 5;
        int refeicoes = 3;
        MesaJantar mesa = new MesaJantar(numFilosofos);

        System.out.println("=== INICIANDO SIMULAÇÃO: O JANTAR DOS FILÓSOFOS ===");

        Filosofo[] filosofos = new Filosofo[numFilosofos];
        for (int i = 0; i < numFilosofos; i++) {
            filosofos[i] = new Filosofo(i, refeicoes, mesa);
            filosofos[i].start();
        }

        for (Filosofo f : filosofos) {
            f.join();
        }

        System.out.println("\n=== VALIDAÇÃO ===");
        boolean ok = true;
        for (int i = 0; i < numFilosofos; i++) {
            System.out.println("Filósofo " + i + " comeu " + filosofos[i].getVezesQueComeu() + " vez(es).");
            if (filosofos[i].getVezesQueComeu() != refeicoes) ok = false;
        }
        System.out.println("Vizinhos comendo ao mesmo tempo: " + mesa.getViolacoes());
        if (mesa.getViolacoes() > 0) ok = false;
        System.out.println(ok ? "[OK] Sem deadlock, sem starvation e sem vizinhos comendo juntos."
                              : "[FALHA] Sincronização incorreta!");
    }
}
