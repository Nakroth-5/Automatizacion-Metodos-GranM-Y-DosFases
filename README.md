# Automatización de los Métodos Gran M y Dos Fases

[![Java](https://img.shields.io/badge/Java-17%2B-blue)](https://www.java.com/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-purple)](https://openjfx.io/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)
![Interfaz de la Aplicación](https://github.com/user-attachments/assets/d85e18b6-d207-49b1-a167-819cc2434f9c)


## 📌 Descripción

Aplicación Java que automatiza los métodos de la **Gran M** y **Dos Fases**
para resolver problemas de Programación Lineal (PL), con interfaz gráfica intuitiva y visualización detallada del proceso Simplex.

## 📥 Instalación
1. Clonar repositorio:
```bash
git clone https://github.com/Nakroth-5/Automatizacion-Metodos-GranM-Y-DosFases.git
cd Automatizacion-Metodos-GranM-Y-DosFases
```
2. Ejecutar con Maven: mvn clean javafx:run

## ✨ Características

- **Métodos implementados**:
  - Algoritmo Simplex estándar
  - Método de la Gran M
  - Método de las Dos Fases
- **Visualización completa**:
  - Tableaus por iteración
  - Variables entrantes/salientes
  - Explicaciones paso a paso
- **Soporte para**:
  - Maximización/Minimización
  - Restricciones (≤, ≥, =)
  - Hasta 100 variables y 500 restricciones

## 🛠️ Estructura del Proyecto
src/
├── main/
│ ├── java/
│ │ ├── metodos/ # Lógica de algoritmos
│ │ │ ├── SimplexBase.java
│ │ │ ├── GranM.java
│ │ │ └── DosFases.java
│ │ └── ui/ # Interfaz gráfica
│ │ ├── controllers/
│ │ └── views/
├── resources/
│ ├── styles/
│ └── fxml/



## 🖥️ Manual de Uso
1.	Inicio de la Aplicación:
	Ejecute el programa. Se abrirá la ventana principal.
2.	Configuración del Problema:
	Paso 1: Use los selectores (spinners) en la parte superior para definir el "Número de Variables" y el "Número de Restricciones" de su problema.
	Paso 2: Haga clic en el botón "Generar Modelo". La interfaz se actualizará para mostrar los campos necesarios.
3.	Ingreso de Datos:
1.	Función Objetivo: 
a.	Seleccione "Maximizar" o "Minimizar" en el menú desplegable.
b.	Seleccione el método a utilizar: "Gran M" o "Dos Fases".
c.	Ingrese los coeficientes de cada variable x en los campos de texto correspondientes.
2.	Restricciones: 
a.	Para cada restricción, ingrese los coeficientes de las variables.
b.	Seleccione el tipo de desigualdad (≤, ≥, =) en el menú desplegable.
c.	Ingrese el valor del término independiente (lado derecho de la ecuación) en el campo "Valor".
4.	Ejecución y Resultados:
	Paso 1: Una vez que todos los datos estén ingresados, haga clic en el botón "Resolver Problema".
	Paso 2: Si los datos son válidos, se abrirá una nueva ventana titulada "Proceso del Método Simplex".
	Paso 3: En esta ventana, podrá ver el tableau inicial. Use los botones "Siguiente" y "Anterior" para navegar a través de cada iteración del algoritmo.
	Paso 4: Al final del proceso, en la parte inferior de la ventana, se mostrará un resumen con la "Solución Óptima", detallando los valores finales de las variables de decisión y el valor óptimo de la función objetivo Z.
	En caso de un error (ej. problema no acotado o infactible), se mostrará un mensaje descriptivo.
5.	Limpiar y Salir:
	Use el botón "Limpiar" en la ventana principal para borrar todos los campos y comenzar un nuevo problema.
	Use el botón "Salir" para cerrar la aplicación.
## 🧑‍💻 Autores
    Evert Rodriguez Araúz - @Nakroth-5
    
    Maikol Anthony Molina Cortez 
## 🔗 Enlace
    Proyecto IO1.docx
    [Uploading Presentacion6.html…]()



