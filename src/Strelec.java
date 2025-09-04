import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Arrays;

/**
 * Trida slouzi pro vytvareni instanci strelcu. Dedi od abstraktni tridy AFigurka.
 * @author Filip Valtr
 */
public class Strelec extends AFigurka {
    //== Konstantni tridni atributy instanci.
    /** Konstanta ktera se pouziva pro vypocet velikosti stelce. */
    private static final double NASOBNOST_VEL_STR= 0.75;
    /** Konstanta ktera se pouziva pro vypocet velikosti prostoru mimo strelce v poli. */
    private static final double NASOBNOST_VEL_PROSTORU_STR = 0.25;
    //== Atributy instanci
    /** Velikost sirky a vysky strelce. */
    private double velikostStr;
    /** Udava velikost volneho mista v poli okolo strelce. Pomaha vez vycentrovat doprostred hraciho pole. */
    private double okraj;
    //==Konstruktory
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
    public Strelec(int iRadkuPoleF, int jSloupcePoleF, double velikostCtv, Color tym, Sachovnice s) {
        super(iRadkuPoleF, jSloupcePoleF, velikostCtv, tym, s);
        this.velikostStr = velikostCtv * NASOBNOST_VEL_STR;
        this.okraj = (velikostCtv * NASOBNOST_VEL_PROSTORU_STR) / 2.0;
    }

    @Override
    public void vytvorT() {
        //Pomery pro jednotlive casti figurky
        final double ZAKLAD_X1 = 0.1;
        final double ZAKLAD_X2 = 0.2;
        final double ZAKLAD_X3 = 0.3;
        final double ZAKLAD_X4 = 0.5;
        final double ZAKLAD_X5 = 0.7;
        final double ZAKLAD_X6 = 0.8;
        final double ZAKLAD_X7 = 0.9;
        final double ZAKLAD_X8 = 1.0;
        final double ZAKLAD_Y1 = 1.0;
        final double ZAKLAD_Y2 = 0.8;
        final double ZAKLAD_Y3 = 0.7;
        final double ZAKLAD_Y4 = 0.6;
        final double ZAKLAD_Y5 = 0.5;
        final double ZAKLAD_Y6 = 0.4;
        final double ZAKLAD_Y7 = 0.2;
        final double ZAKLAD_Y8 = 0.1;
        final double ZAKLAD_Y9 = 0.0;

        this.figurka = new Path2D.Double();

        // Telo figurky
        figurka.moveTo(x(ZAKLAD_X1), y(ZAKLAD_Y1));
        figurka.curveTo(x(ZAKLAD_X2), y(ZAKLAD_Y2),
                x(ZAKLAD_X3), this.getPoziciY() + velikostStr + velikostStr * ZAKLAD_Y8,
                x(ZAKLAD_X4), y(ZAKLAD_Y3));
        figurka.curveTo(x(ZAKLAD_X5), y(ZAKLAD_X7),
                x(ZAKLAD_X6), y(ZAKLAD_Y2),
                x(ZAKLAD_X7), y(ZAKLAD_Y1));
        figurka.lineTo(x(ZAKLAD_X8), y(ZAKLAD_X7));
        figurka.curveTo(x(ZAKLAD_X8), y(ZAKLAD_Y2),
                x(ZAKLAD_X5), y(ZAKLAD_X7),
                x(ZAKLAD_X4), y(ZAKLAD_Y4));
        figurka.lineTo(x(ZAKLAD_X5), y(ZAKLAD_Y5));
        figurka.lineTo(x(ZAKLAD_X5), y(ZAKLAD_Y6));
        figurka.curveTo(x(ZAKLAD_X6), y(ZAKLAD_Y7),
                x(ZAKLAD_X6), y(ZAKLAD_Y7),
                x(ZAKLAD_X4), y(ZAKLAD_Y8));
        figurka.curveTo(x(ZAKLAD_X3), y(ZAKLAD_Y9),
                x(ZAKLAD_X5), y(ZAKLAD_Y9),
                x(ZAKLAD_X4), y(ZAKLAD_Y8));
        figurka.curveTo(x(ZAKLAD_X2), y(ZAKLAD_Y7),
                x(ZAKLAD_X2), y(ZAKLAD_Y7),
                x(ZAKLAD_X3), y(ZAKLAD_Y6));
        figurka.lineTo(x(ZAKLAD_X3), y(ZAKLAD_Y5));
        figurka.lineTo(x(ZAKLAD_X4), y(ZAKLAD_Y4));
        figurka.curveTo(x(ZAKLAD_X3), this.getPoziciY() + velikostStr + velikostStr * ZAKLAD_Y8,
                this.getPoziciX() + okraj, y(ZAKLAD_Y2),
                this.getPoziciX() + okraj, y(ZAKLAD_X7));
        figurka.closePath();
    }

    @Override
    public void skalujFigurku() {
        this.velikostStr = this.velikostCtv * NASOBNOST_VEL_STR;
        this.okraj = (this.velikostCtv * NASOBNOST_VEL_PROSTORU_STR) / 2.0;
    }

    @Override
    public void vytvorMozneTahy() {
        final int POCET_SMERU = 4;
        final int POCET_SLOZEK_SMER_VEKTORU = 2;
        //vynulujeme dosavadni tahy
        Arrays.fill(mozneTahy, null);
        this.setPocitadlo(0);
        //pole urcujici smery 4 diagonalne 2 horizontalne 2 vertikalne
        final int[] SMERY = new int[]{
                -1, -1, // nahoru vlevo
                -1,  1, // nahoru vpravo
                1, -1, // dolu vlevo
                1,  1  // dolu vpravo
        };
        //po jednom smeru nacteme jednotliva pole
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
        return this.getPoziciX() + okraj + velikostStr * nasobek;
    }

    /**
     * Pomocna metoda. Spocita y-ovou soradnici bodu na zaklade predaneho nasobku.
     * @param nasobek - Cislo pomahajici urcit y-ovou soradnci
     * @return (double) - Y-ovou souradnici.
     */
    private double y(double nasobek) {
        return this.getPoziciY() + okraj + velikostStr * nasobek;
    }

    /**
     * Metoda zjisti jestli se strelec nachazi na bilem poli.
     * @throws IllegalStateException Pokud ma figurka neplatne index pole.
     * @return True pokud ano, jinak false.
     */
    public boolean jeNaBilemPoli() {
        if (this.getS().nejsouIndexyVSachovnici(this.getIndexRadkuPoleF(), this.getIndexSloupcePoleF())) {
            throw new IllegalStateException("Figurka ma neplatne indexy pole");
        }
        return this.getIndexRadkuPoleF() % 2 == this.getIndexSloupcePoleF() % 2;
    }

    /**
     * Metoda zjisti jestli se strelec nachazi na cernem poli.
     * @throws IllegalStateException Pokud ma figurka neplatne indexy pole.
     * @return True pokud ano, jinak false.
     */
    public boolean jeNaCernemPoli() {
        if (this.getS().nejsouIndexyVSachovnici(this.getIndexRadkuPoleF(), this.getIndexSloupcePoleF())) {
            throw new IllegalStateException("Figurka ma neplatne indexy pole");
        }
        return this.getIndexRadkuPoleF() % 2 != this.getIndexSloupcePoleF() % 2;
    }
}
