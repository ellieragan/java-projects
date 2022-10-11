/**
 * InfoStorer class to keep track of both characters and their frequencies
 * @author Ellie Boyd
 * 05-05-21
 */

public class InfoStorer {
    private Character character;
    private Integer frequency;

    
    public InfoStorer(Integer frequency) {
        this.character = null;
        this.frequency = frequency;
    }

    public InfoStorer(Character character, Integer frequency) {
        this.character = character;
        this.frequency = frequency;
    }


    public char getCharacter() { return character; }
    public int getFrequency() { return frequency; }

    public void setCharacter(Character character) { this.character = character; }
    public void setFrequency(Integer frequency) { this.frequency = frequency; }

    @Override
    public String toString() {
        if (character != null) {
            return "\"" + character + "\"" + " - " + frequency;
        }
        else {
            return "" + frequency;
        }
    }

}
