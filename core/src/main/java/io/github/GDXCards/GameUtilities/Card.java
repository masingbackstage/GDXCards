package io.github.GDXCards.GameUtilities;

public class Card {

    private final Card.Rank rank;
    private final Card.Suit suit;



    public enum Rank { A, K, Q, J, N10, N9, N8, N7, N6, N5, N4, N3, N2}
    public enum Suit { Clubs, Diamonds, Hearts, Spades}

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Card() {
        this.rank = Rank.A;
        this.suit = Suit.Spades;

    }

    public Rank getRank() { return rank; }

    public Suit getSuit() { return suit; }
}
