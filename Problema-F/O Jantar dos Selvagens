import java.util.concurrent.Semaphore;
import java.util.Random;

class Caldeirao {
    private final int capacidade;
    private int porcoes = 0;
    private int vezesEnchido = 0;
    private int porcoesServidas = 0;

    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore caldeiraoVazio = new Semaphore(0);
    private final Semaphore caldeiraoCheio = new Semaphore(0);

    public Caldeirao(int capacidade) {
        this.capacidade = capacidade;
    }

    public void servir(int idSelvagem) {
        try {
            mutex.acquire();

            if (porcoes == 0) {
                System.out.println("  !! [Selvagem " + idSelvagem + "] Caldeirão vazio! Acordando o cozinheiro...");
                caldeiraoVazio.release();
                caldeiraoCheio.acquire();
            }
            porcoes--;
            porcoesServidas++;
            System.out.println("[Selvagem " + idSelvagem + "] Serviu-se de uma porção. Restam: " + porcoes);

            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void encher() {
        try {
            caldeiraoVazio.acquire();
            porcoes = capacidade;
            vezesEnchido++;
            System.out.println("\n  ** [COZINHEIRO] Encheu o caldeirão com " + capacidade + " porções e voltou a dormir.\n");
            caldeiraoCheio.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getVezesEnchido() {
        return vezesEnchido;
    }

    public int getPorcoesServidas() {
        return porcoesServidas;
    }

    public int getPorcoes() {
        return porcoes;
    }
}

class Cozinheiro extends Thread {
    private final Caldeirao caldeirao;

    public Cozinheiro(Caldeirao caldeirao) {
        this.caldeirao = caldeirao;
        setDaemon(true);
    }

    @Override
    public void run() {
        System.out.println("[COZINHEIRO] Dormindo... Aguardando o caldeirão esvaziar.");
        while (true) {
            caldeirao.encher();
        }
    }
}

class Selvagem extends Thread {
    private final int id;
    private final int refeicoes;
    private final Caldeirao caldeirao;
    private final Random random = new Random();

    public Selvagem(int id, int refeicoes, Caldeirao caldeirao) {
        this.id = id;
        this.refeicoes = refeicoes;
        this.caldeirao = caldeirao;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < refeicoes; i++) {
                caldeirao.servir(id);
                System.out.println("   [Selvagem " + id + "] Comendo...");
                Thread.sleep(200 + random.nextInt(600));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class MainSelvagens {
    public static void main(String[] args) throws InterruptedException {
        int nPorcoes = 5;
        int nSelvagens = 4;
        int refeicoes = 3;
        Caldeirao caldeirao = new Caldeirao(nPorcoes);

        System.out.println("=== INICIANDO SIMULAÇÃO: O JANTAR DOS SELVAGENS ===");

        Cozinheiro cozinheiro = new Cozinheiro(caldeirao);
        cozinheiro.start();

        Selvagem[] selvagens = new Selvagem[nSelvagens];
        for (int i = 0; i < nSelvagens; i++) {
            selvagens[i] = new Selvagem(i + 1, refeicoes, caldeirao);
            selvagens[i].start();
        }

        for (Selvagem s : selvagens) {
            s.join();
        }

        int totalEsperado = nSelvagens * refeicoes;
        int enchimentosEsperados = (int) Math.ceil((double) totalEsperado / nPorcoes);
        System.out.println("\n=== VALIDAÇÃO ===");
        System.out.println("Porções servidas: " + caldeirao.getPorcoesServidas() + " (esperado: " + totalEsperado + ")");
        System.out.println("Vezes que o caldeirão foi enchido: " + caldeirao.getVezesEnchido() + " (esperado: " + enchimentosEsperados + ")");
        System.out.println("Porções restantes no caldeirão: " + caldeirao.getPorcoes());
        boolean ok = caldeirao.getPorcoesServidas() == totalEsperado
                && caldeirao.getVezesEnchido() == enchimentosEsperados
                && caldeirao.getPorcoes() >= 0;
        System.out.println(ok ? "[OK] Nenhum selvagem se serviu de caldeirão vazio e o cozinheiro só encheu quando vazio."
                              : "[FALHA] Sincronização incorreta!");
    }
}
