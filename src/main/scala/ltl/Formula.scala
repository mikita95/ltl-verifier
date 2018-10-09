package ltl
import scala.collection.JavaConverters._

trait Formula {

  def subformulas: Set[Formula] = Set(this) ++ {
    this match {
      case _: Prop => Set()
      case _: TRUE => Set()
      case _: FALSE => Set()
      case p: Not => p.body.subformulas
      case p: And => p.left.subformulas ++ p.right.subformulas
      case p: Or => p.left.subformulas ++ p.right.subformulas
      case p: Impl => p.left.subformulas ++ p.right.subformulas
      case p: Until => p.left.subformulas ++ p.right.subformulas
      case p: Release => p.left.subformulas ++ p.right.subformulas
      case p: Future => p.body.subformulas
      case p: Global => p.body.subformulas
      case p: Next => p.body.subformulas
      case _ => Set()
    }
  }

  override def equals(obj: scala.Any): Boolean = obj match {
    case obj: Formula => obj.toString == this.toString
    case _ => false
  }

  override def hashCode: Int = this.toString.hashCode

  override def toString: String = this match {
    case p: Prop => p.name
    case p: And => s"(${p.left}) & (${p.right})"
    case p: Or => s"(${p.left}) | (${p.right})"
    case p: Impl => s"${p.left} -> ${p.right}"
    case p: Until => s"(${p.left}) U (${p.right})"
    case p: Release =>  s"(${p.left}) R (${p.right})"
    case p: Future => s"F(${p.body})"
    case p: Global => s"G(${p.body})"
    case p: Next => s"X(${p.body})"
    case p: Not => s"!(${p.body})"
    case p: TRUE => "true"
    case p: FALSE => "false"
    case _ => "unknown"
  }

  def subformulasJava(): java.util.Set[Formula] = subformulas.asJava

  def variables(): Set[Prop] = this match {
    case p: Prop => Set(p)
    case p: And => p.left.variables() ++ p.right.variables()
    case p: Or => p.left.variables() ++ p.right.variables()
    case p: Impl => p.left.variables() ++ p.right.variables()
    case p: Until => p.left.variables() ++ p.right.variables()
    case p: Release => p.left.variables() ++ p.right.variables()
    case p: Future => p.body.variables()
    case p: Global => p.body.variables()
    case p: Next => p.body.variables()
    case p: Not => p.body.variables()
    case p: TRUE => Set()
    case p: FALSE => Set()
    case _ => Set()
  }
  def varsJava(): java.util.Set[Prop] = variables().asJava

  def getBody: Formula = this match {
    case p: Not => p.body
    case p: Future => p.body
    case p: Global => p.body
    case p: Next => p.body
  }

  def negation: Formula = this match {
    case p: TRUE => new FALSE
    case p: FALSE => new TRUE
    case p: Not => p.body
    case _ => new Not(this)
  }

  def negationNormalForm: Formula = this match {
    case p: Not =>
      val f = p.body
      f match {
        case a: Prop => p
        case a: TRUE => new FALSE
        case a: FALSE => new TRUE
        case a: Not => a.body.negationNormalForm
        case a: And => new Or(new Not(a.left).negationNormalForm, new Not(a.right).negationNormalForm)
        case a: Or => new And(new Not(a.left).negationNormalForm, new Not(a.right).negationNormalForm)
        case a: Until => new Release(new Not(a.left).negationNormalForm, new Not(a.right).negationNormalForm)
        case a: Release => new Until(new Not(a.left).negationNormalForm, new Not(a.right).negationNormalForm)
        case a: Next => new Next(new Not(a.body).negationNormalForm)
        case a: Impl => new Not(a.negationNormalForm).negationNormalForm
        case a: Future => new Not(a.negationNormalForm).negationNormalForm
        case a: Global => new Not(a.negationNormalForm).negationNormalForm
      }
    case p: Future => new Until(new TRUE, p.body.negationNormalForm)
    case p: Global => new Release(new FALSE, p.body.negationNormalForm)
    case p: Impl => new Or(new Not(p.left).negationNormalForm, p.right.negationNormalForm)
    case p: Prop => p
    case p: And => new And(p.left.negationNormalForm, p.right.negationNormalForm)
    case p: Or => new Or(p.left.negationNormalForm, p.right.negationNormalForm)
    case p: Until => new Until(p.left.negationNormalForm, p.right.negationNormalForm)
    case p: Release => new Release(p.left.negationNormalForm, p.right.negationNormalForm)
    case p: Next => new Next(p.body.negationNormalForm)
    case p: TRUE => p
    case p: FALSE => p
  }
}

class Prop(val name: String) extends Formula

class Not(val body: Formula) extends Formula

class And(val left: Formula, val right: Formula) extends Formula

class Or(val left: Formula, val right: Formula) extends Formula

class Impl(val left: Formula, val right: Formula) extends Formula

class Until(val left: Formula, val right: Formula) extends Formula

class Release(val left: Formula, val right: Formula) extends Formula

class Future(val body: Formula) extends Formula

class Global(val body: Formula) extends Formula

class Next(val body: Formula) extends Formula

class TRUE extends Formula

class FALSE extends Formula

object tmp {
  def negation(formula: Formula): Formula = formula match {
    case p: TRUE => new FALSE
    case p: FALSE => new TRUE
    case p: Not => p.body
    case _ => new Not(formula)
  }

  def allSubsets[T](set: Set[T]): List[Set[T]] = Nil

  def negationNormalForm(formula: Formula): Formula = formula match {
    case p: Not =>
      val f = p.body
      f match {
        case a: Prop => p
        case a: TRUE => new FALSE
        case a: FALSE => new TRUE
        case a: Not => negationNormalForm(a.body)
        case a: And => new Or(negationNormalForm(new Not(a.left)), negationNormalForm(new Not(a.right)))
        case a: Or => new And(negationNormalForm(new Not(a.left)), negationNormalForm(new Not(a.right)))
        case a: Until => new Release(negationNormalForm(new Not(a.left)), negationNormalForm(new Not(a.right)))
        case a: Release => new Until(negationNormalForm(new Not(a.left)), negationNormalForm(new Not(a.right)))
        case a: Next => new Next(negationNormalForm(new Not(a.body)))
        case a: Impl => negationNormalForm(new Not(negationNormalForm(a)))
        case a: Future => negationNormalForm(new Not(negationNormalForm(a)))
        case a: Global => negationNormalForm(new Not(negationNormalForm(a)))
      }
    case p: Future => new Until(new TRUE, negationNormalForm(p.body))
    case p: Global => new Release(new FALSE, negationNormalForm(p.body))
    case p: Impl => new Or(negationNormalForm(new Not(p.left)), negationNormalForm(p.right))
    case p: Prop => p
    case p: And => new And(negationNormalForm(p.left), negationNormalForm(p.right))
    case p: Or => new Or(negationNormalForm(p.left), negationNormalForm(p.right))
    case p: Until => new Until(negationNormalForm(p.left), negationNormalForm(p.right))
    case p: Release => new Release(negationNormalForm(p.left), negationNormalForm(p.right))
    case p: Next => new Next(negationNormalForm(p.body))
    case p: TRUE => p
    case p: FALSE => p
  }
}
