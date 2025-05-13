package com.dam.entity;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Bar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;
    private String imagenUrl; // 🔹 Nuevo campo para almacenar la imagen

    public String getImagenUrl() {
		return imagenUrl;
	}

	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	@ManyToOne
    @JoinColumn(name = "propietario_id")
	@JsonIgnore
    private Usuario propietario;

    @OneToMany(mappedBy = "bar", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Desayuno> desayunos; // Aquí definimos la lista de desayunos

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }

    public List<Desayuno> getDesayunos() { // ✅ Asegúrate de que este método exista
        return desayunos;
    }

    public void setDesayunos(List<Desayuno> desayunos) {
        this.desayunos = desayunos;
    }
}
