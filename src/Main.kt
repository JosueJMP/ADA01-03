import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import org.w3c.dom.Document
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

fun main() {
    // Ruta de archivos
    val rutaTexto = "C:\\Users\\Josue\\Documents\\Proyectos\\ADA01-03\\src\\resource\\empleados.csv"
    val rutaXML = "C:\\Users\\Josue\\Documents\\Proyectos\\ADA01-03\\src\\resource\\empleados.xml"

    // 1. Leer empleados desde archivo de texto
    val empleados = leerDesdeTexto(rutaTexto)

    // 2. Crear archivo XML desde los empleados leídos
    crearXML(empleados, rutaXML)

    // 3. Modificar el salario de un empleado
    modificarEmpleado(rutaXML, 13, 131313.0)

    // 4. Leer y mostrar empleados desde el archivo XML
    leerDesdeXML(rutaXML)
}

// Función para leer empleados desde un archivo de texto
fun leerDesdeTexto(rutaArchivo: String): List<Empleado> {
    val empleados = mutableListOf<Empleado>()
    val path = Paths.get(rutaArchivo)

    Files.newBufferedReader(path).use { br ->
        br.readLine() // Leer encabezado
        br.forEachLine { linea ->
            val campos = linea.split(",")
            val empleado = Empleado(
                id = campos[0].toInt(),
                apellido = campos[1],
                departamento = campos[2],
                salario = campos[3].toDouble()
            )
            empleados.add(empleado)
        }
    }
    return empleados
}

// Función para crear un archivo XML desde los datos de empleados
fun crearXML(empleados: List<Empleado>, rutaArchivo: String) {
    try {
        val docFactory = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docFactory.newDocument()

        // Elemento raíz <empleados>
        val rootElement = doc.createElement("empleados")
        doc.appendChild(rootElement)

        for (empleado in empleados) {
            // Crear <empleado>
            val empleadoElement = doc.createElement("empleado")
            empleadoElement.setAttribute("id", empleado.id.toString())
            rootElement.appendChild(empleadoElement)

            // Añadir <apellido>
            val apellidoElement = doc.createElement("apellido")
            apellidoElement.appendChild(doc.createTextNode(empleado.apellido))
            empleadoElement.appendChild(apellidoElement)

            // Añadir <departamento>
            val departamentoElement = doc.createElement("departamento")
            departamentoElement.appendChild(doc.createTextNode(empleado.departamento))
            empleadoElement.appendChild(departamentoElement)

            // Añadir <salario>
            val salarioElement = doc.createElement("salario")
            salarioElement.appendChild(doc.createTextNode(empleado.salario.toString()))
            empleadoElement.appendChild(salarioElement)
        }

        // Guardar el archivo XML
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        val source = DOMSource(doc)
        val result = StreamResult(File(rutaArchivo))
        transformer.transform(source, result)

        println("Archivo XML generado exitosamente.")
    } catch (e: ParserConfigurationException) {
        e.printStackTrace()
    }
}

// Función para modificar el salario de un empleado en el archivo XML basado en su ID
fun modificarEmpleado(rutaArchivo: String, idEmpleado: Int, nuevoSalario: Double) {
    val archivo = File(rutaArchivo)
    val docFactory = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val doc = docFactory.parse(archivo)

    val empleados = doc.getElementsByTagName("empleado")
    for (i in 0 until empleados.length) {  // Corrección aquí
        val empleado = empleados.item(i) as Element
        if (empleado.getAttribute("id").toInt() == idEmpleado) {
            val salarioNode = empleado.getElementsByTagName("salario").item(0)
            salarioNode.textContent = nuevoSalario.toString()
        }
    }

    // Guardar el archivo modificado
    val transformerFactory = TransformerFactory.newInstance()
    val transformer = transformerFactory.newTransformer()
    val source = DOMSource(doc)
    val result = StreamResult(archivo)
    transformer.transform(source, result)

    println("Salario del empleado con ID $idEmpleado modificado a $nuevoSalario")
}


// Función para leer empleados desde el archivo XML y mostrarlos en consola
fun leerDesdeXML(rutaArchivo: String) {
    val archivo = File(rutaArchivo)
    val docFactory = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val doc = docFactory.parse(archivo)

    val empleados = doc.getElementsByTagName("empleado")
    for (i in 0 until empleados.length) {
        val empleado = empleados.item(i) as Element
        val id = empleado.getAttribute("id")
        val apellido = empleado.getElementsByTagName("apellido").item(0).textContent
        val departamento = empleado.getElementsByTagName("departamento").item(0).textContent
        val salario = empleado.getElementsByTagName("salario").item(0).textContent
        println("ID: $id, Apellido: $apellido, Departamento: $departamento, Salario: $salario")
    }
}
