package com.mcmiddleearth.minigames.quiz.question;

import com.mcmiddleearth.minigames.util.Style;

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
    public String[] getDetails() {
        return new String[]{Style.HIGHLIGHT+"[Type]"+Style.HIGHLIGHT_STRESSED+" SINGLE choice question",
                Style.HIGHLIGHT+"[Question] "+Style.HIGHLIGHT_STRESSED+getQuestion(),
                (correctAnswers[0]?Style.STRESSED:Style.HIGHLIGHT)+"[A] "+answers[0],
                (correctAnswers[1]?Style.STRESSED:Style.HIGHLIGHT)+"[B] "+answers[1],
                (correctAnswers[2]?Style.STRESSED:Style.HIGHLIGHT)+"[C] "+answers[2],
                (correctAnswers[3]?Style.STRESSED:Style.HIGHLIGHT)+"[D] "+answers[3]};
    }
}