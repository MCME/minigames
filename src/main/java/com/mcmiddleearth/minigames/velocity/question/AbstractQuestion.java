package com.mcmiddleearth.minigames.velocity.question;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class AbstractQuestion {

    private String question;
    private final QuestionType type;

    private String categories = "";

    private boolean answered = false;
    private int id = 0;

    public AbstractQuestion(String question, QuestionType type, String categories) {
        if(categories !=null) {
            this.categories = categories;
        }
        this.question = question;
        this.type = type;
    }

    public abstract boolean isCorrectAnswer(String answer);

    public abstract String getCorrectAnswer();

    public void setCategories(String categories) {
        if(categories!=null) {
            this.categories=categories;
        }
    }

    public abstract String[] getDetails();

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public QuestionType getType() {
        return type;
    }

    public String getCategories() {
        return categories;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}