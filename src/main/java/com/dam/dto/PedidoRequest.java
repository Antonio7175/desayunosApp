package com.dam.dto;

public class PedidoRequest {
    private Long usuarioId;
    private Long barId;
    private Long desayunoId;

    // Getters y setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getBarId() {
        return barId;
    }

    public void setBarId(Long barId) {
        this.barId = barId;
    }

    public Long getDesayunoId() {
        return desayunoId;
    }

    public void setDesayunoId(Long desayunoId) {
        this.desayunoId = desayunoId;
    }
}
