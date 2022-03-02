package drasli_tana.dice_parser.commands;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import org.json.simple.JSONObject;

import drasli_tana.dice_parser.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandsHandler{
    public void command(MessageReceivedEvent event) {
        String contenu = event.getMessage().getContentRaw();
        Message message = event.getMessage();
        if (!event.getAuthor().isBot() &&
                contenu.startsWith(Main.prefix)) {
            // Prevents a bot from triggering command
            if (contenu.length() < 6) {
                contenu = contenu + " ".repeat(6 - contenu.length());
            }
            
            if ("help".equalsIgnoreCase(contenu.substring(1, 5))) {
                help(message);
                
            } else if ("ping".equalsIgnoreCase(contenu.substring(1, 5))) {
                ping(message);
            
            } else if ("shut".equalsIgnoreCase(contenu.substring(1, 5))) {
                disconnect(message);
                
            } else if ("add".equalsIgnoreCase(contenu.substring(1, 4))) {
                add(message);
                
            } else if ("stats".equalsIgnoreCase(contenu.substring(1, 6))) {
                stats(message);
            }
        }
    }
    
    private void disconnect(Message message) {
        if (
                message.getMember().hasPermission(Permission.MANAGE_SERVER)
                || message.getAuthor().getIdLong() == 408628468939489290l
                ) {
            
            try {
                FileWriter file = new FileWriter("statistiques.json");
                file.write(Main.json.toJSONString());
                file.close();
                System.out.println("JSON sauvegardé.");
                
            } catch (IOException e) {
                System.out.println("Impossible de sauvegarder.");
            
            } finally {
                Main.jda.shutdown();
            }
        
        } else {
            message.reply("Permissions insuffisantes").queue();
        }
    }
    
    private void help(Message message) {
        message.reply("```markdown\n"
                + "Ce n'est pas le message d'aide que vous recherchez\n"
                + "Oui, j'envoie tout le debug dans le channel, la flemme\n"
                + "de lire la console\n"
                + "```").queue();
    }
    
    private void ping(Message message) {
        message.reply("[OOM-9] Roger, roger.").queue();
    }
    
    private void add(Message message) {
        message.reply("Pas encore implémenté").queue();
    }
    
    private void stats(Message message) {
        String[] args = message.getContentRaw().strip().split(" ");
        JSONObject dicoGuild = (JSONObject) Main.json.get(message.getGuild());
        int[] clefs;
        
        if (args.length >= 2) {
            clefs = new int[args.length - 1];
            
            for (int i = 1; i < args.length; i++) {
                if (((JSONObject) Main.json.get(message.getGuild())).containsKey(
                        args[i])) {
                    
                        clefs[i - 1] = Integer.valueOf(args[i]);

                } 
            } 
        
        } else {
            Object[] listeClefs = dicoGuild.keySet().toArray();
            clefs = new int[listeClefs.length];
            
            for (int i = 0; i < listeClefs.length; i++) {
                clefs[i] = (int) listeClefs[i];
            }
        }
        
        Arrays.sort(clefs);
        EmbedBuilder retour = new EmbedBuilder();
        StringBuffer sortie = new StringBuffer();
        int total = 0;
        
        retour.setTitle("Statistiques");
        for (int clef : clefs) {
            if (clef != 0) {
                JSONObject dico = (JSONObject) dicoGuild.get(clef);
                int[] faces = new int[dico.keySet().toArray().length];
                Object[] resultat =  dico.keySet().toArray();
                for (int i = 0; i < resultat.length; i++) {
                    faces[i] = Integer.valueOf((String) resultat[i]);
                }
                Arrays.sort(faces);
                
                sortie.append("```fix\n");
                for (Object quantite: dico.values()) {
                    total += (int) quantite;
                }
                
                for (int face: faces) {                    
                    sortie.append(
                            "# " + String.format("%3d", face) +
                            ": " +
                            String.format("%7s", dico.get(String.valueOf(face))) +
                            " (" + String.format("%5.2f", 
                                    Float.parseFloat(
                                            dico.get(String.valueOf(face)).toString())
                                    / total * 100) + "%)\n");
                        }
                
                sortie.append("```");
                sortie.append("Dés lancés: " +  String.valueOf(total));
                
                retour.addField(
                        "Dé " + String.valueOf(clef),
                        sortie.toString(),
                        false);
                sortie.delete(0, sortie.length());
            }
        }
        message.replyEmbeds(retour.build()).queue();
        retour.clear();
    }
}


