package main.pokemons;

import main.Attacks.PhysicalAttacks.DoubleEdge;
import main.Attacks.PhysicalAttacks.FireFang;
import main.Attacks.PhysicalAttacks.RockSlide;
import main.Attacks.PhysicalAttacks.Tackle;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;


public class Steelix extends Pokemon {

    public Steelix(String name, int level) {
        super(name, level);
        super.setType(Type.STEEL);
        super.setType(Type.GROUND);
        super.setStats(75, 85, 200, 55, 65, 30);
        DoubleEdge doubleEdge = new DoubleEdge(120,100);
        Tackle tackle = new Tackle(40,100);
        RockSlide rockslide = new RockSlide(75,90);
        FireFang firefang = new FireFang(65,95);
        super.setMove(firefang,rockslide,doubleEdge,tackle);
    }

}
