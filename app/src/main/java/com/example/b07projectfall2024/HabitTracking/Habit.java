package com.example.b07projectfall2024.HabitTracking;

/**
 * This class represents a habit and it's associated data fields, identically to the structure
 * in the Firebase Realtime Database
 */
public class Habit {
    private String Name;
    private String KeywordOne;
    private String KeywordTwo;
    private String AntiHabit;
    private String Description;
    private String Impact;
    private String TextDisplay;

    public Habit() {
    }

    public Habit(String Name, String KeywordOne, String KeywordTwo, String AntiHabit,
                 String Description, String Impact, String TextDisplay) {
        this.Name = Name;
        this.KeywordOne = KeywordOne;
        this.KeywordTwo = KeywordTwo;
        this.AntiHabit = AntiHabit;
        this.Description = Description;
        this.Impact = Impact;
        this.TextDisplay = TextDisplay;
    }

    public String getName() {
        return Name;
    }

    public String getKeywordOne() {
        return KeywordOne;
    }

    public String getKeywordTwo() {
        return KeywordTwo;
    }

    public String getAntiHabit() {
        return AntiHabit;
    }

    public String getDescription() {
        return Description;
    }

    public String getImpact() {
        return Impact;
    }

    public String getTextDisplay() {
        return TextDisplay;
    }
}
