package fxglapp;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class FXGLGameApp extends GameApplication {

    private Entity player;
    private static final int PLAYER_SPEED = 5;
    private int score = 0;
    private Text scoreText;
    private Text difficultyText;
    private double obstacleDifficulty = 1.0;

    // Tipos de entidades para identificación
    public enum EntityType {
        PLAYER, OBSTACLE, COIN
    }

    // Componente de movimiento para obstáculos
    public class ObstacleMovementComponent extends Component {
        private double speedX;
        private double speedY;

        public ObstacleMovementComponent(double difficulty) {
            // La velocidad base se multiplica por la dificultad
            speedX = (Math.random() - 0.5) * 3 * difficulty;
            speedY = (Math.random() - 0.5) * 3 * difficulty;
        }

        @Override
        public void onUpdate(double tpf) {
            entity.translateX(speedX);
            entity.translateY(speedY);

            // Rebotar en los bordes de la pantalla
            if (entity.getX() <= 0 || entity.getX() >= 740) {
                speedX *= -1;
            }
            if (entity.getY() <= 0 || entity.getY() >= 540) {
                speedY *= -1;
            }
        }
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Juego de Obstáculos Dinámicos");
        settings.setVersion("1.0");
        settings.setWidth(800);
        settings.setHeight(600);
    }

    @Override
    protected void initGame() {
        // Crear jugador
        player = entityBuilder()
                .type(EntityType.PLAYER)
                .at(400, 300)
                .viewWithBBox(new Rectangle(40, 40, Color.BLUE))
                .collidable()
                .buildAndAttach();

        // Crear obstáculos iniciales
        for (int i = 0; i < 5; i++) {
            createObstacle();
        }

        // Crear monedas
        for (int i = 0; i < 3; i++) {
            createCoin();
        }

        // Aumentar dificultad cada 10 segundos
        runOnce(() -> {
            FXGL.getGameTimer().runAtInterval(() -> {
                obstacleDifficulty += 0.2;
                difficultyText.setText("Dificultad: " + String.format("%.1f", obstacleDifficulty));

                // Crear obstáculos adicionales con mayor dificultad
                createObstacle();
            }, Duration.seconds(10));
        }, Duration.seconds(5));
    }

    private void createObstacle() {
        Entity obstacle = entityBuilder()
                .type(EntityType.OBSTACLE)
                .at(Math.random() * 700, Math.random() * 500)
                .viewWithBBox(new Rectangle(60, 60, Color.RED))
                .with(new ObstacleMovementComponent(obstacleDifficulty))
                .collidable()
                .buildAndAttach();
    }

    private void createCoin() {
        Entity coin = entityBuilder()
                .type(EntityType.COIN)
                .at(Math.random() * 700, Math.random() * 500)
                .viewWithBBox(new Rectangle(20, 20, Color.GOLD))
                .collidable()
                .buildAndAttach();
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.W, () -> player.translateY(-PLAYER_SPEED));
        onKey(KeyCode.S, () -> player.translateY(PLAYER_SPEED));
        onKey(KeyCode.A, () -> player.translateX(-PLAYER_SPEED));
        onKey(KeyCode.D, () -> player.translateX(PLAYER_SPEED));
    }

    @Override
    protected void initPhysics() {
        // Manejar colisión con obstáculos
        onCollisionBegin(EntityType.PLAYER, EntityType.OBSTACLE, (player, obstacle) -> {
            showMessage("¡Chocaste con un obstáculo! Fin del juego");
            getGameController().exit();
        });

        // Manejar recolección de monedas
        onCollisionBegin(EntityType.PLAYER, EntityType.COIN, (player, coin) -> {
            coin.removeFromWorld();
            score += 10;
            scoreText.setText("Puntuación: " + score);
            createCoin();  // Crear una nueva moneda
        });
    }

    @Override
    protected void initUI() {
        // Texto de puntuación
        scoreText = new Text("Puntuación: 0");
        scoreText.setTranslateX(20);
        scoreText.setTranslateY(50);
        getGameScene().addUINode(scoreText);

        // Texto de dificultad
        difficultyText = new Text("Dificultad: 1.0");
        difficultyText.setTranslateX(20);
        difficultyText.setTranslateY(80);
        getGameScene().addUINode(difficultyText);

        // Instrucciones
        Text instructions = new Text("Usa WASD para moverte\nEvita los obstáculos rojos\nRecoge las monedas doradas");
        instructions.setTranslateX(20);
        instructions.setTranslateY(120);
        getGameScene().addUINode(instructions);
    }

    public static void main(String[] args) {
        launch(args);
    }
}