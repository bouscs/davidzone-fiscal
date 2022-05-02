package com.example.davidzonefiscal.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
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
            if (binding.radioGroup.checkedRadioButtonId!=View.NO_ID){
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
                Snackbar.make(binding.tvIrregularidade, "Selecione uma opção", Snackbar.LENGTH_LONG).show()
            }
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