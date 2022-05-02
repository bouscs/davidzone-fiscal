package com.example.davidzonefiscal.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

import com.example.davidzonefiscal.databinding.ActivityConsultarBinding
import androidx.fragment.app.Fragment
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.entities.DavidGlobals
import com.google.gson.Gson

import com.google.gson.GsonBuilder
import org.json.JSONObject


class ConsultarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConsultarBinding
    var globals: DavidGlobals = DavidGlobals()

    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()

    private val logEntry = "CONSULTA_PLACA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConsultarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        functions = Firebase.functions("southamerica-east1")

        binding.btnConsult.setOnClickListener{
            globals.timerbotao(binding.btnConsult)
            hideKeyboard()
            onConsultarPlacaClicked()
        }
    }

    private fun validarPlaca(placa:String):Boolean {
        if (placa.length != 7) {
            return false
        } else {
            for (letter in 0..2){
                if(placa[letter].isLetter() == false) return false
            }
            for (num in 3..6){
                if(placa[num].isDigit() == false) return false
            }
        }
        return true
    }

    private fun consultarPlaca (placa: String): Task<String> {
        val data = hashMapOf(
            "placa" to placa
        )
        return functions
            .getHttpsCallable("consultaPlaca")
            .call(data)
            .continueWith { task ->
                val result = gson.toJson(task.result?.data)
                result
            }
    }

    private fun onConsultarPlacaClicked() {
        val placa = binding.etPlaca.text.toString().lowercase()

        if (!validarPlaca(placa)) {
            // showSnackbar("Please enter a message.")
            binding.textField.error = "Digite uma placa válida!"
            return
        }

        consultarPlaca (placa)
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    if (e is FirebaseFunctionsException) {
                        val code = e.code
                        val details = e.details
                        Log.e("FirebaseFunctionsExc", "Error code $code : $details")
                    }
                    Log.w(TAG, "consultarPlaca:onFailure", e)
                    Snackbar.make(binding.etPlaca, "Erro no servidor. Se o problema persistir ligue 0800-000-0000", Snackbar.LENGTH_LONG)
                        .setAction("Tente novamente") {
                            onConsultarPlacaClicked()
                        }
                        .show()
                    return@OnCompleteListener
                }

                val result = task.result

                val genericConsultRes = gson.fromJson(result, GenericConsultarPlacaResponse::class.java)
                Log.i(logEntry, genericConsultRes.toString())

                val message = genericConsultRes.resultConsult.message
                val status = genericConsultRes.resultConsult.status
                Log.i(logEntry, message)
                Log.i(logEntry, status)

                if(status == "SUCCESS"){
                    val consultPlacaExistenteRes = gson.fromJson(result, ConsultarPlacaExistenteResponse::class.java)
                    val ticketRegular = consultPlacaExistenteRes.result.payload.regular
                    Log.i(logEntry, ticketRegular.toString())

                    if(ticketRegular){
                        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                        builder.setTitle(R.string.consulta_regular_titulo)
                        builder.setMessage(R.string.consulta_regular_mensagem)
                        builder.setPositiveButton(R.string.ok) { dialog, which ->
                            //val intentConsultaValid = Intent(this@ConsultarActivity, MapsActivity::class.java)
                            //startActivity(intentConsultaValid)
                            finish()
                        }
                        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)

                    } else {
                        val tipo = 2
                        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                        builder.setTitle(R.string.consulta_irregular_titulo)
                        builder.setMessage(R.string.consulta_irregular_mensagem)
                        builder.setPositiveButton(R.string.registrar_irregularidade_short) { _, _ ->
                            val intentConsultaNotValid = Intent(this@ConsultarActivity, TirarFotosActivity::class.java)
                            intentConsultaNotValid.putExtra("placa", placa)
                            intentConsultaNotValid.putExtra("tipo", tipo)
                            startActivity(intentConsultaNotValid)
                            finish()
                        }
                        builder.setNeutralButton(R.string.voltar) { dialog, _ ->
                            dialog.dismiss()
                        }
                        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
                        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.WHITE)
                    }
                } else {
                    if(status == "E_INVALID_INPUT") binding.textField.error = "Digite uma placa válida!"
                    else if (status == "E_DOC_NOT_FOUND"){
                        binding.textField.error = "Veículo não encontrado na Base de Dados."
                    }
                }
            })
    }


    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "ConsultarActivity"
    }

    // Funções para esconder o teclado
    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }
    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
