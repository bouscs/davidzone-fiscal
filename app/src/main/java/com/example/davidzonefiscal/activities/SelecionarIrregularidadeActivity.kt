package com.example.davidzonefiscal.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.davidzonefiscal.R
import com.example.davidzonefiscal.databinding.ActivitySelecionarIrregularidadeBinding
import com.example.davidzonefiscal.entities.DavidGlobals
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class SelecionarIrregularidadeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelecionarIrregularidadeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelecionarIrregularidadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Validar seleção ao clicar no botão próximo
        binding.btnProx.setOnClickListener{
            hideKeyboard()
            validarSelecao()
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

    private fun validarSelecao() {
        if (binding.radioGroup.checkedRadioButtonId!=View.NO_ID && validarPlaca(binding.etPlaca.text.toString())){
            val intentTirarFotos = Intent(this, TirarFotosActivity::class.java)

            intentTirarFotos.putExtra("placa", binding.etPlaca.text.toString())

            if (binding.radio1.isChecked) {
                val tipo = 1
                intentTirarFotos.putExtra("tipo", tipo)
            }
            if (binding.radio2.isChecked) {
                val tipo = 2
                intentTirarFotos.putExtra("tipo", tipo)
            }
            startActivity(intentTirarFotos)
            finish()
        } else {
            Toast.makeText(this, "Preencha os dados corretamente!", Toast.LENGTH_SHORT).show()
        }
    }

    // Funções para esconder o teclado
    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}