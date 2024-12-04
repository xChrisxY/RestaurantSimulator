package fxglapp.ordenes;

import com.almasb.fxgl.entity.Entity;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BufferComidas {

    private ConcurrentLinkedQueue<Orden> buffer = new ConcurrentLinkedQueue<>();
    private Lock lock = new ReentrantLock();
    private Condition comidaListaCondition = lock.newCondition();

    public void agregarComida(Orden orden) {
        lock.lock();
        try {
            buffer.add(orden);
            comidaListaCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public Orden verificarComidaLista(Entity mesero) throws InterruptedException {
        lock.lock();
        try {
            while (buffer.isEmpty()) {
                comidaListaCondition.await();
            }

            // Buscar una orden lista para este mesero
            for (Orden orden : buffer) {
                if (orden.getEstado() == EstadoOrden.LISTO) {
                    buffer.remove(orden);
                    return orden;
                }
            }

            return null;
        } finally {
            lock.unlock();
        }
    }
}