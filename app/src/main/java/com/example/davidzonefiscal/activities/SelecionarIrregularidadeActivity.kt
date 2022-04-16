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
                startActivity(intentTirarFotos)
            } else {
                Snackbar.make(binding.tvIrregularidade, "Selecione uma opção", Snackbar.LENGTH_LONG).show()
            }
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