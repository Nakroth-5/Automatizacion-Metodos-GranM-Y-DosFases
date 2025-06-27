# Automatización de los Métodos Gran M y Dos Fases

[![Java](https://img.shields.io/badge/Java-24%2B-blue)](https://www.java.com/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-purple)](https://openjfx.io/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen)]()

![Interfaz de la Aplicación](https://github.com/user-attachments/assets/d85e18b6-d207-49b1-a167-819cc2434f9c)

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Características](#-características)
- [Instalación de Dependencias](#-instalación-de-dependencias)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación del Proyecto](#-instalación-del-proyecto)
- [Manual de Uso](#-manual-de-uso)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Contribución](#-contribución)
- [Autores](#-autores)
- [Licencia](#-licencia)
- [Enlaces Adicionales](#-enlaces-adicionales)

## 📌 Descripción

Aplicación de escritorio desarrollada en Java que automatiza la resolución de problemas de **Programación Lineal (PL)** mediante los métodos de la **Gran M** y **Dos Fases**. La aplicación cuenta con una interfaz gráfica intuitiva construida con JavaFX que permite visualizar paso a paso el proceso del algoritmo Simplex, facilitando tanto el aprendizaje como la resolución práctica de problemas de optimización.

### Objetivo

Esta herramienta está diseñada para estudiantes, profesores e ingenieros que necesiten resolver problemas de programación lineal de manera visual y educativa, proporcionando una comprensión detallada del funcionamiento interno de estos algoritmos fundamentales.

## ✨ Características

### Métodos Implementados
- **Algoritmo Simplex Estándar**: Implementación completa del método Simplex
- **Método de la Gran M**: Para problemas con restricciones de igualdad y mayor-igual
- **Método de las Dos Fases**: Alternativa robusta para encontrar soluciones factibles

### Funcionalidades Principales
- **Visualización Paso a Paso**: Muestra cada iteración del tableau Simplex con explicaciones detalladas
- **Identificación de Variables**: Destaca variables entrantes y salientes en cada iteración
- **Soporte Completo de Restricciones**: Maneja restricciones de tipo ≤, ≥ y =
- **Problemas de Gran Escala**: Soporta hasta 100 variables y 500 restricciones
- **Detección de Casos Especiales**: Identifica problemas no acotados e infactibles
- **Interfaz Intuitiva**: Diseño limpio y fácil de usar

### Capacidades Técnicas
- Maximización y minimización de funciones objetivo
- Generación automática de variables de holgura y artificiales
- Cálculo preciso con manejo de decimales
- Exportación de resultados detallados

## 🔧 Instalación de Dependencias

Antes de instalar el proyecto, necesitas tener **Git** y **Java 24** instalados en tu sistema. Sigue las instrucciones según tu sistema operativo:

### 🖥️ Windows

#### Instalación desde Terminal (PowerShell)

**1. Instalar Chocolatey (Gestor de Paquetes para Windows)**
```powershell
# Abrir PowerShell como Administrador y ejecutar:
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

**2. Instalar Git**
```cmd
# Con Chocolatey:
choco install git -y

# O con winget (Windows Package Manager):
winget install --id Git.Git -e --source winget
```

**3. Instalar Java 24**
```cmd
# Con Chocolatey (OpenJDK):
choco install openjdk --version=24.0.2 -y

# O con winget:
winget install EclipseAdoptium.Temurin.24.JDK
```

**4. Verificar Instalación**
```cmd
git --version
java --version
javac --version
```

### 🐧 Linux

#### Arch Linux

**1. Actualizar el sistema**
```bash
sudo pacman -Syu
```

**2. Instalar Git**
```bash
sudo pacman -S git
```

**3. Instalar Java 24**
```bash
# Instalar OpenJDK 24
sudo pacman -S jdk-openjdk

# O si prefieres usar AUR para versiones específicas:
# Instalar yay (AUR helper) si no lo tienes
sudo pacman -S base-devel git
git clone https://aur.archlinux.org/yay.git
cd yay
makepkg -si

# Buscar e instalar Java 24 desde AUR
yay -S jdk24-openjdk
```

**4. Configurar Java por defecto**
```bash
sudo archlinux-java set java-24-openjdk
```

#### Debian/Ubuntu

**1. Actualizar repositorios**
```bash
sudo apt update && sudo apt upgrade -y
```

**2. Instalar Git**
```bash
sudo apt install git -y
```

**3. Instalar Java 24**
```bash
# Método 1: Usar SDKMAN (Recomendado)
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 24-open

# Método 2: Repositorio de Oracle/OpenJDK
wget -O- https://apt.corretto.aws/corretto.key | sudo apt-key add -
sudo add-apt-repository 'deb https://apt.corretto.aws stable main'
sudo apt update
sudo apt install java-24-amazon-corretto-jdk -y

# Método 3: Instalar desde repositorios oficiales (si está disponible)
sudo apt install openjdk-24-jdk -y
```

**4. Configurar Java por defecto**
```bash
sudo update-alternatives --config java
sudo update-alternatives --config javac
```

#### Instalación de Maven (Todas las distribuciones Linux)

**Arch Linux:**
```bash
sudo pacman -S maven
```

**Debian/Ubuntu:**
```bash
sudo apt install maven -y
```

**Con SDKMAN (Recomendado para todas las distribuciones):**
```bash
sdk install maven
```

### ✅ Verificar Instalación

Ejecuta estos comandos para verificar que todo esté correctamente instalado:

```bash
# Verificar Git
git --version
# Salida esperada: git version 2.x.x

# Verificar Java
java --version
# Salida esperada: openjdk 24.x.x

# Verificar Compilador Java
javac --version
# Salida esperada: javac 24.x.x

# Verificar Maven
mvn --version
# Salida esperada: Apache Maven 3.x.x
```

### 🔧 Configuración Adicional

**Configurar Variables de Entorno (si es necesario):**

**Windows:**
```cmd
# Verificar JAVA_HOME
echo %JAVA_HOME%

# Si no está configurada, agregar manualmente:
# Ir a: Panel de Control > Sistema > Configuración avanzada del sistema > Variables de entorno
# Agregar JAVA_HOME apuntando al directorio de instalación de Java
```

**Linux:**
```bash
# Agregar al archivo ~/.bashrc o ~/.zshrc
echo 'export JAVA_HOME="/usr/lib/jvm/java-24-openjdk"' >> ~/.bashrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc
```

## 🛠️ Requisitos Previos

Una vez instaladas las dependencias, asegúrate de tener:

- **Java Development Kit (JDK) 24 o superior** ✅
- **Git** ✅
- **Apache Maven 3.6+** ✅
- **Sistema Operativo**: Windows, macOS o Linux
- **Memoria RAM**: Mínimo 4GB recomendado
- **Espacio en Disco**: 100MB libres

## 📥 Instalación del Proyecto

### Método 1: Clonar desde GitHub

```bash
# Clonar el repositorio
git clone https://github.com/Nakroth-5/Automatizacion-Metodos-GranM-Y-DosFases.git

# Navegar al directorio del proyecto
cd Automatizacion-Metodos-GranM-Y-DosFases

# Compilar y ejecutar con Maven
mvn clean javafx:run
```

### Método 2: Ejecutar con Java directamente

```bash
# Después de clonar, compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn exec:java -Dexec.mainClass="Main"
```

### Método 3: Generar JAR ejecutable

```bash
# Generar JAR con dependencias
mvn clean package

# Ejecutar el JAR generado
java -jar target/simplex-automation-1.0.jar
```

## 🖥️ Manual de Uso

### 1. Inicio de la Aplicación
Ejecute el programa siguiendo los pasos de instalación. Se abrirá la ventana principal de la aplicación.

### 2. Configuración del Problema
- **Paso 1**: Utilice los selectores numéricos en la parte superior para definir:
  - **Número de Variables**: Cantidad de variables de decisión (x₁, x₂, ..., xₙ)
  - **Número de Restricciones**: Cantidad de restricciones del problema
- **Paso 2**: Haga clic en **"Generar Modelo"** para crear la interfaz de entrada de datos

### 3. Ingreso de Datos

#### Función Objetivo
- Seleccione el tipo de optimización: **"Maximizar"** o **"Minimizar"**
- Elija el método de resolución: **"Gran M"** o **"Dos Fases"**
- Ingrese los coeficientes de cada variable en los campos correspondientes

#### Restricciones
Para cada restricción:
- Ingrese los coeficientes de las variables de decisión
- Seleccione el tipo de desigualdad: **≤** (menor igual), **≥** (mayor igual), o **=** (igual)
- Especifique el valor del lado derecho de la restricción

### 4. Resolución y Visualización de Resultados
- **Paso 1**: Haga clic en **"Resolver Problema"** después de ingresar todos los datos
- **Paso 2**: Se abrirá la ventana **"Proceso del Método Simplex"** mostrando:
  - Tableau inicial con todas las variables y restricciones
  - Botones de navegación **"Siguiente"** y **"Anterior"** para recorrer iteraciones
  - Explicaciones detalladas de cada paso del algoritmo
- **Paso 3**: Al finalizar, se mostrará un resumen con:
  - **Solución Óptima**: Valores finales de las variables de decisión
  - **Valor Óptimo**: Resultado final de la función objetivo
  - **Estado del Problema**: Óptimo, no acotado o infactible

### 5. Funciones Adicionales
- **Limpiar**: Borra todos los campos para comenzar un nuevo problema
- **Salir**: Cierra la aplicación de forma segura

## 🗂️ Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   ├── metodos/              # Lógica de algoritmos
│   │   │   ├── SimplexBase.java  # Clase base del algoritmo Simplex
│   │   │   ├── GranM.java        # Implementación método Gran M
│   │   │   └── DosFases.java     # Implementación método Dos Fases
│   │   ├── ui/                   # Interfaz gráfica de usuario
│   │   │   ├── controllers/      # Controladores JavaFX
│   │   │   │   ├── MainController.java
│   │   │   │   └── ResultsController.java
│   │   │   └── views/           # Archivos FXML
│   │   │       ├── MainView.fxml
│   │   │       └── ResultsView.fxml
│   │   ├── models/              # Modelos de datos
│   │   │   ├── Problem.java
│   │   │   └── Solution.java
│   │   └── utils/               # Utilidades
│   │       ├── MathUtils.java
│   │       └── TableauFormatter.java
│   └── resources/
│       ├── styles/              # Hojas de estilo CSS
│       │   └── application.css
│       ├── fxml/               # Archivos de interfaz
│       └── images/             # Recursos gráficos
├── test/                       # Pruebas unitarias
│   └── java/
└── pom.xml                     # Configuración Maven
```

## 💻 Tecnologías Utilizadas

- **Lenguaje**: Java 24
- **Framework GUI**: JavaFX 17
- **Gestor de Dependencias**: Apache Maven
- **IDE Recomendado**: IntelliJ IDEA, Eclipse, o NetBeans
- **Control de Versiones**: Git

### Librerías Principales
- JavaFX Controls
- JavaFX FXML
- JUnit 5 (para pruebas)

## 🤝 Contribución

Las contribuciones son bienvenidas. Para contribuir:

1. Fork el proyecto
2. Crea una rama para tu función (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

### Directrices para Contribuir
- Sigue las convenciones de código Java
- Incluye pruebas unitarias para nuevas funcionalidades
- Documenta el código apropiadamente
- Actualiza la documentación según sea necesario

## 👥 Autores

| Desarrollador | GitHub | Rol |
|---------------|--------|-----|
| **Evert Rodriguez Araúz** | [@Nakroth-5](https://github.com/Nakroth-5) | Desarrollador Principal |
| **Maikol Anthony Molina Cortez** | - | Desarrollador |

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.

## 🔗 Enlaces Adicionales

### 📊 Documentación y Presentaciones

| Recurso | Descripción | Enlace |
|---------|-------------|--------|
| **🎯 Presentación Interactiva** | Presentación HTML completa del proyecto | [Ver en Navegador](https://nakroth-5.github.io/Automatizacion-Metodos-GranM-Y-DosFases/Presentacion.html) |
| **📄 Documentación Técnica** | Documento completo del proyecto en formato Word | [Descargar PDF](https://github.com/Nakroth-5/Automatizacion-Metodos-GranM-Y-DosFases/raw/master/Proyecto%20IO1.docx) |
| **💻 Código Fuente** | Repositorio completo en GitHub | [Ver Código](https://github.com/Nakroth-5/Automatizacion-Metodos-GranM-Y-DosFases) |

### 🛠️ Soporte y Desarrollo

- **🐛 Reportar Bugs**: [Issues](https://github.com/Nakroth-5/Automatizacion-Metodos-GranM-Y-DosFases/issues)
- **✨ Solicitar Funcionalidades**: [Feature Requests](https://github.com/Nakroth-5/Automatizacion-Metodos-GranM-Y-DosFases/issues/new?template=feature_request.md)
- **💬 Discusiones**: [Discussions](https://github.com/Nakroth-5/Automatizacion-Metodos-GranM-Y-DosFases/discussions)

### 🎓 Recursos Educativos

- **📚 Tutorial de Programación Lineal**: Conceptos básicos y avanzados
- **🔢 Ejemplos Prácticos**: Casos de uso reales resueltos paso a paso
- **📖 Guía de Algoritmos**: Explicación detallada de Gran M y Dos Fases

---

⭐ **¡Si te ha sido útil este proyecto, no olvides darle una estrella en GitHub!**

**Desarrollado con ❤️ para la comunidad académica y profesional**
