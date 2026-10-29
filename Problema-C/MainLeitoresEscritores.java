import java.util.concurrent.Semaphore;
import java.util.Random;

// Classe que gerencia o recurso compartilhado (vetor M) e os semáforos de controle
class BaseDados {
    private final int[] M = {3, 7, 1, 9}; // Vetor compartilhado mencionado no enunciado
    private int readCount = 0;             // Contador de leitores ativos
    
    // Semáforos de sincronização
    private final Semaphore mutex = new Semaphore(1); // Protege a variável readCount
    private final Semaphore db = new Semaphore(1);    // Garante exclusão mútua para escrita (e bloqueia leitores se houver escritor)

    // Operação de Leitura
    public void ler(int idLeitor, int indice) {
        try {
            // Seção de Entrada do Leitor
            mutex.acquire();
            readCount++;
            if (readCount == 1) {
                db.acquire(); // O primeiro leitor bloqueia o acesso dos escritores
            }
            mutex.release();

            // --- REGIÃO CRÍTICA DE LEITURA ---
            if (indice >= 0 && indice < M.length) {
                System.out.println("[LEITOR " + idLeitor + "] Leu M[" + indice + "] = " + M[indice]);
            } else {
                // Leitura geral do vetor se o índice não for especificado
                System.out.print("[LEITOR " + idLeitor + "] Leu o vetor M: [");
                for (int i = 0; i < M.length; i++) {
                    System.out.print(M[i] + (i < M.length - 1 ? ", " : ""));
                }
                System.out.println("]");
            }
            Thread.sleep(800); // Simula o tempo de leitura
            // ---------------------------------

            // Seção de Saída do Leitor
            mutex.acquire();
            readCount--;
            if (readCount == 0) {
                db.release(); // O último leitor libera o acesso para os escritores
            }
            mutex.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Operação de Escrita
    public void escrever(int idEscritor, int indice, int novoValor) {
        try {
            // Seção de Entrada do Escritor (Exclusivo)
            db.acquire();

            // --- REGIÃO CRÍTICA DE ESCRITA ---
            if (indice >= 0 && indice < M.length) {
                M[indice] = novoValor;
                System.out.println("--> [ESCRITOR " + idEscritor + "] Atualizou M[" + indice + "] = " + novoValor);
            }
            Thread.sleep(1200); // Simula o tempo de escrita
            // ---------------------------------

            // Seção de Saída do Escritor
            db.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Thread Leitora
class Leitor extends Thread {
    private final int id;
    private final BaseDados baseDados;
    private final Random random = new Random();

    public Leitor(int id, BaseDados baseDados) {
        this.id = id;
        this.baseDados = baseDados;
    }

    @Override
    public void run() {
        // Escolhe aleatoriamente se vai ler um índice específico ou o vetor todo
        int indice = random.nextBoolean() ? random.nextInt(4) : -1;
        baseDados.ler(id, indice);
    }
}

// Thread Escritora
class Escritor extends Thread {
    private final int id;
    private final BaseDados baseDados;
    private final Random random = new Random();

    public Escritor(int id, BaseDados baseDados) {
        this.id = id;
        this.baseDados = baseDados;
    }

    @Override
    public void run() {
        int indice = random.nextInt(4);       // Posição aleatória do vetor M
        int novoValor = random.nextInt(10);   // Novo valor entre 0 e 9
        baseDados.escrever(id, indice, novoValor);
    }
}

// Classe Principal com a Main para Execução e Validação
public class MainLeitoresEscritores {
    public static void main(String[] args) {
        BaseDados baseDados = new BaseDados();

        System.out.println("=== INICIANDO SIMULAÇÃO: LEITORES E ESCRITORES ===");

        // Criação de várias threads de leitores e escritores de forma concorrente
        Leitor l1 = new Leitor(1, baseDados);
        Escritor e1 = new Escritor(1, baseDados);
        Leitor l2 = new Leitor(2, baseDados);
        Leitor l3 = new Leitor(3, baseDados);
        Escritor e2 = new Escritor(2, baseDados);
        Leitor l4 = new Leitor(4, baseDados);

        // Disparando as threads concorrentemente
        l1.start();
        e1.start();
        l2.start();
        l3.start();
        e2.start();
        l4.start();
    }
}
