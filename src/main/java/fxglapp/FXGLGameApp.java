package fxglapp;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import fxglapp.cliente.CustomerFactory;
import fxglapp.cliente.CustomerManager;
//import fxglapp.cocinero.CookerFactory;

import fxglapp.cocinero.CookerManager;
import fxglapp.mesero.WaiterFactory;
import fxglapp.mesero.WaiterManager;
import fxglapp.ordenes.BufferComidas;
import fxglapp.ordenes.BufferOrdenes;
import fxglapp.ui.FloorFactory;
import fxglapp.ui.GameWorldBuilder;

import static com.almasb.fxgl.dsl.FXGL.*;

public class FXGLGameApp extends GameApplication {

    private static final int TILE_SIZE = 64;
    private boolean[] tableOccupied = new boolean[12];

    private CustomerManager customerManager;
    private WaiterManager waiterManager;
    private CookerManager cookerManager;

    private BufferOrdenes bufferOrdenes = new BufferOrdenes();
    private BufferComidas bufferComidas = new BufferComidas();

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(850);
        settings.setHeight(670);
        settings.setTitle("Restaurant Game");
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new FloorFactory());
        getGameWorld().addEntityFactory(new CustomerFactory());
        getGameWorld().addEntityFactory(new WaiterFactory());
        //getGameWorld().addEntityFactory(new CookerFactory());

        GameWorldBuilder.createWorld(tableOccupied);

        waiterManager = new WaiterManager(bufferOrdenes, bufferComidas, tableOccupied);
        waiterManager.initWaiters();

        cookerManager = new CookerManager(bufferOrdenes, bufferComidas);
        cookerManager.initCookers();

        customerManager = new CustomerManager(tableOccupied, waiterManager);
        customerManager.spawnCustomersSequence();

        waiterManager.setCustomerManager(customerManager);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
