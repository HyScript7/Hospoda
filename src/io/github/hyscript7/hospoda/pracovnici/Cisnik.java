package io.github.hyscript7.hospoda.pracovnici;

import io.github.hyscript7.hospoda.Predmet;
import io.github.hyscript7.hospoda.Sklad;
import io.github.hyscript7.hospoda.stav.Stav;
import io.github.hyscript7.hospoda.stav.StromChovani;
import io.github.hyscript7.hospoda.stav.univerzalni.Cekani;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Cisnik extends Thread {
    private final Sklad skladHotovychVeci;
    private final StromChovani<Cisnik> stromChovani;
    private final Kostka kostka;

    public Cisnik(Sklad skladHotovychVeci) {
        this.skladHotovychVeci = skladHotovychVeci;
        this.stromChovani = vytvorStromChovani();
        this.kostka = new Kostka(6);
    }

    private StromChovani<Cisnik> vytvorStromChovani() {
        StromChovani<Cisnik> strom = new StromChovani<>();

        Stav<Cisnik> rozcestnik = new Stav<>(this, strom);
        Cekani<Cisnik> cekaniPredZopakovanimPokusem = new Cekani<>(this, strom, Duration.ofSeconds(1));
        // TODO

        strom.zmenStav(rozcestnik);
        return strom;
    }

    private static class Kostka {
        private final int pocetStavu;
        private int hod;

        public Kostka(int pocetStavu) {
            this.pocetStavu = pocetStavu;
            this.hod = 1;
        }

        public void hodKoskou() {
            hod = ThreadLocalRandom.current().nextInt(1, pocetStavu);
        }

        public int getPocetStavu() {
            return pocetStavu;
        }
    }

    private static class StavObjednavky extends Stav<Cisnik> {
        private final Map<Predmet, Double> objednavka;
        private final Sklad sklad;
        private boolean retry;

        public StavObjednavky(Cisnik herec, StromChovani<Cisnik> strom, Map<Predmet, Double> objednavka) {
            super(herec, strom);
            this.objednavka = objednavka;
            this.sklad = herec.skladHotovychVeci;
            this.retry = false;
        }

        @Override
        public void chovani() {
            retry = !sklad.ziskej(objednavka);
        }

        @Override
        public void priOdchodu(Stav<Cisnik> novyStav) {
            if (!retry) {
                getHerec().kostka.hodKoskou();
            }
        }
    }
}
