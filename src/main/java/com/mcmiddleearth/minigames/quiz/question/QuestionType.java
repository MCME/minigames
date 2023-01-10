package com.mcmiddleearth.minigames.quiz.question;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eriol_Eandur
 */
public enum QuestionType {
    FREE    ("Free"),
    NUMBER  ("number"),
    MULTI   ("Multi"),
    SINGLE  ("Single");

    private final String name;

    private QuestionType(String name) {
        this.name = name;
    }

    public static QuestionType getQuestionType(String name) {
        for(QuestionType type: QuestionType.values()) {
            if(type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public static List<String> toList(){
        List<String> names = new ArrayList<>();
        for(QuestionType type : QuestionType.values()){
            names.add(type.name);
        }
        return names;
    }

    public String getName() {
        return name;
    }
}