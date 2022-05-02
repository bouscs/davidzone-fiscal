package com.example.davidzonefiscal.entities

import android.os.CountDownTimer
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DavidGlobals {
    companion object {
        lateinit var itinerario: Itinerario

        val REQUEST_REGISTRAR_IRREGULARIDADE = 1
        val REQUEST_CONSULTA_PLACA = 2
    }


    //função de timer pra botões pressionados
    fun timerbotao(botao: Button) {
        val timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                botao.setEnabled(false)
            }

            override fun onFinish() {
                botao.setEnabled(true)
            }
        }
        timer.start()
    }
    fun timerbotaoFAB(botao: FloatingActionButton) {
        val timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                botao.setEnabled(false)
            }

            override fun onFinish() {
                botao.setEnabled(true)
            }
        }
        timer.start()
    }

}