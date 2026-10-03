# Cómo contribuir

Gracias por querer mejorar este repositorio. Antes de abrir un issue o un pull
request, lee esta guía: te va a ahorrar tiempo a ti y a quien lo revise.

> [!IMPORTANT]
> Este repositorio contiene **trabajos de coursework** entregados para la
> asignatura *Temas Selectos de Criptografía*. El código se documenta tal como
> se entregó, sin correcciones, para conservar la trazabilidad de las
> prácticas. Eso cambia qué se espera de una contribución: **no se espera que
> "arregles" el código**, sino que ayudes a que se entienda mejor.

---

## 📌 Qué se espera de una contribución

| Sí ayuda | No ayuda |
|:--|:--|
| Corregir errores de documentación | Cambiar la lógica criptográfica |
| Aclarar un README o un diagrama | Renombrar paquetes o clases |
| Añadir un ejemplo de salida real | Reordenar o "limpiar" el código |
| Añadir una prueba unitaria | Migrar de Ant a Maven (o al revés) |
| Corregir un `README` desactualizado | Eliminar el proyecto gemelo |

La sección [Deuda técnica conocida](#-deuda-técnica-conocida) lista los
problemas que ya están registrados. Si quieres atacar alguno, abre primero un
issue para acordar el alcance.

---

## 🧱 Cómo está organizado

```
.
├── SumaPuntos/                    # proyecto 1 — Maven
├── Diffie-Hellman/                # proyecto 2 — Ant / NetBeans
├── DiffieHellmanCurvaEliptica/    # proyecto 3 — Maven (gemelo del 2)
├── Proyecto1Cripto/               # proyecto 4 — Ant / NetBeans + Bouncy Castle
├── Practica6Cripto/               # proyecto 5 — Ant / NetBeans
├── docs/                          # enunciados y reportes en PDF
└── .github/                       # plantillas de issue y pull request
```

**Cada carpeta es un proyecto autónomo** con su propio sistema de build, su
propio paquete y su propio `README`. No hay un build unificado: compila solo
uno a la vez.

No comparten un módulo común, así que **cambiar una clase de curvas elípticas
significa cambiarla en las dos o tres carpetas donde está replicada**.

---

## 🛠️ Requisitos

| Herramienta | Versión | Nota |
|:--|:--|:--|
| JDK | 8 o superior | `Practica6Cripto` usa `readAllBytes()`, que requiere **Java 9+**. Para una experiencia homogénea, usa **Java 19 o superior**. |
| Apache Maven | 3.8+ | Solo para `SumaPuntos` y `DiffieHellmanCurvaEliptica`. |
| Apache Ant | 1.10+ | Solo para `Diffie-Hellman`, `Proyecto1Cripto` y `Practica6Cripto`. |
| Apache NetBeans | 15+ | Opcional. Abre la carpeta del proyecto y ya tiene el build configurado. |
| Bouncy Castle | 1.73 | **Solo** para `Proyecto1Cripto`, y no está en el repositorio: descárgalo de <https://www.bouncycastle.org/latest_releases.html>. |

---

## 🚀 Compilar y ejecutar

### Maven (proyectos 1 y 3)

```bash
cd SumaPuntos
mvn clean package
mvn exec:java
```

### Ant (proyectos 2, 4 y 5)

```bash
cd Diffie-Hellman
ant run
```

### Con `javac`, sin build

Útil para verificar rápido que una clase sigue compilando:

```bash
cd Practica6Cripto
javac -d out $(find src -name "*.java")
java -cp out practica6cripto.ECDSAPractica
```

> ⚠️ **Los `main.class` declarados no coinciden con las clases reales.** Cada
> README del proyecto lo indica y da el comando correcto. Si usas Ant o
> Maven, o corrige la propiedad en `nbproject/project.properties` o
> `pom.xml`, o pasa la clase explícitamente.

### Pruebas con dos procesos

`Diffie-Hellman` y `Proyecto1Cripto` son **cliente/servidor**: abren el puerto
`45000` y necesitan dos terminales. Primero el servidor (Alice), luego el
cliente (Bob). Verifica que el puerto esté libre antes de arrancar:

```bash
# Linux / macOS
lsof -i :45000
# Windows
netstat -ano | findstr :45000
```

---

## 📏 Convenciones

### Documentación

- **Todo en español**, con acentos. Es lo que usa el código y los mensajes de
  consola, y el repositorio es público.
- Los diagramas son **Mermaid** y las fórmulas **KaTeX**: GitHub los renderiza
  nativamente, no subas imágenes de diagramas.
- Un README por proyecto, con la misma estructura: contexto, qué hace,
  diagrama, cómo ejecutarlo, estructura de archivos y estado del proyecto.
- Si cambias el comportamiento de un programa, **actualiza también la salida de
  consola de ejemplo** en su README.

### Código

- No hay convención de nombres establecida y cada proyecto usa la suya. Si
  agregas código nuevo, sigue el estilo del proyecto en el que trabajas en vez
  de imponer uno nuevo: este repositorio documenta trabajos ya entregados.
- Comentarios y Javadoc en español, como el resto del código.

---

## 🔐 Deuda técnica conocida

Estas observaciones están **a propósito**: el código se documenta tal como se
entregó. No las trates como errores que haya que arreglar de urgencia.

### Crítica

- **Aritmética de curvas elípticas en `int`.** Se usan `int` y
  `(int) Math.pow(x, 2)`, lo que desborda para `p` grande; una curva como
  `secp256r1` no cabe en `int`. El tipo correcto es `BigInteger`.
- **`Proyecto1Cripto` no compila tal como está.** Las ocho entradas
  `file.reference.*` de `nbproject/project.properties` apuntan a rutas
  absolutas de una máquina personal (`C:\Users\pakma\Downloads\`).
- **Tres puntos de entrada declarados no existen:** `diffie.hellman.DiffieHellman`,
  `proyecto1cripto.Proyecto1Cripto` y el `exec.mainClass` de
  `DiffieHellmanCurvaEliptica`.
- **`Practica6Cripto` arrastra 8 `.jar` (~27 MB) que no usa.** Su código solo
  usa la JCA. Ya están en `.gitignore`.
- **ECDH puro sin autenticar.** Vulnerable a man-in-the-middle; falta ECDHE
  autenticado o un certificado.
- **Secreto de ECDH usado directamente como llave AES.** Debería pasar por una
  KDF (HKDF-SHA256).

### Estructura

- **`Diffie-Hellman` y `DiffieHellmanCurvaEliptica` son duplicados exactos.**
- **Las clases de curvas elípticas están replicadas** en tres carpetas, en dos
  linajes distintos (ver el README raíz).
- **Java inconsistente:** `javac.source/target` es `1.8` en dos proyectos y
  `19` en los otros tres.

### Calidad

- `Scanner` sin cerrar y sin try-with-resources.
- `catch (Exception e) { e.printStackTrace(); }` en `Practica6Cripto`.
- Separador de rutas hardcodeado (`"\\"`) en `Practica6Cripto`: no funciona en
  Linux ni macOS.
- `ObjectOutputStream` y `BufferedWriter` mezclados sobre el mismo socket.
- Multiplicación escalar lineal $O(k)$ en lugar de *double-and-add* $O(\log k)$.
- Los `.pem` de ECDSA son **DER crudo**, no PEM legible por OpenSSL.

---

## 🐛 Reportar un problema

Abre un issue con la plantilla de <kbd>Bug report</kbd> e incluye:

- Qué proyecto y qué clase.
- Tu versión de Java (`java -version`) y del sistema.
- Los pasos exactos para reproducirlo, con la salida de consola.
- Qué esperabas y qué pasó.

Si el problema es criptográfico, indica la curva y los valores de $a$, $b$ y $p$
que usaste.

## 💡 Proponer una mejora

Usa la plantilla de <kbd>Feature request</kbd> y explica:

- Qué problema resuelve.
- Qué proyecto afecta.
- Por qué no se puede resolver con documentación.

## 🔀 Enviar un cambio

1. Haz `fork` del repositorio y crea una rama con prefijo descriptivo:
   `docs/fix-ecdh-diagram`, `readme/clarify-aes-gcm`, `test/curva-eliptica`.
2. **Un proyecto por pull request.** Como no hay build unificado, es más fácil
   de revisar y no mistura cambios de código con cambios de documentación.
3. Si tocas código, actualiza el `README` del proyecto correspondiente en el
   mismo pull request.
4. Describe **qué** cambias y **por qué**. No hace falta que sea largo.

---

## 📄 Licencia

Al contribuir aceptas que tu trabajo se publique bajo la
[licencia MIT](LICENSE) del repositorio.

---

## 👤 Autoría

El código es trabajo académico de **Francisco Uziel Córdova Pichardo**
(`@Uziel-Leizu`), grupo 7CM2 de ESCOM, Instituto Politécnico Nacional.
La variante en OpenSSL de la práctica de firma digital
([`docs/practica-04-curva-eliptica-y-openssl.pdf`](docs/practica-04-curva-eliptica-y-openssl.pdf))
fue elaborada por Córdova Pichardo Francisco Uziel, García Jiménez Juan Carlos
y Montes Lozada Cristofer, bajo la supervisión de la profesora Sandra Díaz
Santiago.