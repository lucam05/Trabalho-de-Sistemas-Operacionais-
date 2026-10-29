import java.util.concurrent.Semaphore;

// Classe que gerencia a barbearia, as cadeiras e a sincronização
class Barbearia {
    private final int numCadeiras;
    private int waiting = 0; // Quantidade de clientes na sala de espera
    
    // Semáforos exigidos pelo problema
    private final Semaphore mutex = new Semaphore(1);     // Exclusão mútua para alterar 'waiting'
    private final Semaphore customers = new Semaphore(0); // Conta clientes esperando
    private final Semaphore barbers = new Semaphore(0);   // Indica barbeiro livre/ocupado

    public Barbearia(int numCadeiras) {
        this.numCadeiras = numCadeiras;
    }

    // Método chamado pela thread do Cliente
    public void cortarCabelo(int idCliente) {
        try {
            mutex.acquire(); // Entra na região crítica
            if (waiting < numCadeiras) {
                waiting++;
                System.out.println("-> Cliente " + idCliente + " sentou na sala de espera. Clientes esperando: " + waiting);
                customers.release(); // Acorda o barbeiro (incrementa o semáforo de clientes)
                mutex.release();     // Libera a região crítica
                
                barbers.acquire();   // Aguarda a sua vez de cortar o cabelo
                receberCorte(idCliente);
            } else {
                // Se a barbearia estiver cheia, libera o mutex e vai embora
                mutex.release();
                System.out.println(">>> Barbearia lotada! Cliente " + idCliente + " foi embora sem cortar o cabelo.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Simulação do atendimento na cadeira do barbeiro
    private void receberCorte(int idCliente) {
        System.out.println("  [+] Barbeiro está cortando o cabelo do Cliente " + idCliente + "...");
        try {
            Thread.sleep(1500); // Tempo do corte de cabelo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("  [-] Cliente " + idCliente + " terminou o corte e saiu da barbearia.");
    }

    // Método chamado pela thread do Barbeiro
    public void proximoCliente() {
        try {
            customers.acquire(); // Bloqueia/Dorme se não houver clientes
            mutex.acquire();     // Região crítica para atualizar a contagem de espera
            waiting--;           // Cliente sai da espera e vai para a cadeira do barbeiro
            barbers.release();   // Libera o barbeiro para o cliente
            mutex.release();     // Libera a região crítica
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Thread que representa o Barbeiro
class Barbeiro extends Thread {
    private final Barbearia barbearia;

    public Barbeiro(Barbearia barbearia) {
        this.barbearia = barbearia;
    }

    @Override
    public void run() {
        System.out.println("[BARBEIRO] Chegou para trabalhar e abriu a barbearia.");
        while (true) {
            System.out.println("[BARBEIRO] Dormindo... Aguardando clientes.");
            barbearia.proximoCliente(); // Fica bloqueado até chegar um cliente
        }
    }
}

// Thread que representa cada Cliente
class Cliente extends Thread {
    private final int id;
    private final Barbearia barbearia;

    public Cliente(int id, Barbearia barbearia) {
        this.id = id;
        this.barbearia = barbearia;
    }

    @Override
    public void run() {
        System.out.println("Cliente " + id + " chegou à barbearia.");
        barbearia.cortarCabelo(id);
    }
}

// Classe Principal com a Main para Executar no Compilador Online
public class Main {
    public static void main(String[] args) {
        int nCadeiras = 3; // Total de cadeiras na sala de espera
        Barbearia barbearia = new Barbearia(nCadeiras);

        // Inicia a thread do Barbeiro
        Barbeiro barbeiro = new Barbeiro(barbearia);
        barbeiro.start();

        // Simula a chegada de 10 clientes sequencialmente
        for (int i = 1; i <= 10; i++) {
            Cliente cliente = new Cliente(i, barbearia);
            cliente.start();
            try {
                Thread.sleep(500); // Chegada de um novo cliente a cada 500ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
