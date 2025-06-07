package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.model.Categoria;
import com.proyecto.dejatuhuella.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional
    public Categoria crearCategoria(Categoria categoria) {
        if (categoriaRepository.existsByNombre(categoria.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }
        return categoriaRepository.save(categoria);
    }

    @Transactional(readOnly = true)
    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> obtenerCategoriaPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);
    }

    @Transactional
    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));

        // Verificar si el nuevo nombre ya existe en otra categoría
        if (categoriaActualizada.getNombre() != null &&
                !categoriaActualizada.getNombre().equals(categoriaExistente.getNombre()) &&
                categoriaRepository.existsByNombre(categoriaActualizada.getNombre())) {
            throw new RuntimeException("Ya existe otra categoría con el nombre: " + categoriaActualizada.getNombre());
        }

        if (categoriaActualizada.getNombre() != null) {
            categoriaExistente.setNombre(categoriaActualizada.getNombre());
        }
        if (categoriaActualizada.getDescripcion() != null) {
            categoriaExistente.setDescripcion(categoriaActualizada.getDescripcion());
        }
        // Los productos asociados se manejan a través de la entidad Producto

        return categoriaRepository.save(categoriaExistente);
    }

    @Transactional
    public void eliminarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));

        // Antes de eliminar, podrías querer verificar si hay productos asociados
        // y decidir qué hacer (ej. desasociarlos, impedir eliminación, etc.)
        // Por simplicidad, aquí solo eliminamos. Si hay productos con esta categoría,
        // la restricción de FK podría fallar si no está configurado CascadeType.REMOVE
        // o si los productos no se actualizan para no tener categoría o tener otra.
        // En Categoria.java, @OneToMany tiene CascadeType.PERSIST, no REMOVE.
        // Si la categoría tiene productos, la eliminación fallará a menos que los productos se desasocien primero.
        // Una mejor práctica sería verificar `categoria.getProductos().isEmpty()`
        if (!categoria.getProductos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene productos asociados. Desasócielos primero.");
        }

        categoriaRepository.deleteById(id);
    }
}