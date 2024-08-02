package Fernando.Morales.hospbloom.ui.list

import Fernando.Morales.hospbloom.ClaseConexion
import Fernando.Morales.hospbloom.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import Fernando.Morales.hospbloom.databinding.FragmentListaBinding
import android.text.InputFilter
import android.text.InputType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import Recycler.Adaptador
import Recycler.DataClassPaciente
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.withContext
import java.util.UUID

class ListFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_lista, container, false)

        val rcvPaciente = root.findViewById<RecyclerView>(R.id.RCVPacientes)
        rcvPaciente .layoutManager = LinearLayoutManager(requireContext())

        fun mostrarDatos(): List<DataClassPaciente> {
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Creo un Statement
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM Pacientes")!!


            val Pacientes = mutableListOf<DataClassPaciente>()

            while (resultSet.next()){
                val Nombre = resultSet.getString("Nombre")
                val Sangr3 = resultSet.getString("TipoSangre")
                val Telefono = resultSet.getString("Telefono")
                val Enfermedades = resultSet.getString("Enfermedad")
                val Habitacion = resultSet.getInt("Num_Habitacion")
                val Cama = resultSet.getInt("Num_Cama")
                val Medicamentos = resultSet.getString("Lista_Medicamentos")
                val Nacimiento = resultSet.getString("FechaNacimiento")
                val Hora = resultSet.getString("horaMedicacion")
                val uuid = resultSet.getString("PacienteUUID")
                val paciente = DataClassPaciente(uuid, Nombre, Sangr3, Telefono, Enfermedades, Habitacion, Cama, Medicamentos, Nacimiento, Hora)
                Pacientes.add(paciente)
            }
            return Pacientes
        }

        CoroutineScope(Dispatchers.IO).launch{
            val PacienteDB = mostrarDatos()
            withContext(Dispatchers.Main){
                val miAdaptador = Adaptador(PacienteDB)
                rcvPaciente .adapter = miAdaptador
            }
        }

        return root
    }
}