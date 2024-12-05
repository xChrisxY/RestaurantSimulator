package fxglapp.cocinero;

import com.almasb.fxgl.entity.Entity;
import fxglapp.ordenes.OrdenMonitor;
import fxglapp.ordenes.Orden;
import fxglapp.ordenes.EstadoOrden;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Cooker implements Runnable {
    private Entity cookerEntity;
    private final OrdenMonitor ordenMonitor;
    private final ScheduledExecutorService scheduledExecutorService;
    private volatile boolean isRunning = true;

    public Cooker(OrdenMonitor ordenMonitor) {
        this.ordenMonitor = ordenMonitor;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public Entity crearCocinero(double x, double y) {
        cookerEntity = entityBuilder()
                .at(x, y)
                .type(EntityTypes.COOKER)
                .viewWithBBox(texture("cooker.png"))
                .scale(0.45, 0.45)
                .buildAndAttach();
        return cookerEntity;
    }

    @Override
    public void run() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!isRunning) {
                scheduledExecutorService.shutdown();
                return;
            }

            try {
                // Obtener orden del monitor
                Orden orden = ordenMonitor.obtenerOrden();

                // Preparar la orden
                prepararOrden(orden);
            } catch (InterruptedException e) {
                System.out.println("Error en la preparación de orden: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private synchronized void prepararOrden(Orden orden) {
        if (cookerEntity == null) return;

        // Animación de cocinar
        animarPreparacionOrden();

        // Simular tiempo de preparación
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Cambiar estado de la orden
        orden.setEstado(EstadoOrden.LISTO);
        System.out.println("[+] La ORDEN está lista! Cocinero: " + cookerEntity);

        // Agregar comida al monitor
        try {
            ordenMonitor.agregarComida(orden);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void animarPreparacionOrden() {
        System.out.println("[+] Cocinando");

        // Ejemplo de animación similar al ejemplo proporcionado
        var currentPosition = cookerEntity.getPosition();
        var subirAnimacion = animationBuilder()
                .duration(javafx.util.Duration.seconds(1))
                .translate(cookerEntity)
                .from(currentPosition)
                .to(currentPosition.subtract(0, 150))
                .build();
        subirAnimacion.start();

        runOnce(() -> {
            var bajarAnimacion = animationBuilder()
                    .duration(javafx.util.Duration.seconds(1))
                    .translate(cookerEntity)
                    .from(cookerEntity.getPosition())
                    .to(currentPosition)
                    .build();
            bajarAnimacion.start();
        }, javafx.util.Duration.seconds(3));
    }

    public synchronized void detener() {
        isRunning = false;
        scheduledExecutorService.shutdown();
    }
}