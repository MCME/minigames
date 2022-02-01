package com.mcmiddleearth.minigames.conversation.geoguessr;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.GeoGuessrGame;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class GeoGuessrConversation implements ConversationAbandonedListener {

    private final ConversationFactory factory;

    public GeoGuessrConversation(Plugin plugin, int answerTime){
        Map<Object,Object> initData = new HashMap<>();
        initData.put("input", false);
        factory = new ConversationFactory(plugin)
                .withModality(false)
                .withPrefix(new GeoGuessrRoundPrefix())
                .withFirstPrompt(new GeoGuessrRoundPrompt())
                .withTimeout(answerTime)
                .withInitialSessionData(initData)
                .addConversationAbandonedListener(this);
    }

    public Conversation start(Player player, GeoGuessrGame game,String correctAnswer){
        Conversation conversation = factory.buildConversation(player);
        ConversationContext context = conversation.getContext();
        context.setSessionData("game", game);
        context.setSessionData("player", player);
        context.setSessionData("correctAnswer", correctAnswer);
        conversation.begin();
        return conversation;
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
        ConversationContext cc = abandonedEvent.getContext();
        Player player = (Player) cc.getSessionData("player");
        String correctAnswer = (String) cc.getSessionData("correctAnswer");
        String answer = (String) cc.getSessionData("answer");
        if (!abandonedEvent.gracefulExit()) {
            if (abandonedEvent.getCanceller() instanceof ManuallyAbandonedConversationCanceller) {
                sendRoundCancelledMessage(player);
            } else {
                sendAbordMessage(player, correctAnswer);
            }
        } else {
            if (correctAnswer.equalsIgnoreCase(answer)) {
                if(!(((GeoGuessrGame)cc.getSessionData("game")).incrementFirstScore(player))){
                    ((GeoGuessrGame)cc.getSessionData("game")).incrementScore(player);
                }
                sendSuccessMessage(player);
            } else {
                sendFailMessage(player, correctAnswer);
            }

        }
        GeoGuessrGame game = (GeoGuessrGame) cc.getSessionData("game");
        game.removePlayerFromRound(player);
        if (!game.isPlayerInRound()) {
            game.stopRound();
        }
    }




    private void sendAbordMessage(Player player, String answer) {
        PluginData.getMessageUtil().sendInfoMessage(player, "Time to answer expired. Correct answer: "
                +answer);
    }

    private void sendSuccessMessage(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "You answered this round correctly.");
    }

    private void sendFailMessage(Player player, String answer) {
        PluginData.getMessageUtil().sendInfoMessage(player, "You failed to answer this Round correctly. Correct answer: "+answer);
    }

    private void sendRoundCancelledMessage(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "Round cancelled.");
    }

}
