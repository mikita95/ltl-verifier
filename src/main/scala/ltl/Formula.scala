package ltl

import scala.collection.mutable

trait Formula {

  def subformulas: Set[Formula] = Set(this) ++ {this match {
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

  def variables() :Set[Prop] = Set()

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

    def negation(formula: Formula) :Formula = formula

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
