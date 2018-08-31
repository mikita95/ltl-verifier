import automate.DiagramAutomate;
import automate.Variable;
import buchi.BuchiAutomate;
import diagram.Diagram;
import diagram.Parser;
import kripke.ModelKripke;
import kripke.StateKripke;
import ltl.Formula;
import ltl.ParserRunner;
import ltl.Prop;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

public class Verifier {
    public static void main(String[] args) throws IOException {
/*        Parser parser = new Parser();
        final Diagram d = parser.parse(Paths.get("/home/nikita/development/ltl/src/main/resources/diagram1.xml"));
        final DiagramAutomate diagramAutomate = DiagramAutomate.fromDiagram(d);
        System.out.println(d.toString());
        System.out.println(d.getName());
        System.out.println(diagramAutomate.toString());
        ParserRunner.parseFormula("q");*/
        Options options = new Options();

        Option xmlOption = new Option("x", "xml", true, "xml file path");
        xmlOption.setRequired(true);
        options.addOption(xmlOption);

        Option formulaOption = new Option("f", "formula", true, "ltl-formula");
        formulaOption.setRequired(true);
        options.addOption(formulaOption);

        Option ltlOption = new Option("l", "file", true, "ltl-file path");
        options.addOption(ltlOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("ltl-verifier", options);
            System.exit(1);
        }

        String xmlFilePath = cmd.getOptionValue("xml");
        String ltlFormula = cmd.getOptionValue("formula");
        String ltlFilePath = cmd.getOptionValue("file");

        Parser diagramParser = new Parser();
        final Diagram d = diagramParser.parse(Paths.get(xmlFilePath));

        if (ltlFilePath != null) {

        } else {
            final Formula formula = ParserRunner.parseFormula(ltlFormula);
        }
     /*   if (ltlFormulaString != null) {
            val formula = parseLtlFormula(ltlFormulaString!!)
            val result = runVerification(diagram, formula)
            showResult(formula, result)
        } else if (ltlFormulaeFile != null) {
            ltlFormulaeFile!!.readLines().map { parseLtlFormula(it) }.forEach { formula ->
                    val result = runVerification(diagram, formula)
                showResult(formula, result)
            }
        } else {
            print("Enter formula:")
            val formula = parseLtlFormula(readLine()!!)
            val result = runVerification(diagram, formula)
            showResult(formula, result)
        }*/
     }

    private static void runVerification(Diagram diagram, Formula formula) {
        DiagramAutomate diagramAutomate = DiagramAutomate.fromDiagram(diagram);
        ModelKripke modelKripke = ModelKripke.fromDiagramAutomate(diagramAutomate);
        BuchiAutomate<StateKripke, Set<Variable>> buchi = BuchiAutomate.of(modelKripke);
        BuchiAutomate<StateKripke, Set<Prop>> buchiProp = BuchiAutomate.variableAutomatonToPropAutomaton(buchi);
     /*   val automaton = automatonFromDiagram(diagram)
        val kripke = kripkeModelFromAutomaton(automaton)
        val buchi = buchiAutomatonFromKripkeModel(kripke)
        val buchiProp = variableAutomatonToPropAutomaton(buchi)

        return runVerificationOnAutomaton(buchiProp, formula)*/
    }

    private static void runVerificationOnAutomaton(BuchiAutomate<StateKripke, Set<Prop>> buchiProp, Formula formula) {
        Formula negation = formula.negation();
        Formula nnf = negation.negationNormalForm();

        BuchiAutomate<Integer, Set<Prop>> generalizedBuchiForLtl = BuchiAutomate.of(nnf);
        BuchiAutomate<Integer, Set<Prop>> buchiForLtl = BuchiAutomate.degeneralize(generalizedBuchiForLtl);

        Set<Prop> props = buchiProp.getTransitions().entrySet().stream().flatMap(
                x -> x.getValue().entries().stream().flatMap(y -> y.getKey().stream())
        ).collect(Collectors.toSet());
        props.removeAll(nnf.)
        BuchiAutomate.cross(buchiProp, buchiForLtl, props);


    }

   /* un runVerificationOnAutomaton(
            automaton: BuchiAutomaton<*, Set<Prop>>,
            formula: Formula
    ): VerificationResult {
        val negation = Not(formula)
        val nnf = negationNormalForm(negation)

        return verifyNnf(automaton, nnf)
    }

    fun verifyNnf(
            automaton: BuchiAutomaton<*, Set<Prop>>,
            nnf: Formula
    ): VerificationResult {

        val props = automaton.transitions.flatMap { it.value.flatMap { it.first } }.toSet()

        val generalizedBuchiForLtl = buchiAutomatonFromLtl(nnf)
        val buchiForLtl = degeneralize(generalizedBuchiForLtl)

        val crossAutomaton = crossIgnoringProps(automaton, buchiForLtl, props - nnf.variables())
        val cycle = findReachableCycle(crossAutomaton)

        return if (cycle != null) CounterExample(cycle) else Correct
    }*/
}
