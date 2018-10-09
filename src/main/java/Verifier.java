import automate.DiagramAutomate;
import automate.Variable;
import buchi.BuchiAutomate;
import buchi.BuchiAutomateCycleFinder;
import buchi.BuchiState;
import diagram.Diagram;
import diagram.Parser;
import kripke.ModelKripke;
import kripke.StateKripke;
import ltl.Formula;
import ltl.ParserRunner;
import ltl.Prop;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Verifier {
    public static void main(String[] args) throws IOException {
        Options options = new Options();

        Option xmlOption = new Option("x", "xml", true, "xml file path");
        xmlOption.setRequired(true);
        options.addOption(xmlOption);

        Option formulaOption = new Option("f", "formula", true, "ltl-formula");
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
        final Diagram d;
        try {
            Parser diagramParser = new Parser();
            d = diagramParser.parse(Paths.get(xmlFilePath));
        } catch (Exception e) {
            System.out.println("Unable to parse diagram file:" + xmlFilePath);
            return;
        }
        if (ltlFilePath != null) {
            System.out.println(Files.lines(Paths.get(ltlFilePath)).map(line -> handleFormulaLine(d, line)).collect(Collectors.joining("\n\n")));
        } else {
            System.out.println(handleFormulaLine(d, ltlFormula));
        }
    }

    private static String handleFormulaLine(Diagram d, String line) {
        try {
            final Formula formula = ParserRunner.parseFormula(line);
            final Optional<List<BuchiState<Pair<BuchiState<StateKripke>, BuchiState<Integer>>>>> result = runVerification(d, formula);
            final Result printableResult = result.map(Result::fail).orElseGet(Result::correct);
            return String.format("Formula: %s\n%s", line, printableResult.print());
        } catch (Throwable e) {
            return String.format("Formula: %s\n%s", line, "Unable to verify");
        }
    }

    private static Optional<List<BuchiState<Pair<BuchiState<StateKripke>, BuchiState<Integer>>>>>
    runVerification(Diagram diagram, Formula formula) {
        DiagramAutomate diagramAutomate = DiagramAutomate.fromDiagram(diagram);
        ModelKripke modelKripke = ModelKripke.fromDiagramAutomate(diagramAutomate);
        BuchiAutomate<StateKripke, Set<Variable>> buchi = BuchiAutomate.of(modelKripke);
        BuchiAutomate<StateKripke, Set<Prop>> buchiProp = BuchiAutomate.variableAutomatonToPropAutomaton(buchi);
        return runVerificationOnAutomaton(buchiProp, formula);
    }

    private static Optional<List<BuchiState<Pair<BuchiState<StateKripke>, BuchiState<Integer>>>>>
    runVerificationOnAutomaton(BuchiAutomate<StateKripke, Set<Prop>> buchiProp, Formula formula) {
        Formula negation = formula.negation();
        Formula nnf = negation.negationNormalForm();

        BuchiAutomate<Integer, Set<Prop>> generalizedBuchiForLtl = BuchiAutomate.of(nnf);
        BuchiAutomate<Integer, Set<Prop>> buchiForLtl = BuchiAutomate.degeneralize(generalizedBuchiForLtl);

        Set<Prop> props = buchiProp.getTransitions().entrySet().stream().flatMap(
                x -> x.getValue().entries().stream().flatMap(y -> y.getKey().stream())
        ).collect(Collectors.toCollection(LinkedHashSet::new));
        props.removeAll(nnf.varsJava());
        BuchiAutomate<Pair<BuchiState<StateKripke>, BuchiState<Integer>>, Set<Prop>> cross = BuchiAutomate.cross(buchiProp, buchiForLtl, props);
        List<BuchiState<Pair<BuchiState<StateKripke>, BuchiState<Integer>>>> cycle =
                new BuchiAutomateCycleFinder<Pair<BuchiState<StateKripke>, BuchiState<Integer>>>().findReachableCycle(cross);

        return Optional.ofNullable(cycle);
    }
}
