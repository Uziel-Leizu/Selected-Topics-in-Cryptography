# Firma digital ECDSA

<div align="center">

**Práctica 4 · Firma de archivos con curvas elípticas**

[![Java](https://img.shields.io/badge/java-19-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Apache NetBeans](https://img.shields.io/badge/Apache%20NetBeans-087BAB?logo=apachenetbeans&logoColor=white)](https://netbeans.apache.org/)
[![Sin dependencias](https://img.shields.io/badge/dependencias-ninguna-2ecc71)](https://docs.oracle.com/en/java/javase/19/security/oracle-providers.html)
[![Licencia](https://img.shields.io/badge/licencia-MIT-2ecc71)](../LICENSE)

`Temas Selectos de Criptografía` · Grupo 7CM2 · ESCOM · IPN

[← Volver al índice](../README.md)

</div>

---

## 📖 Contexto

Aplicación de consola con menús que permite **generar llaves, firmar archivos y
verificar firmas** usando `SHA256withECDSA` sobre la curva NIST **`secp256r1`**
(P-256).

La firma digital aporta tres garantías que el cifrado no da por sí solo:

| Propiedad | Significado |
|:--|:--|
| **Autenticidad** | La firma prueba quién la produjo. |
| **Integridad** | Si el archivo cambia, la firma deja de validar. |
| **No repudio** | El firmante no puede negar haber firmado. |

> **No da confidencialidad**: el archivo se firma tal cual, sin cifrar.

La versión equivalente hecha con la herramienta de línea de comandos está en
[`docs/practica-04-curva-eliptica-y-openssl.pdf`](../docs/practica-04-curva-eliptica-y-openssl.pdf) — *Curva elíptica criptográfica y
OpenSSL* — donde el mismo ejercicio se resuelve con `openssl ecparam`,
`openssl ec` y `openssl dgst`.

---

## 🔄 Cómo funciona

```mermaid
flowchart TD
    A([Menú de consola]) -->|1| G["Generar llaves"]
    A -->|2| S["Firmar archivo"]
    A -->|3| V["Verificar firma"]
    A -->|4| X([Salir])

    G --> G1["KeyPairGenerator EC<br/>secp256r1"]
    G1 --> G2["Privada → PKCS8<br/>&lt;nombre&gt;PrKey.pem"]
    G1 --> G3["Pública → X509<br/>&lt;nombre&gt;PuKey.pem"]

    S --> S1["JFileChooser: elegir .pem<br/>y archivo a firmar"]
    S1 --> S2["Signature.initSign(privada)"]
    S2 --> S3["update(bytes) → sign()"]
    S3 --> S4["signature.bin junto al archivo"]

    V --> V1["JFileChooser: elegir .pem,<br/>archivo y signature.bin"]
    V1 --> V2["Signature.initVerify(pública)"]
    V2 --> V3["update(bytes) → verify()"]
    V3 -->|true| OK["La firma es válida"]
    V3 -->|false| KO["La firma no es válida"]

    style OK fill:#0b3d2e,stroke:#2ecc71,color:#ecfdf1
    style KO fill:#4a1111,stroke:#e74c3c,color:#fdecea
```

---

## 📐 Cómo funciona ECDSA

La firma tiene tres pasos, y aquí es donde la aritmética de curvas elípticas se
convierte en criptografía:

**1 · Generar el par de llaves.** Un punto aleatorio $G$ se multiplica por un
entero privado $d$:

$$
Q = d \cdot G
$$

La clave privada es $d$ y la pública es $Q$.

**2 · Firmar.** Se elige un $k$ aleatorio y se calcula:

$$
r = (k \cdot G)_x \bmod n
\qquad
s = k^{-1}\bigl(z + d \cdot r\bigr) \bmod n
$$

donde $z$ es el hash del mensaje truncado y $n$ es el orden de $G$. El par
$(r, s)$ **es** la firma.

**3 · Verificar.** Con la clave pública $Q$ se recalcula:

$$
u_1 = z\,s^{-1} \bmod n
\qquad
u_2 = r\,s^{-1} \bmod n
\qquad
P = u_1 G + u_2 Q
$$

y la firma es válida **si y solo si** $P_x \equiv r \pmod n$.

Todo eso lo encapsula `Signature.getInstance("SHA256withECDSA")` de la JCA, así
que el código solo llama a `initSign`/`initVerify`, `update` y `sign`/`verify`.

### El detalle del hash y el truncado

Como el grupo tiene orden primo $n$, el hash del mensaje debe reducirse a un
entero de $n$ bits. El truncamiento es obligatorio, y la firma resultante tiene
**dos valores de $n$ bits** ($r$ y $s$), el doble que la clave privada: ECDSA no
es un esquema compacto, a diferencia de Schnorr o EdDSA.

Hay que firmar siempre **el hash** del mensaje, nunca el mensaje directamente:
firmar sin hash abre la puerta a ataques de último recurso, donde se
construyen dos mensajes distintos con el mismo valor de hash.

### Los dos formatos de llave

Java no guarda las llaves en PEM, así que el código las serializa a mano:

| Formato | Clase | Contiene | Contenedor |
|:--|:--|:--|:--|
| **PKCS#8** | `PKCS8EncodedKeySpec` | clave **privada** | `<nombre>PrKey.pem` |
| **X.509** | `X509EncodedKeySpec` | clave **pública** | `<nombre>PuKey.pem` |

> A pesar de la extensión `.pem`, los archivos que escribe este código son
> **DER crudo, no PEM en texto Base64**. Para producir archivos que OpenSSL y
> otras herramientas sí puedan leer hay que codificarlos en Base64 y
> envolverlos entre `-----BEGIN PRIVATE KEY-----` y `-----END PRIVATE KEY-----`.

Esa es exactamente la diferencia con [`docs/practica-04-curva-eliptica-y-openssl.pdf`](../docs/practica-04-curva-eliptica-y-openssl.pdf):
OpenSSL genera PEM legibles; este código escribe los bytes DER crudos con la
misma extensión.

---

## 🚀 Cómo ejecutarlo

### NetBeans

Abre `Practica6Cripto/` en NetBeans y presiona <kbd>F6</kbd>. El
`main.class` declarado (`practica6cripto.ECDSAPractica`) sí existe, así que
funciona sin cambios.

> Es la única de las prácticas cuya entrada está bien declarada. Aun así,
> revisa `nbproject/project.properties`: las referencias `file.reference.*`
> apuntan a rutas absolutas de otra máquina (ver
> [Estado del repositorio](#-estado-del-repositorio)).

### Ant

```bash
cd Practica6Cripto
ant run
```

### Ejecución directa

Esta práctica **no necesita ninguna biblioteca externa**: solo usa la JCA
(`java.security`) y Swing (`javax.swing`).

```bash
cd Practica6Cripto
javac -d out $(find src -name "*.java")
java -cp out practica6cripto.ECDSAPractica
```

---

## 🎮 Cómo se usa

Es un menú de consola; las llaves y los archivos se eligen con diálogos Swing
(`JFileChooser`), por lo que necesita un entorno gráfico.

```
---------------------------------------------------------
Programa para firmar archivos...
Grupo: 7CM2
---------------------------------------------------------
Bienvenido <usuario>
---------------------------------------------------------
Selecciona una de las siguientes opciones...

1) Generar Llaves.
2) Firmar archivo.
3) Verificar firma.
4) Salir

R:
```

**1 · Generar llaves.** Pide un nombre y una carpeta, y escribe ahí el par:

```
Generando las llaves asimetricas...
Escriba el nombre para guardar la llave...
R: uziel
Elija la carpeta donde guardar su par de llaves...
Usted selecciono la carpeta: /home/uziel/llaves
```

**2 · Firmar.** Elige el `.pem` de la clave privada y el archivo. La firma queda
como `signature.bin` **en la misma carpeta que el archivo firmado**:

```
Seleccione la llave privada para cifrar...
Ahora seleccione el archivo a firmar...
Se ha generado la firma del archivo Hola.txt
```

**3 · Verificar.** Elige la clave **pública**, el archivo y `signature.bin`:

```
Seleccione la llave publica para verificar...
Ahora seleccione el archivo firmado...
Ahora seleccione la firma del archivo
La firma es valida.
```

Modifica un byte del archivo y vuelve a verificar: el resultado pasa a
`La firma no es valida`.

---

## 🌳 Estructura

```
Practica6Cripto/
├── build.xml
├── manifest.mf
└── src/practica6cripto/
    └── ECDSAPractica.java   # menú + las tres operaciones
```

| Método | Responsabilidad |
|:--|:--|
| `main` | Menú en bucle con las cuatro opciones. |
| `GenerarLlaves()` | `KeyPairGenerator.getInstance("EC")` sobre `secp256r1`; exporta privada en PKCS#8 y pública en X.509. |
| `firmar()` | Carga la clave privada, calcula `Signature.getInstance("SHA256withECDSA")` y escribe `signature.bin`. |
| `Verificar()` | Carga la clave pública, recalcula la firma y llama a `verify()`. |

---

## 👤 Autor

**Francisco Uziel Córdova Pichardo** — `@Uziel-Leizu`

Práctica del grupo 7CM2. La versión equivalente en OpenSSL
([`docs/practica-04-curva-eliptica-y-openssl.pdf`](../docs/practica-04-curva-eliptica-y-openssl.pdf)) fue elaborada por
Córdova Pichardo Francisco Uziel, García Jiménez Juan Carlos y
Montes Lozada Cristofer.

---

## 📌 Estado del repositorio

Se documenta tal como está, sin modificar el código:

- **Los ocho `.jar` de esta carpeta no se usan.** Son unos 27 MB de Bouncy Castle
  que el proyecto **no necesita**: el código solo importa `java.security` y
  `javax.swing`, y usa `KeyPairGenerator.getInstance("EC")` y
  `Signature.getInstance("SHA256withECDSA")`, ambos del proveedor estándar de
  Java. Las referencias `file.reference.*` apuntan además a la carpeta de
  OneDrive de otro proyecto.
- **Archivos DER con extensión `.pem`.** No son PEM legibles, sino DER crudo,
  por lo que OpenSSL y otras herramientas no los abren directamente (ver
  [El detalle del hash y el truncado](#el-detalle-del-hash-y-el-truncado)).
- **Separador de rutas hardcodeado.** El nombre del archivo se construye con
  `"\\"`, que solo funciona en Windows:
  `new File(selectedFolder.getAbsolutePath() + "\\" + nombre + "PrKey.pem")`.
- **`signature.bin` se sobrescribe siempre** junto al archivo firmado, sin
  preguntar; no permite firmar dos archivos distintos en la misma carpeta.
- **Sin validación de la curva.** No se comprueba que el `.pem` corresponda a
  `secp256r1` antes de firmar o verificar.
- **Sin `try-with-resources`.** Los `FileInputStream`/`FileOutputStream` se
  cierran a mano dentro del flujo normal, así que una excepción los deja
  abiertos, y los errores se reportan con `e.printStackTrace()`.
- **Métodos en español con mayúscula inicial** (`GenerarLlaves`, `firmar`,
  `Verificar`), mezcla de convención con el resto del proyecto.
