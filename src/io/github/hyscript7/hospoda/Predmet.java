package io.github.hyscript7.hospoda;

import java.util.Collections;
import java.util.Map;

public enum Predmet {
    LIMONADA("Limonáda", "L"),
    PIVO("Pivo", "L"),
    PAREK("Párek", "ks"),
    ROHLIK("Rohlík", "ks"),
    PAREK_V_ROHLIKU("Párek v Rohlíku", "ks", Map.of(PAREK, 1D, ROHLIK, 1D)),
    SPINAVA_SKLENICE("Špinavá Sklenice", "ks"),
    SKLENICE("Sklenice", "ks", Map.of(SPINAVA_SKLENICE, 5D), 5D),
    SKLENICE_LIMONADY("Sklenice Limonády", "ks", Map.of(SKLENICE, 1D, LIMONADA, 0.5D)),
    SKLENICE_PIVA("Sklenice Piva", "ks", Map.of(SKLENICE, 1D, PIVO, 0.5D));

    private final String hezkeJmeno;
    private final String jednotka;
    private final Map<Predmet, Double> recept;
    private final double citatelVyroby;

    Predmet(String hezkeJmeno, String jednotka) {
        this.hezkeJmeno = hezkeJmeno;
        this.jednotka = jednotka;
        this.recept = Collections.emptyMap();
        this.citatelVyroby = 1;
    }

    Predmet(String hezkeJmeno, String jednotka, Map<Predmet, Double> recept) {
        this.hezkeJmeno = hezkeJmeno;
        this.jednotka = jednotka;
        this.recept = recept;
        this.citatelVyroby = 1;
    }

    Predmet(String hezkeJmeno, String jednotka, Map<Predmet, Double> recept, double citatelVyroby) {
        this.hezkeJmeno = hezkeJmeno;
        this.jednotka = jednotka;
        this.recept = recept;
        this.citatelVyroby = citatelVyroby;
    }


    public String getHezkeJmeno() {
        return hezkeJmeno;
    }

    public String getJednotka() {
        return jednotka;
    }

    public Map<Predmet, Double> getRecept() {
        return recept;
    }

    public double getCitatelVyroby() {
        return citatelVyroby;
    }
}
