package com.example.davidzonefiscal.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.davidzonefiscal.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ConsultarActivity : AppCompatActivity() {

    private lateinit var etPlaca : EditText
    private lateinit var btnConsult : Button
    private lateinit var textField : TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultar)

        etPlaca = findViewById(R.id.etPlaca)
        btnConsult = findViewById(R.id.btnConsult)
        btnConsult.setOnClickListener{
            hideKeyboard()
            consulta()
        }
        textField = findViewById(R.id.textField)

    }

    private fun consulta(){
        if(etPlaca.text.isNullOrEmpty()||etPlaca.text.length!=7){
            textField.error = "Digite uma placa válida!"
        } else if(etPlaca.text.toString()!="AAA1111"&&etPlaca.text.toString()!="AAA1112"){
            textField.error = "Veículo não encontrado na Base de Dados."
        } else if(etPlaca.text.toString()=="AAA1111"){
            val intentConsult = Intent(this@ConsultarActivity, ResultadoConsultaActivity::class.java )
            startActivity(intentConsult)
        } else if(etPlaca.text.toString()=="AAA1112"){
            val intentConsultNoTicket = Intent(this@ConsultarActivity, ResultadoNoTicketActivity::class.java )
            startActivity(intentConsultNoTicket)
        }

    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}