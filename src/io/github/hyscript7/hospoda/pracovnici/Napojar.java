package io.github.hyscript7.hospoda.pracovnici;

import io.github.hyscript7.hospoda.Predmet;
import io.github.hyscript7.hospoda.Sklad;
import io.github.hyscript7.hospoda.stav.Stav;
import io.github.hyscript7.hospoda.stav.StromChovani;
import io.github.hyscript7.hospoda.stav.univerzalni.CekaniSResetemVlajky;
import io.github.hyscript7.hospoda.stav.univerzalni.IVlajkaUspesnosti;
import io.github.hyscript7.hospoda.stav.univerzalni.ObecnyVyrobceSVlajkouUspesnosti;

import java.time.Duration;
import java.util.Map;
import java.util.function.Predicate;

public class Napojar extends Thread implements IVlajkaUspesnosti {
    private final Sklad sklad;
    private final StromChovani<Napojar> stromChovani;
    private final Map<Predmet, Double> limity = Map.of(Predmet.PIVO, 3D, Predmet.LIMONADA, 5D);
    private boolean posledniAkce = false;

    public Napojar(Sklad sklad) {
        this.sklad = sklad;
        this.stromChovani = vytvorStromChovani();
    }

    @Override
    public void nastavUspesnostPosledniAkce(boolean uspech) {
        posledniAkce = uspech;
    }

    @Override
    public boolean posledniAkceUspela() {
        return posledniAkce;
    }

    private static Predicate<Napojar> vytvorPrechodnik(Predmet a, Predmet b) {
        return napojar -> {
            double pocetA = napojar.sklad.getMnozstvi(a);
            double pocetB = napojar.sklad.getMnozstvi(b);
            double limitA = napojar.limity.get(a);
            double limitB = napojar.limity.get(b);
            return pocetA < limitA && (pocetB >= limitB || pocetA <= pocetB);
        };
    }

    private StromChovani<Napojar> vytvorStromChovani() {
        StromChovani<Napojar> strom = new StromChovani<>();

        ObecnyVyrobceSVlajkouUspesnosti<Napojar> pivo = new ObecnyVyrobceSVlajkouUspesnosti<>(this, strom, Predmet.SKLENICE_PIVA, sklad, limity, this);
        ObecnyVyrobceSVlajkouUspesnosti<Napojar> limonada = new ObecnyVyrobceSVlajkouUspesnosti<>(this, strom, Predmet.SKLENICE_LIMONADY, sklad, limity, this);
        CekaniSResetemVlajky<Napojar> spanek = new CekaniSResetemVlajky<>(this, strom, Duration.ofSeconds(1), this);
        Stav<Napojar> rozcestnik = new Stav<>(this, strom);

        rozcestnik.pridejPrechod(napojar -> !napojar.posledniAkceUspela(), spanek);
        rozcestnik.pridejPrechod(vytvorPrechodnik(Predmet.SKLENICE_PIVA, Predmet.SKLENICE_LIMONADY), pivo);
        rozcestnik.pridejPrechod(vytvorPrechodnik(Predmet.SKLENICE_LIMONADY, Predmet.SKLENICE_PIVA), limonada);
        rozcestnik.pridejPrechod(_ -> true, spanek);

        Predicate<Napojar> vzdy = _ -> true;
        pivo.pridejPrechod(vzdy, rozcestnik);
        limonada.pridejPrechod(vzdy, rozcestnik);
        spanek.pridejPrechod(vzdy, rozcestnik);

        strom.zmenStav(rozcestnik);
        return strom;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            stromChovani.run();
        }
    }
}
