package drasli_tana.dice_parser;

import java.io.FileReader;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import drasli_tana.dice_parser.commands.CommandsHandler;
import drasli_tana.dice_parser.commands.MessageHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;


public class Main {
    public static JDA jda;
    public static String prefix;
    // Changement du préfixe du bot
    public static JSONObject json;
    // Création d'un objet JSON pour gérer les statistiques par serveur
    
    public static CommandsHandler commandes;
    
    private static String token;
    
    public static void main(String[] args) throws LoginException {
        try {
            Object fichier = new JSONParser().parse(new FileReader("settings.json"));
            json = (JSONObject) fichier;
            Main.prefix = String.valueOf(json.getOrDefault("prefix", ":"));
            token = String.valueOf(json.getOrDefault("token", ":"));
            
        } catch (ParseException | IOException e) {
            // Gestion d'un fichier de statistiques inexistant, des erreurs propres
            // au JSON et des permissions trop basses
            json = new JSONObject();
        }
        
        try {
            Object fichier = new JSONParser().parse(new FileReader("statistiques.json"));
            json = (JSONObject) fichier;
            
        } catch (ParseException | IOException e) {
            // Gestion d'un fichier de statistiques inexistant, des erreurs propres
            // au JSON et des permissions trop basses
            json = new JSONObject();
        }
        commandes = new CommandsHandler();
        
        
        JDABuilder builder = JDABuilder.createLight(Main.token);
        // Instancie le bot (pas encore connecté) avec le token
        
        builder.setActivity(Activity.watching("une partie de JDR"));
        builder.setStatus(OnlineStatus.IDLE);
        // Purement esthétique: change le statut du bot
        
        builder.addEventListeners(new MessageHandler());
        // On ajoute l'écouteru d'évènements
        
        jda = builder.build();
        // Connecte le bot à discord
        
        System.out.println("Logged in.");
    }
}
