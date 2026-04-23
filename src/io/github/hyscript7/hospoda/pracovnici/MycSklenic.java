package io.github.hyscript7.hospoda.pracovnici;

import io.github.hyscript7.hospoda.Predmet;
import io.github.hyscript7.hospoda.Sklad;
import io.github.hyscript7.hospoda.stav.Stav;
import io.github.hyscript7.hospoda.stav.StromChovani;
import io.github.hyscript7.hospoda.stav.univerzalni.Cekani;
import io.github.hyscript7.hospoda.stav.univerzalni.IVlajkaUspesnosti;
import io.github.hyscript7.hospoda.stav.univerzalni.ObecnyVyrobceSVlajkouUspesnosti;

import java.time.Duration;
import java.util.Map;
import java.util.function.Predicate;

public class MycSklenic extends Thread implements IVlajkaUspesnosti {
    private final Sklad sklad;
    private final StromChovani<MycSklenic> stromChovani;
    private final Map<Predmet, Double> limity = Map.of(Predmet.SKLENICE, 30D);

    private boolean posledniAkce = true;

    @Override
    public void nastavUspesnostPosledniAkce(boolean uspech) {
        this.posledniAkce = uspech;
    }

    @Override
    public boolean posledniAkceUspela() {
        return posledniAkce;
    }

    public MycSklenic(Sklad sklad) {
        this.sklad = sklad;
        this.stromChovani = vytvorStromChovani();
    }

    private StromChovani<MycSklenic> vytvorStromChovani() {
        StromChovani<MycSklenic> strom = new StromChovani<>();

        Stav<MycSklenic> rozcestnik = new Stav<>(this, strom);
        Cekani<MycSklenic> neniCoDelat = new Cekani<>(this, strom, Duration.ofSeconds(1));
        ObecnyVyrobceSVlajkouUspesnosti<MycSklenic> umyjSklenice = new ObecnyVyrobceSVlajkouUspesnosti<>(this, strom, Predmet.SKLENICE, sklad, limity, this);

        Predicate<MycSklenic> vzdy = _ -> true;

        umyjSklenice.pridejPrechod(vzdy, rozcestnik);
        neniCoDelat.pridejPrechod(vzdy, rozcestnik);
        rozcestnik.pridejPrechod(_ -> sklad.getMnozstvi(Predmet.SKLENICE) < 30 && sklad.getMnozstvi(Predmet.SPINAVA_SKLENICE) >= 5, umyjSklenice);
        rozcestnik.pridejPrechod(vzdy, neniCoDelat);

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
