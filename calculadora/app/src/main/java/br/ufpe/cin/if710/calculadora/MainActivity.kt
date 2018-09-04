package br.ufpe.cin.if710.calculadora

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var text: String = ""
        val editText = findViewById<EditText>(R.id.text_calc)
        val textView = findViewById<TextView>(R.id.text_info)

        if(savedInstanceState != null){
            textView.text = savedInstanceState.getString("FROM_TEXT_VIEW")
            text = savedInstanceState.getString("FROM_EDIT_TEXT")
            editText.setText(savedInstanceState.getString("FROM_EDIT_TEXT"))
        }
       

        //acoes dos botoes da calculadora
        //numeros/operacoes printados na tela
        // clear apaga o que tiver e "= calcula
        btn_0.setOnClickListener {
            text += "0"
            editText.setText(text) }
        btn_1.setOnClickListener {
            text += "1"
            editText.setText(text)}
        btn_2.setOnClickListener {
            text += "2"
            editText.setText(text)
        }
        btn_3.setOnClickListener {
            text += "3"
            editText.setText(text) }
        btn_4.setOnClickListener {
            text += "4"
            editText.setText(text) }
        btn_5.setOnClickListener {
            text += "5"
            editText.setText(text)
        }
        btn_6.setOnClickListener {
            text += "6"
            editText.setText(text) }
        btn_7.setOnClickListener {
            text += "7"
            editText.setText(text)
        }
        btn_8.setOnClickListener {
            text += "8"
            editText.setText(text)
        }
        btn_9.setOnClickListener {
            text += "9"
            editText.setText(text)
        }
        btn_Add.setOnClickListener {
            text += "+"
            editText.setText(text)
        }
        btn_Subtract.setOnClickListener {
            text += "-"
            editText.setText(text)
        }
        btn_Multiply.setOnClickListener {
            text += "*"
            editText.setText(text) }
        btn_Divide.setOnClickListener {
            text += "/"
            editText.setText(text)
        }
        btn_Power.setOnClickListener {
            text += "^"
            editText.setText(text)
        }
        btn_Equal.setOnClickListener {
            try {
                val result: String = eval(text).toString()
                textView.text = result
            } catch (e: ParseException){
                //se houver erro na funcao eval eh pq uma expressao invalida foi inserida
                //lanca um toast para notificar
                val text = "Expressao invalida"
                val duration = Toast.LENGTH_LONG

                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }
            text = ""
            editText.setText(text)
        }
        btn_Dot.setOnClickListener {
            text += "."
            editText.setText(text)
        }
        btn_LParen.setOnClickListener {
            text += "("
            editText.setText(text)
        }
        btn_RParen.setOnClickListener {
            text += ")"
            editText.setText(text)
        }
        btn_Clear.setOnClickListener {
            text = ""
            editText.setText(text)
        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("FROM_TEXT_VIEW", text_info.text.toString())
        outState?.putString("FROM_EDIT_TEXT", text_calc.text.toString())
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