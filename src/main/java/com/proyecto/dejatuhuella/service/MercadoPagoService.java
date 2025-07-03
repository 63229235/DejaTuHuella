package com.proyecto.dejatuhuella.service;

import com.mercadopago.exceptions.MPException;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.proyecto.dejatuhuella.dto.MercadoPagoRequestDTO;
import com.proyecto.dejatuhuella.dto.MercadoPagoResponseDTO;
import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.repository.PedidoRepository;
import com.proyecto.dejatuhuella.service.PedidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoPagoService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoService.class);

    @Value("${mercadopago.success.url}")
    private String successUrl;

    @Value("${mercadopago.failure.url}")
    private String failureUrl;

    @Value("${mercadopago.pending.url}")
    private String pendingUrl;

    @Value("${mercadopago.notification.url}")
    private String notificationUrl;

    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private PedidoService pedidoService;

    /**
     * Crea una preferencia de pago en Mercado Pago
     * @param requestDTO Datos para crear la preferencia
     * @return Respuesta con los datos de la preferencia creada
     */
    public MercadoPagoResponseDTO crearPreferenciaPago(MercadoPagoRequestDTO requestDTO) {
        try {
            // Crear el cliente de preferencias
            PreferenceClient client = new PreferenceClient();

            // Crear el ítem para la preferencia
            List<PreferenceItemRequest> items = new ArrayList<>();
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .id(requestDTO.getPedidoId().toString())
                    .title(requestDTO.getDescripcion())
                    .quantity(1)
                    .unitPrice(requestDTO.getMonto())
                    .currencyId("PEN") // Moneda peruana (Soles)
                    .build();
            items.add(item);

            // Configurar las URLs de redirección
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(successUrl + "?pedido_id=" + requestDTO.getPedidoId())
                    .failure(failureUrl + "?pedido_id=" + requestDTO.getPedidoId())
                    .pending(pendingUrl + "?pedido_id=" + requestDTO.getPedidoId())
                    .build();

            // Crear la preferencia
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .notificationUrl(notificationUrl)
                    .externalReference(requestDTO.getPedidoId().toString())
                    .autoReturn("approved")
                    .build();

            // Ejecutar la solicitud a Mercado Pago
            Preference preference = client.create(preferenceRequest);

            // Crear y devolver la respuesta
            return new MercadoPagoResponseDTO(
                    preference.getId(),
                    preference.getInitPoint(),
                    preference.getSandboxInitPoint()
            );

        } catch (MPApiException apiException) {
            logger.error("Error en la API de Mercado Pago: {}", apiException.getMessage());
            return new MercadoPagoResponseDTO("error", "Error en la API de Mercado Pago: " + apiException.getMessage());
        } catch (MPException mpException) {
            logger.error("Error en Mercado Pago: {}", mpException.getMessage());
            return new MercadoPagoResponseDTO("error", "Error en Mercado Pago: " + mpException.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage());
            return new MercadoPagoResponseDTO("error", "Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Procesa una notificación de pago de Mercado Pago
     * @param notificationId ID de la notificación
     * @param topic Tipo de notificación
     * @return true si se procesó correctamente, false en caso contrario
     */
    public boolean procesarNotificacionPago(String notificationId, String topic) {
        try {
            // Verificar que sea una notificación de pago
            if ("payment".equals(topic)) {
                // Obtener el pago desde Mercado Pago
                PaymentClient paymentClient = new PaymentClient();
                Payment payment = paymentClient.get(Long.parseLong(notificationId));
                
                // Obtener el ID del pedido desde la referencia externa
                String externalReference = payment.getExternalReference();
                if (externalReference != null && !externalReference.isEmpty()) {
                    Long pedidoId = Long.parseLong(externalReference);
                    
                    // Actualizar el estado del pedido según el estado del pago
                    return actualizarEstadoPedido(pedidoId, payment.getStatus());
                }
            }
            
            logger.info("Notificación recibida - Topic: {}, ID: {}", topic, notificationId);
            return true;
        } catch (Exception e) {
            logger.error("Error al procesar notificación de pago: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el estado de un pedido según la respuesta de Mercado Pago
     * @param pedidoId ID del pedido
     * @param estado Estado del pago (approved, pending, rejected)
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizarEstadoPedido(Long pedidoId, String estado) {
        try {
            // Verificar que el pedido existe
            if (!pedidoService.existePedido(pedidoId)) {
                logger.error("Pedido no encontrado con ID: {}", pedidoId);
                return false;
            }

            // Mapear el estado de Mercado Pago al estado del pedido
            String estadoPedido;
            switch (estado) {
                case "approved":
                    estadoPedido = "PAGADO";
                    break;
                case "pending":
                    estadoPedido = "PENDIENTE";
                    break;
                case "rejected":
                    estadoPedido = "CANCELADO";
                    break;
                default:
                    estadoPedido = "PENDIENTE";
            }

            // Actualizar el estado del pedido usando el servicio de pedidos
            pedidoService.actualizarEstadoPedido(pedidoId, estadoPedido);
            logger.info("Estado del pedido {} actualizado a {}", pedidoId, estadoPedido);
            return true;
        } catch (Exception e) {
            logger.error("Error al actualizar el estado del pedido: {}", e.getMessage());
            return false;
        }
    }
}