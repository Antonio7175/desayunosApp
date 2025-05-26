package com.dam.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
public class Comanda {

    @Id @GeneratedValue
    private Long id;
    
    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ComandaItem> items;


    public List<ComandaItem> getItems() {
		return items;
	}

	public void setItems(List<ComandaItem> items) {
		this.items = items;
	}

	@Column(unique = true, nullable = false)
    private String codigoUnico;

    @ManyToOne
    @JoinColumn(name = "admin_usuario_id", nullable = false)
    private Usuario admin;

    @ManyToOne
    @JoinColumn(name = "bar_id", nullable = false)
    private Bar bar;

    @Enumerated(EnumType.STRING)
    private EstadoComanda estado = EstadoComanda.ABIERTA;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private LocalDateTime horaVisita;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public Usuario getAdmin() {
        return admin;
    }

    public void setAdmin(Usuario admin) {
        this.admin = admin;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public EstadoComanda getEstado() {
        return estado;
    }

    public void setEstado(EstadoComanda estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getHoraVisita() {
        return horaVisita;
    }

    public void setHoraVisita(LocalDateTime horaVisita) {
        this.horaVisita = horaVisita;
    }
}
