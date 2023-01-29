package com.mcmiddleearth.minigames.listener.quizListener;

/**
 * @author Jubo
 */
public enum QuestionConversationType {
    QUESTION        ("question"),
    FREEANSWER      ("freeAnswer"),
    NUMBERANSWER    ("numberAnswer"),
    TOLERANCE       ("tolerance"),
    AANSWER         ("aAnswer"),
    BANSWER         ("bAnswer"),
    CANSWER         ("cAnswer"),
    DANSWER         ("dAnswer"),
    CORRECTANSWER   ("correctAnswer"),
    CATEGORIES      ("categories");

    private final String name;

    private QuestionConversationType(String name){this.name = name;}

    public String getName(){return name;}
}
