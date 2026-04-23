package io.github.hyscript7.hospoda.pracovnici;

import io.github.hyscript7.hospoda.Predmet;
import io.github.hyscript7.hospoda.Sklad;
import io.github.hyscript7.hospoda.stav.Stav;
import io.github.hyscript7.hospoda.stav.StromChovani;
import io.github.hyscript7.hospoda.stav.univerzalni.CekaniSResetemVlajky;
import io.github.hyscript7.hospoda.stav.univerzalni.IVlajkaUspesnosti;
import io.github.hyscript7.hospoda.stav.univerzalni.ObecnyVyrobceSVlajkouUspenosti;

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
            double pocetAlpha = napojar.sklad.getMnozstvy(a);
            double pocetBeta = napojar.sklad.getMnozstvy(b);
            if (pocetAlpha < napojar.limity.get(a)) {
                return !(pocetBeta < napojar.limity.get(b)) || !(pocetBeta < pocetAlpha);
            }
            return false;
        };
    }

    private StromChovani<Napojar> vytvorStromChovani() {
        StromChovani<Napojar> strom = new StromChovani<>();

        ObecnyVyrobceSVlajkouUspenosti<Napojar> pivo = new ObecnyVyrobceSVlajkouUspenosti<>(this, strom, Predmet.SKLENICE_PIVA, sklad, limity, this);
        ObecnyVyrobceSVlajkouUspenosti<Napojar> limonada = new ObecnyVyrobceSVlajkouUspenosti<>(this, strom, Predmet.SKLENICE_LIMONADY, sklad, limity, this);
        CekaniSResetemVlajky<Napojar> spanek = new CekaniSResetemVlajky<>(this, strom, Duration.ofSeconds(1), this);
        Stav<Napojar> rozcestnik = new Stav<>(this, strom);

        // Určení akce
        rozcestnik.pridejPrechod(napojar -> !napojar.posledniAkceUspela(), spanek); // Pokud poslední akce selhala (tedy, pokud došli suroviny)
        rozcestnik.pridejPrechod(vytvorPrechodnik(Predmet.SKLENICE_PIVA, Predmet.SKLENICE_LIMONADY), pivo);
        rozcestnik.pridejPrechod(vytvorPrechodnik(Predmet.SKLENICE_LIMONADY, Predmet.SKLENICE_PIVA), limonada);
        rozcestnik.pridejPrechod(_ -> true, spanek); // Žádná z předchozích akcí neproběhla, čiže spinkáme

        // Nastavení návratu do rozcestníku po vykonání akce stavu
        Predicate<Napojar> navrat = _ -> true;
        pivo.pridejPrechod(navrat, rozcestnik);
        limonada.pridejPrechod(navrat, rozcestnik);
        spanek.pridejPrechod(navrat, rozcestnik);

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
