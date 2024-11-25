package fxglapp.cliente;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import fxglapp.table.RestaurantManager;

import static com.almasb.fxgl.dsl.FXGL.*;

public class CustomerFactory implements EntityFactory {
    @Spawns("client_1")
    public Entity newCustomer(SpawnData data) {
        Entity customer = new CustomerEntity();
        // Usar el nuevo sistema de restaurante
        RestaurantManager.handleNewCustomer(customer);
        return customer;
    }
}