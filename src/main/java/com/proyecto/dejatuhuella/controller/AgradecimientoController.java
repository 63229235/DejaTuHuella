package com.proyecto.dejatuhuella.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pagos")
public class AgradecimientoController {

    @GetMapping("/agradecimiento")
    public String mostrarAgradecimiento() {
        return "pago/agradecimiento";
    }
}