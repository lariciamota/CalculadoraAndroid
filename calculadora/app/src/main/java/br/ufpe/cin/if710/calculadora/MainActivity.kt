package br.ufpe.cin.if710.calculadora

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    val FROM_TEXT_VIEW = "FROM_TEXT_VIEW"
    val FROM_EDIT_TEXT = "FROM_EDIT_TEXT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.text_calc)
        val textView = findViewById<TextView>(R.id.text_info)

        //recebendo de volta o estado anterior à mudanca de configuracao, se houve
        if(savedInstanceState != null){
            textView.text = savedInstanceState.getString(FROM_TEXT_VIEW)
            editText.setText(savedInstanceState.getString(FROM_EDIT_TEXT))
        }


        //acoes dos botoes da calculadora
        //numeros/operacoes printados na tela
        // clear apaga o que tiver e "= calcula
        btn_0.setOnClickListener { editText.setText(editText.text.toString() + "0")}
        btn_1.setOnClickListener { editText.setText(editText.text.toString() + "1") }
        btn_2.setOnClickListener { editText.setText(editText.text.toString() + "2") }
        btn_3.setOnClickListener { editText.setText(editText.text.toString() + "3") }
        btn_4.setOnClickListener { editText.setText(editText.text.toString() + "4") }
        btn_5.setOnClickListener { editText.setText(editText.text.toString() + "5") }
        btn_6.setOnClickListener { editText.setText(editText.text.toString() + "6") }
        btn_7.setOnClickListener { editText.setText(editText.text.toString() + "7") }
        btn_8.setOnClickListener { editText.setText(editText.text.toString() + "8") }
        btn_9.setOnClickListener { editText.setText(editText.text.toString() + "9") }
        btn_Add.setOnClickListener { editText.setText(editText.text.toString() + "+") }
        btn_Subtract.setOnClickListener { editText.setText(editText.text.toString() + "-") }
        btn_Multiply.setOnClickListener { editText.setText(editText.text.toString() + "*") }
        btn_Divide.setOnClickListener { editText.setText(editText.text.toString() + "/") }
        btn_Power.setOnClickListener { editText.setText(editText.text.toString() + "^") }
        btn_Equal.setOnClickListener {
            try {
                val result: String = eval(editText.text.toString()).toString()
                textView.text = result
            } catch (e: ParseException){
                //se houver erro na funcao eval eh pq uma expressao invalida foi inserida
                //lanca um toast para notificar
                val text = "Expressao invalida"
                val duration = Toast.LENGTH_LONG

                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }
            editText.setText("")
        }
        btn_Dot.setOnClickListener { editText.setText(editText.text.toString() + ".") }
        btn_LParen.setOnClickListener { editText.setText(editText.text.toString() + "(") }
        btn_RParen.setOnClickListener { editText.setText(editText.text.toString() + ")") }
        btn_Clear.setOnClickListener { editText.setText("") }

    }

    //salvando o estado para nao perder info qnd configuracao mudar
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString(FROM_TEXT_VIEW, text_info.text.toString())
        outState?.putString(FROM_EDIT_TEXT, text_calc.text.toString())
        super.onSaveInstanceState(outState)
    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw ParseException("Caractere inesperado: " + ch)
                //exception criada para ser tratada especialmente, sem quebrar aplicacao
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw ParseException("Função desconhecida: " + func)
                } else {
                    throw ParseException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }
}