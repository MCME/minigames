package com.mcmiddleearth.minigames.velocity.runners.util;

public enum QuizRandomness {
    OFF,
    QUESTION_ORDER,
    ANSWER_ORDER,
    ALL;

    public static QuizRandomness getQuizRandomness(String randomness){
        return switch(randomness) {
            case "off":
                yield OFF;
            case "questions":
                yield QUESTION_ORDER;
            case "choices":
                yield ANSWER_ORDER;
            case "all":
                yield ALL;
            default:
                throw new IllegalArgumentException("No such randomness '" + randomness + "' exists for quiz's");
        };
    }
}
