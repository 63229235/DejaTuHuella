package com.proyecto.dejatuhuella.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "carritos")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CarritoItem> items = new HashSet<>();

    // Constructores
    public Carrito() {
    }

    public Carrito(Usuario usuario) {
        this.usuario = usuario;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Set<CarritoItem> getItems() {
        return items;
    }

    public void setItems(Set<CarritoItem> items) {
        this.items = items;
    }

    // Método para añadir un item al carrito
    public void addItem(CarritoItem item) {
        items.add(item);
        item.setCarrito(this);
    }

    // Método para eliminar un item del carrito
    public void removeItem(CarritoItem item) {
        items.remove(item);
        item.setCarrito(null);
    }

    // Método para vaciar el carrito
    public void clear() {
        items.clear();
    }
}