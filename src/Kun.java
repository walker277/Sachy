import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Arrays;

/**
 * Trida slouzi pro vytvareni instanci koni. Dedi od abstraktni tridy AFigurka.
 * @author Filip Valtr
 */
public class Kun extends AFigurka{
    //== Konstantni tridni atributy instanci
    /** Konstanta ktera se pouziva pro vypocet velikosti kone. */
    private static final double NASOBNOST_VEL_KUN= 0.75;
    /** Konstanta ktera se pouziva pro vypocet velikosti prostoru mimo kone v poli. */
    private static final double NASOBNOST_VEL_PROSTORU_KUN = 0.25;
    //== Atributy instanci
    /** Velikost sirky a vysky krale. */
    private double velikostKun;
    /** Udava velikost volneho mista v poli okolo kone. Pomaha kone vycentrovat doprostred hraciho pole. */
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
    public Kun(int iRadkuPoleF, int jSloupcePoleF, double velikostCtv, Color tym, Sachovnice s) {
        super(iRadkuPoleF, jSloupcePoleF, velikostCtv, tym, s);
        this.velikostKun = velikostCtv * NASOBNOST_VEL_KUN;
        this.okraj = (velikostCtv * NASOBNOST_VEL_PROSTORU_KUN) / 2.0;
    }

    @Override
    public void vytvorT() {
        //Pomery pro tvar kone.
        final double K_X1 = 0.3;
        final double K_X2 = 1.0;
        final double K_X3 = 0.9;
        final double K_X4 = 0.4;
        final double K_X5 = 0.38;
        final double K_X6 = 0.25;
        final double K_X7 = 0.17;
        final double K_X8 = 0.2;
        final double K_X9 = 0.5;
        final double K_Y1 = 1.0;
        final double K_Y2 = 0.5;
        final double K_Y3 = 0.1;
        final double K_Y4 = 0.15;
        final double K_Y5 = 0.05;
        final double K_Y6 = 0.17;
        final double K_Y7 = 0.2;
        final double K_Y8 = 0.11;
        final double K_Y9 = 0.25;
        final double K_Y10 = 0.6;
        final double K_Y11 = 0.9;
        final double K_Y12 = 0.7;
        final double K_Y13 = 0.8;
        //vytvoreni tvaru kone
        this.figurka = new Path2D.Double();

        figurka.moveTo(x(K_X1), y(K_Y1));
        figurka.lineTo(x(K_X2), y(K_Y1));
        figurka.curveTo(x(K_X3), y(K_Y2), x(K_X4), y(K_Y3), x(K_X4), y(K_Y4));
        figurka.lineTo(x(K_X5), y(K_Y5));
        figurka.lineTo(x(K_X1), y(K_Y6));
        figurka.lineTo(x(K_X6), y(K_Y7));
        figurka.lineTo(x(K_X7), y(K_Y8));
        figurka.lineTo(x(K_X7), y(K_Y9));
        figurka.curveTo(x(0.0), y(K_Y10), x(0.0), y(K_Y11), x(K_X4), y(K_Y2));
        figurka.curveTo(x(K_X9), y(K_Y12), x(K_X8), y(K_Y13), x(K_X8), y(K_Y1));
        figurka.closePath();
    }

    /**
     * Pomocna metoda. Spocita x-ovou soradnici bodu na zaklade predaneho nasobku.
     * @param nasobek - Cislo pomahajici urcit x-ovou soradnci
     * @return (double) - X-ovou souradnici.
     */
    private double x(double nasobek) {
        return this.getPoziciX() + okraj + velikostKun * nasobek;
    }

    /**
     * Pomocna metoda. Spocita y-ovou soradnici bodu na zaklade predaneho nasobku.
     * @param nasobek - Cislo pomahajici urcit y-ovou soradnci
     * @return (double) - Y-ovou souradnici.
     */
    private double y(double nasobek) {
        return this.getPoziciY() + okraj + velikostKun * nasobek;
    }

    @Override
    public void skalujFigurku() {
        this.velikostKun = this.velikostCtv * NASOBNOST_VEL_KUN;
        this.okraj = (this.velikostCtv * NASOBNOST_VEL_PROSTORU_KUN) / 2.0;
    }

    @Override
    public void vytvorMozneTahy() {
        //vynulujeme dosavadni tahy
        Arrays.fill(mozneTahy, null);
        this.setPocitadlo(0);
        final int POCET_SMERU = 4;

        //pole urcujici smery 4 diagonalne 2 horizontalne 2 vertikalne
        final int[] SMERY = new int[]{
                -1,  0, 0, 1, // nahoru
                1,  0, 0, 1,// dolu
                0, -1, 1, 0,// vlevo
                0,  1, 1, 0// vpravo
        };
        // po jednom smeru nacteme jednotliva pole
        for (int i = 0; i < POCET_SMERU; i++) {
            int[] indexyAPripocty = zjistiIndexyAPripocty(i, SMERY);
            int iSmerVektoruRadkoveSlozky = indexyAPripocty[0];
            int iSmerVektoruSloupcoveSlozky = indexyAPripocty[1];
            int pripocetKRadku = indexyAPripocty[2];
            int pripocetKSloupci  = indexyAPripocty[3];
            nactiPoleVeSmeruProKone(iSmerVektoruRadkoveSlozky, iSmerVektoruSloupcoveSlozky, pripocetKRadku,
                                    pripocetKSloupci, this.getS());
        }
    }

    /**
     * Pomocna metoda. Metoda zjsit smerove vektory a pripocty k radum a sloupcum prislusneho smeru.
     * @param i - Index urcujici prislusny smerovy vektor a pripocty v poli smeru
     * @param smery - Pole obsahujici smerove vektory a pripocty k radkum a sloupcum kazdeho smeru.
     * @return int[] -> Prvni prvek je smer radku, druhy smer sloupce, 3 pripocet k radku a 4 pripocet k sloupci.
     */
    private int[] zjistiIndexyAPripocty(int i, int[] smery) {
        final int POCET_SLOZEK_SMER_VEKTORU = 4;
        int[] indexyAPripocty = new int[4];
        indexyAPripocty[0] = smery[i * POCET_SLOZEK_SMER_VEKTORU];
        indexyAPripocty[1] = smery[(i * POCET_SLOZEK_SMER_VEKTORU) + 1];
        indexyAPripocty[2] = smery[(i * POCET_SLOZEK_SMER_VEKTORU) + 2];
        indexyAPripocty[3] = smery[(i * POCET_SLOZEK_SMER_VEKTORU) + 3];

        return indexyAPripocty;
    }

    /**
     * Metoda nacte v urcitem smeru pole pro kone a ty prida do moznych tahu.
     * Bere v potaz i ostatni figurky ve hre.
     * @param smerVRadku - Cislo udavajici smer v radku.
     * @param smerVSloup - Cislo udavajici smer v sloupci.
     * @param pricitatKRadku - Cislo udavajici jestli chceme ziska pole horizontalne (1) nebo  ne (0).
     * @param pricitatKSloupci - Cislo udavajici jestli chceme ziska pole vertikalne (1) nebo  ne (0).
     * @param s - Instance tridy Sachovnice.
     */
    private void nactiPoleVeSmeruProKone(int smerVRadku, int smerVSloup, int pricitatKRadku, int pricitatKSloupci,
                                         Sachovnice s) {
        int indexR;
        int indexS;
        final int SKOK = 2;
        //upravime index ve smeru
        indexR = this.getIndexRadkuPoleF() + (smerVRadku * SKOK);
        indexS = this.getIndexSloupcePoleF() + (smerVSloup * SKOK);
        int indexRL = indexR;
        int indexRR = indexR;
        int indexSL = indexS;
        int indexSR = indexS;
        if (pricitatKRadku == 1) {
            indexRR++;
            indexRL--;
            nactiPole(indexRL, indexS, s, this);
            nactiPole(indexRR, indexS, s, this);
        }
        if (pricitatKSloupci == 1) {
            indexSR++;
            indexSL--;
            nactiPole(indexR, indexSL, s, this);
            nactiPole(indexR, indexSR, s, this);
        }
    }

    /**
     * Metoda z indexu urcujici pole zjisti, zda pole muze pridat do moznych tahu kone.
     * Pokud ano tak pole prida, jinak ne.
     * @param i - Index radku pole na sachovnici.
     * @param j - Index sloupce pole na sachovnici.
     * @param s - Instance tridy Sachovnice.
     * @param vybranaF - Figurka u ktere vytvarime pole (kun).
     */
    private void nactiPole(int i, int j, Sachovnice s, AFigurka vybranaF) {
        //zkontrolujeme jestli indexy jsou validni
        if (s.jeIRadkuVSachovnici(i) && s.jeISloupVSachovnici(j)) {
            //zjistime jestli je na poli figurka
            if (s.jeNaPoliFigurka(i, j)) {
                AFigurka figurka = s.getSTAV_HRY()[i][j];
                if (!s.jsouFigStejnehoTymu(vybranaF, figurka)) { //pokud je figurka jineho tymu
                    pridejPoleDoTahu(s.getHraciPole()[i][j]);
                }
            } else { //pokud neni figurka tak pridame pole do moznych tahu
                pridejPoleDoTahu(s.getHraciPole()[i][j]);
            }
        }
    }
}
