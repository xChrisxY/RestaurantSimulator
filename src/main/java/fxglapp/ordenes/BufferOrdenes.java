package fxglapp.ordenes;

import fxglapp.models.Orden;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BufferOrdenes {
    private ConcurrentLinkedQueue<Orden> buffer = new ConcurrentLinkedQueue<>();
    private Lock lock = new ReentrantLock();
    private Condition ordenDisponible = lock.newCondition();

    public void agregarOrden(Orden orden) {
        lock.lock();
        try {
            buffer.add(orden);
            ordenDisponible.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public Orden obtenerOrden() throws InterruptedException {
        lock.lock();
        try {
            while (buffer.isEmpty()) {
                ordenDisponible.await();
            }
            return buffer.poll();
        } finally {
            lock.unlock();
        }
    }
}

