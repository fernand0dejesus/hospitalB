package Recycler

import Fernando.Morales.hospbloom.ClaseConexion
import Fernando.Morales.hospbloom.R
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Adaptador(var Datos: List<DataClassPaciente>): RecyclerView.Adapter<ViewHolder>() {
    fun ActualizarListaDespuesDeEditar(uuid: String, nuevoNombre: String){
        val Index = Datos.indexOfFirst { it.uuid == uuid }
        Datos[Index].Nombre = nuevoNombre
        notifyItemChanged(Index)
    }

    fun eliminarDatos(nombrePaciente: String, posicion: Int){
        //Eliminarlo de la lista
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        //Eliminarlo de la base de datos
        GlobalScope.launch(Dispatchers.IO){
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- creo una variable que contenga
            val deletePaciente = objConexion?.prepareStatement("DELETE Pacientes WHERE PacienteUUID = ?")!!
            deletePaciente.setString(1, nombrePaciente)
            deletePaciente.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    fun actualizarDato(
        nombrePaciente: String,
        tipo_sangre: String?,
        telefono: String?,
        enfermedad: String?,
        numero_habitacion: Int?,
        numero_cama: Int?,
        medicamentos: String?,
        fecha_nacimiento: String?,
        hora_medicacion: String?,
        uuid: String,
        scope: CoroutineScope
    ) {
        scope.launch(Dispatchers.IO) {
            // 1- Creo un obj de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            // 2- Creo una variable que contenga un PrepareStatement
            val updatePaciente = objConexion?.prepareStatement(
                "UPDATE Pacientes SET " + "Nombre = ?, TipoSangre = ?, Telefono = ?, Enfermedad = ?, " + "Num_Habitacion = ?, Num_Cama = ?, Lista_Medicamentos = ?, " + "FechaNacimiento = ?, horaMedicacion = ? WHERE PacienteUUID = ?"
            )
            updatePaciente?.apply {
                setString(1, nombrePaciente)
                setString(2, tipo_sangre)
                setString(3, telefono)
                setString(4, enfermedad)
                setObject(5, numero_habitacion)
                setObject(6, numero_cama)
                setString(7, medicamentos)
                setString(8, fecha_nacimiento)
                setString(9, hora_medicacion)
                setString(10, uuid)
                executeUpdate()
            }

            withContext(Dispatchers.Main) {
                ActualizarListaDespuesDeEditar(uuid, nombrePaciente)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Conectar el RecyclerView con la Card
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Poder darle clic a la elemento de la card
        val item = Datos[position]
        holder.txtNombre.text = item.Nombre

        //Todo: clic al icono de borrar
        holder.IMGBORAR.setOnClickListener {
            //Creo la alerta para confirmar la eliminacion
            //1) Invoco el contexto

            val context = holder.itemView.context


            //2)Creo la alerta en blanco
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro que quiere borrar?")

            builder.setPositiveButton("Si"){dialog, wich ->
                eliminarDatos(item.uuid, position)
            }

            builder.setNegativeButton("No"){dialog, wich ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        //TODO: ClIC AL ICONO DE EDITAR

        holder.IMGEDITAR.setOnClickListener {
            val context = holder.itemView.context

            val dialogView = LayoutInflater.from(context).inflate(R.layout.update, null)

            val Txt_Nombre = dialogView.findViewById<EditText>(R.id.Edit_Nombre)
            val Txt_TipoSangre = dialogView.findViewById<EditText>(R.id.Edit_TipoSangre)
            val Txt_Telefono = dialogView.findViewById<EditText>(R.id.Edit_Telefono)
            val Txt_Enfermedad = dialogView.findViewById<EditText>(R.id.Edit_Enfermedad)
            val Txt_FechaNacimiento = dialogView.findViewById<EditText>(R.id.Edit_FechaNacimiento)
            val Txt_HoraMedicion = dialogView.findViewById<EditText>(R.id.Edit_HoraMedicion)
            val Txt_NumeroHabitacion = dialogView.findViewById<EditText>(R.id.Edit_NumeroHabitacion)
            val Txt_NumeroCama = dialogView.findViewById<EditText>(R.id.Edit_NumeroCama)
            val Txt_Medicamentos = dialogView.findViewById<EditText>(R.id.Edit_Medicamentos)

            Txt_Nombre.setText(item.Nombre)
            Txt_TipoSangre.setText(item.Sangre)
            Txt_Telefono.setText(item.Telefono)
            Txt_Enfermedad.setText(item.Enfermedad)
            Txt_FechaNacimiento.setText(item.Nacimiento)
            Txt_HoraMedicion.setText(item.HoraMedicacion)
            Txt_NumeroHabitacion.setText(item.Habitacion?.toString() ?: "")
            Txt_NumeroCama.setText(item.Cama?.toString() ?: "")
            Txt_Medicamentos.setText(item.Medicamentos)

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar Paciente")
            builder.setView(dialogView)

            builder.setPositiveButton("Actualizar") { dialog, _ ->
                val nombrePaciente = Txt_Nombre.text.toString()
                val Sangre = Txt_TipoSangre.text.toString()
                val Telefono = Txt_Telefono.text.toString()
                val Enfermedad = Txt_Enfermedad.text.toString()
                val Habitacion = Txt_NumeroHabitacion.text.toString().takeIf { it.isNotEmpty() }?.toIntOrNull()
                val Cama = Txt_NumeroCama.text.toString().takeIf { it.isNotEmpty() }?.toIntOrNull()
                val medicamentos = Txt_Medicamentos.text.toString()
                val Nacimiento = Txt_FechaNacimiento.text.toString()
                val horaMedicion = Txt_HoraMedicion.text.toString()


                actualizarDato(nombrePaciente, Sangre, Telefono, Enfermedad, Habitacion, Cama, medicamentos, Nacimiento, horaMedicion, item.uuid, GlobalScope)

                dialog.dismiss()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("nombres", item.Nombre)
                putString("tipo_sangre", item.Sangre)
                putString("telefono", item.Telefono)
                putString("enfermedad", item.Enfermedad)
                putInt("numero_habitacion", item.Habitacion)
                putInt("numero_cama", item.Cama)
                putString("medicamentos", item.Medicamentos)
                putString("fecha_nacimiento", item.Nacimiento)
                putString("hora_medicacion", item.HoraMedicacion)
            }
            val navController = findNavController(holder.itemView)
            navController.navigate(R.id.Detalles, bundle)
        }
    }
    private fun findNavController(view: View): NavController {
        val fragment = view.findFragment<Fragment>()
        return NavHostFragment.findNavController(fragment)
    }
}