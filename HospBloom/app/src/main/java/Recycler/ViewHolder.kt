package Recycler

import Fernando.Morales.hospbloom.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolder (view: View): RecyclerView.ViewHolder(view) {
    val txtNombre: TextView = view.findViewById(R.id.txtNombreCard)
    val IMGBORAR: ImageView = view.findViewById(R.id.imgBorrar)
    val IMGEDITAR: ImageView = view.findViewById(R.id.imgEditar)
}