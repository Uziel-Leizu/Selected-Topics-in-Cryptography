## ¿Qué cambia?

<!-- Resume el cambio en una o dos líneas. -->

## ¿Por qué?

<!-- El problema que resuelve. Si es solo documentación, basta con decir qué
     parte quedaba confusa. -->

Clave: <!-- #documentacion | #fix | #test | #build -->

## Proyecto afectado

<!-- Una carpeta: SumaPuntos, Diffie-Hellman, DiffieHellmanCurvaEliptica,
     Proyecto1Cripto, Practica6Cripto, o "raíz". -->

- [ ] Un solo proyecto por pull request

## Verificación

<!-- Cómo comprobaste que funciona. -->

- [ ] `mvn clean package` pasa (proyectos Maven)
- [ ] `ant compile` pasa (proyectos Ant)
- [ ] `javac -d out $(find src -name "*.java")` compila sin errores
- [ ] Ejecuté el programa y el comportamiento no cambió (si tocaste código)
- [ ] Actualicé el `README` del proyecto si cambió el comportamiento

## Capturas o salida

<!-- Si el cambio es visible, pega la salida de consola antes y después. -->

```text
<!-- antes -->

<!-- después -->
```

## Checklist

- [ ] Actualicé la documentación afectada (README raíz o del proyecto)
- [ ] No agregué dependencias nuevas sin explicarlas aquí
- [ ] No subí archivos generados (`build/`, `target/`, `out/`, `*.class`, `*.jar`)
- [ ] Revisé que los enlaces y diagramas Mermaid siguen funcionando
- [ ] Entiendo que este repositorio documenta trabajos de coursework y que no
      busco modificar la lógica criptográfica entregada

<!-- Si el pull request es grande, conviene dividirlo en varios pequeños:
     la documentación de cada proyecto es más fácil de revisar por separado. -->

Closes #