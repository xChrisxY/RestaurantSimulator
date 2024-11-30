package fxglapp.ordenes;

import com.almasb.fxgl.entity.Entity;

public class Orden {
    private Entity cliente;
    private EstadoOrden estado;
    private String detalles;

    public Orden(Entity cliente) {
        this.cliente = cliente;
        this.estado = EstadoOrden.PENDIENTE;
    }

    public Entity getCliente() { return cliente; }
    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }
}