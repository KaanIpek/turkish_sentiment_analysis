import py4j.GatewayServer;
import zemberek.core.logging.Log;
//import zemberek.morphology;
import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.AnalysisFormatters;
import zemberek.morphology.analysis.SingleAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.normalization.TurkishSpellChecker;
import zemberek.tokenization.Token;
import zemberek.tokenization.TurkishTokenizer;
import zemberek.langid.model.BaseCharNgramModel;
import zemberek.langid.model.CharNgramCountModel;
import zemberek.langid.model.CharNgramLanguageModel;
import zemberek.langid.model.CompressedCharNgramModel;
import zemberek.langid.model.MapBasedCharNgramLanguageModel;
import zemberek.langid.LanguageIdentifier;
import java.io.IOException;
import java.util.*;

import zemberek.core.logging.Log;
import zemberek.langid.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;
//import zemberek.core.io.SimpleTextReader;
//import zemberek.core.logging.Log;
//import zemberek.core.math.LogMath;



public class ServerCreator
{
    static TurkishTokenizer tokenizer = TurkishTokenizer.DEFAULT;
    static TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
    LanguageIdentifier lid;
    {
        try
        {
            lid = LanguageIdentifier.fromInternalModelGroup("tr_group");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    ServerCreator()
    {

    }

    public String morp_analys(String Article)
    {
        StringBuilder full_result = new StringBuilder();
        Log.info("Word = " + Article);
        WordAnalysis results = morphology.analyze(Article);
        for (SingleAnalysis result : results)
        {
            Log.info("Lexical and Surface : " + result.formatLong());
            Log.info("Only Lexical        : " + result.formatLexical());
            Log.info("Oflazer style       : " +
                    AnalysisFormatters.OFLAZER_STYLE.format(result));
            full_result.append(result.formatLong());
            Log.info();
        }
        return full_result.toString().trim();
    }

    public String tokenize(String Article)
    {
        StringBuilder result = new StringBuilder();
        System.out.println("Input = " + Article);
        Iterator<Token> tokenIterator = tokenizer.getTokenIterator(Article);
        while (tokenIterator.hasNext())
        {
            Token token = tokenIterator.next();
            result.append(token.content).append("\n");
        }

        return result.toString().trim();

    }

    public String stemming_and_lemmatization(String Article)
    {
        StringBuilder sb_result = new StringBuilder();

        WordAnalysis results = morphology.analyze(Article);

        for (SingleAnalysis result : results)
        {
            //Log.info("\tFormatLong = " + result.formatLong());
            //sb_result.append("FormatLong = ").append(result.formatLong()).append("\n");
            Log.info("\tWord = " + Article);
            Log.info("\tStems = " + result.getStem());
            sb_result.append(result.getStem()).append(" ");
            break;
            //Log.info("\tLemmas = " + result.getLemmas());
            //sb_result.append("Lemmas = ").append(result.getLemmas()).append("\n");

        }
        return sb_result.toString().trim();

    }

    public String normalize(String Article) throws IOException
    {
        StringBuilder output = new StringBuilder();
        TurkishSpellChecker spellChecker = new TurkishSpellChecker(morphology);

        for (Token token : tokenizer.tokenize(Article))
        {
            String text = token.getText();
            if (analyzeToken(token) && !spellChecker.check(text))
            {
                List<String> strings = spellChecker.suggestForWord(token.getText());
                if (!strings.isEmpty())
                {
                    String suggestion = strings.get(0);
                    output.append(suggestion).append(" ");
                } else {
                    output.append(text).append(" ");
                }
            } else {
                output.append(text).append(" ");
            }
        }
        return output.toString().trim();
    }

    static boolean analyzeToken(Token token)
    {
        return token.getType() != Token.Type.NewLine
                && token.getType() != Token.Type.SpaceTab
                && token.getType() != Token.Type.UnknownWord
                && token.getType() != Token.Type.RomanNumeral
                && token.getType() != Token.Type.Unknown;
    }

    public static void main(String[] args) {
        GatewayServer gatewayServer = new GatewayServer(new ServerCreator());
        gatewayServer.start();
        System.out.println("Gateway Server Started");
    }
    public String langid(String word)
    {
        return (lid.identify(word,50));
    }
}
