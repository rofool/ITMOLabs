package main;

import main.pokemons.*;
import ru.ifmo.se.pokemon.Battle;

public class Main {
    public static void main(String[] args) {
        Battle b = new Battle();
        Luvdics luvdics = new Luvdics("Apple", 1);
        Onix onix = new Onix("Banana", 1);
        Steelix steelix = new Steelix("Orange", 1);
        Sewaddle sewaddle = new Sewaddle("Pineapple", 1);
        Swadloon swadloon = new Swadloon("Mango", 1);
        Leavanny leavanny = new Leavanny("Peach", 1);
        b.addAlly(luvdics);
        b.addAlly(onix);
        b.addAlly(steelix);
        b.addFoe(sewaddle);
        b.addFoe(swadloon);
        b.addFoe(leavanny);
        b.go();
    }
}
