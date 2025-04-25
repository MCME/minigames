package com.mcmiddleearth.minigames.velocity.question;

import java.util.StringTokenizer;
import org.json.simple.parser.ParseException;

public class QuestionParser {
    public static AbstractQuestion parseQuestionFromString(String questionData) throws ParseException, ArrayIndexOutOfBoundsException {
        StringTokenizer tokenizer = new StringTokenizer(questionData,";");
        String categories = tokenizer.nextToken();
        QuestionType type = QuestionType.values()[Integer.parseInt(tokenizer.nextToken())-1];
        String question = tokenizer.nextToken();
        String[] choices = new String[]{"","","",""};
        if(type.equals(QuestionType.MULTI) || type.equals(QuestionType.SINGLE)) {
            for(int i = 0; i<4; i++) {
                choices[i] = tokenizer.nextToken();
            }
        }
        String answer = tokenizer.nextToken();
        return switch(type){
            case FREE -> new FreeQuestion(question,answer,categories);
            case NUMBER -> {
                int precision = Integer.parseInt(tokenizer.nextToken());
                int answerInt = Integer.parseInt(answer);
                yield new NumberQuestion(question,answerInt, precision,categories);
            }
            case MULTI -> new ChoiceQuestion(question,choices,answer,categories);
            case SINGLE -> new SingleChoiceQuestion(question,choices,answer,categories);
            default -> throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN);
        };
    }
}
