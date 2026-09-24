package main.pokemons;

import main.Attacks.PhysicalAttacks.DoubleEdge;
import main.Attacks.PhysicalAttacks.RockSlide;
import main.Attacks.PhysicalAttacks.Tackle;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;


public class Onix extends Pokemon {

    public Onix(String name, int level) {
        super(name, level);
        super.setType(Type.ROCK);
        super.setType(Type.GROUND);
        super.setStats(35, 45, 160, 30, 45, 70);
        DoubleEdge doubleEdge = new DoubleEdge(120,100);
        Tackle tackle = new Tackle(40,100);
        RockSlide rockslide = new RockSlide(75,90);
        super.setMove(tackle,rockslide,doubleEdge);
    }

}
