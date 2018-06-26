package ltl

import buchi._

import scala.collection.mutable

object LtlToBuchi {

    private def curr1Rule(f: Formula): Set[Formula] = f match {
      case p: Until => Set(p.left)
      case p: Release => Set(p.right)
      case p: Or => Set(p.right)
      case _ => throw new Exception("undefined")
    }

    private def curr2Rule(f: Formula): Set[Formula] = f match {
      case p: Until => Set(p.right)
      case p: Release => Set(p.left, p.right)
      case p: Or => Set (p.left)
      case _ => throw new Exception("undefined")
    }

    private def next1Rule(f: Formula): Set[Formula] = f match {
      case p: Until => Set(p)
      case p: Release => Set(p)
      case p: Or => Set()
      case _ => throw new Exception("undefined")
    }

    private class LtlBuchiState(
       id: Int,
       var incoming: Set[LtlBuchiState],
       var now: Set[Formula],
       var next: Set[Formula]
     ) extends BuchiState[Int](id)

    def buchiAutomatonFromLtl(ltlFormula: Formula): BuchiAutomate[Int, Set[Prop]] = {

      val nodes: mutable.Set[LtlBuchiState] = mutable.Set()

      var nextTag = 1

      // TODO
      BuchiAutomate.of(null, null, null)
    }
  }
