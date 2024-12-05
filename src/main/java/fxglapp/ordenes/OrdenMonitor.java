package fxglapp.ordenes;

import com.almasb.fxgl.entity.Entity;
import java.util.concurrent.LinkedBlockingQueue;

public class OrdenMonitor {

    private LinkedBlockingQueue<Orden> bufferOrdenes;
    private LinkedBlockingQueue<Orden> bufferComidas;

    public OrdenMonitor() {
        this.bufferOrdenes = new LinkedBlockingQueue<>();
        this.bufferComidas = new LinkedBlockingQueue<>();
    }

    public synchronized void agregarOrden(Orden orden) throws InterruptedException {
        bufferOrdenes.put(orden);
        notifyAll();
    }


    public synchronized Orden obtenerOrden() throws InterruptedException {
        while (bufferOrdenes.isEmpty()) {
            wait();
        }
        return bufferOrdenes.take();
    }


    public synchronized void agregarComida(Orden orden) throws InterruptedException {
        bufferComidas.put(orden);
        notifyAll();
    }

    public synchronized Orden verificarComidaLista(Entity mesero) throws InterruptedException {
        while (bufferComidas.isEmpty()) {
            wait();
        }

        for (Orden orden : bufferComidas) {
            if (orden.getEstado() == EstadoOrden.LISTO) {
                bufferComidas.remove(orden);
                return orden;
            }
        }

        return null;
    }

    public synchronized boolean hayOrdenesPendientes() {
        return !bufferOrdenes.isEmpty();
    }

    public synchronized boolean hayComidasListas() {
        return !bufferComidas.isEmpty();
    }
}