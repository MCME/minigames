/*
 * Copyright (C) 2015 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.minigames.quizQuestion;

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
