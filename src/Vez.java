import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Arrays;

/**
 * Trida slouzi pro vytvareni instanci vezi. Dedi od abstraktni tridy AFigurka.
 * @author Filip Valtr
 */
public class Vez extends AFigurka{
    //== Konstantni tridni atributy instanci.
    /** Konstanta, ktera se pouziva pro vypocet velikosti veze. */
    private static final double NASOBNOST_VEL_VEZ= 0.75;
    /** Konstanta, ktera se pouziva pro vypocet velikosti prostoru mimo vez v poli. */
    private static final double NASOBNOST_VEL_PROSTORU_VEZ = 0.25;
    //== Atributy instanci.
    /** Velikost sirky a vysky veze. */
    private double velikostVez;
    /** Udava velikost volneho mista v poli okolo veze. Pomaha vez vycentrovat doprostred hraciho pole. */
    private double okraj;
    /** Atribut rika jestli se kral pohnul ze zacatecni pozice. */
    private boolean pohnulSe = false;
    /** Rika jestli se kral veze pred pohnutim veze nepohnul ze zacatecni pozice. */
    private boolean stavKrale = false;
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
    public Vez(int iRadkuPoleF, int jSloupcePoleF, double velikostCtv, Color tym, Sachovnice s) {
        super(iRadkuPoleF, jSloupcePoleF, velikostCtv, tym, s);
        this.velikostVez = velikostCtv * NASOBNOST_VEL_VEZ;
        this.okraj = (velikostCtv * NASOBNOST_VEL_PROSTORU_VEZ) / 2.0;
    }

    @Override
    public void vytvorT() {
        //Body veze
        double[][] body = {
                {1.0, 1.0}, {1.0, 0.9}, {0.8, 0.75}, {0.8, 0.4}, {0.9, 0.3}, {0.9, 0.0},
                {0.7, 0.0}, {0.7, 0.15}, {0.6, 0.15}, {0.6, 0.0}, {0.5, 0.0}, {0.4, 0.0},
                {0.4, 0.15}, {0.3, 0.15}, {0.3, 0.0}, {0.1, 0.0}, {0.1, 0.3}, {0.2, 0.4},
                {0.2, 0.75}, {0.0, 0.9}
        };

        figurka = new Path2D.Double();
        figurka.moveTo(this.getPoziciX() + okraj, this.getPoziciY() + okraj + velikostVez); //vychozi bod
        //postupne body napojime
        for (double[] bod : body) {
            double x = this.getPoziciX() + okraj + velikostVez * bod[0];
            double y = this.getPoziciY() + okraj + velikostVez * bod[1];
            figurka.lineTo(x, y);
        }

        figurka.closePath();
    }

    @Override
    public void skalujFigurku() {
        this.velikostVez = this.velikostCtv * NASOBNOST_VEL_VEZ;
        this.okraj = (this.velikostCtv * NASOBNOST_VEL_PROSTORU_VEZ) / 2.0;
    }

    @Override
    public void vytvorMozneTahy() {
        final int POCET_SMERU = 4;
        //vynulujeme dosavadni tahy
        Arrays.fill(mozneTahy, null);
        this.setPocitadlo(0);
        final int POCET_SLOZEK_SMER_VEKTORU = 2;
        //pole urcujici smery 4 diagonalne 2 horizontalne 2 vertikalne
        final int[] SMERY = new int[]{
                -1,  0, // nahoru
                1,  0, // dolu
                0, -1, // vlevo
                0,  1, // vpravo
        };
        //po jednom smeru nacteme jednotliva pole
        for (int i = 0; i < POCET_SMERU; i++) {
            int iSmerVektoruRadkoveSlozky = SMERY[i * POCET_SLOZEK_SMER_VEKTORU];
            int iSmerVektoruSloupcoveSlozky = SMERY[(i * POCET_SLOZEK_SMER_VEKTORU) + 1];
            nactiPoleVeSmeru(iSmerVektoruRadkoveSlozky, iSmerVektoruSloupcoveSlozky, this.getS().getRadky());
        }
    }

    /**
     * Geter k atributu pohnulsSe.
     * @return pohnulSe.
     */
    public boolean getPohnulSe() {
        return pohnulSe;
    }

    /**
     * Seter k atributu pohnuleSe.
     * @param pohnulSe boolean.
     */
    public void setPohnulSe(boolean pohnulSe) {
        this.pohnulSe = pohnulSe;
    }

    /**
     * Zjisti jestli se vez nachazi ve sloupci urcenem pro kralovskou rosadu.
     * @return True pokud ano, jinak false.
     */
    public boolean jeVezKralovska() {
        return this.getIndexSloupcePoleF() == this.getS().getDOLNI_A_PRAVA_HRANICE_SACH();
    }

    /**
     * Zjisti jestli se vez nachazi ve sloupci urcenem pro damskou rosadu.
     * @return True pokud ano, jinak false.
     */
    public boolean jeVezDamska() {
        return this.getIndexSloupcePoleF() == this.getS().getHORNI_A_LEVA_HRANICE_SACH();
    }

    /**
     * Metoda zjisti jestli je vez mimo vychozi pozici radku.
     * @return True pokud ano, jinak ne.
     */
    public boolean jeVezMimoVychoziPoziciRadku() {
        int radekVeze;
        if (this.getTym() == this.getS().getTym1()) {
            radekVeze = this.getS().getDOLNI_A_PRAVA_HRANICE_SACH();
        } else {
            radekVeze = this.getS().getHORNI_A_LEVA_HRANICE_SACH();
        }

        return this.getIndexRadkuPoleF() != radekVeze;
    }

    /**
     * Metoda nastavi krali atribut pohnuti na fasle, pokud se kral pred prvnim pohybem veze nepohnul z pozice
     */
    public void nastavKraliPohnuti() {
        //pokud se kral pred pohnutim veze nepohnul ze zacatecni pozice
        if (stavKrale) {
            this.getKral(this.getS().getFIGURKY()).setPohnulSe(false);
        }
    }

    /**
     * Metoda nastavi atribut stavKrale.
     * @param stav -> boolean
     */
    public void setStavKrale(boolean stav) {

        this.stavKrale = stav;
    }
}
