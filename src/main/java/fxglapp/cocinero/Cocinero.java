package fxglapp.cocinero;

public class Cocinero implements Runnable {
    private final Object monitor;
    private boolean debeCocinar;
    int comidaPreparada;

    public Cocinero(Object monitor) {
        this.monitor = monitor;
        this.debeCocinar = false;
        this.comidaPreparada = 0;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (monitor) {
                while (!debeCocinar) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Cocinero interrumpido, terminando...");
                        return;
                    }
                }
                // Cocinar
                prepararComida();
                debeCocinar = false;
                monitor.notifyAll();
            }
        }
    }

    private void prepararComida() {
        comidaPreparada++;
        System.out.println("Cocinero ha preparado comida. Total: " + comidaPreparada);
        try {
            Thread.sleep(2000); // Simula el tiempo que tarda en cocinar
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Cocinero interrumpido durante la preparación.");
        }
    }

    // Método para indicar que debe cocinar
    public void activarCocina() {
        synchronized (monitor) {
            debeCocinar = true;
            monitor.notifyAll(); // Notificar que puede empezar a cocinar
        }
    }
}
