package com.example.davidzonefiscal.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.davidzonefiscal.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BottomSheetFragmento:BottomSheetDialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_fragmento,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var btnFechar = view.findViewById(R.id.btnFechar) as FloatingActionButton
        var ruaAtual = view.findViewById(R.id.tvRua) as TextView
        var ptoAtual = view.findViewById(R.id.tvPonto) as TextView
        var ruaRestante1 = view.findViewById(R.id.tvLogradourosRestantes) as TextView
        var ruaRestante2 = view.findViewById(R.id.tvLogradourosRestantes2) as TextView



        btnFechar.setOnClickListener { dismiss() }
        ruaAtual.setText((activity as MapsActivity).logradouros.rua)
        ptoAtual.setText("(${(activity as MapsActivity).ptoAtual.latitude}, ${(activity as MapsActivity).ptoAtual.longitude})")


        when ((activity as MapsActivity).ptoAtualNo) {
            0 -> ptoAtual.setText("1 ")

            1 -> ptoAtual.setText("2 ")

            -1 -> ptoAtual.setText("3 ")
        }

        when ((activity as MapsActivity).logradouroAtualNo) {
            0 -> {
                ruaRestante1.setText((activity as MapsActivity).itinerario.logradouro2.rua)
                ruaRestante2.visibility = View.VISIBLE
                ruaRestante2.setText((activity as MapsActivity).itinerario.logradouro3.rua)
            }
            1 -> {
                ruaRestante1.setText((activity as MapsActivity).itinerario.logradouro3.rua)
            }
        }
    }
}

