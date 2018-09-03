package br.ufpe.cin.if710.calculadora

class ParseException(override var message: String): Exception("Expressao invalida")