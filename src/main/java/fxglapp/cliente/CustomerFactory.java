package fxglapp.cliente;

import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class CustomerFactory implements EntityFactory {

    public enum EntityType {
        CUSTOMER
    }

    @Spawns("client_1")
    public Entity newCustomer(SpawnData data) {
        return new CustomerEntity();
    }

    public void spawnCustomer() {

        Entity customer = spawn("client_1", 65, 0);

        int tempAnimation = 2;

        for (int x = 0; x <= 10; x++) {
            runOnce(() -> {
                entityBuilder()
                        .at(customer.getPosition())
                        .with(new ProjectileComponent(new Point2D(0, 1), 1))
                        .buildAndAttach();

                customer.translate(0, 50);
            }, Duration.seconds(tempAnimation+1));

            tempAnimation += 1;
        }

    }

}
