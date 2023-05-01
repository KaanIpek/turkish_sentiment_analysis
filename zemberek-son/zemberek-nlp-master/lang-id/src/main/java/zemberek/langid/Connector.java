package zemberek.langid;

import java.util.*;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Pattern;
import zemberek.core.io.SimpleTextReader;
import zemberek.core.logging.Log;
import zemberek.core.math.LogMath;
import zemberek.langid.model.BaseCharNgramModel;
import zemberek.langid.model.CharNgramCountModel;
import zemberek.langid.model.CharNgramLanguageModel;
import zemberek.langid.model.CompressedCharNgramModel;
import zemberek.langid.model.MapBasedCharNgramLanguageModel;
//import py4j.GatewayServer;

class Connector
{
    Connector(String[] args) throws IOException {
        LanguageIdentifier lid = LanguageIdentifier.fromInternalModelGroup("tr_group");
        lid.identify("için", 50);
    }
    public static void main(String[] args)
    {

        //Map<String, CharNgramLanguageModel> newMap = new HashMap<String, CharNgramLanguageModel>();
        //newMap.put("A", new CharNgramLanguageModel());
        //GatewayServer gatewayServer = new GatewayServer(new LanguageIdentifier(newMap));
        //gatewayServer.start();
        //System.out.println("Gateway Server Started");
    }
}
