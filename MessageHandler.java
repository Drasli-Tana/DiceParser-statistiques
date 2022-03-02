package drasli_tana.dice_parser.commands;

import org.json.simple.JSONObject;

import drasli_tana.dice_parser.Main;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageHandler extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        
        String content;
        if (message.getContentRaw().startsWith("```Markdown\n")) {
            // On fait le tri entre les messages de sortie des dés et une
            // entrée utilisateur normale
            content = message.getContentRaw().substring(
                    12, (message.getContentRaw().length() - 5));
        
        } else {
            content = message.getContentRaw(); 
        
        }
        if (!message.getAuthor().isBot()) {
            // Teste si l'auteur du message n'est pas un bot, pour
            //pouvoir lancer les commandes
            Main.commandes.command(event);
        
        } else if (event.getAuthor() != Main.jda.getSelfUser()) {
            // Teste si le message ne vient pas de lui-même ou d'un utilisateur
            // quelconque (revient à tester si le message provient d'un autre bot)
            
            if (content.contains("Warning")) {
                // On vérifie si le message ne signifie pas une erreur
                System.out.println("Une erreur est survenue dans la commande");
            
            } else if (
                    content.contains(";") ||
                    content.contains("$") ||
                    content.contains("&") ||
                    content.split("d").length > 2 ||
                    !content.contains("\n")) {
                message.reply("Résultat trop compliqué ou non pris en charge, "
                        + "ajoutez le manuellement avec la commande `add`").queue();
                System.out.println(content);
                System.out.println("Commande Bizarre");
                
            } else {
                String[] resultat = content.split(
                        "\\n")[1].substring(9).split(" ");
                    // On récupère le détail des dés
                
                String[] detail = new String[resultat.length - 1];
                for (int i = 1; i < resultat.length; i++) {
                    detail[i - 1] = resultat[i].replaceAll(
                            "\\(", "").replaceAll("\\)", "");
                }
                
                String[] splitted = resultat[0].split("d");
                /*
                 * Inutilisé actuellement, uniquement lors de lancers composés
                int quantite;
                try {
                    quantite = Integer.valueOf(splitted[0]);
                    // On récupère la quantité de dés lancés
                
                } catch (IllegalArgumentException e) {
                    System.out.println("Pas de quantité spécifiée");
                    quantite = 1;
                }
                */
                int faces;
                try {
                    faces = Integer.valueOf(
                            splitted[1].split(" ")[0].split("\\+")[0]);
                
                }
                catch (NullPointerException e) {
                    System.out.println("Une erreur est survenue");
                    faces = 1;
                }
                // On récupère le nombre de faces de chaque dé
                //
                
                JSONObject dicoGuild = ((JSONObject) Main.json.getOrDefault(
                        message.getGuild(), new JSONObject()));
                // Dicoguild contient le dictionnaire pour le serveur discord concerné
                
                JSONObject stats = (JSONObject) dicoGuild.getOrDefault(
                        faces, new JSONObject());
                // Contient les statistiques pour un type de dés
                
                for (int i = 0; i < detail.length; i++) {
                    stats.put(detail[i], (int)stats.getOrDefault(detail[i], 0) + 1);
                    
                }
                dicoGuild.put(faces, stats);
                Main.json.put(message.getGuild(), dicoGuild);
            }
        }
    }
}
