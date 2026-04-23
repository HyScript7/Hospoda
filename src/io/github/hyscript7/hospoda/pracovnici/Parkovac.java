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

public class Parkovac extends Thread implements IVlajkaUspesnosti {
    private final Sklad sklad;
    private final StromChovani<Parkovac> stromChovani;
    private final Map<Predmet, Double> limity = Map.of(Predmet.PAREK_V_ROHLIKU, 2D);
    private boolean uspesnostPosledniAkce;

    public Parkovac(Sklad sklad) {
        this.sklad = sklad;
        this.stromChovani = vytvorStromChovani();
    }

    private StromChovani<Parkovac> vytvorStromChovani() {
        Predicate<Parkovac> vzdy = _ -> true;
        StromChovani<Parkovac> strom = new StromChovani<>();

        Stav<Parkovac> rozcestnik = new Stav<>(this, strom);
        CekaniSResetemVlajky<Parkovac> behani = new CekaniSResetemVlajky<>(this, strom, Duration.ofSeconds(6), this);
        ObecnyVyrobceSVlajkouUspesnosti<Parkovac> vyrobParekVRohliku = new ObecnyVyrobceSVlajkouUspesnosti<>(this, strom, Predmet.PAREK_V_ROHLIKU, sklad, limity, this);
        DoplnZasoby nakup = new DoplnZasoby(this, strom);
        CekaniSResetemVlajky<Parkovac> cekaniPoNakupu = new CekaniSResetemVlajky<>(this, strom, Duration.ofSeconds(5), this);

        rozcestnik.pridejPrechod(_ -> sklad.getMnozstvi(Predmet.PAREK) <= 0 || sklad.getMnozstvi(Predmet.ROHLIK) <= 0, nakup);
        rozcestnik.pridejPrechod(_ -> sklad.getMnozstvi(Predmet.PAREK_V_ROHLIKU) < limity.get(Predmet.PAREK_V_ROHLIKU), vyrobParekVRohliku);
        rozcestnik.pridejPrechod(vzdy, behani);

        behani.pridejPrechod(vzdy, rozcestnik);
        vyrobParekVRohliku.pridejPrechod(vzdy, rozcestnik);
        nakup.pridejPrechod(vzdy, cekaniPoNakupu);
        cekaniPoNakupu.pridejPrechod(vzdy, rozcestnik);

        strom.zmenStav(rozcestnik);
        return strom;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            stromChovani.run();
        }
    }

    @Override
    public void nastavUspesnostPosledniAkce(boolean uspech) {
        uspesnostPosledniAkce = uspech;
    }

    @Override
    public boolean posledniAkceUspela() {
        return uspesnostPosledniAkce;
    }

    private static class DoplnZasoby extends Stav<Parkovac> {

        public DoplnZasoby(Parkovac herec, StromChovani<Parkovac> strom) {
            super(herec, strom);
        }

        @Override
        public void chovani() {
            getHerec().sklad.uloz(Predmet.PAREK, 10);
            getHerec().sklad.uloz(Predmet.ROHLIK, 10);
        }
    }
}
