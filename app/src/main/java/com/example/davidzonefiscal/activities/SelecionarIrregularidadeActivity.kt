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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class SelecionarIrregularidadeActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var txtLayout : TextInputLayout
    private lateinit var btnProx : Button
    private lateinit var tvIrregularidade : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selecionar_irregularidade)

        tvIrregularidade = findViewById(R.id.tvIrregularidade)
        txtLayout = findViewById(R.id.txtLayout)
        radioGroup = findViewById(R.id.radioGroup)
        btnProx = findViewById(R.id.btnProx)
        btnProx.setOnClickListener{
            btnProx()
        }

        val checkedRadioButtonId = radioGroup.checkedRadioButtonId
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId!=R.id.radio3){
                txtLayout.visibility = View.GONE
                hideKeyboard()
            }
            else txtLayout.visibility = View.VISIBLE
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

    private fun btnProx() {
        if (radioGroup.checkedRadioButtonId!=View.NO_ID){
            val intentTirarFotos = Intent(this, TirarFotosActivity::class.java)
            startActivity(intentTirarFotos)
        } else {
            Snackbar.make(tvIrregularidade, "Selecione uma opção", Snackbar.LENGTH_LONG).show()
        }
    }

}