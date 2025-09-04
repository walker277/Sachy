import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Arrays;

/**
 * Trida slouzi pro vytvareni instanci dam. Dedi od abstraktni tridy AFigurka.
 * @author Filip Valtr
 */
public class Dama extends AFigurka {
    //== Konstantni tridni atributy instanci
    /** Konstanta ktera se pouziva pro vypocet velikosti damy. */
    private static final double NASOBNOST_VEL_DAM = 0.75;
    /** Konstanta ktera se pouziva pro vypocet velikosti prostoru mimo damy v poli. */
    private static final double NASOBNOST_VEL_PROSTORU_DAM = 0.25;
    //== Atributy instanci
    /** Velikost sirky a vysky damy. */
    private double velikostDam;
    /** Udava velikost volneho mista v poli okolo damy. Pomaha krale vycentrovat doprostred hraciho pole. */
    private double okraj;
    //== Konstruktory
    /**
     * Konstruktor, nastavi klicove atributy jako jsou indexy pole figurky ve kterem se nachazi, velikost pole,
     * barvu tymu.
     *
     * @param iRadkuPoleF               - Index radku pole figruky.
     * @param jSloupcePoleF             - Index sloupce pole figurky.
     * @param velikostCtv               - Velikost jednoho herniho pole (ctverce).
     * @param tym                       - Barva tymu ktereho je figurka soucasti.
     * @param s                         - Instance tridy Sachovnice.
     */
    public Dama(int iRadkuPoleF, int jSloupcePoleF, double velikostCtv, Color tym, Sachovnice s) {
        super(iRadkuPoleF, jSloupcePoleF, velikostCtv, tym, s);
        this.velikostDam = velikostCtv * NASOBNOST_VEL_DAM;
        this.okraj = (velikostCtv * NASOBNOST_VEL_PROSTORU_DAM) / 2.0;
    }

    @Override
    public void vytvorT() {
        // Konstanty
        final double DAMA_SPODNI_LEVY_OKRAJ_X = 0.1;
        final double DAMA_SPODNI_PRAVY_OKRAJ_X = 0.9;
        final double DAMA_SPODNI_OKRAJ_Y = 0.9;
        final double DAMA_SPODNI_OBLOUK_VLEVO_X = 0.3;
        final double DAMA_SPODNI_OBLOUK_VPRAVO_X = 0.7;
        final double DAMA_OBLOUK_Y = 1.0;
        final double DAMA_STREDNI_OBLOUK_Y = 0.8;
        final double DAMA_STREDNI_SNIZENI_Y = 0.7;
        final double DAMA_KRIVKA_VPRAVO_X1 = 0.85;
        final double DAMA_KRIVKA_VPRAVO_X2 = 0.8;
        final double DAMA_KRIVKA_VPRAVO_Y1 = 0.6;
        final double DAMA_KRIVKA_VPRAVO_Y2 = 0.4;
        final double[] DAMA_SPICKY_X = {0.82, 0.68, 0.62, 0.56, 0.50, 0.44, 0.38, 0.32, 0.20, 0.20};
        final double[] DAMA_SPICKY_Y = {
                0.0, 0.4, 0.0, 0.4, 0.0, 0.4, 0.0, 0.4, 0.0, 0.4
        };
        final double DAMA_KRIVKA_VLEVO_X1 = 0.2;
        final double DAMA_KRIVKA_VLEVO_X2 = 0.15;
        final double DAMA_KRIVKA_VLEVO_X3 = 0.3;
        final double DAMA_KRIVKA_VLEVO_Y1 = 0.6;
        final double DAMA_KRIVKA_VLEVO_Y2 = 0.7;
        final double DAMA_KRIVKA_VLEVO_Y3 = 0.8;

        this.figurka = new Path2D.Double();

        //Spodni oblouk
        figurka.moveTo(x(DAMA_SPODNI_LEVY_OKRAJ_X), y(DAMA_SPODNI_OKRAJ_Y));
        figurka.curveTo(x(DAMA_SPODNI_OBLOUK_VLEVO_X), y(DAMA_OBLOUK_Y),
                x(DAMA_SPODNI_OBLOUK_VPRAVO_X), y(DAMA_OBLOUK_Y),
                x(DAMA_SPODNI_PRAVY_OKRAJ_X), y(DAMA_SPODNI_OKRAJ_Y));

        //Oblouk ke spickam
        figurka.curveTo(x(DAMA_SPODNI_OBLOUK_VPRAVO_X), y(DAMA_STREDNI_OBLOUK_Y),
                x(DAMA_SPODNI_OBLOUK_VPRAVO_X), y(DAMA_STREDNI_OBLOUK_Y),
                x(DAMA_SPODNI_OBLOUK_VPRAVO_X), y(DAMA_STREDNI_SNIZENI_Y));

        figurka.curveTo(x(DAMA_KRIVKA_VPRAVO_X1), y(DAMA_KRIVKA_VPRAVO_Y1),
                x(DAMA_KRIVKA_VPRAVO_X2), y(DAMA_KRIVKA_VPRAVO_Y2),
                x(DAMA_KRIVKA_VPRAVO_X2), y(DAMA_KRIVKA_VPRAVO_Y2));

        //Spicky damy
        for (int i = 0; i < DAMA_SPICKY_X.length; i++) {
            figurka.lineTo(x(DAMA_SPICKY_X[i]), y(DAMA_SPICKY_Y[i]));
        }

        //Leva strana zpet dolu
        figurka.curveTo(x(DAMA_KRIVKA_VLEVO_X1), y(DAMA_KRIVKA_VLEVO_Y2),
                x(DAMA_KRIVKA_VLEVO_X2), y(DAMA_KRIVKA_VLEVO_Y1),
                x(DAMA_KRIVKA_VLEVO_X3), y(DAMA_KRIVKA_VLEVO_Y2));

        figurka.curveTo(x(DAMA_KRIVKA_VLEVO_X3), y(DAMA_KRIVKA_VLEVO_Y3),
                x(DAMA_KRIVKA_VLEVO_X3), y(DAMA_KRIVKA_VLEVO_Y3),
                x(DAMA_SPODNI_LEVY_OKRAJ_X), y(DAMA_SPODNI_OKRAJ_Y));

        figurka.closePath();
    }

    @Override
    public void skalujFigurku() {
        this.velikostDam = this.velikostCtv * NASOBNOST_VEL_DAM;
        this.okraj = (this.velikostCtv * NASOBNOST_VEL_PROSTORU_DAM) / 2.0;
    }

    @Override
    public void vytvorMozneTahy() {
        //vynulujeme dosavadni tahy
        Arrays.fill(mozneTahy, null);
        this.setPocitadlo(0);
        final int POCET_SMERU = 8;
        final int POCET_SLOZEK_SMER_VEKTORU = 2;
        //pole urcujici smery 4 diagonalne 2 horizontalne 2 vertikalne
        final int[] SMERY = new int[]{
                -1,  0, // nahoru
                1,  0, // dolu
                0, -1, // vlevo
                0,  1, // vpravo
                -1, -1, // nahoru vlevo
                -1,  1, // nahoru vpravo
                1, -1, // dolu vlevo
                1,  1  // dolu vpravo
        };
        // po jednom smeru nacteme jednotliva pole
        for (int i = 0; i < POCET_SMERU; i++) {
            int iSmerVektoruRadkoveSlozky = SMERY[i * POCET_SLOZEK_SMER_VEKTORU];
            int iSmerVektoruSloupcoveSlozky = SMERY[(i * POCET_SLOZEK_SMER_VEKTORU) + 1];
            nactiPoleVeSmeru(iSmerVektoruRadkoveSlozky, iSmerVektoruSloupcoveSlozky, this.getS().getRadky());
        }
    }

    /**
     * Pomocna metoda. Spocita x-ovou soradnici bodu na zaklade predaneho nasobku.
     * @param nasobek - Cislo pomahajici urcit x-ovou soradnci
     * @return (double) - X-ovou souradnici.
     */
    private double x(double nasobek) {
        return this.getPoziciX() + okraj + velikostDam * nasobek;
    }

    /**
     * Pomocna metoda. Spocita y-ovou soradnici bodu na zaklade predaneho nasobku.
     * @param nasobek - Cislo pomahajici urcit y-ovou soradnci
     * @return (double) - Y-ovou souradnici.
     */
    private double y(double nasobek) {
        return this.getPoziciY() + okraj + velikostDam * nasobek;
    }
}
