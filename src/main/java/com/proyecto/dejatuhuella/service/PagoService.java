package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.dto.PagoRequestDTO;
import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class PagoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Verifica si el pedido pertenece al usuario autenticado actualmente
     */
    public boolean verificarPedidoPertenecaUsuario(Pedido pedido) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            
            // Obtener el usuario del pedido
            Usuario usuarioPedido = pedido.getUsuario();
            
            // Verificar si el email del usuario autenticado coincide con el del pedido
            return usuarioPedido != null && 
                   usuarioPedido.getEmail() != null && 
                   usuarioPedido.getEmail().equals(userDetails.getUsername());
        }
        return false;
    }

    /**
     * Procesa el pago de un pedido
     * @return true si el pago fue exitoso, false en caso contrario
     */
    @Transactional
    public boolean procesarPago(Long pedidoId, PagoRequestDTO pagoRequest) {
        // Validar que el pedido existe
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
        
        // Validar que el pedido pertenece al usuario autenticado
        if (!verificarPedidoPertenecaUsuario(pedido)) {
            throw new RuntimeException("No tienes permiso para pagar este pedido");
        }
        
        // Validar que el pedido está en estado PENDIENTE
        if (!"PENDIENTE".equals(pedido.getEstado().getNombreEstado())) {
            throw new RuntimeException("Este pedido no está pendiente de pago");
        }
        
        // Validar la tarjeta de crédito
        validarTarjeta(pagoRequest);
        
        // Simular procesamiento de pago (en un entorno real, aquí se integraría con un gateway de pago)
        boolean pagoExitoso = simularProcesoPago(pagoRequest, pedido);
        
        if (pagoExitoso) {
            // Registrar la información del pago (en un entorno real, se guardaría en una tabla de pagos)
            registrarPago(pedido, pagoRequest);
        }
        
        return pagoExitoso;
    }
    
    /**
     * Valida los datos de la tarjeta
     */
    public void validarTarjeta(PagoRequestDTO pagoRequest) {
        // Validar que el número de tarjeta tenga 16 dígitos
        if (!pagoRequest.getNumeroTarjeta().matches("^[0-9]{16}$")) {
            throw new RuntimeException("El número de tarjeta debe tener 16 dígitos");
        }
        
        // Validar que la fecha de vencimiento tenga el formato MM/YY
        if (!pagoRequest.getFechaVencimiento().matches("^(0[1-9]|1[0-2])/([0-9]{2})$")) {
            throw new RuntimeException("La fecha de vencimiento debe tener el formato MM/YY");
        }
        
        // Validar que la fecha de vencimiento no esté expirada
        String[] partesFecha = pagoRequest.getFechaVencimiento().split("/");
        int mes = Integer.parseInt(partesFecha[0]);
        int anio = Integer.parseInt(partesFecha[1]) + 2000; // Convertir YY a YYYY
        
        YearMonth fechaVencimiento = YearMonth.of(anio, mes);
        YearMonth ahora = YearMonth.now();
        
        if (fechaVencimiento.isBefore(ahora)) {
            throw new RuntimeException("La tarjeta ha expirado");
        }
        
        // Validar que el código de seguridad tenga 3 o 4 dígitos
        if (!pagoRequest.getCodigoSeguridad().matches("^[0-9]{3,4}$")) {
            throw new RuntimeException("El código de seguridad debe tener 3 o 4 dígitos");
        }
    }
    
    /**
     * Simula el proceso de pago con un gateway externo
     * En un entorno real, aquí se integraría con un servicio de pago como PayPal, Stripe, etc.
     */
    private boolean simularProcesoPago(PagoRequestDTO pagoRequest, Pedido pedido) {
        // Simulamos un proceso de pago con 95% de probabilidad de éxito
        Random random = new Random();
        return random.nextDouble() <= 0.95;
    }
    
    /**
     * Registra la información del pago
     * En un entorno real, se guardaría en una tabla de pagos
     */
    private void registrarPago(Pedido pedido, PagoRequestDTO pagoRequest) {
        // En un entorno real, aquí se crearía un registro en la tabla de pagos
        // Por ahora, solo simulamos el registro
        System.out.println("Pago registrado para el pedido " + pedido.getId() + 
                           " con tarjeta terminada en " + 
                           pagoRequest.getNumeroTarjeta().substring(pagoRequest.getNumeroTarjeta().length() - 4) + 
                           " a las " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}