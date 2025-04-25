package com.mcmiddleearth.minigames.velocity.runners.quiz.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.common.Channels;
import com.mcmiddleearth.minigames.velocity.runners.GameRunner;
import com.mcmiddleearth.minigames.velocity.runners.quiz.QuizRunner;
import com.mcmiddleearth.minigames.velocity.runners.GameListener;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import org.jetbrains.annotations.NotNull;

public class AnswerListener extends GameListener {
    public AnswerListener(@NotNull GameRunner runner) {
        super(runner);
    }

    @Subscribe
    public void onPluginMessageFromBackend(PluginMessageEvent event){
        if (!Channels.QUIZ.equals(event.getIdentifier())) {
            return;
        }

        // mark PluginMessage as handled, indicating that the contents
        // should not be forwarding to their original destination.
        event.setResult(PluginMessageEvent.ForwardResult.handled());

        // Alternatively:

        // mark PluginMessage as forwarded, indicating that the contents
        // should be passed through, as if Velocity is not present.
        //
        // this should be used with extreme caution,
        // as any client can freely send whatever it wants, pretending to be the proxy
        //event.setResult(PluginMessageEvent.ForwardResult.forward());

        // only attempt parsing the data if the source is a backend server
        if (!(event.getSource() instanceof ServerConnection backend)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String command = in.readUTF();
        if(command.equals("answered"))
            ((QuizRunner)runner).Answer(backend.getPlayer(), in.readUTF());
    }
}
