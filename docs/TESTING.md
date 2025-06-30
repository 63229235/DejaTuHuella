# Guía de Pruebas para DejaTuHuella

Este documento proporciona una guía completa sobre cómo realizar pruebas en el proyecto DejaTuHuella, incluyendo pruebas unitarias, de integración y de sistema.

## Índice

1. [Introducción](#introducción)
2. [Configuración del Entorno de Pruebas](#configuración-del-entorno-de-pruebas)
3. [Tipos de Pruebas](#tipos-de-pruebas)
   - [Pruebas Unitarias](#pruebas-unitarias)
   - [Pruebas de Integración](#pruebas-de-integración)
   - [Pruebas de Sistema](#pruebas-de-sistema)
4. [Herramientas Utilizadas](#herramientas-utilizadas)
5. [Ejecución de Pruebas](#ejecución-de-pruebas)
6. [Buenas Prácticas](#buenas-prácticas)
7. [Solución de Problemas Comunes](#solución-de-problemas-comunes)

## Introducción

Las pruebas son una parte fundamental del desarrollo de software que garantiza la calidad y fiabilidad del código. En DejaTuHuella, utilizamos diferentes niveles de pruebas para asegurar que todas las partes del sistema funcionen correctamente, tanto de forma individual como en conjunto.

## Configuración del Entorno de Pruebas

Para ejecutar las pruebas en el proyecto DejaTuHuella, necesitas tener instalado:

- JDK 17 o superior
- Maven 3.6.0 o superior

El proyecto utiliza JUnit 5 como framework principal de pruebas, junto con Mockito para la creación de objetos simulados (mocks).

## Tipos de Pruebas

### Pruebas Unitarias

Las pruebas unitarias verifican el funcionamiento correcto de unidades individuales de código, generalmente métodos o clases. En DejaTuHuella, seguimos estas convenciones para las pruebas unitarias:

#### Estructura de una Prueba Unitaria

```java
@Test
void nombreDelMetodo_escenario_resultadoEsperado() {
    // Configuración (Arrange)
    // Aquí se preparan los datos y objetos necesarios para la prueba
    
    // Ejecución (Act)
    // Aquí se ejecuta el método que se está probando
    
    // Verificación (Assert)
    // Aquí se verifica que el resultado es el esperado
}
```

#### Ejemplo de Prueba Unitaria para un Servicio

```java
@Test
void procesarPago_deberiaRetornarTrue_cuandoPedidoExisteYEsValido() {
    // Configuración
    when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("test@example.com");
    
    // Ejecución
    boolean resultado = pagoService.procesarPago(1L, pagoRequestDTO);
    
    // Verificación
    assertTrue(resultado);
    verify(pedidoRepository).save(pedido);
}
```

#### Uso de Mocks

Utilizamos Mockito para simular el comportamiento de dependencias externas. Esto nos permite probar una unidad de código de forma aislada:

```java
@Mock
private PedidoRepository pedidoRepository;

@Mock
private EstadoPedidoRepository estadoPedidoRepository;

@InjectMocks
private PagoService pagoService;

@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
    // Configuración adicional...
}
```

### Pruebas de Integración

Las pruebas de integración verifican que diferentes componentes del sistema funcionen correctamente juntos. En DejaTuHuella, utilizamos la anotación `@SpringBootTest` para pruebas de integración que requieren el contexto completo de Spring.

#### Ejemplo de Prueba de Integración

```java
@SpringBootTest
class PedidoIntegrationTest {

    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Test
    void crearPedido_deberiaGuardarPedidoEnBaseDeDatos() {
        // Configuración
        Pedido nuevoPedido = new Pedido();
        // Configurar el pedido...
        
        // Ejecución
        Pedido pedidoGuardado = pedidoService.guardarPedido(nuevoPedido);
        
        // Verificación
        assertNotNull(pedidoGuardado.getId());
        Optional<Pedido> pedidoRecuperado = pedidoRepository.findById(pedidoGuardado.getId());
        assertTrue(pedidoRecuperado.isPresent());
        assertEquals(nuevoPedido.getTotal(), pedidoRecuperado.get().getTotal());
    }
}
```

### Pruebas de Sistema

Las pruebas de sistema verifican el funcionamiento completo de la aplicación, incluyendo la interfaz de usuario. Para DejaTuHuella, utilizamos Selenium para pruebas de la interfaz web.

#### Ejemplo de Prueba de Sistema con Selenium

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PagoSystemTest {

    @LocalServerPort
    private int port;
    
    private WebDriver driver;
    
    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    void realizarPago_deberiaCompletarProcesoYMostrarConfirmacion() {
        // Navegar a la página de pago
        driver.get("http://localhost:" + port + "/pagos/procesar/1");
        
        // Rellenar formulario de pago
        driver.findElement(By.id("numeroTarjeta")).sendKeys("4111111111111111");
        driver.findElement(By.id("nombreTitular")).sendKeys("Usuario Test");
        driver.findElement(By.id("fechaVencimiento")).sendKeys("12/25");
        driver.findElement(By.id("codigoSeguridad")).sendKeys("123");
        driver.findElement(By.id("metodoPago")).sendKeys("VISA");
        
        // Enviar formulario
        driver.findElement(By.id("btnPagar")).click();
        
        // Verificar redirección a página de confirmación
        assertTrue(driver.getCurrentUrl().contains("/pagos/confirmacion/"));
        
        // Verificar mensaje de confirmación
        WebElement mensajeConfirmacion = driver.findElement(By.className("confirmacion-mensaje"));
        assertTrue(mensajeConfirmacion.getText().contains("¡Pago realizado con éxito!"));
    }
}
```

## Herramientas Utilizadas

- **JUnit 5**: Framework principal para pruebas unitarias y de integración.
- **Mockito**: Biblioteca para crear objetos simulados (mocks) en pruebas unitarias.
- **Spring Test**: Proporciona utilidades para pruebas de integración con Spring Boot.
- **H2 Database**: Base de datos en memoria para pruebas.
- **Selenium**: Para pruebas de sistema que involucran la interfaz de usuario web.

## Ejecución de Pruebas

### Ejecutar Todas las Pruebas

```bash
mvn test
```

### Ejecutar una Clase de Prueba Específica

```bash
mvn test -Dtest=PagoServiceTest
```

### Ejecutar un Método de Prueba Específico

```bash
mvn test -Dtest=PagoServiceTest#procesarPago_deberiaRetornarTrue_cuandoPedidoExisteYEsValido
```

### Ejecutar Pruebas por Paquete

```bash
mvn test -Dtest="com.proyecto.dejatuhuella.service.*"
```

### Ejecutar Pruebas Excluyendo las Pruebas de Sistema

```bash
mvn test -Dtest="!**/*SystemTest.java"
```

## Buenas Prácticas

1. **Nomenclatura clara**: Utiliza nombres descriptivos para las pruebas siguiendo el patrón `método_escenario_resultadoEsperado`.
2. **Independencia**: Cada prueba debe ser independiente y no depender del estado dejado por otras pruebas.
3. **Aislamiento**: Utiliza mocks para aislar la unidad que estás probando de sus dependencias.
4. **Cobertura**: Intenta cubrir tanto los casos de éxito como los de error.
5. **Mantenimiento**: Actualiza las pruebas cuando cambies el código.
6. **Simplicidad**: Mantén las pruebas simples y enfocadas en un solo aspecto.
7. **Documentación**: Documenta el propósito de las pruebas complejas.

## Solución de Problemas Comunes

### Pruebas que Fallan Intermitentemente

Si tienes pruebas que fallan de forma intermitente, puede deberse a:

- **Dependencias entre pruebas**: Asegúrate de que cada prueba sea independiente.
- **Problemas de concurrencia**: Verifica si hay problemas de sincronización en pruebas que utilizan hilos.
- **Recursos externos**: Minimiza la dependencia de recursos externos o utiliza mocks.

### Errores de Contexto de Spring

Si encuentras errores relacionados con el contexto de Spring:

- Verifica que estás utilizando las anotaciones correctas (`@SpringBootTest`, `@WebMvcTest`, etc.).
- Asegúrate de que las dependencias necesarias están disponibles en el contexto de prueba.
- Considera usar `@DirtiesContext` si necesitas reiniciar el contexto entre pruebas.

### Problemas con Mocks

Si tienes problemas con los mocks:

- Verifica que estás inicializando correctamente los mocks con `MockitoAnnotations.openMocks(this)`.
- Asegúrate de que los métodos que estás simulando coinciden exactamente con los que se llaman en el código.
- Comprueba que estás utilizando los matchers correctos en los argumentos.

---

Esta guía proporciona una base sólida para realizar pruebas en el proyecto DejaTuHuella. Recuerda que las pruebas son una inversión en la calidad y mantenibilidad del código a largo plazo.