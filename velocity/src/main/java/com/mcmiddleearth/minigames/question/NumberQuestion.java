package com.mcmiddleearth.minigames.question;

import com.mcmiddleearth.minigames.Style;

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
    public String[] getDetails() {
        return new String[]{Style.HIGHLIGHT+"[Type]"+Style.HIGHLIGHT_STRESSED+" NUMBER answer question",
                Style.HIGHLIGHT+"[Question] "+Style.HIGHLIGHT_STRESSED+getQuestion(),
                Style.STRESSED+"[Answer] "+answer,
                Style.STRESSED+"[Precision] "+precision};
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