package org.sfsteam.easyscrum.data;

import java.io.Serializable;

/**
 * Created by vkurinov on 14.06.13.
 */
public class DeckDT implements Serializable {

    private String name;
    private String deckString;
    private int id;

    public DeckDT(int id, String name, String deckString) {
        this.id = id;
        this.name = name;
        this.deckString = deckString;
    }

    public String getDeckString() {
        return deckString;
    }

    public void setDeckString(String deckString) {
        this.deckString = deckString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final String[] getDeckAsArray() {
        return deckString.split(",");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeckDT other = (DeckDT) obj;
        if (id != other.id)
            return false;
        if (name != other.getName())
            return false;
        if (deckString != other.getDeckString())
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return 31 * id + name.hashCode() + deckString.hashCode();
    }
}
