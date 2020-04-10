package game;

public enum SuitEnum {
    HEART("H"), DIAMOND("D"), CLUB("C"), SPADE("S");

    private String name;

    SuitEnum (String name) {
        this.name = name;
    }

    String getSuit() {
        return this.name;
    }
}