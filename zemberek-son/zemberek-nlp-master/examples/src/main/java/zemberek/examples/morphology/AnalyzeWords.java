package zemberek.examples.morphology;

import zemberek.core.logging.Log;
import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.AnalysisFormatters;
import zemberek.morphology.analysis.SingleAnalysis;
import zemberek.morphology.analysis.WordAnalysis;

public class AnalyzeWords {

  public static void main(String[] word) {
    TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
    String words = "Hi";
    Log.info("Word = " + words);
    WordAnalysis results = morphology.analyze(words);
    for (SingleAnalysis result : results) {
      Log.info("Lexical and Surface : " + result.formatLong());
      Log.info("Only Lexical        : " + result.formatLexical());
      Log.info("Oflazer style       : " +
          AnalysisFormatters.OFLAZER_STYLE.format(result));
      Log.info();
    }
  }
}
