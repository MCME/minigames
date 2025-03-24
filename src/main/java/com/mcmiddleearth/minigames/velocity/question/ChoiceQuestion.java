package com.mcmiddleearth.minigames.velocity.question;

import com.mcmiddleearth.minigames.util.Style;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Eriol_Eandur,Jubo
 */
public class ChoiceQuestion extends AbstractQuestion {

    protected String[] answers;
    protected final boolean[] correctAnswers = new boolean[answerCount];
    public final static int answerCount = 4;

    public ChoiceQuestion(String question, String[] answers, String correctAnswers, String categories) {
        this(question, QuestionType.MULTI, answers, correctAnswers, categories);
    }

    protected ChoiceQuestion(String question, QuestionType type, String[] answers, String correctAnswers,
                             String categories) {
        super(question, type, categories);
        this.answers = answers;
        setCorrectAnswers(correctAnswers);
    }

    public String[] getInRandomOrder() {
        String[] result = new String[answerCount];
        for(int i = 0; i< answerCount; i++) {
            int rand = (int) Math.round(Math.floor(answerCount*Math.random()));
            while(result[rand]!=null) {
                rand++;
                if(rand==answerCount) {
                    rand=0;
                }
            }
            result[rand]=getAnswerCharacter(i)+answers[i];
        }
        return result;
    }

    public String[] getInProperOrder() {
        String[] result = new String[answerCount];
        for(int i=0; i<answerCount;i++) {
            result[i] = getAnswerCharacter(i)+answers[i];
        }
        return result;
    }

    public void setCorrectAnswers(String correctLetters) {
        correctLetters = correctLetters.trim();
        Arrays.fill(correctAnswers, false);
        for (char i : correctLetters.toCharArray()) {
            if(isLetterValid(i))
                correctAnswers[getAnswerIndex(i)] = true;
        }
    }

    @Override
    public String getCorrectAnswer() {
        StringBuilder result = new StringBuilder();
        for(int i = 0;i<answerCount;i++) {
            if(correctAnswers[i])
                result.append(getAnswerCharacter(i));
        }
        return result.toString();
    }

    public static int getAnswerIndex(char answer) {
        return switch (answer) {
            case 'A', 'a' -> 0;
            case 'B', 'b' -> 1;
            case 'C', 'c' -> 2;
            case 'D', 'd' -> 3;
            default -> -1;
        };
    }

    public static boolean isLetterValid(char answer) {
        return getAnswerIndex(answer)>=0;
    }

    public static boolean isAnswerValid(String answer) {
        String answerCopy = answer;
        answerCopy = answerCopy.trim();
        while(!answerCopy.isEmpty()) {
            if(!isLetterValid(answerCopy.charAt(0))) {
                return false;
            }
            answerCopy = answerCopy.substring(1).trim();
        }
        return true;
    }

    public static char getAnswerCharacter(int index) {
        return switch (index) {
            case 0 -> 'A';
            case 1 -> 'B';
            case 2 -> 'C';
            case 3 -> 'D';
            default -> 'x';
        };
    }

    private boolean isIn(Character letter, Character[] answers) {
        int letterIndex = getAnswerIndex(letter);
        for(char search: answers) {
            if(letterIndex == getAnswerIndex(search)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCorrectAnswer(Character[] answers) {
        for(int i = 0;i<answerCount;i++) {
            char answerLetter = getAnswerCharacter(i);
            if(correctAnswers[i]!=isIn(answerLetter,answers)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isCorrectAnswer(String answer) {
        return isCorrectAnswer(parseAnswer(answer));
    }

    public static Character[] parseAnswer(String answer) {
        answer = answer.trim();
        List<Character> answerList = new ArrayList<>();
        while(!answer.isEmpty()) {
            answerList.add(answer.charAt(0));
            answer = answer.substring(1).trim();
        }
        return answerList.toArray(new Character[0]);
    }

    @Override
    public String[] getDetails() {
        return new String[]{Style.HIGHLIGHT+"[Type]"+Style.HIGHLIGHT_STRESSED+" MULTI choice question",
                Style.HIGHLIGHT+"[Question] "+Style.HIGHLIGHT_STRESSED+getQuestion(),
                (correctAnswers[0]?Style.STRESSED:Style.HIGHLIGHT)+"[A] "+answers[0],
                (correctAnswers[1]?Style.STRESSED:Style.HIGHLIGHT)+"[B] "+answers[1],
                (correctAnswers[2]?Style.STRESSED:Style.HIGHLIGHT)+"[C] "+answers[2],
                (correctAnswers[3]?Style.STRESSED:Style.HIGHLIGHT)+"[D] "+answers[3]};
    }

    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }
}