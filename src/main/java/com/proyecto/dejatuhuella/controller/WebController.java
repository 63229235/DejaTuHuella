package com.proyecto.dejatuhuella.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "home"; // Devuelve el nombre de la plantilla HTML (home.html)
    }

    @GetMapping("/registro")
    public String registro() {
    return "registro"; // Devuelve el nombre de la plantilla HTML (registro.html)
    }

    @GetMapping("/login")
    public String login() {
    return "login"; // Devuelve el nombre de la plantilla HTML (login.html)
    }

    @GetMapping("/panel-control")
    public String panelControl() {
        // Aquí podrías agregar lógica para verificar la autenticación antes de mostrar el panel
        return "panel-control"; // Devuelve el nombre de la plantilla HTML (panel-control.html)
    }
}