package fxglapp.cocinero;

import com.almasb.fxgl.entity.Entity;
import fxglapp.ordenes.BufferOrdenes;
import fxglapp.ordenes.BufferComidas;
import fxglapp.ordenes.Orden;
import fxglapp.ordenes.EstadoOrden;

import static com.almasb.fxgl.dsl.FXGL.*;

public class CookerManager {
    private Entity cooker1;
    private Entity cooker2;
    private BufferOrdenes bufferOrdenes;
    private BufferComidas bufferComidas;

    public CookerManager(BufferOrdenes bufferOrdenes, BufferComidas bufferComidas) {
        this.bufferOrdenes = bufferOrdenes;
        this.bufferComidas = bufferComidas;
    }

    public void initCookers() {
        cooker1 = spawn("cooker", 300, -18);
        cooker2 = spawn("cooker", 600, -18);

        // Iniciar procesos de cocina concurrentes
        initCookerThread(cooker1);
        initCookerThread(cooker2);
    }

    private void initCookerThread(Entity cooker) {
        new Thread(() -> {
            while (true) {
                try {
                    Orden orden = bufferOrdenes.obtenerOrden();

                    prepararOrden(cooker, orden);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    private void prepararOrden(Entity cooker, Orden orden) throws InterruptedException {

        animarPreparacionOrden(cooker);

        Thread.sleep(3000);

        orden.setEstado(EstadoOrden.LISTO);
        System.out.println("[+] La ORDEN está lista! Cocinero: " + cooker);

        // Agregar al buffer de comidas
        bufferComidas.agregarComida(orden);
    }

    private void animarPreparacionOrden(Entity cooker) {
        System.out.println("[+] Cocinando");
    }
}