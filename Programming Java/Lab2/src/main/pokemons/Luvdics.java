package main.pokemons;

import main.Attacks.PhysicalAttacks.AquaJet;
import main.Attacks.StatusAttacks.Rest;
import main.Attacks.StatusAttacks.Supersonic;
import main.Attacks.StatusAttacks.SweetKiss;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;


public class Luvdics extends Pokemon {

    public Luvdics(String name, int level) {
        super(name, level);
        super.setType(Type.WATER);
        super.setStats(43, 30, 55, 40, 65, 97);
        Rest rest = new Rest(0,100);
        AquaJet aquajet = new AquaJet(40,100);
        SweetKiss sweetkiss = new SweetKiss(0,75);
        Supersonic supersonic = new Supersonic(0,55);
        super.setMove(supersonic, aquajet, sweetkiss, rest);
    }
}
