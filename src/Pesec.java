import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * Trida slouzi pro vytvareni instanci pescu. Dedi od abstraktni tridy AFigurka.
 * @author Filip Valtr
 */
public class Pesec extends AFigurka {
    //== Konstantni tridny atributy instanci.
    /** Konstanta ktera se pouziva pro vypocet velikosti pesce. */
    private static final double NASOBNOST_VEL_PES = 0.6;
    /** Konstanta ktera se pouziva pro vypocet velikosti prostoru mimo pesce v poli. */
    private static final double NASOBNOST_VEL_PROSTORU_PES = 0.4;
    /** Urcuje maximalni mozny pocet pohybu v smeru, neboli rovne, sikmo vlevo a sikmo vpravo. */
    private static final int MAX_POCET_POHYBU = 3;
    //== Atributy instanci
    /** Velikost sirky a vysky pesce. */
    private double velikostPes;
    /** Udava velikost volneho mista v poli okolo pesce. Pomaha pesce vycentrovat doprostred hraciho pole. */
    private double okraj;
    /** Atribut sedlujici, jestli pesec muze byt sebran technikou mimochodem. */
    private boolean braniMimochodem = false;
    /** Atribut definuje pole, ktere vyhazuje pesce nachazejiciho se za timto polem pri brani mimochodem. */
    private Rectangle2D poleBraniMimochodem;
    /** Atribut predstavuje index urcujici pole, na kterem se nachazi pesec, ktery se odstranuje pri brani mimochodem. */
    private int[] indexyOdstranovanehoPesce = new int[2];
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
    public Pesec(int iRadkuPoleF, int jSloupcePoleF, double velikostCtv, Color tym, Sachovnice s) {
        super(iRadkuPoleF, jSloupcePoleF, velikostCtv, tym, s);
        this.velikostPes = velikostCtv * NASOBNOST_VEL_PES;
        this.okraj = (velikostCtv * NASOBNOST_VEL_PROSTORU_PES) / 2.0;
    }

    @Override
    public void vytvorT() {
        //Vyska stredu hrudniku pesce.
        final double TVAR_HRUDNIK = 0.75;
        //Sirka v oblasti boku.
        final double TVAR_BOK = 0.6;
        //Spodni cast tela dolni bok pesce.
        final double TVAR_SPODNI_CAST = 0.4;
        //Horni cast hlavy pesce celo.
        final double TVAR_VRSEK = 0.8;
        //Leva strana hlavy pesce vlevo nahore.
        final double TVAR_LEVY_OKRAJ = 0.2;
        //Sirka horniho trupu pesce prechod mezi rameny a hlavou.
        final double TVAR_TRUP_SIRKA = 0.7;
        //Vrchol pesce spicka hlavy.
        final double TVAR_SPICKA = 0.0;
        //Prava spodni hrana pesce bok smerem dolu a pomer pro levou spodni cast pesce leva noha.
        final double TVAR_SPODNI_BOK_LEVA_NOHA = 0.3;
        //vytvoreni pesce
        this.figurka = new Path2D.Double();
        figurka.moveTo(x(0.0), y(1.0));
        figurka.lineTo(x(1.0), y(1.0));
        figurka.curveTo(x(1.0), y(1.0), x(1.0), y(TVAR_HRUDNIK), x(TVAR_TRUP_SIRKA),
                        y(TVAR_BOK));
        figurka.curveTo(x(TVAR_TRUP_SIRKA), y(TVAR_BOK), x(1.0), y(TVAR_SPODNI_CAST), x(TVAR_BOK),
                        y(TVAR_SPODNI_BOK_LEVA_NOHA));
        figurka.curveTo(x(TVAR_VRSEK), y(TVAR_SPICKA), x(TVAR_LEVY_OKRAJ), y(TVAR_SPICKA), x(TVAR_SPODNI_CAST),
                        y(TVAR_SPODNI_BOK_LEVA_NOHA));
        figurka.curveTo(x(TVAR_SPODNI_CAST), y(TVAR_SPODNI_BOK_LEVA_NOHA), x(0.0), y(TVAR_SPODNI_CAST),
                        x(TVAR_SPODNI_BOK_LEVA_NOHA), y(TVAR_BOK));
        figurka.curveTo(x(TVAR_SPODNI_BOK_LEVA_NOHA), y(TVAR_BOK), x(0.0), y(TVAR_HRUDNIK), x(0.0),
                        y(1.0));
        figurka.closePath();
    }

    /**
     * Pomocna metoda. Spocita x-ovou soradnici bodu na zaklade predaneho nasobku.
     * @param nasobek - Cislo pomahajici urcit x-ovou soradnci
     * @return (double) - X-ovou souradnici.
     */
    private double x(double nasobek) {
        return this.getPoziciX() + okraj + velikostPes * nasobek;
    }

    /**
     * Pomocna metoda. Spocita y-ovou soradnici bodu na zaklade predaneho nasobku.
     * @param nasobek - Cislo pomahajici urcit y-ovou soradnci
     * @return (double) - Y-ovou souradnici.
     */
    private double y(double nasobek) {
        return this.getPoziciY() + okraj + velikostPes * nasobek;
    }

    @Override
    public void skalujFigurku() {
        this.velikostPes = this.velikostCtv * NASOBNOST_VEL_PES;
        this.okraj = (this.velikostCtv * NASOBNOST_VEL_PROSTORU_PES) / 2.0;
    }

    @Override
    public void vytvorMozneTahy() {
        //vynulujeme dosavadni tahy
        Arrays.fill(mozneTahy, null);
        this.setPocitadlo(0);
        //zjistime jakym smerem se pesec pohybuje
        int smer = (this.getTym().equals(this.getS().getTym1())) ? -1 : 1;
        //zjistime pohyby pesce
        int[] pohyby = nastavMoznePohybyPesce(this.getS(), this.getS().getTym1());
        //nacteme primy pohyb pesce
        pridejPoleDoTahuPesVPrimemSmeru(pohyby, smer, this.getS());
        //nacteme tahy v sikmem smeru
        for (int i = 1; i < pohyby.length; i++) {
            if (pohyby[i] != 0) {
                int iRadkuPolePredVeSmeru = this.getIndexRadkuPoleF() + smer;
                int iSloupcePoleSikmoVeSmeru = this.getIndexSloupcePoleF() + pohyby[i];
                pridejPoleDoTahu(this.getS().getHraciPole()[iRadkuPolePredVeSmeru][iSloupcePoleSikmoVeSmeru]);
            }
        }
        //Test braniMimochodem
        pridejPoleDoTahuPesProBraniMimo(smer, this.getS());
    }

    /**
     * Metoda prida tah pro brani mimo (pokud je realizovatelny) do moznych tahu.
     * @param smer - Smer pohybu pesce.
     * @param s - Instance tridy Sachovnice.
     */
    private void pridejPoleDoTahuPesProBraniMimo(int smer, Sachovnice s){
        if (this.lzeBratMimochodem()) { //pokud je pesec v pozici kdy muze brat mimo
            Rectangle2D poleBraniMimo;
            poleBraniMimo = this.zkontrolujMoznostBraniMimo(s, smer);
            if (poleBraniMimo != null) { //pokud pesec muze pouzit techniku brani mimo
                this.setPoleBraniMimochodem(poleBraniMimo);
                pridejPoleDoTahu(poleBraniMimo);
            }
        }
    }

    /**
     * Metoda prida zakladni prime tahy pesce do moznych tahu.
     * @param pohyby - Pole obsahujici na 0 index pocet kontrolovanych poli pro pesce v primem smeru.
     * @param smer - Smer pohybu pesce.
     * @param s - Instance tridy Sachovnice.
     */
    private void pridejPoleDoTahuPesVPrimemSmeru(int[] pohyby, int smer, Sachovnice s) {
        int iPosunu;
        if (pohyby[0] != 0) {
            // tahy v primem smeru
            boolean blok = false;
            for (int j = 0; j < pohyby[0]; j++) {
                iPosunu = (this.getIndexRadkuPoleF() + ((j + 1) * (smer)));
                if (s.jeNaPoliFigurka(iPosunu, this.getIndexSloupcePoleF())) { //pokud je na poli figurka
                    blok = true; //nastavime blokaci pro dalsi pole
                } else { //pole je prazdne tim padem je to validni tah
                    pridejPoleDoTahuNaZakladeBloku(blok, s, iPosunu);
                }
            }
        }
    }

    /**
     * Pomocna metoda. Metoda prida pole do tahu pokud je neni pole blokovane v ceste jinou figurkou.
     * @param blok - nastaveni bloku.
     * @param s - Instance tridy Sachovnice.
     * @param iPosunu - Index radku posunu ve smeru.
     */
    private void pridejPoleDoTahuNaZakladeBloku(boolean blok, Sachovnice s, int iPosunu) {
        if (!blok) {
            pridejPoleDoTahu(s.getHraciPole()[iPosunu][this.getIndexSloupcePoleF()]);
        }
    }

    /**
     * Metoda sdeli jesti je pesec v miste odkud muze teoreticky pouzit techniku brani mimo.
     * @return True pokud ano, jinak false.
     */
    private boolean lzeBratMimochodem() {
        AFigurka vybranaF = this;
        //konstanta urcuje index pro radek odkud muze pesec hrace 2 pouzit techniku brani mimo
        final int I_RADKU_BRANI_MIMO_2 = 4;
        //konstanta urcuje index pro radek odkud muze pesec hrace 1 pouzit techniku brani mimo
        final int I_RADKU_BRANI_MIMO_1 = 3;
        //pokud je figurka druheho tymu na miste odkud muze pouzit techniku braniMimochdem
        if (this.getIndexRadkuPoleF() == I_RADKU_BRANI_MIMO_2 && vybranaF.getTym().equals(this.getS().getTym2()) ) {
            return true;
        }
        //pokud je figurka prvniho tymu na miste odkud muze pouzit techniku braniMimochdem
        return this.getIndexRadkuPoleF() == I_RADKU_BRANI_MIMO_1 && vybranaF.getTym().equals(this.getS().getTym1());
    }

    /**
     * Metoda si zkontroluje pole vedle pesce a zjisti jestli muze vzit nepratelskeho pesce technikou brani mimo.
     * @param s - Instance tridy Sachovnice.
     * @param smer - Smer pohybu pesce.
     * @return Pole mozneho tahu pro brani mimo nebo null pokud takove pole neexistuje.
     */
    private Rectangle2D zkontrolujMoznostBraniMimo(Sachovnice s, int smer) {
        int iSloupceL = getIndexSloupcePoleF() - 1;
        int iSloupceR = getIndexSloupcePoleF() + 1;
        Rectangle2D pole;

        pole = zkontrolujPoleNaBraniMimo(iSloupceL, smer, s);

        if (pole != null) {
            return pole;
        }

        return zkontrolujPoleNaBraniMimo(iSloupceR, smer, s);
    }

    /**
     * Metoda zkontroluje pole vedle pesce na brani mimochodem a nastavi prislusne atributy pro pripadne vyhozeni.
     * @param iPosunutehoSloupce - Index pro upresneni sloupce vedle figurky, ktera tahne.
     * @param smer - Smer figurky ktera tahne.
     * @param s - Instance tridy sachovnice.
     * @return Null pokud nelze zrealizovat techniku brani mimo, jinak pole mozneho tahu pro techniku.
     */
    private Rectangle2D zkontrolujPoleNaBraniMimo(int iPosunutehoSloupce, int smer, Sachovnice s) {
        AFigurka sousedniF;
        //pokud je na poli figurka
        if (s.jeISloupVSachovnici(iPosunutehoSloupce) && s.jeNaPoliFigurka(this.getIndexRadkuPoleF(), iPosunutehoSloupce)) {
            sousedniF = s.getSTAV_HRY()[this.getIndexRadkuPoleF()][iPosunutehoSloupce];
            //pokud je vedle pesec jineho tymu
            if (!s.jsouFigStejnehoTymu(this, sousedniF) && sousedniF instanceof Pesec p) {
                return muzeBytBranPesMimoAJePoleZaNimVolne(s, p, smer, iPosunutehoSloupce);
            }
        }

        return null;
    }

    /**
     * Pomocna metoda. Zjisti jestli predany pesec muze byt sebran branim mimo (pohnul se ze zacatecni pozice) a jestli
     * pole za nim je volne.
     * @param s - Instance tridy Sachovnice.
     * @param p - Instance tridy Pesec.
     * @param smer - Urcuje smer kterym se pesec ktery bere hybe.
     * @param iPosunutehoSloupce - Index sloupce sachovnice pole za pascem ktery ma byt bran.
     * @return True pokud pesec muze byt bran mimo a pole za nim je volne, jinak false.
     */
    private Rectangle2D muzeBytBranPesMimoAJePoleZaNimVolne(Sachovnice s, Pesec p, int smer, int iPosunutehoSloupce) {
        if (p.getBraniMimochodem()) {//pokud muze byt bran technikou
            //pokud na poli za nepratelskym pescem je volne pole
            int iRadkuPoleZaBranymPescem = this.getIndexRadkuPoleF() + smer;
            if (s.jeIRadkuVSachovnici(iRadkuPoleZaBranymPescem) &&
                    s.getSTAV_HRY()[iRadkuPoleZaBranymPescem][iPosunutehoSloupce] == null) {
                //ulozime indexy pesce ktereho bychom vyhodili pri tahnuti na pole brani mimo
                this.setIndexyOdstranovanehoPesce(new int[] {this.getIndexRadkuPoleF(), iPosunutehoSloupce});
                return s.getHraciPole()[iRadkuPoleZaBranymPescem][iPosunutehoSloupce];
            }
        }

        return null;
    }

    /**
     * Metoda zjisti v jakych smerech a o kolik poli muze peces tahnout.
     * @param s - Instance tridy Sachovnice.
     * @param tym1 - Instance tridy Color, ktera urcuje tym 1.
     * @return Pole int[], kde prvni prvek urucje pocet krokru v pred, dalsi dva urcuji kroky v sikmem smeru.
     */
    private int[] nastavMoznePohybyPesce(Sachovnice s, Color tym1) {
        int[] pohyby;
        int smer = (this.getTym().equals(tym1)) ? -1 : 1;
        if (smer == -1) { //pokud je figurka tymu 1, neboli jde smerem odzdola nahoru
            //pokud se pesec nehnul z pocatecni pozice
            if (this.getIndexRadkuPoleF() == Pesec.POCATECNI_POZICE_RADKU_PES_TYM_1) {
                pohyby = zkontrolujPocatecniPohyby(s, smer, this);
            } else { //hnul se z pocatecni pozice
                pohyby = nastavPrimeASikmePohybyPes(s, smer, this);
            }
        } else { //pokud je figurka tymu 2, neboli jde smerem odshora dolu
            //pokud se pesec nehnul z pocatecni pozice
            if (this.getIndexRadkuPoleF() == Pesec.POCATECNI_POZICE_RADKU_PES_TYM_2) {
                pohyby = zkontrolujPocatecniPohyby(s, smer, this);
            } else { //hnul se z pocatecni pozice
                pohyby = nastavPrimeASikmePohybyPes(s, smer, this);
            }
        }

        return pohyby;
    }

    /**
     * Metoda zkontroluje vsechny mozne pocatecni pohyby pesce, neboli 2 policka v pred a 2 sikmo.
     * @param s - Instance sachovnice.
     * @param smer - Smer v jakem se pesec pohybuje.
     * @param vybranaF - Dany pesec u ktereho se vytvari mozne tahy.
     * @return Pole int[], kde prvni prvek urucje pocet krokru v pred, dalsi dva urcuji pripocty k indexu sloupce.
     */
    private int[] zkontrolujPocatecniPohyby(Sachovnice s, int smer, AFigurka vybranaF) {
        int[] pohyby = new int[MAX_POCET_POHYBU];
        pohyby[0] = 2; //muze jit az o 2 kroky
        //KONTROLA SIKMYCH POHYBU
        int[] sikmePohyby = zkontrolujSikmePohyby(s, smer, vybranaF);
        pohyby[1] = sikmePohyby[0];
        pohyby[2] = sikmePohyby[1];

        return pohyby;
    }

    /**
     * Metoda zkontroluje vsechny mozne pohyby, neboli 1 policko v pred a 2 sikmo.
     * @param s - Instance tridy Sachovnice.
     * @param smer - Smer v jakem se pesec pohybuje.
     * @param vybranaF - Dany pesec u ktereho se vytvareji mozne tahy.
     * @return Pole int[], kde prvni prvek urucje pocet krokru v pred, dalsi dva urcuji pripocty k indexu sloupce.
     */
    private int[] nastavPrimeASikmePohybyPes(Sachovnice s, int smer, AFigurka vybranaF) {
        int[] pohyby = new int[MAX_POCET_POHYBU];

        if (s.jeIRadkuVSachovnici(this.getIndexRadkuPoleF() + smer)) {
            pohyby[0] = 1; //muze jit o jeden krok vpred
            //KONTROLA SIKMYCH POHYBU
            int[] sikmePohyby = zkontrolujSikmePohyby(s, smer, vybranaF);
            pohyby[1] = sikmePohyby[0];
            pohyby[2] = sikmePohyby[1];
        }

        return pohyby;
    }

    /**
     * Metoda zkontroluje zda pesec muze vzit jine figurky, pokud ano, tak nastavi pripocet k indexu sloupce.
     * @param s - Instance tridy Sachovnice.
     * @param smer - Smer v jakem se pesec pohybuje.
     * @param vybranaF - Dany pesec se kterym se hybe.
     * @return Pole int[], kde prvky reprezentuji pripocet k indexu sloupce.
     */
    private int[] zkontrolujSikmePohyby(Sachovnice s, int smer, AFigurka vybranaF) {
        final int POCET_SIKMYCH_POHYBU = 2;
        int[] sikmePohyby = new int[POCET_SIKMYCH_POHYBU];
        int iRadkuPredPes = this.getIndexRadkuPoleF() + smer;
        int iLevSolupcePredPes = this.getIndexSloupcePoleF() - 1;
        int iPravSloupcePredPes = this.getIndexSloupcePoleF() + 1;
        //pokud je figurka vlevo od vybrane a nejsou stejneho tymu
        if (nachaziSeNaIndexechNepratelskaFigurka(s, iRadkuPredPes, iLevSolupcePredPes, vybranaF)) {
            sikmePohyby[0] = -1;
        }
        //pokud je figurka vpravo od vybrane a nejsou stejneho tymu
        if (nachaziSeNaIndexechNepratelskaFigurka(s, iRadkuPredPes, iPravSloupcePredPes, vybranaF)) {
            sikmePohyby[1] = 1;
        }

        return sikmePohyby;
    }

    /**
     * Pomocna metoda. Metoda zjisti jestli, idnex sloupce ja validni, je na poli (iRadku,iSloupce) figurka a jestli
     * je nepratelska, pokud ano, tak vrati true, jinak false
     * @param s - Instance tridy Sachovnice.
     * @param iRadku - Index radku sachovnice.
     * @param iSloupce - Index sloupce sachovnice.
     * @param vybranaF - predany Pesec.
     * @return True pokud je iSloupce validni, je na poli (iRadku,iSloupce) figurka a je nepratelska, jinak false.
     */
    private boolean nachaziSeNaIndexechNepratelskaFigurka(Sachovnice s, int iRadku, int iSloupce, AFigurka vybranaF) {
        return s.jeISloupVSachovnici(iSloupce) && s.getSTAV_HRY()[iRadku][iSloupce] != null &&
               !s.jsouFigStejnehoTymu(s.getSTAV_HRY()[iRadku][iSloupce], vybranaF);
    }

    /**
     * Geter k atributu braniMimochodem.
     * @return braniMimochodem.
     */
    public boolean getBraniMimochodem() {
        return braniMimochodem;
    }

    /**
     * Geter k atributu indexyOdstranovanehoPesce.
     * @return indexyOdstranovanehoPesce.
     */
    public int[] getIndexyOdstranovanehoPesce() {
        return indexyOdstranovanehoPesce;
    }

    /**
     * Geter k atributu poleBraniMimochodem.
     * @return poleBraniMimochodem.
     */
    public Rectangle2D getPoleBraniMimochodem() {
        return this.poleBraniMimochodem;
    }

    /**
     * Seter k atributu braniMimochodem.
     * @param braniMimochodem boolean.
     */
    public void setBraniMimochodem(boolean braniMimochodem) {
        this.braniMimochodem = braniMimochodem;
    }

    /**
     * Seter k atributu poleBraniMimochodem.
     * @param pole Rectangle2D.
     */
    public void setPoleBraniMimochodem(Rectangle2D pole) {
        this.poleBraniMimochodem = pole;
    }

    /**
     * Seter k atributu indexyOdstranovanehoPesce.
     * @param indexyOdstranovanehoPesce int[].
     */
    public void setIndexyOdstranovanehoPesce(int[] indexyOdstranovanehoPesce) {
        this.indexyOdstranovanehoPesce = indexyOdstranovanehoPesce;
    }

    /**
     * Metoda zjisti, zda pesec provedl prvni tah ze sve pocatecni pozice (dvojkrok).
     * @param i  - Index radku, odkud pesec startoval.
     * @param ip - Index radku, kam pesec dosel.
     * @throws IllegalArgumentException Pokud indexy nejsou v sachovnici.
     * @return True pokud pesec provedl dvojkrok z pocatecni pozice, jinak false.
     */
    public boolean tahnePesecZeZacatecniPozice(int i, int ip) {
        if (this.getS().nejsouIndexyVSachovnici(i, ip)) {
            throw new IllegalArgumentException("Predane indexy nejsou v sachovnici.");
        }
        if (jePrvniTahPescuTymu1(i, ip)) {
            return true;
        }

        return jePrvniTahPescuTymu2(i, ip);
    }

    /**
     * Pomocna metoda. Zjisti, zda pesec tymu 1 tahne z radku 6 na radek 4.
     * @param i  - Pocatecni radek.
     * @param ip - Cilovy radek.
     * @return true pokud odpovida pohybu pescu tymu 1 z vychozi pozice.
     */
    private boolean jePrvniTahPescuTymu1(int i, int ip) {
        return this.getTym().equals(this.getS().getTym1()) && i == 6 && ip == 4;
    }

    /**
     * Pomocna metoda. Zjisti, zda pesec tymu 2 tahne z radku 1 na radek 3.
     * @param i  - Pocatecni radek.
     * @param ip - Cilovy radek.
     * @return true pokud odpovida pohybu pescu tymu 2 z vychozi pozice.
     */
    private boolean jePrvniTahPescuTymu2(int i, int ip) {
        return this.getTym().equals(this.getS().getTym2()) && i == 1 && ip == 3;
    }

    /**
     * Zjisti jestli pesec jde na hranici sachovnice, coz inicializuje promenu pesce.
     * @param ip - Cilovy radek.
     * @return True pokud ano, jinak false.
     */
    public boolean jdeOPromenu(int ip) {
        return ip == this.getS().getDOLNI_A_PRAVA_HRANICE_SACH() ||
               ip == this.getS().getHORNI_A_LEVA_HRANICE_SACH();
    }

    /**
     * Metoda zjisti jestli je pesec v pozici radku kde na sebe mohl umoznit brani mimo.
     * @return True pokud ano, jinak false.
     */
    public boolean jePesecVPozicProBraniMimo() {
        return this.getIndexRadkuPoleF() == urciVychoziRadkyProPescePriBraniMimo()[1];
    }

    /**
     * Metoda zjisti indexy radku, ve kterych se pesec pri tahu musi nachazet pokud umozni ostatnim
     * pescum techniku brani mimo.
     * @return - int[] -> Prvni prvek odpovida pocatecni pozici radku pesce a druhy pozici radku pro umozneni techniky
     * brani mimo.
     */
    public int[] urciVychoziRadkyProPescePriBraniMimo() {
        if (this.getTym().equals(this.getS().getTym1())) {
            return new int[] {6, 4}; // {iRadkuVychozPozice, indexRadkuBraniMimo}
        } else {
            return new int[] {1, 3};
        }
    }
}

