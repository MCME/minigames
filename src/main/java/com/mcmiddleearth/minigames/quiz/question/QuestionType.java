package com.mcmiddleearth.minigames.quiz.question;

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

    public String getName() {
        return name;
    }
}
