# Restaurant Simulator 🍽️

![FXGL](https://img.shields.io/badge/FXGL-17-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![License](https://img.shields.io/badge/license-MIT-green)
Restaurant Simulator es una aplicación de simulación desarrollada con FXGL que modela el funcionamiento de un restaurante, incluyendo la gestión de clientes, meseros y cocineros utilizando conceptos de concurrencia y multithreading.
## 🎯 Características del Simulador
- **Simulación de clientes**: Llegada aleatoria de clientes siguiendo una distribución Poisson
- **Sistema de filas de espera**: Gestión de clientes cuando el restaurante está lleno
- **Meseros concurrentes**: Múltiples meseros atendiendo mesas de manera simultánea
- **Cocineros en paralelo**: Preparación de órdenes con buffers compartidos
- **Sincronización**: Uso de locks y condiciones para coordinar hilos
- **Interfaz visual**: Mundo 2D con tiles y sprites personalizados
## 🛠️ Tecnologías y Conceptos Implementados
- **FXGL**: Framework para desarrollo de juegos/simulaciones en Java
- **Multithreading**: Manejo de hilos para simular concurrencia real
- **Buffers compartidos**: Comunicación productor-consumidor entre meseros y cocineros
- **Locks y Conditions**: Sincronización avanzada de hilos
- **Colas concurrentes**: ConcurrentLinkedQueue para operaciones thread-safe
- **Programación basada en entidades**: Sistema ECS (Entity-Component-System)
## 📁 Estructura del Proyecto

src/  
├── main/  
│ └── java/  
│ └── fxglapp/  
│ ├── FXGLGameApp.java # Clase principal  
│ ├── cliente/  
│ │ ├── CustomerEntity.java # Entidad cliente  
│ │ ├── CustomerFactory.java # Fábrica de clientes  
│ │ └── CustomerManager.java # Gestión de clientes (hilos)  
│ ├── cocinero/  
│ │ ├── CookerEntity.java # Entidad cocinero  
│ │ ├── CookerFactory.java # Fábrica de cocineros  
│ │ └── CookerManager.java # Gestión de cocineros (hilos)  
│ ├── mesero/  
│ │ ├── WaiterEntity.java # Entidad mesero  
│ │ ├── WaiterFactory.java # Fábrica de meseros  
│ │ └── WaiterManager.java # Gestión de meseros (hilos)  
│ ├── ordenes/  
│ │ ├── BufferComidas.java # Buffer thread-safe de comidas  
│ │ ├── BufferOrdenes.java # Buffer thread-safe de órdenes  
│ │ ├── EstadoOrden.java # Enum de estados  
│ │ └── Orden.java # Modelo de orden  
│ └── ui/  
│ ├── FloorFactory.java # Fábrica de elementos visuales  
│ └── GameWorldBuilder.java # Constructor del mundo  
└── resources/  
└── assets/  
├── images/ # Sprites del juego  
├── sounds/ # Efectos de sonido  
└── fonts/ # Fuentes personalizadas

text

## 🧠 Implementación de Concurrencia
### 1. Gestión de Clientes (Hilos)
```java
// Llegada de clientes con distribución Poisson
public void spawnCustomersSequence() {
    double lambda = 0.5; // Tasa de llegada
    Random random = new Random();
    
    for (int i = 1; i <= TOTAL_CUSTOMERS; i++) {
        double timeBetweenArrivals = -Math.log(1.0 - random.nextDouble()) / lambda;
        
        runOnce(() -> {
            Entity customer = spawn("client_1", 65, 0);
            // Lógica de asignación de mesa o fila de espera
        }, Duration.seconds(timeBetweenArrivals * i));
    }
}
```

### 2. Buffers Compartidos Thread-Safe

```java

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
            // Buscar orden lista para el mesero
            // ...
        } finally {
            lock.unlock();
        }
    }
}
``` 


### 3. Gestión de Mesas Compartidas

```java

private boolean[] tableOccupied = new boolean[12]; // Recurso compartido
// Los meseros acceden concurrentemente a las mesas
public synchronized void assignWaiterToTable(int tableIndex, Entity waiter) {
    if (!tableOccupied[tableIndex]) {
        tableOccupied[tableIndex] = true;
        // Asignar mesero a la mesa
    }
}
``` 

## ⚙️ Configuración del Simulador

```java

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
        settings.setTitle("Restaurant Simulator");
    }
}
``` 

## 🎮 Elementos del Mundo

### Tiles y Decoración

- **Piso de cocina**: Textura con líneas
- **Piso de concreto**: Textura con variaciones aleatorias
- **Mesas**: Para clientes (12 mesas en total)
- **Sillas**: Acompañan a cada mesa
- **Equipamiento de cocina**: Estufas, mesas de trabajo, lavaplatos
- **Elementos decorativos**: Arbustos, lámparas, botes de basura

### Posiciones de Mesas

```java

private static final int[][] TABLE_POSITIONS = {
    {3, 3}, {5, 3}, {7, 3}, {9, 3},  // Fila superior
    {3, 5}, {5, 5}, {7, 5}, {9, 5},  // Fila media
    {3, 7}, {5, 7}, {7, 7}, {9, 7}   // Fila inferior
};
```  

## 🚀 Instalación y Ejecución

1. **Clona el repositorio**
    
    ```bash
    git clone https://github.com/tuusuario/restaurant-simulator.git
    cd restaurant-simulator
    ``` 
    
2. **Compila el proyecto**
    
    ```bash
    
    # Usando Maven
    mvn clean compile
    # Usando Gradle
    gradle build
    ``` 
    
3. **Ejecuta la simulación**
    
    ```bash
    
    # Usando Maven
    mvn exec:java -Dexec.mainClass="fxglapp.FXGLGameApp"
    # Usando Gradle
    gradle run
    ``` 

## 📊 Parámetros de Simulación

- **TASA_LLEGADA_CLIENTES**: λ = 0.5 (distribución Poisson)
    
- **CAPACIDAD_RESTAURANTE**: 12 clientes simultáneos
    
- **TOTAL_CLIENTES**: 20 por simulación
    
- **TIEMPO_SERVICIO**: Variable según el mesero
    
- **TIEMPO_COCCION**: Variable según el cocinero
    

## 🔄 Flujo de la Simulación

1. **Llegada de clientes** (distribución Poisson)
    
2. **Asignación de mesa** o fila de espera
    
3. **Toma de orden** por mesero
    
4. **Buffer de órdenes** (cocineros consumen)
    
5. **Preparación de comida** (cocineros producen)
    
6. **Buffer de comidas** (meseros consumen)
    
7. **Entrega de comida** al cliente
    
8. **Salida del cliente** y liberación de mesa
