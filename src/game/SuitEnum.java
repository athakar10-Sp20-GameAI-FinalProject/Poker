package game;

import java.util.Arrays;
import java.util.List;

public enum SuitEnum {
    HEART("H"), DIAMOND("D"), CLUB("C"), SPADE("S");

    private String name;

    SuitEnum (String name) {
        this.name = name;
    }

    String getSuit() {
        return this.name;
    }
    
    public static List<SuitEnum> allSuits() {
    	return Arrays.asList(HEART, DIAMOND, CLUB, SPADE);
    }
}