package com.mcmiddleearth.minigames.quiz.question;

import com.mcmiddleearth.minigames.util.Style;

/**
 *
 * @author Eriol_Eandur,Jubo
 */
public class FreeQuestion extends AbstractQuestion{

    private String answer;

    private static char[][] charReplacement =  new char[][]{{'a','\u00E0','\u00E1','\u00E2','\u00E3','\u00E4','\u00E5'},
            {'e','\u00E8','\u00E9','\u00EA','\u00EB','\u0113','\u011B'},
            {'i','\u00EC','\u00ED','\u00EE','\u00EF','\u0129','\u012B'},
            {'o','\u00F2','\u00F3','\u00F4','\u00F5','\u00F6','\u014D'},
            {'u','\u00F9','\u00FA','\u00FB','\u00FC','\u0169','\u016B'},
            {'n','\u00F1'}};

    public FreeQuestion(String question, String answer, String categories){
        super(question, QuestionType.FREE, categories);
        this.answer = answer;
    }

    @Override
    public boolean isCorrectAnswer(String str) {
        str = replaceSpecialChar(str);
        return str.equals(replaceSpecialChar(answer));
        //return answer.equalsIgnoreCase(this.answer);
    }

    private String replaceSpecialChar(String str) {
        str = str.toLowerCase();
        for (char[] letterReplace : charReplacement) {
            for (int j = 1; j < letterReplace.length; j++) {
                str = str.replace(letterReplace[j], letterReplace[0]);
            }
        }
        return str;
    }

    @Override
    public String getCorrectAnswer() {
        return answer;
    }

    @Override
    public String[] getDetails() {
        return new String[]{Style.HIGHLIGHT+"[Type]"+ Style.HIGHLIGHT_STRESSED+" FREE answer question",
                Style.HIGHLIGHT+"[Question] "+Style.HIGHLIGHT_STRESSED+getQuestion(),
                Style.STRESSED+"[Answer] "+answer};
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
