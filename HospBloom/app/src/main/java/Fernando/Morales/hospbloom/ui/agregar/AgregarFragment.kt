package Fernando.Morales.hospbloom.ui.agregar

import Fernando.Morales.hospbloom.ClaseConexion
import Fernando.Morales.hospbloom.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class AgregarFragment : Fragment() {
    private lateinit var txt_Nombre: EditText
    private lateinit var txt_Sangre: EditText
    private lateinit var txt_Telefono: EditText
    private lateinit var txt_Enfermedades: EditText
    private lateinit var txt_Habitacion: EditText
    private lateinit var txt_Medicamentos: EditText
    private lateinit var txt_Nacimiento: EditText
    private lateinit var txt_Hora: EditText
    private lateinit var txt_Cama: EditText
    private lateinit var btn_Agregar: Button
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        val root = inflater.inflate(R.layout.fragment_agregar, container, false)

        txt_Nombre = root.findViewById(R.id.txt_Nombre)
        txt_Sangre = root.findViewById(R.id.txt_Sangre)
        txt_Telefono = root.findViewById(R.id.txt_Telefono)
        txt_Enfermedades = root.findViewById(R.id.txt_Enfermedades)
        txt_Habitacion = root.findViewById(R.id.txt_Habitacion)
        txt_Medicamentos = root.findViewById(R.id.txt_Medicamentos)
        txt_Nacimiento = root.findViewById(R.id.txt_Nacimiento)
        txt_Hora = root.findViewById(R.id.txt_HoraMedicacion)
        txt_Cama = root.findViewById(R.id.txt_Cama)
        btn_Agregar = root.findViewById(R.id.Agregar)

        btn_Agregar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                val objConexion = ClaseConexion().cadenaConexion()

                val Agregar = objConexion?.prepareStatement(
                    "INSERT INTO Pacientes (PacienteUUID, Nombre, TipoSangre, Telefono, Enfermedad, Num_Habitacion, Num_Cama, Lista_Medicamentos, FechaNacimiento, horaMedicacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                )!!

                Agregar.setString(1, UUID.randomUUID().toString())
                Agregar.setString(2, txt_Nombre.text.toString())
                Agregar.setString(3, txt_Sangre.text.toString())
                Agregar.setInt(4, txt_Telefono.text.toString().toInt())
                Agregar.setString(5, txt_Enfermedades.text.toString())
                Agregar.setInt(6, txt_Habitacion.text.toString().toInt())
                Agregar.setInt(7, txt_Cama.text.toString().toInt())
                Agregar.setString(8, txt_Medicamentos.text.toString())
                Agregar.setString(9, txt_Nacimiento.text.toString())
                Agregar.setString(10, txt_Hora.text.toString())
                Agregar.executeUpdate()
                LimpiarCampos()
            }
        }

        return root
    }
    private fun LimpiarCampos(){
        txt_Nombre.text.clear()
        txt_Sangre.text.clear()
        txt_Telefono.text.clear()
        txt_Enfermedades.text.clear()
        txt_Nacimiento.text.clear()
        txt_Hora.text.clear()
        txt_Habitacion.text.clear()
        txt_Cama.text.clear()
        txt_Medicamentos.text.clear()
    }
}