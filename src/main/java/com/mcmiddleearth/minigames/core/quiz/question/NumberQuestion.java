package com.mcmiddleearth.minigames.core.quiz.question;

import com.mcmiddleearth.base.core.message.Message;
import com.mcmiddleearth.minigames.core.MiniGames;
import com.mcmiddleearth.minigames.core.util.Style;

/**
 *
 * @author Eriol_Eandur,Jubo
 */
public class NumberQuestion extends AbstractQuestion{

    private int answer;
    private int precision;

    public NumberQuestion(String question, int answer, int precision, String categories) {
        super(question, QuestionType.NUMBER, categories);
        this.answer = answer;
        this.precision = precision;
    }

    public boolean isCorrectAnswer(int answer) {
        return this.answer-precision<=answer && this.answer+precision>=answer;
    }

    @Override
    public boolean isCorrectAnswer(String answer) {
        try {
            return isCorrectAnswer(Integer.parseInt(answer));
        }
        catch(NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getCorrectAnswer() {
        return answer+"";
    }

    @Override
    public Message[] getDetails() {
        return new Message[]{MiniGames.message("[Type]",Style.HIGHLIGHT).add(" NUMBER answer question",Style.HIGHLIGHT_STRESSED),
                MiniGames.message("[Question] ",Style.HIGHLIGHT).add(getQuestion(),Style.HIGHLIGHT_STRESSED),
                MiniGames.message("[Answer] "+answer, Style.STRESSED),
                MiniGames.message("[Precision] "+precision, Style.STRESSED)};
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }
}