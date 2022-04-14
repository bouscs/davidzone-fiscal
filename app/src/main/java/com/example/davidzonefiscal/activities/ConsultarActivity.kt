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
import com.example.davidzonefiscal.databinding.ActivityConsultarBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ConsultarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConsultarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConsultarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConsult.setOnClickListener{
            hideKeyboard()
            consulta()
        }
    }

    private fun consulta() {
        var internetError = false
        var backendError = false

        if (internetError) {
            Snackbar.make(binding.etPlaca, "Sem conexão de internet", Snackbar.LENGTH_LONG)
                .setAction("Tente novamente") {
                    consulta()
                }
                .show()
        } else if (backendError){
            Snackbar.make(binding.etPlaca, "Erro no servidor. Se o problema persistir ligue 0800-000-0000", Snackbar.LENGTH_LONG)
                .setAction("Tente novamente") {
                    consulta()
                }
                .show()

        // TODO validar placa mais formal (3 char seguido de 4 int)
        } else if(binding.etPlaca.text.isNullOrEmpty()|| binding.etPlaca.text!!.length!=7){
            binding.textField.error = "Digite uma placa válida!"
        } else if(binding.etPlaca.text.toString()!="AAA1111"&&binding.etPlaca.text.toString()!="AAA1112"){
            binding.textField.error = "Veículo não encontrado na Base de Dados."
        } else if(binding.etPlaca.text.toString()=="AAA1111"){
            val intentConsult = Intent(this@ConsultarActivity, ResultadoConsultaActivity::class.java )
            // Ticket valido
            startActivity(intentConsult)
        } else if(binding.etPlaca.text.toString()=="AAA1112"){
            // Ticket não valido
            val intentConsultNoTicket = Intent(this@ConsultarActivity, ResultadoNoTicketActivity::class.java )
            startActivity(intentConsultNoTicket)
        }
    }

    // Funções para esconder o teclado
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