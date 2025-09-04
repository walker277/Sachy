import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Trida slouzi pro vytvareni instanci kralu. Dedi od abstraktni tridy AFigurka.
 * @author Filip Valtr
 */
public class Kral extends AFigurka{
    //== Konstantni tridni atributy instanci
    /** Konstanta ktera se pouziva pro vypoce velikosti pesce. */
    private static final double NASOBNOST_VEL_KRL = 0.75;
    /** Konstanta ktera se pouziva pro vypoce velikosti prostoru mimo pesce v poli. */
    private static final double NASOBNOST_VEL_PROSTORU_KRL = 0.25;
    /** Konstantni pole urcujici smery 4 diagonalne 2 horizontalne 2 vertikalne. */
    private static final int[] SMERY = new int[]{
            -1,  0, // nahoru
            1,  0, // dolu
            0, -1, // vlevo
            0,  1, // vpravo
            -1, -1, // nahoru vlevo
            -1,  1, // nahoru vpravo
            1, -1, // dolu vlevo
            1,  1  // dolu vpravo
    };
    /** Urcuje pocet slozek smer vektoru. */
    private static final int POCET_SLOZEK_SMER_VEKTORU = 2;
    /** Urcuje pocet smeru ve kterych se kral pohybuje. */
    private static final int POCET_SMERU = (SMERY.length / POCET_SLOZEK_SMER_VEKTORU);
    //== Atributy instanci
    /** Velikost sirky a vysky krale. */
    private double velikostKrl;
    /** Udava velikost volneho mista v poli okolo krale. Pomaha krale vycentrovat doprostred hraciho pole. */
    private double okraj;
    /** Atribut rika jestli se kral pohnul ze zacatecni pozice. */
    private boolean pohnulSe = false;
    /** Atribut urcujici pole rosady, neboi pole kam se posune kral a vez pri rosade. */
    private Rectangle2D[] rosada = new Rectangle2D[4];
    /** Atribut uchovava indexy vezi pro rosady. */
    private int[] indexyVeziRosady = new int[4];
    /** Atribut urcujici jestli je kral v sachu. */
    private boolean sach = false;
    /** Atribut urcujici jestli kral uz udelal rosadu. */
    private boolean udelalRosadu = false;
    /** Rika jestli se vez krale na prislusne strane pred pohnutim krale pohnula ze zacatecni pozice. */
    private boolean[] stavVezi = new boolean[] {false, false};
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
    public Kral(int iRadkuPoleF, int jSloupcePoleF, double velikostCtv, Color tym, Sachovnice s) {
        super(iRadkuPoleF, jSloupcePoleF, velikostCtv, tym, s);
        this.velikostKrl = velikostCtv * NASOBNOST_VEL_KRL;
        this.okraj = (velikostCtv * NASOBNOST_VEL_PROSTORU_KRL) / 2.0;
    }

    @Override
    public void vytvorT() {
        //vytvoreni cele figurky krale vcetne koruny
        this.figurka = new Path2D.Double();
        // telo krale
        vykresliTeloKrale();
        // koruna krale
        vykresliKorunuKrale();
    }

    /**
     * Vykresli telo krale pomoci sady krivek a car na zaklade zadanych proporci.
     *
     */
    private void vykresliTeloKrale() {
        //Horizontalni pozice leveho dolniho bodu. Horizontalni pozice horniho leveho boku.
        //Vertikalni pozice stredu v horni leve casti.
        final double TVAR_LEVA_DOLNI_LEVY_BOK_HRONI_LEVA = 0.2;
        //Horizontalni pozice praveho dolniho bodu.
        final double TVAR_PRAVA_DOLNI = 0.8;
        //Vertikalni pozice dolniho stredu.
        final double TVAR_DOLNI_STRED = 0.9;
        //Vertikalni pozice praveho boku ve vysce 0.6. Vertikalni pozice stredu v horni casti.
        //Vertikalni pozice leveho dolniho okraje.
        final double TVAR_PRAVY_BOK_VYSKA_STRED_HORNI_LEVA_SPONDI = 0.6;
        //Vertikalni pozice praveho horniho vrcholu.
        final double TVAR_PRAVY_VRCH = 0.3;
        //Vertikalni pozice horniho leveho vrcholu.
        final double TVAR_HORNI_LEVY_VRCHOL = 0.1;
        //Konstanta pro vertikalni pozici priblizne 0.22 pouzita ve stredu krale pri vykreslovani krivek.
        final double TVAR_STREDNI_VYSKA = 0.22;
        //Konstanta pro horizontalni pozici priblizne 0.7, pouzita pri kresleni krivek krale.
        final double TVAR_HORNI_PRAVY_STRED_1 = 0.7;
        //Konstanta pro horizontalni pozici priblizne 0.65, pouzita pri kresleni krivek krale.
        final double TVAR_HORNI_PRAVY_STRED_2 = 0.65;
        //Konstanta pro horizontalni pozici priblizne 0.5, pouzita pri kresleni krivek krale (stred).
        final double TVAR_STRED = 0.5;
        //Konstanta pro horizontalni pozici priblizne 0.35, pouzita pri kresleni krivek krale.
        final double TVAR_HORNI_LEVA_STRED = 0.35;
        //Konstanta pro horizontalni pozici priblizne 0.4, pouzita pri kresleni krivek krale.
        final double TVAR_HORNI_LEVA_MENSI = 0.4;

        figurka.moveTo(x(TVAR_LEVA_DOLNI_LEVY_BOK_HRONI_LEVA), y(TVAR_DOLNI_STRED));
        figurka.curveTo(x(TVAR_PRAVY_VRCH), y(1.0), x(TVAR_HORNI_PRAVY_STRED_1), y(1.0),
                        x(TVAR_PRAVA_DOLNI), y(TVAR_DOLNI_STRED));
        figurka.lineTo(x(TVAR_PRAVA_DOLNI), y(TVAR_PRAVY_BOK_VYSKA_STRED_HORNI_LEVA_SPONDI));
        figurka.curveTo(x(1.0), y(0.0), x(TVAR_HORNI_PRAVY_STRED_2), y(TVAR_PRAVY_VRCH), x(TVAR_STRED),
                        y(TVAR_PRAVY_BOK_VYSKA_STRED_HORNI_LEVA_SPONDI));
        figurka.curveTo(x(TVAR_PRAVA_DOLNI), y(TVAR_HORNI_LEVY_VRCHOL), x(TVAR_PRAVY_BOK_VYSKA_STRED_HORNI_LEVA_SPONDI),
                        y(TVAR_STREDNI_VYSKA), x(TVAR_STRED), y(TVAR_LEVA_DOLNI_LEVY_BOK_HRONI_LEVA));
        figurka.curveTo(x(TVAR_HORNI_LEVA_MENSI), y(TVAR_STREDNI_VYSKA), x(TVAR_LEVA_DOLNI_LEVY_BOK_HRONI_LEVA),
                        y(TVAR_HORNI_LEVY_VRCHOL), x(TVAR_STRED), y(TVAR_PRAVY_BOK_VYSKA_STRED_HORNI_LEVA_SPONDI));
        figurka.curveTo(x(TVAR_HORNI_LEVA_STRED), y(TVAR_PRAVY_VRCH), x(0.0), y(0.0),
                        x(TVAR_LEVA_DOLNI_LEVY_BOK_HRONI_LEVA), y(TVAR_PRAVY_BOK_VYSKA_STRED_HORNI_LEVA_SPONDI));
        figurka.closePath();
    }

    /**
     * Vykresli korunu krale, umistenou na vrcholu tela figurky.
     */
    private void vykresliKorunuKrale() {
        //Konstanty pro rozmery koruny krale a vnitrni sirka zakladu koruny.
        final double KORUNA_SIRKA_VNITRNI = 0.45;
        //Vnejsi sirka koruny.
        final double KORUNA_SIRKA_VNEJSI = 0.55;
        //Vyska stredu zakladu koruny.
        final double KORUNA_VYSKA_STRED = 0.2;
        //Prvni vodorovna hrana koruny.
        final double KORUNA_VYSKA_HRANA_1 = 0.15;
        //Spicka koruny vpravo.
        final double KORUNA_SIRKA_SPICKA_VPRAVO = 0.575;
        //Druha vodorovna hrana koruny.
        final double KORUNA_VYSKA_HRANA_2 = 0.1;
        //Vrchol spicky koruny.
        final double KORUNA_VYSKA_SPICKA = 0.05;
        final double KORUNA_SIRKA_SPICKA_VLEVO = 0.425;

        figurka.moveTo(x(KORUNA_SIRKA_VNITRNI), y(KORUNA_VYSKA_STRED));
        figurka.lineTo(x(KORUNA_SIRKA_VNEJSI), y(KORUNA_VYSKA_STRED));
        figurka.lineTo(x(KORUNA_SIRKA_VNEJSI), y(KORUNA_VYSKA_HRANA_1));
        figurka.lineTo(x(KORUNA_SIRKA_SPICKA_VPRAVO), y(KORUNA_VYSKA_HRANA_1));
        figurka.lineTo(x(KORUNA_SIRKA_SPICKA_VPRAVO), y(KORUNA_VYSKA_HRANA_2));
        figurka.lineTo(x(KORUNA_SIRKA_VNEJSI), y(KORUNA_VYSKA_HRANA_2));
        figurka.lineTo(x(KORUNA_SIRKA_VNEJSI), y(KORUNA_VYSKA_SPICKA));
        figurka.lineTo(x(KORUNA_SIRKA_VNITRNI), y(KORUNA_VYSKA_SPICKA));
        figurka.lineTo(x(KORUNA_SIRKA_VNITRNI), y(KORUNA_VYSKA_HRANA_2));
        figurka.lineTo(x(KORUNA_SIRKA_SPICKA_VLEVO), y(KORUNA_VYSKA_HRANA_2));
        figurka.lineTo(x(KORUNA_SIRKA_SPICKA_VLEVO), y(KORUNA_VYSKA_HRANA_1));
        figurka.lineTo(x(KORUNA_SIRKA_VNITRNI), y(KORUNA_VYSKA_HRANA_1));
        figurka.closePath();
    }

    /**
     * Pomocna metoda. Spocita x-ovou soradnici bodu na zaklade predaneho nasobku.
     * @param nasobek - Cislo pomahajici urcit x-ovou soradnci
     * @return (double) - X-ovou souradnici.
     */
    private double x(double nasobek) {
        return this.getPoziciX() + okraj + velikostKrl * nasobek;
    }

    /**
     * Pomocna metoda. Spocita y-ovou soradnici bodu na zaklade predaneho nasobku.
     * @param nasobek - Cislo pomahajici urcit y-ovou soradnci
     * @return (double) - Y-ovou souradnici.
     */
    private double y(double nasobek) {
        return this.getPoziciY() + okraj + velikostKrl * nasobek;
    }

    @Override
    public void skalujFigurku() {
        this.velikostKrl = this.velikostCtv * NASOBNOST_VEL_KRL;
        this.okraj = (this.velikostCtv * NASOBNOST_VEL_PROSTORU_KRL) / 2.0;
    }

    @Override
    public void vytvorMozneTahy() {
        Arrays.fill(mozneTahy, null);
        this.setPocitadlo(0);
        //konstanta urcuje max pocet kroku. pro nacitani v metode je treba pricist k maximu + 1 tzn 2 znamena 1 krok.
        final int MAX_POCET_KROKU = 2;
        //po jednom smeru nacteme jednotliva pole
        for (int i = 0; i < POCET_SMERU; i++) {
            int iSmerVektoruRadkoveSlozky = SMERY[i * POCET_SLOZEK_SMER_VEKTORU];
            int iSmerVektoruSloupcoveSlozky = SMERY[(i * POCET_SLOZEK_SMER_VEKTORU) + 1];
            nactiPoleVeSmeru(iSmerVektoruRadkoveSlozky, iSmerVektoruSloupcoveSlozky, MAX_POCET_KROKU);
        }
        //zkontrolujeme rosadu
        if (!this.pohnulSe) {
            nactiPoleDoRosady(this.getS());
        }
    }

    /**
     * Metoda prida pole spoustejici rosadu do moznych tahu krale, pokud se vez a kral nepohli,
     * cesta mezi vezi a kralem volna a kral by nebyl pri pohybu v sachu.
     * @param s - Instance tridy Sachovnice.
     */
    private void nactiPoleDoRosady(Sachovnice s) {
        //ziskame si indexy poli vezi prislusejici krali
        int indexRadku = this.getIndexRadkuPoleF();
        int indexSloupceVezL = s.getHORNI_A_LEVA_HRANICE_SACH();
        int indexSloupceVezR = s.getDOLNI_A_PRAVA_HRANICE_SACH();
        //damska rosada
        int iZacSloupDamRos = indexSloupceVezL + 1;
        int iSloupKralDamRos = indexSloupceVezL + 2;
        int iSloupVezDamRos = indexSloupceVezL + 3;
        //kralovska rosada
        int iZacSloupKralRos = this.getIndexSloupcePoleF() + 1;
        int iSloupKralKralRos = indexSloupceVezR - 1;
        int iSloupVezKralRos = indexSloupceVezR - 2;

        //pokud je vez v pocatecni pozici a nepohla se za celou hru
        if (jsouPodminkyRosadySplneny(s, indexRadku, indexSloupceVezL, iZacSloupDamRos, this.getIndexSloupcePoleF())) {
            this.rosada[0] = s.getHraciPole()[indexRadku][iSloupKralDamRos];
            this.rosada[2] = s.getHraciPole()[indexRadku][iSloupVezDamRos];
            this.indexyVeziRosady[0] = indexRadku;
            this.indexyVeziRosady[1] = indexSloupceVezL;
            this.pridejPoleDoTahu(s.getHraciPole()[indexRadku][iSloupKralDamRos]);
        }
        //pokud je vez v pocatecni pozici a nepohla se za celou hru
        if (jsouPodminkyRosadySplneny(s, indexRadku, indexSloupceVezR, iZacSloupKralRos, indexSloupceVezR)) {
            this.rosada[1] = s.getHraciPole()[indexRadku][iSloupKralKralRos];
            this.rosada[3] = s.getHraciPole()[indexRadku][iSloupVezKralRos];
            this.indexyVeziRosady[2] = indexRadku;
            this.indexyVeziRosady[3] = indexSloupceVezR;
            this.pridejPoleDoTahu(s.getHraciPole()[indexRadku][iSloupKralKralRos]);
        }
    }

    /**
     * Pomocna metoda. Meotda zjsiti jestli jsou podminky pro rosadu splneny, neboli jestli se vez doposud nepohla a
     * jestli je cesta mezi kralem a vezi volna a bez sachu.
     * @param s - Instance tridy Sachovnice.
     * @param iRadkuVeze - Index pocatecni pozice veze.
     * @param iSloupceVeze - Index sloupce pocatecni pozice veze.
     * @param indexZacSloup - Index zacatecniho sloupce v sachovnici (prvni kontrolovane pole je urceno timto indexem).
     * @param indexKonSloup - Index Koncoveho sloupce v sachovici (pole pod timto indexem se nekontroluje).
     * @return True pokud splnuje podminky rosady, jinak false.
     */
    private boolean jsouPodminkyRosadySplneny(Sachovnice s, int iRadkuVeze, int iSloupceVeze, int indexZacSloup,
                                              int indexKonSloup)  {
        //pokud je vez v pocatecni pozici a nepohla se za celou hru
        if (s.getSTAV_HRY()[iRadkuVeze][iSloupceVeze] instanceof Vez v && !v.getPohnulSe()) {
            //pokud je volna cesta mezi vezi a kralem a kral by nebyl pri presun v sachu.
            return jeVolnaCesta(iRadkuVeze, indexZacSloup, indexKonSloup, s);
        }

        return false;
    }

    /**
     * Metoda zjisti jestli je jsou pole mezi index volna a nejsou pod utokem jinych figurek.
     * Projde vsechny pole v radku mezi zacinajicim a koncicim sloupcem.
     * @param indexRadku - Index radeku sachovnice.
     * @param indexZacSloup - Index zacatecniho sloupce v sachovnici (prvni kontrolovane pole je urceno timto indexem).
     * @param indexKonSloup - Index Koncoveho sloupce v sachovici (pole pod timto indexem se nekontroluje).
     * @param s - Instance tridy Sachovnice.
     * @return True pokud jsou pole v ceste rosady volne a nejsou pod utokem nepratelskych figurek.
     */
    private boolean jeVolnaCesta(int indexRadku, int indexZacSloup, int indexKonSloup, Sachovnice s) {
        if (this.getSach()) {
            return false;
        }

        ArrayList<Rectangle2D> cesta = ziskejVolnouCestu(indexRadku, indexZacSloup, indexKonSloup);
        if (cesta == null) { //pokud neni volna cesta
            return false;
        }
        //volnou cestu si prevedeme na pole
        Rectangle2D[] cestaRosady = cesta.toArray(new Rectangle2D[0]);
        //vyfiltrujeme sach jako kdyby to byli mozne tahy krale
        int pocitadlo = this.getPocitadlo();
        this.setPocitadlo(cestaRosady.length);
        s.filtrujSachNaMoznychTazich(cestaRosady, this);
        this.setPocitadlo(pocitadlo);
        //vratime jestli je cesta volna
        return jeCestaBezpecna(cestaRosady, indexZacSloup, indexKonSloup);
    }

    /**
     * Pomocna metoda ktera zjisti jestli jsou vsechna pole mezi kralem a vezi prazdna.
     * Projde vsechny pole v radku od zacatku do konce a prida je do seznamu pokud jsou volna.
     * @param indexRadku - Index radku sachovnice.
     * @param indexZacSloup - Index zacatecniho sloupce (vcetne).
     * @param indexKonSloup - Index koncoveho sloupce (exkluzivne).
     * @return Seznam poli pokud jsou vsechna volna, jinak null.
     */
    private ArrayList<Rectangle2D> ziskejVolnouCestu(int indexRadku, int indexZacSloup, int indexKonSloup) {
        ArrayList<Rectangle2D> cesta = new ArrayList<>();
        // Projdeme vsechna pole mezi zacatkem a koncem (vyjma koncoveho)
        for (int i = indexZacSloup; i < indexKonSloup; i++) {
            // Pokud je na poli figurka, cesta neni volna
            if (this.getS().jeNaPoliFigurka(indexRadku, i)) {
                return null;
            }
            // Pole je volne, pridame do cesty
            cesta.add(this.getS().getHraciPole()[indexRadku][i]);
        }

        return cesta;
    }

    /**
     * Pomocna metoda ktera porovna volnou cestu s validnimi tahy. Pokud se nektere pole z volne cesty nenachazi mezi
     * validnimi tahy krale, neni mozna rosada.
     * @param cestaRosady - Pole volnych poli mezi kralem a vezi.
     * @param indexZacSloup - Index zacatecniho sloupce (pro vypocet relativniho indexu).
     * @param indexKonSloup - Index koncoveho sloupce (exkluzivne).
     * @return True pokud jsou vsechna pole z volne cesty mezi validnimi tahy, jinak false.
     */
    private boolean jeCestaBezpecna(Rectangle2D[] cestaRosady, int indexZacSloup, int indexKonSloup) {
        for (int i = indexZacSloup; i < indexKonSloup; i++) {
            int relativniIndex = i - indexZacSloup;
            if (!cestaRosady[relativniIndex].equals(this.getS().getValidniTahy()[relativniIndex])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Geter k atributu pohnulSe.
     * @return pohnulSe.
     */
    public boolean getPohnulSe() {
        return pohnulSe;
    }

    /**
     * Geter k atributu rosada.
     * @return rosada.
     */
    public Rectangle2D[] getRosada() {
        return rosada;
    }

    /**
     * Geter k atributu indexyVeziRosady.
     * @return indexyVeziRosady.
     */
    public int[] getIndexyVeziRosady() {
        return indexyVeziRosady;
    }

    /**
     * Geter k atributu sach.
     * @return sach.
     */
    public boolean getSach() {
        return sach;
    }

    /**
     * Seter k atributu pohnulSe.
     * @param pohnulSe boolean.
     */
    public void setPohnulSe(boolean pohnulSe) {
        this.pohnulSe = pohnulSe;
    }

    /**
     * Seter k atributu rosada.
     * @param rosada Rectangle2D[].
     */
    public void setRosada(Rectangle2D[] rosada) {
        if (rosada == null) {
            throw new IllegalArgumentException("Predane pole urcujici rosadu je null.");
        }
        this.rosada = rosada;
    }

    /**
     * Seter k atributu indexyVeziRosady.
     * @param indexyVeziRosady int[].
     */
    public void setIndexyVeziRosady(int[] indexyVeziRosady) {
        if (indexyVeziRosady == null) {
            throw new IllegalArgumentException("Predane pole urcujici indexy vezi rosady je null.");
        }
        this.indexyVeziRosady = indexyVeziRosady;
    }

    /**
     * Seter k atributu sach.
     * @param sach boolean.
     */
    public void setSach(boolean sach) {
        this.sach = sach;
    }

    /**
     * Metoda zjisti jestli se kral muze pohnout. Tzn. jestli ma mozny validni tah.
     * @return True pokud ano, jinak false.
     */
    public boolean nemuzeSeKralHybat() {
        //vytvorime krali mozne tahy
        this.vytvorMozneTahy();
        //zvalidujeme tahy na sach
        this.getS().filtrujSachNaMoznychTazich(this.getMozneTahy(), this);
        //projdeme zvalidovane tahy
        for (int i = 0; i < this.getS().getValidniTahy().length; i++) {
            //pokud najdeme aspon jedno volne pole kam muze
            if (this.getS().getValidniTahy()[i] != null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Metoda zjisti jestli se nemuzou hybat figurky krale. Tzn. jestli neexistuje figurka ktera ma mozny validni tah.
     * @return True pokud se nemuzou hybat vsechny figurky krale, jinak false.
     */
    public boolean nemuzouSeHybatFigurkyKrale() {
        for (AFigurka figurka : this.getS().getFIGURKY()) {
            //pokud mame figurku nalezejici krali
            if (figurka != null && this.getS().jsouFigStejnehoTymu(figurka, this)) {
                //vytvorime figruce mozne tahy
                figurka.vytvorMozneTahy();
                //zvalidujeme tahy na sach
                this.getS().filtrujSachNaMoznychTazich(figurka.getMozneTahy(), figurka);
                //projdeme zvalidovane tahy
                if (muzeSeHybnout()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Metoda zjisti jestli se muze figurka krale hybnout. Tzn. jestli ma mozny validni tah.
     * @return True pokud ano, jinak false.
     */
    private boolean muzeSeHybnout() {
        for (int k = 0; k < this.getS().getValidniTahy().length; k++) {
            //pokud najdeme aspon jedno volne pole kam muze
            if (this.getS().getValidniTahy()[k] != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Geter k atributu udelalRosadu.
     * @return udelalRosadu.
     */
    public boolean getUdelalRosadu() {
        return udelalRosadu;
    }

    /**
     * Seter k atributu udelalRosadu.
     * @param udelalRosadu boolean.
     */
    public void setUdelalRosadu(boolean udelalRosadu) {
        this.udelalRosadu = udelalRosadu;
    }

    /**
     * Metoda ziska veze krale pro rosadu. Pokud se veze nepohli z pocatecnich pozic a nebyli vyhozeny.
     * @return Pole instanci vezi, kdy druhy prvek urcuje vez pro damskou rosadu
     * (null znaci ze vez se pohla nebo byla vyhozena) a prvni pro kralovskou.
     */
    public Vez[] ziskejVezeKraleProRosadu() {
        final int POCET_VEZI_ROSADY = 2;
        Vez[] veze = new Vez[POCET_VEZI_ROSADY];
        for (AFigurka figurka : this.getS().getFIGURKY()) {
            if (figurka instanceof Vez v && v.getTym().equals(this.getTym())) {
                if (!v.getPohnulSe()) {
                    veze = pridejVezPokudUmoznujerosadu(v, veze);
                }
            }
        }
        return veze;
    }

    /**
     * Pomocna metoda. Metoda do predaneho pole veze vlozi dle typu rosady.
     * @param testovanaVez - Predana vez.
     * @param veze - Predane pole vezi.
     * @return Vez[] -> Pole vezi ktere umoznuji s kralem rosadu. Prvni prvek odpovida kralovske a druhy damske.
     */
    private Vez[] pridejVezPokudUmoznujerosadu(Vez testovanaVez, Vez[] veze) {
        Vez[] novePoleVezi = Arrays.copyOf(veze, veze.length);
        if (testovanaVez.getIndexSloupcePoleF() == this.getS().getHORNI_A_LEVA_HRANICE_SACH()) { //jedna se o vez pro damskou rosadu
            novePoleVezi[1] = testovanaVez;
        } else if (testovanaVez.getIndexSloupcePoleF() == this.getS().getDOLNI_A_PRAVA_HRANICE_SACH()) { //jedna se o vez pro kralovskou rosadu
            novePoleVezi[0] = testovanaVez;
        }

        return novePoleVezi;
    }

    /**
     * Metoda nastavi vezim atribut pohnuti na false, pokud se vez pred prvnim pohybem krale pohla ze zacatecni pozice.
     */
    public void nastavVezimPohnuti() {
        //pokud se vez na kralovske strane pred pohybem krale nepohla ze zacatecni pozice
        if (stavVezi[0]) {
            ((Vez)this.getS().getSTAV_HRY()[ziskejVychoziRadekVezeKrale()][getS().getDOLNI_A_PRAVA_HRANICE_SACH()]).setPohnulSe(false);
        }
        //pokud se vez na damske strane pred pohybem krale nepohla ze zacatecni pozice
        if (stavVezi[1]) {
            ((Vez)this.getS().getSTAV_HRY()[ziskejVychoziRadekVezeKrale()][getS().getHORNI_A_LEVA_HRANICE_SACH()]).setPohnulSe(false);
        }
    }

    /**
     * Metoda ziska inicializacni radek pro danou vez.
     * @return Dolni hranici sachovnice pokud je tymu 1, jinak horni.
     */
    private int ziskejVychoziRadekVezeKrale() {
        if (this.getTym() == this.getS().getTym1()) {
            return this.getS().getDOLNI_A_PRAVA_HRANICE_SACH();
        } else {
            return this.getS().getHORNI_A_LEVA_HRANICE_SACH();
        }
    }

    /**
     * Seter k atributu stavVezi.
     * @param stavVezi - Pole obsahujici boolean hodnoty pro urceni jestli se vez uz vehre pohla pri prvnim pohybu
     *                 krale.
     * @throws IllegalArgumentException - Pokud je predano null pole.
     */
    public void setStavVezi(boolean[] stavVezi) {
        if (stavVezi == null) {
            throw new IllegalArgumentException("Pole stavu vezi pri pohybu krale bylo predano jako null.");
        }

        this.stavVezi = stavVezi;
    }

    /**
     * Pomocna metoda zjisti jestli sousedni pole pole na indexech R a S obsahuji nepratelskeho krale
     * @param indexR - Index urucujici radek sachovnice.
     * @param indexS - Index urucujici sloupec sachovnice.
     * @return True pokud ano, jinak false.
     */
    protected boolean obsahujeSousedniPoleNaNepratelskehoKrale(int indexR, int indexS) {
        //konstanta urcuje max pocet kroku. pro nacitani v metode je treba pricist k maximu + 1 tzn 2 znamena 1 krok.
        final int POCET_SLOZEK_SMER_VEKTORU = 2;
        //po jednom smeru nacteme jednotliva pole
        for (int i = 0; i < POCET_SMERU; i++) {
            int iSmerVektoruRadkoveSlozky = SMERY[i * POCET_SLOZEK_SMER_VEKTORU];
            int iSmerVektoruSloupcoveSlozky = SMERY[(i * POCET_SLOZEK_SMER_VEKTORU) + 1];
            if (obsahujePoleKrale(iSmerVektoruRadkoveSlozky, iSmerVektoruSloupcoveSlozky, indexR, indexS)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Pomocna metoda pomoci smeru ziska sousedni pole pole (indexR,indexS) a zjisti jestli obsahuje nepratelskeho
     * krale.
     * @param iSmerVektoruRadkoveSlozky - Index urcujici smer posunu v radkove slozce.
     * @param iSmerVektoruSloupcoveSlozky - Index urucujici smer posun v sloupcove slozce.
     * @param indexR - Index urucujici radek sachovnice.
     * @param indexS - Index urucujici sloupec sachovnice.
     * @return True pokud obsahuje, jinak false.
     */
    private boolean obsahujePoleKrale(int iSmerVektoruRadkoveSlozky, int iSmerVektoruSloupcoveSlozky, int indexR,
                                      int indexS) {
        int iRadku = indexR + iSmerVektoruRadkoveSlozky;
        int iSloupce = indexS + iSmerVektoruSloupcoveSlozky;
        if (this.getS().nejsouIndexyVSachovnici(iRadku, iSloupce)) {
            return false;
        }

        AFigurka f = this.getS().getSTAV_HRY()[iRadku][iSloupce];
        return f instanceof Kral && !this.getS().jsouFigStejnehoTymu(f, this);
    }
}
