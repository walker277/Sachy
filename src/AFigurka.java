import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * Abstraktni trida poskytujici sablonu pro jednotlive figurky.
 * @author Filip Valtr
 */
public abstract class AFigurka {
    //== Konstantni tridni atributy instanci
    /** Urcuje pocatecni pozici radku pescu 1 hrace. */
    protected static final int POCATECNI_POZICE_RADKU_PES_TYM_1 = 6;
    /** Urcuje pocatecni pozici radku pescu 2 hrace. */
    protected static final int POCATECNI_POZICE_RADKU_PES_TYM_2 = 1;
    /** Urcuje maximalni pocet tahu figurky. */
    private static final int MAX_POCET_TAHU = 63;
    //== Konstantni atributy instanci
    /** odkaz na sachovnici pro kontrolu hranici */
    private final Sachovnice S;
    //== Atributy instanci
    /** Index radku pole ve kterem se figurka nachazi. */
    private int indexRadkuPoleF;
    /** Index sloupce pole ve kterem se figurka nachazi. */
    private int indexSloupcePoleF;
    /** X-ova pozice pesce. */
    protected double poziceX;
    /** Y-ova pozice pesce. */
    protected double poziceY;
    /** Velikost jednoho pole. */
    protected double velikostCtv;
    /** Barva urcujici tym. */
    private final Color tym;
    /** Telo figurky. */
    protected Path2D figurka;
    /** Atribut pro ulozeni moznych tahu figurky. */
    protected Rectangle2D[] mozneTahy = new Rectangle2D[MAX_POCET_TAHU];
    /** Atribut pro pridavani poli do pole. */
    protected int pocitadlo = 0;

    //==Konstruktory
    /**
     * Konstruktor, nastavi klicove atributy jako jsou indexy pole figurky ve kterem se nachazi, velikost pole,
     * barvu tymu.
     *
     * @param iRadkuPoleF   - Index radku pole figruky.
     * @param jSloupcePoleF - Index sloupce pole figurky.
     * @param velikostCtv   - Velikost jednoho herniho pole (ctverce).
     * @param tym           - Barva tymu ktereho je figurka soucasti.
     * @param s             - Instance tridy Sachovnice.
     * @throws IllegalArgumentException - Pokud predana sachovnice je null.
     */
    public AFigurka(int iRadkuPoleF, int jSloupcePoleF, double velikostCtv, Color tym, Sachovnice s) {
        if (s == null) {
            throw new IllegalArgumentException("Predana null sachovnice.");
        }

        zkontrolujZbyvajiciParametryKonstruktoru(iRadkuPoleF, jSloupcePoleF, velikostCtv, tym, s);

        this.indexRadkuPoleF = iRadkuPoleF;
        this.indexSloupcePoleF = jSloupcePoleF;
        this.velikostCtv = velikostCtv;
        this.tym = tym;
        this.S = s;
    }

    /**
     * Metoda zjisti jestli figurka obsahuje predany bod.
     *
     * @param x - X-ova souradnice bodu.
     * @param y - Y-ova souradnice bodu.
     * @return True pokud se bod nachazi ve figurce, jinak false.
     * @throws IllegalArgumentException Pokud telo figurky neni incializovano, je predana souradnice jako NaN nebo je
     *                                  souradnice mimo vykreslovaci okno.
     */
    public boolean jeFVybrana(double x, double y) {
        if (this.figurka == null) {
            throw new IllegalArgumentException("Telo figurky neni inicializovano.");
        }

        if (!jeBodValidni(x, y)) {
            throw new IllegalArgumentException("Neplatne souradnice.");
        }

        return this.figurka.contains(x, y);
    }

    /**
     * Metoda nastavi figurce pozici dle hodonot atributu pro definovani indexu radku a sloupce.
     */
    public void vraFDoPuvodnihoStavu() {
        this.setPoziceX(this.getS().getHraciPole()[this.getIndexRadkuPoleF()][this.getIndexSloupcePoleF()].getX());
        this.setPoziceY(this.getS().getHraciPole()[this.getIndexRadkuPoleF()][this.getIndexSloupcePoleF()].getY());
    }

    /**
     * Metoda vytvori telo figurky a nastavi tim atribut prislusne figurky.
     */
    public abstract void vytvorT();

    /**
     * Metoda skaluje rozmery figurky.
     */
    public abstract void skalujFigurku();

    /**
     * Geter k atributu indexRadkuPoleF.
     *
     * @return indexRadkuPoleF
     */
    public int getIndexRadkuPoleF() {
        return this.indexRadkuPoleF;
    }

    /**
     * Geter k atributu indexSloupcePoleF.
     *
     * @return indexSloupcePoleF
     */
    public int getIndexSloupcePoleF() {
        return this.indexSloupcePoleF;
    }

    /**
     * Geter k atributu tym.
     *
     * @return Barvu tymu ke kteremu figurka patri.
     */
    public Color getTym() {
        return this.tym;
    }

    /**
     * Geter k atributu poziceX.
     *
     * @return X-ova souradnice leveho horniho bodu hraciho pole, ve kterem se nachazi figurka.
     */
    public double getPoziciX() {
        return this.poziceX;
    }

    /**
     * Geter k atributu poziceY.
     *
     * @return Y-ova souradnice leveho horniho bodu hraciho pole, ve kterem se nachazi figurka.
     */
    public double getPoziciY() {
        return this.poziceY;
    }

    /**
     * Geter k atributu mozneTahy.
     *
     * @return Pole rectanglu2D reprezentujici mozne tahy pro danou figurku.
     */
    public Rectangle2D[] getMozneTahy() {
        return this.mozneTahy;
    }

    /**
     * Geter k atributu pocitadlo.
     *
     * @return pocitadlo.
     */
    public int getPocitadlo() {
        return this.pocitadlo;
    }

    /**
     * Geter k atributu s
     *
     * @return Instance tridy Sachovnise.
     */
    public Sachovnice getS() {
        return this.S;
    }

    /**
     * Nastavi atribut velikostCtv.
     *
     * @param velikost - Velikost hraciho pole.
     * @throws IllegalArgumentException Pokud je predana velikost mensi nez nula, nekonecno nebo neni cislo.
     */
    public void setVelikostCtv(double velikost) {
        if (Double.isNaN(velikost) || Double.isInfinite(velikost) || velikost <= 0) {
            throw new IllegalArgumentException("Neplatna velikost.");
        }

        this.velikostCtv = velikost;
    }

    /**
     * Seter k atributu pocitadlo.
     *
     * @param hodnota - int.
     * @throws IllegalArgumentException Pokud je hodnota mensi nez nula nebo vetsi velikost pole moznych tahu
     *                                  (zaporne indexy a vetsi nez velikost).
     */
    public void setPocitadlo(int hodnota) {
        if (hodnota < 0 || hodnota >= MAX_POCET_TAHU) {
            throw new IllegalArgumentException("Neplatne nastaveni ukazatele pocitadlo v poli.");
        }

        this.pocitadlo = hodnota;
    }

    /**
     * Seter k atributu poziceX.
     *
     * @param x - X-ova souradnice, na kterou se ma figurka presunout.
     * @throws IllegalArgumentException Pokud je souradnice NaN nebo neni v okne.
     */
    public void setPoziceX(double x) {
        if (Double.isNaN(x) || neniXSouradniceVOkne(x)) {
            throw new IllegalArgumentException("Neplatne souradnice.");
        }

        this.poziceX = x;
    }

    /**
     * Seter k atributu poziceY.
     *
     * @param y - Y-ova souradnice, na kterou se ma figurka presunout.
     * @throws IllegalArgumentException Pokud je souradnice NaN nebo neni v okne.
     */
    public void setPoziceY(double y) {
        if (Double.isNaN(y) || neniYSouradniceVOkne(y)) {
            throw new IllegalArgumentException("Neplatne souradnice.");
        }

        this.poziceY = y;
    }

    /**
     * Seter k atributu index radku pole figurky.
     *
     * @param i - Index radku pole figurky.
     * @throws IllegalArgumentException Pokud je index radku pole figurky mimo hranice sachovnice.
     */
    public void setIndexRadkuPoleF(int i) {
        if (!this.getS().jeIRadkuVSachovnici(i)) {
            throw new IllegalArgumentException("Predany index radku je mimo hranice sachovnice.");
        }

        this.indexRadkuPoleF = i;
    }

    /**
     * Seter k atributu index sloupce pole figurky.
     *
     * @param j - Index sloupce figurky.
     * @throws IllegalArgumentException Pokud je predany index sloupce mimo hranice sachovnice.
     */
    public void setIndexSloupcePoleF(int j) {
        if (!this.getS().jeISloupVSachovnici(j)) {
            throw new IllegalArgumentException("Predany index sloupce je mimo hranice sachovnice.");
        }

        this.indexSloupcePoleF = j;
    }

    /**
     * Metoda prida hraci pole do moznych tahu figurky a navysi ukazovatel na dalsi volne misto v poli.
     *
     * @param pole - Intstance Rectangle2D reprezentujici herni pole.
     * @throws IllegalArgumentException Pokud je predano null pole.
     * @throws IllegalStateException    Pokud je pridano pole do plneho pole.
     */
    public void pridejPoleDoTahu(Rectangle2D pole) {
        if (pole == null) {
            throw new IllegalArgumentException("Bylo predano null pole.");
        }

        if (this.getPocitadlo() >= MAX_POCET_TAHU) {
            throw new IllegalStateException("Pridavani prvku do plneho pole.");
        }

        this.mozneTahy[this.getPocitadlo()] = pole;
        this.pocitadlo++;
    }

    /**
     * Metoda vytvory mozne tahy figurce. Tahy jsou pouze zakladni forma pohybu tzn. nejsou validovane sachem. U krale
     * testuje i rosadu. U pescu testuje je i brani mimochodem.
     */
    public abstract void vytvorMozneTahy();

    /**
     * Metoda vytvori telo figurky a nasledne ji vykresli na platno.
     *
     * @param g2 - Instance tridy Graphics2D.
     * @throws IllegalArgumentException Pokud je g2 null.
     */
    public void vykresliFigurku(Graphics2D g2) {
        if (g2 == null) {
            throw new IllegalArgumentException("Predana instance graphics2D je null.");
        }

        this.vytvorT();
        g2.setColor(this.tym);
        g2.fill(this.figurka);
    }

    /**
     * Metoda nacte v urcitem smeru pole a ty prida do moznych tahu. Bere v potaz i ostatni figurky ve hre.
     *
     * @param smerVRadku    - Cislo udavajici smer v radku.
     * @param smerVSloup    - cislo udavajici smer v sloupci.
     * @param maxPocetKroku - Cislo definujici maximalni pocet poli, ktery se ma nacist (pro presnou definici je treba
     *                      pricist 1, neboli pokud chceme nacist 2 pole pak predame 3 jako parametr).
     * @throws IllegalArgumentException Pokud chybny smerovy vektor nebo maximalni pocet krokru, ktery jasne presahne
     *                                  hranice sachovnice.
     */
    protected void nactiPoleVeSmeru(int smerVRadku, int smerVSloup, int maxPocetKroku) {
        if (!jeSemrovyVektorValidni(smerVRadku, smerVSloup)) {
            throw new IllegalArgumentException("Byl zadan chybny smer (smer je pouze -1 nebo 1).");
        }

        if (maxPocetKroku > this.getS().getRadky()) {
            throw new IllegalArgumentException("Zadan parametr pro nacitani kroku pres hranice sachovnice");
        }

        projdiPoleANactiJe(smerVRadku, smerVSloup, maxPocetKroku);
    }

    /**
     * Metoda vrati krale prislusne figurky.
     *
     * @param figurky - Pole figurek.
     * @return - Instanci tridy Krale, jinak null.
     * @throws IllegalArgumentException Pokud predane pole figurek je null.
     * @throws IllegalStateException    Mezi figurkami neni kral.
     */
    public Kral getKral(AFigurka[] figurky) {
        if (figurky == null) {
            throw new IllegalArgumentException("Predane pole figurek je null");
        }


        for (AFigurka aFigurka : figurky) {
            if (aFigurka instanceof Kral k && k.getTym().equals(this.getTym())) {
                return k;
            }
        }

        throw new IllegalStateException("Mezi figurakmi neni kral prislusne figurky");
    }

    /**
     * Metoda vrati popis hrace na zaklade figurky.
     *
     * @return Popis hrace.
     */
    public String getPopis() {
        if (this.getTym().equals(this.getS().getTym1())) {
            return "HRÁČ 1";
        } else {
            return "HRÁČ 2";
        }
    }

    /**
     * Pomocna metoda. Zjisti z predanych indexu pole, jestli je pole zakladnim pohybem figurky.
     * V pripade ze se na poli nachazi figurka ciziho tymu, tak pole prida rovnou do moznych tahu.
     *
     * @param indexR - Cislo reprezentujici index radku pole (mozny tah) ktere se kontroluje.
     * @param indexS - Cislo reprezentujici index sloupce pole (mozny tah) ktere se kontroluje.
     * @return True pokud je pole zakladnim pohybem figurky, jinak false.
     */
    private boolean jePoleZakladniKrokFigurky(int indexR, int indexS) {

        //zjistime jestli je na poli figurka
        if (this.getS().jeNaPoliFigurka(indexR, indexS)) {
            AFigurka figurka = this.getS().getSTAV_HRY()[indexR][indexS];
            if (!this.getS().jsouFigStejnehoTymu(this, figurka)) {//je to figurka soupere
                //pridame pole do tahu
                pridejPoleDoTahu(this.getS().getHraciPole()[indexR][indexS]);
            }
            //dalsi tahy nebudeme nacitat
            return false;
        } else { //pokud neni figurka
            //kralove nesmi byt vedle sebe
            if (this instanceof Kral k) {
                return !k.obsahujeSousedniPoleNaNepratelskehoKrale(indexR, indexS);
            }
            return true;
        }
    }

    /**
     * Metoda zjisti jestli je predany bod validni, neboli ze je cislo a je v okne.
     *
     * @param x - X-ova souradnice.
     * @param y - Y-ova souradnice.
     * @return True pokud je bod (x,y) v okne a je cislo, jinak false.
     */
    private boolean jeBodValidni(double x, double y) {
        return !Double.isNaN(x) && !Double.isNaN(y) && !neniXSouradniceVOkne(x) && !neniYSouradniceVOkne(y);
    }

    /**
     * Pomocna metoda pro validaci x-ove souradnice jestli se nachazi v okne.
     *
     * @param souradnice - Predana souradnice.
     * @return True pokud souradnice neni v sirce okna na x-ove ose, jinak false.
     */
    private boolean neniXSouradniceVOkne(double souradnice) {
        return (souradnice < 0.0) || (souradnice > this.getS().getSirka());
    }

    /**
     * Pomocna metoda pro validaci y-ove souradnice jestli se nachazi v okne.
     *
     * @param souradnice - Predana souradnice.
     * @return True pokud souradnice neni ve vysce okna na y-ove ose, jinak false.
     */
    private boolean neniYSouradniceVOkne(double souradnice) {
        return (souradnice < 0.0) || (souradnice > this.getS().getVyska());
    }

    /**
     * Pomocna metoda pro otestovani smeru ve kterem se maji nacitat pohyby figurky.
     *
     * @param smerVRadku - Cislo urcujici smer ve radku
     * @param smerVSloup - Cislo urcujici smer ve sloupci
     * @return True pokud smerVRadku a smerVSloupci nabyva pouze hodnot -1, 1 nebo 0 a zaroven neni 0,0 vektorem.
     */
    private boolean jeSemrovyVektorValidni(int smerVRadku, int smerVSloup) {
        boolean validniHodnoty = jsouValidniHodnoty(smerVRadku, smerVSloup);

        boolean neniNulovyVektor = !(smerVRadku == 0 && smerVSloup == 0);

        return validniHodnoty && neniNulovyVektor;
    }

    /**
     * Pomocna metoda pro otestovani hodnot smeru.
     *
     * @param smerVRadku - Cislo urcujici smer ve radku.
     * @param smerVSloup - Cislo urcujici smer ve sloupci.
     * @return True pokud smerVRadku a smerVSloupci nabyva pouze hodnot -1, 1 nebo 0.
     */
    private boolean jsouValidniHodnoty(int smerVRadku, int smerVSloup) {
        return (smerVRadku >= -1 && smerVRadku <= 1) && (smerVSloup >= -1 && smerVSloup <= 1);
    }

    /**
     * Pomocna metoda pro ziskani pohybu figurky. Metoda zjisti pole z predanych indexu a zvaliduje jestli je zakladnim
     * pohybem figurky.
     *
     * @param indexR - Index radku pole na sachovnici.
     * @param indexS - Index sloupce pole na sachovnici.
     * @return True pokud je pole zakladnim krokem figurky. False pokud jsme se dostali mimo hranice sachovnice
     * popripade pole neni zakladnim krokem figurky.
     */
    private boolean nactiKonkretniPoleVeSmeru(int indexR, int indexS) {
        //zkontrolujeme jestli indexy jsou validni
        if (this.getS().nejsouIndexyVSachovnici(indexR, indexS)) {
            return false;
        }
        //pokud je pole soucasti normalni pohybu figurky
        if (!jePoleZakladniKrokFigurky(indexR, indexS)) {
            return false;
        }

        pridejPoleDoTahu(this.getS().getHraciPole()[indexR][indexS]);
        return true;
    }

    /**
     * Pomocna metoda pro ziskani pohybu figurky. Metoda nacte figurce v urcitem smeru na sachovnici predany pocet poli,
     * paklize jsou jejim zakladnim pohybem.
     *
     * @param smerVRadku    - Cislo urcujici smer ve radku.
     * @param smerVSloup    - Cislo urcujici smer ve sloupci.
     * @param maxPocetKroku - Cislo definujici maximalni pocet poli, ktery se ma nacist (pro presnou definici je treba
     *                      pricist 1, neboli pokud chceme nacist 2 pole pak predame 3 jako parametr).
     */
    private void projdiPoleANactiJe(int smerVRadku, int smerVSloup, int maxPocetKroku) {
        int indexR;
        int indexS;
        //jdeme pres maximalni mozny pocet poli
        for (int i = 1; i < maxPocetKroku; i++) {
            //upravime index ve smeru
            indexR = this.getIndexRadkuPoleF() + (smerVRadku * i);
            indexS = this.getIndexSloupcePoleF() + (smerVSloup * i);
            boolean lzeNacistKonkretniPole = nactiKonkretniPoleVeSmeru(indexR, indexS);
            if (!lzeNacistKonkretniPole) {
                break;
            }
        }
    }

    /**
     * Pomocna metoda pro otestovani parametru konstruktoru ktere zavisi na parametru s.
     *
     * @param iRadkuPoleF   - Index radku pole figruky.
     * @param jSloupcePoleF - Index sloupce pole figurky.
     * @param velikostCtv   - Velikost jednoho herniho pole (ctverce).
     * @param tym           - Barva tymu ktereho je figurka soucasti.
     * @param s             - Instance tridy Sachovnice.
     * @throws IllegalArgumentException Pokud jsou indexy mimo hranice sachovnice, barva neni validni nebo velikost pole
     * neni validni.
     */
    private void zkontrolujZbyvajiciParametryKonstruktoru(int iRadkuPoleF, int jSloupcePoleF, double velikostCtv,
                                                          Color tym, Sachovnice s) {

        if (s.neniSachovniceValidni()) {
            throw new IllegalArgumentException("Doslo k vnitrnim nesrovnalostem v atributu sachovnice.");
        }

        if (s.nejsouIndexyVSachovnici(iRadkuPoleF, jSloupcePoleF)) {
            throw new IllegalArgumentException("Predane indexy jsou mimo hranice sachovnice.");
        }

        if(this.neniBarvaValidni(tym, s)) {
            throw new IllegalArgumentException("Predana barva neni ani jednoho hrace.");
        }

        if (neodpovidaVelikostPoli(velikostCtv, s)) {
            throw new IllegalArgumentException("Velikost predaneho pole neodpovida velikosti pole na sachovnici.");
        }
    }

    /**
     * Pomocna metoda. Zjisti jestli velikostCtv odpovida skutecne velikosti pole na sachovnici.
     * @param velikostCtv - Velikost jednoho herniho pole (ctverce).
     * @param s           - Instance tridy Sachovnice.
     * @return True pokud neopovida, jinak false.
     */
    private boolean neodpovidaVelikostPoli(double velikostCtv, Sachovnice s) {
        final double  EPSILON = 0.0001;

        return Math.abs(velikostCtv - s.getHraciPole()[0][0].getHeight()) > EPSILON;
    }

    /**
     * Metoda zkontroluje jestli pole moznych tahu obsahuje pole na kterem se nachazi nepratelsky kral.
     * @param tahy - Pole obsahujici mozne tahy.
     * @return Instance krale, pokud je v poli tahu pole, ktere nalezi krali, jinak null.
     */
    public Kral dostalaFigurkaKraleDoSachu(Rectangle2D[] tahy) {
        if (tahy == null) {
            throw new IllegalArgumentException("Predane tahy jsou null.");
        }

        for (Rectangle2D tah : tahy) {
            Kral k = obsahujeTahKrale(tah);
            if (k != null) {
                return k;
            }
        }

        return null;
    }

    /**
     * Pomocna metoda. Zjisti jestli predane pole obsahuje instanci krale.
     * @param tah - Predane pole.
     * @return Instanci krale pokud ano, jinak null.
     */
    private Kral obsahujeTahKrale(Rectangle2D tah) {
        int[] indexyPole;
        AFigurka f;

        if (tah != null) {
            indexyPole = this.getS().getIndexyPole(tah.getX(), tah.getY());
            f = this.getS().getSTAV_HRY()[indexyPole[0]][indexyPole[1]];
            if (f instanceof Kral k) { //pokud je tam kral
                return k;
            }
        }

        return null;
    }

    /**
     * Metoda najde nepretelskeho krale k figurce.
     * @param figurky - Pole instanci figurek na sachovnici.
     * @return Instance krale, jinak null.
     */
    public Kral najdiNepratelskehoKrale(AFigurka[] figurky) {
        if (figurky == null) {
            throw new IllegalArgumentException("Predane pole figurek je null.");
        }

        for (AFigurka f : figurky) {
            //pokud mame nepratelskou figurku
            if (jeFigurkaAJeNepratelska(f) && f instanceof Kral k) {
                return k;
            }
        }

        throw new IllegalStateException("Kral se nenachazi v poli figurek.");
    }

    /**
     * Pomocna metoda. Zjisti jestli je predana instance figurka a ze je nepratelska.
     * @param f - Instance AFigurka.
     * @return True pokud ano, jinak false.
     */
    public boolean jeFigurkaAJeNepratelska(AFigurka f) {
        return f != null && !this.getS().jsouFigStejnehoTymu(f, this);
    }

    /**
     * Metoda zjisti jestli predana figurka dostala seveho krale svym tahem do sachu.
     * @return True pokud ano, jinak false.
     */
    public boolean dostalaTahemKraleDoSachu() {
        AFigurka figurka;
        //PRO KAZDOU NEPRATELSKOU FIGURKU
        for (int i = 0; i < this.getS().getFIGURKY().length; i++) {
            figurka = this.getS().getFIGURKY()[i];
            // pokud se nam podarilo ziskat figurku hrace druheho tymu ktera neni kralem (kral nemuze z principu
            // dat sach)
            if (jeNepratelskaAneniKral(figurka)) {
                //vytvorime ji mozne tahy
                figurka.vytvorMozneTahy();
                int[] indexyPole;
                AFigurka f;
                //projdeme mozne tahy nepratelske figurky
                for (int k = 0; k < figurka.getPocitadlo(); k++) {
                    //ziskame indexy pole mozneho tahu nepratelske figurky
                    indexyPole = this.getS().getIndexyPole(figurka.getMozneTahy()[k].getX(), figurka.getMozneTahy()[k].getY());
                    //ziskame stav na tomto poli
                    f = this.getS().getSTAV_HRY()[indexyPole[0]][indexyPole[1]];
                    if (f instanceof Kral) { //pokud je tam nepratelsky kral
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Pomocna metoda. Zjisti jestli je figurka je neprateslka a jestli neni kral.
     * @param figurka - Instance predane figurky.
     * @return True pokud ano, jinak fasle.
     */
    private boolean jeNepratelskaAneniKral(AFigurka figurka) {
        return jeFigurkaAJeNepratelska(figurka) && !(figurka instanceof Kral);
    }

    /**
     * Pomocna metoda. Zjisti jestli predana barva neni validni tzn. nepatri ani jednomu hraci.
     * @param barva - Instance tridy Color.
     * @return True pokud neni, jinak fasle.
     */
    private boolean neniBarvaValidni(Color barva, Sachovnice s) {
        return !barva.equals(s.getTym1()) && !barva.equals(s.getTym2());
    }
}


