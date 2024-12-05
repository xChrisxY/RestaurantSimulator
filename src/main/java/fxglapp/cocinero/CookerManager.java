package fxglapp.cocinero;

import fxglapp.ordenes.OrdenMonitor;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class CookerManager {
    private Cooker cooker1;
    private Cooker cooker2;
    private OrdenMonitor ordenMonitor;
    private List<Cooker> cookers;

    public CookerManager(OrdenMonitor ordenMonitor) {
        this.ordenMonitor = ordenMonitor;
    }

    public void initCookers() {
        cooker1 = new Cooker(ordenMonitor);
        cooker2 = new Cooker(ordenMonitor);

        cooker1.crearCocinero(225, -90);
        cooker2.crearCocinero(525, -90);

        new Thread(cooker1).start();
        new Thread(cooker2).start();
    }

    public void stopCookers() {
        cooker1.detener();
        cooker2.detener();
    }

    public void detenerTodosLosCocinerros() {
        if (cookers != null) {
            for (Cooker cooker : cookers) {
                cooker.detener();
            }
        }
    }
}