import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.List;

class Controle {
    public final Semaphore semBart = new Semaphore(1);
    public final Semaphore semLisa = new Semaphore(0);
    public final Semaphore semMaggie = new Semaphore(0);

    private final List<String> historico = new ArrayList<>();
    private final Semaphore mutex = new Semaphore(1);

    public void move(String robo) {
        try {
            mutex.acquire();
            historico.add(robo);
            System.out.println("Movimento " + historico.size() + ": " + robo + " se moveu.");
            mutex.release();
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public List<String> getHistorico() {
        return historico;
    }
}

class Bart extends Thread {
    private final Controle controle;
    private final int ciclos;

    public Bart(Controle controle, int ciclos) {
        this.controle = controle;
        this.ciclos = ciclos;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < ciclos; i++) {
                controle.semBart.acquire();
                controle.move("Bart");
                controle.semLisa.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Lisa extends Thread {
    private final Controle controle;
    private final int ciclos;

    public Lisa(Controle controle, int ciclos) {
        this.controle = controle;
        this.ciclos = ciclos;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < ciclos * 2; i++) {
                controle.semLisa.acquire();
                controle.move("Lisa");
                if (i % 2 == 0) {
                    controle.semMaggie.release();
                } else {
                    controle.semBart.release();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Maggie extends Thread {
    private final Controle controle;
    private final int ciclos;

    public Maggie(Controle controle, int ciclos) {
        this.controle = controle;
        this.ciclos = ciclos;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < ciclos; i++) {
                controle.semMaggie.acquire();
                controle.move("Maggie");
                controle.semLisa.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class MainRobos {
    public static void main(String[] args) throws InterruptedException {
        int ciclos = 4;
        Controle controle = new Controle();

        System.out.println("=== INICIANDO SIMULAÇÃO: O PROBLEMA DOS TRÊS ROBÔS ===");

        Maggie maggie = new Maggie(controle, ciclos);
        Lisa lisa = new Lisa(controle, ciclos);
        Bart bart = new Bart(controle, ciclos);
        maggie.start();
        lisa.start();
        bart.start();

        bart.join();
        lisa.join();
        maggie.join();

        String[] padrao = {"Bart", "Lisa", "Maggie", "Lisa"};
        List<String> historico = controle.getHistorico();
        boolean ok = historico.size() == ciclos * padrao.length;
        for (int i = 0; i < historico.size() && ok; i++) {
            if (!historico.get(i).equals(padrao[i % padrao.length])) ok = false;
        }
        System.out.println("\n=== VALIDAÇÃO ===");
        System.out.println("Sequência obtida: " + String.join(" -> ", historico));
        System.out.println(ok ? "[OK] Os robôs se moveram na sequência Bart -> Lisa -> Maggie -> Lisa, um de cada vez."
                              : "[FALHA] Sequência incorreta!");
    }
}
