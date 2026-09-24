package main.pokemons;

import main.Attacks.PhysicalAttacks.*;
import main.Attacks.SpecialAttacks.EnergyBall;
import main.Attacks.StatusAttacks.GrassWhistle;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Leavanny extends Pokemon {
    public Leavanny(String name, int level) {
        super(name, level);
        super.setType(Type.BUG);
        super.setType(Type.GRASS);
        super.setStats(75, 103, 80, 70, 80, 92);
        Tackle tackle = new Tackle(40,100);
        RazorLeaf razorleaf = new RazorLeaf(55,95);
        GrassWhistle grasswhistle = new GrassWhistle(0,55);
        EnergyBall energyball = new EnergyBall(90,100);
        super.setMove(tackle,razorleaf, grasswhistle,energyball);
    }
}
