package ltl

import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

object ParserRunner {

  def parseFormula(input: String): Formula = {
    val lexer = new LtlLexer(new ANTLRInputStream(input))
    val parser = new LtlParser(new CommonTokenStream(lexer))
    val formula = parser.formula()
    new FormulaBuilder().visit(formula)
  }

}
