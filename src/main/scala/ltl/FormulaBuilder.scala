package ltl

class FormulaBuilder extends LtlBaseVisitor[Formula] {

  override def visitParenthesis(ctx: LtlParser.ParenthesisContext): Formula = visit(ctx.getChild(1))

  override def visitNegation(ctx: LtlParser.NegationContext) = new Not(visit(ctx.formula()))

  override def visitConjunction(ctx: LtlParser.ConjunctionContext) = new And(visit(ctx.lhs), visit(ctx.rhs))

  override def visitDisjunction(ctx: LtlParser.DisjunctionContext) = new Or(visit(ctx.lhs), visit(ctx.rhs))

  override def visitImplication(ctx: LtlParser.ImplicationContext) = new Or(new Not(visit(ctx.lhs)), visit(ctx.rhs))

  override def visitNext(ctx: LtlParser.NextContext) = new Next(visit(ctx.formula()))

  override def visitFuture(ctx: LtlParser.FutureContext) = new Future(visit(ctx.formula()))

  override def visitGlobally(ctx: LtlParser.GloballyContext) = new Global(visit(ctx.formula()))

  override def visitUntil(ctx: LtlParser.UntilContext) = new Until(visit(ctx.lhs), visit(ctx.rhs))

  override def visitRelease(ctx: LtlParser.ReleaseContext): Formula = new Release(visit(ctx.lhs), visit(ctx.rhs))

  override def visitVariable(ctx: LtlParser.VariableContext) = new Prop(ctx.ID().getText)

  override def visitBooleanLiteral(ctx: LtlParser.BooleanLiteralContext): Formula = ctx.getText match {
    case "false" => new FALSE
    case "true" => new TRUE
  }
}