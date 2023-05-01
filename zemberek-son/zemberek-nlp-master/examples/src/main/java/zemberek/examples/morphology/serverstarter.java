package zemberek.examples.morphology;
import py4j.GatewayServer;

class Main
{
    public static void main(String[] word)
    {
        //LanguageIdentifier lang = new ;
        String words = "Hi";
        GatewayServer gatewayServer = new GatewayServer(new AnalyzeWords());
        gatewayServer.start();
        System.out.println("Gateway Server Started");
    }
}