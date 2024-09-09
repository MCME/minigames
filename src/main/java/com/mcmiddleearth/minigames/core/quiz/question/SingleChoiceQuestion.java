package com.mcmiddleearth.minigames.core.quiz.question;

import com.mcmiddleearth.base.core.message.Message;
import com.mcmiddleearth.minigames.core.MiniGames;
import com.mcmiddleearth.minigames.core.util.Style;

/**
 *
 * @author Eriol_Eandur,Jubo
 */
public class SingleChoiceQuestion extends ChoiceQuestion{

    public SingleChoiceQuestion(String question, String[] answers, String correctAnswer,
                                String categories) {
        super(question, QuestionType.SINGLE, answers, correctAnswer, categories);
    }

    public boolean isCorrectAnswer(char answer) {
        return isCorrectAnswer(new Character[]{answer}); //invalid json in question list messages
    }

    @Override
    public Message[] getDetails() {
        return new Message[]{MiniGames.message("[Type]",Style.HIGHLIGHT).add(" SINGLE choice question",Style.HIGHLIGHT_STRESSED),
                MiniGames.message("[Question] ",Style.HIGHLIGHT).add(getQuestion(),Style.HIGHLIGHT_STRESSED),
                MiniGames.message("[A] "+answers[0],(correctAnswers[0]?Style.STRESSED:Style.HIGHLIGHT)),
                MiniGames.message("[B] "+answers[1],(correctAnswers[1]?Style.STRESSED:Style.HIGHLIGHT)),
                MiniGames.message("[C] "+answers[2],(correctAnswers[2]?Style.STRESSED:Style.HIGHLIGHT)),
                MiniGames.message("[D] "+answers[3],(correctAnswers[3]?Style.STRESSED:Style.HIGHLIGHT))};
    }
}