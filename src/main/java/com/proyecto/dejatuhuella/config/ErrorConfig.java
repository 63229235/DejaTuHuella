package com.proyecto.dejatuhuella.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ErrorConfig implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Obtener el código de error
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        // Verificar si la URL contiene 'confirmacion'
        String requestURI = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        if (requestURI != null && requestURI.contains("/pagos/confirmacion/")) {
            // Si el error ocurre en la página de confirmación, redirigir a la página de agradecimiento
            return "pago/agradecimiento";
        }
        
        // Para otros errores, mostrar la página de error por defecto
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            if (statusCode == 404) {
                return "error/404";
            } else if (statusCode == 500) {
                return "error/500";
            }
        }
        
        return "error/error";
    }
}