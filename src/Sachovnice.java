import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.geom.Rectangle2D;


/**
 * Sablona pro vytvareni instanci sachovnice, na kterou se vykresluji ostatni figurky,
 * a poskytuje instancim jednotlivych figurek klicove informace jako napr. hraciPole atd.
 * @author Filip Valtr
 */
public class Sachovnice {
    //== Konstantni atributy instanci
    /** Definuje poect radku na sachovnici. */
    private final int SLOUPCE = 8;
    /** Definguje pocet sloupcu na sachovnici. */
    private final int RADKY = 8;
    /** Definuje index pole lezici co nejvice nahore v levo. */
    private final int HORNI_A_LEVA_HRANICE_SACH = 0;
    /** Definuje index pole lezici co nejvice dole v pravo. */
    private final int DOLNI_A_PRAVA_HRANICE_SACH = 7;
    /** Reprezentuje hraci pole a rozmisteni figurek na nem. */
    private final AFigurka[][] STAV_HRY = new AFigurka[RADKY][SLOUPCE];
    /** Slouzi k ulozeni figurek. */
    private final AFigurka[] FIGURKY = new AFigurka[32];
    /** Uchovava instanci algoritmu Fen pro vytvareni fen retezcu a urceni remizy. */
    private final ForsythEdwardsNotation FEN_ALG = new ForsythEdwardsNotation();
    /** Uchova jestli se ma k urictemu krali vykreslit upozorneni sachu. */
    private final Object[] VYKRESLI_SACH = new Object[] {false, null};
    /** Uchova jestli se ma na konci hry vypsat hlaska. */
    private final Object[] VYKRESLI_KONEC = new Object[] {false, ""};
    //== Atributy instanci
    /** Sirka okna. */
    private double sirka;
    /** Vyska okna. */
    private double vyska;
    /** Mensi z sirky a vysky okna. */
    private double nejmensi;
    /** Vetsi z sirky a vysky okna. */
    private double nejvetsi;
    /** Velikost jednoho hraciho pole na sachovnici. */
    private double delkaCtv;
    /** Hraci pole(sachovnice) - pole Rectanglu2D, ktere predstavuji jednotliva hraci pole. */
    private Rectangle2D[][] hraciPole;
    /** Atribut pro iterovani v poli figurek. */
    private int pocetF = 0;
    /** Atribut ukaldajici barvu prvniho tymu. */
    private final Color tym1;
    /** Atribut ukladajici barvu druheho tymu. */
    private final Color tym2;
    /** Atribut urcuje jaky tym je na tahu. */
    private Color naTahu;
    /** Atribut uchovava validni tahy tazene figurky. */
    private Rectangle2D[] validniTahy = null;
    /** Atribut urcujici jestli by tahnuti figurky privedlo jejiho krale do sachu. */
    private boolean kralFigurkyByDostalSach = false;
    /** Atribut zaznamenavajici konec hry. */
    private boolean konecHry = false;
    /** Slouzi pro ulozeni indexu poli na kterych byl uskutecnen posledni validni tah. */
    private int[] znazorneniTahu = null;
    /** Zaznamenava pocet tahu v rade, kdy nebylo tazeno pescem a nebyla sebrana figurka. */
    private int pocetTahuBezBraniAPesce = 0;
    /** Urcuje jestli byla figruka sebrana. */
    private boolean byloBrano = false;
    //==Konstruktory
    /**
     * Konstruktor
     * @param sirka - Sirka okna.
     * @param vyska - Vyska okna.
     * @param nejmensi - Mensi z sirky a vysky okna.
     * @param nejvetsi - Vetsi z sirky a vysky okna.
     * @param tym1  - Barva reprezentujici tym 1.
     * @param tym2  - Barva reprezentujici tym 2.
     * @param obrazovka - Predana instance Dimension pro definovani rozmeru zarizeni.
     * @throws IllegalArgumentException Pokud je predana null obrazovka nebo barvy jsou stejne ci null.
     */
    public Sachovnice(double sirka, double vyska, double nejmensi, double nejvetsi, Color tym1, Color tym2,
                      Dimension obrazovka ) {
        if (obrazovka == null) {
            throw  new IllegalArgumentException("Predana null obrazovka pro vytvoreni sachovnice.");
        }

        overPlatnostBarev(tym1, tym2);

        this.setSachovnice(sirka, vyska, nejmensi, nejvetsi, obrazovka);
        this.tym1 = tym1;
        this.tym2 = tym2;
        this.naTahu = tym1;
    }

    /**
     * Pomocna metoda pro overeni platnosti barev tymu.
     * @param tym1 - Barva tymu 1.
     * @param tym2 - Barva tymu 2.
     * @throws IllegalArgumentException Pokud je nektera barva null nebo jsou barvy stejne.
     */
    private void overPlatnostBarev(Color tym1, Color tym2) {
        if (tym1 == null || tym2 == null || tym1.equals(tym2)) {
            throw new IllegalArgumentException("Spatne predane barvy: jsou null nebo jsou stejne.");
        }
    }

    /**
     * Metoda zjisti jestili rozmer sirky je nesmyslny.
     * @param sirka - Udava inicializacni sirku okna.
     * @param obrazovka - Predana instance Dimension pro definovani rozmeru zarizeni.
     * @return True pokud je rozmer nesmyslny, jinak false.
     */
    private boolean jeSirkaNevalidni(int sirka, Dimension obrazovka) {
        if (obrazovka == null) {
            throw new IllegalArgumentException("Predana obrazovka je null.");
        }
        return sirka <= 0 || sirka > obrazovka.getWidth();
    }

    /**
     * Metoda zjisti jestili rozmer vysky je nesmyslny.
     * @param vyska - Udava inicializacni vysku okna.
     * @param obrazovka - Predana instance Dimension pro definovani rozmeru zarizeni.
     * @return True pokud je rozmer nesmyslny, jinak false.
     */
    private boolean jeVyskaNevalidni(int vyska, Dimension obrazovka) {
        if (obrazovka == null) {
            throw new IllegalArgumentException("Predana obrazovka je null.");
        }
        return vyska <= 0 || vyska > obrazovka.getHeight();
    }

    /**
     * Geter k atributu sirka.
     * @return sirka.
     */
    public double getSirka() {
        return this.sirka;
    }

    /**
     * Geter k atributu vyska.
     * @return vyska.
     */
    public double getVyska() {
        return this.vyska;
    }

    /**
     * Getter k atributu STAV_HRY.
     * @return STAV_HRY.
     */
    public AFigurka[][] getSTAV_HRY(){
        return this.STAV_HRY;
    }

    /**
     * Geter k atributu hraciPole.
     * @return hraciPole.
     */
    public Rectangle2D[][] getHraciPole(){
        return this.hraciPole;
    }

    /**
     * Metoda provede tah figurky na zaklade inexu hernich poli.
     * @param i - Index radku na kterem se figurka nachazi.
     * @param j - Index sloupce na kterem se figurka nachazi.
     * @param ip - Index radku na ktery se bude figurka presouvat.
     * @param jp - Index sloupc na ktery se bude figurka presouvat.
     */
    public void provedTah(int i, int j, int ip, int jp) {
        //zkontroluje zakladni podminky pro presun figurky
        zkontrolujParametryExistenciPresouvaneFValidituTahu(i, j, ip, jp);
        //ziskame presouvanou f
        AFigurka figurka;
        figurka = this.STAV_HRY[i][j];
        //zkontroluje jak je na tom cilove pole a pripadne vyhodi figurku
        zkontrolujCilovePole(figurka, ip, jp);
        //udelame tah
        tahni(i, j, ip, jp, figurka);
        //ulozime si indexy poli pro zobrazeni tahu na sachovnici
        ulozPoleProZobrazeniTahu(i, j, ip, jp);
        //zkontrolujeme mat
        if (jeMat(figurka, this.getFIGURKY())) {

            ukonciHru(figurka, false);
        }
        //zkontrolujeme pat, neboli remizu
        if (jeRemiza(figurka, this.getFIGURKY())) {
            ukonciHru(figurka, true);
        }
    }

    /**
     * Metoda na zaklade predanych parametru ulozi indexy poli do atributu pro jejich uchovani pro
     * nasledne zobrazeni tahu.
     * @param i - Index radku na kterem se figurka nachazela.
     * @param j - Index sloupce na kterem se figurka nachazela.
     * @param ip - Index radku na ktery se figurka presouvala.
     * @param jp - Index sloupce na ktery se figurka presouvala.
     */
    private void ulozPoleProZobrazeniTahu(int i, int j, int ip, int jp) {
        int[] znazorneniTahu = new int[4];
        znazorneniTahu[0] = i;
        znazorneniTahu[1] = j;
        znazorneniTahu[2] = ip;
        znazorneniTahu[3] = jp;

        this.setZnazorneniTahu(znazorneniTahu);
    }

    /**
     * Metoda najde k predane figurce nepratelskeho krale a u nej otestuje jestli je mat.
     * @param figurka -Ppredana figurka se kterou se tahne.
     * @param figurky - Pole figurek na sachovnici.
     * @return True pokud je kral v matu, jinak false.
     */
    private boolean jeMat(AFigurka figurka, AFigurka[] figurky) {
        Kral k = figurka.najdiNepratelskehoKrale(figurky);
        //pokud ma nepratelsky kral sach, nemuze se hybat a nemuze ho zachranit jina figurka
        return k.getSach() && k.nemuzeSeKralHybat() && k.nemuzouSeHybatFigurkyKrale();
    }

    /**
     * Metoda zjisti jestli oba hraci nemaji dostatek figurek k tomu dat mat.
     * @param figurky - Pole obsahujici instance figurek na sachovnici.
     * @return True pokud neni mozne aby nastal mat s figurkami, jinak false.
     */
    private boolean jeSTAV_HRYNemoznehoMatu(AFigurka[] figurky) {
        int pocetKralu = 0;
        int pocetStrelcu = 0;
        int pocetKoni = 0;
        final int MAXIMALNI_POCET_STRELCU = 20;
        Strelec[] poleStrelcu = new Strelec[MAXIMALNI_POCET_STRELCU];
        //remiza nastane pokud neni dostatek materialu k matu
        for (AFigurka figurka : figurky) {
            //pokud mame nepratelskou figurku
            if (figurka != null) {
                switch (figurka) {
                    case Kral ignored -> pocetKralu++;
                    case Strelec s -> {
                        poleStrelcu[pocetStrelcu] = s;
                        pocetStrelcu++;
                    }
                    case Kun ignored -> pocetKoni++;
                    default -> { return false; } //pokud se v poli figurek nachazi dama, pesec nebo vez pak je mat mozny
                }
            }
        }

        return zvalidujStav(pocetKoni, pocetStrelcu, pocetKralu, poleStrelcu);
    }

    /**
     * Metoda na zaklade predanych parametru zda je stav takovy ze nemuze nasta mat.
     * @param pocetKoni - Cislo urcujici pocet koni na sachovnici.
     * @param pocetStrelcu - Cislo urucujici pocet strelcu na sachovnici.
     * @param pocetKralu - Cislo urcujici pocet kralu na sachovnici.
     * @param poleStrelcu - Pole obsahujici instance strelcu na sachovnici.
     * @return True pokud nemuze nastat mat, jiinak false.
     */
    private boolean zvalidujStav(int pocetKoni, int pocetStrelcu, int pocetKralu, Strelec[] poleStrelcu) {
        //ve hre jsou pouze 2 kralove
        int stav = stavFigurek(pocetKralu, pocetStrelcu, pocetKoni);
        switch (stav) {
            case 0 -> {
                System.out.println("Málo materiálu k dosažení matu (pouze 2 králové).");
                VYKRESLI_KONEC[1] = "Málo materiálu k dosažení matu (pouze 2 králové). ";
                return true;
            }
            case 1 -> {
                System.out.println("Málo materiálu k dosažení matu (pouze 2 králové a 1 střelec).");
                VYKRESLI_KONEC[1] = "Málo materiálu k dosažení matu (pouze 2 králové). ";
                return true;
            }
            case 2 -> {
                System.out.println("Málo materiálu k dosažení matu (pouze 2 králové a 1 kůň).");
                VYKRESLI_KONEC[1] = "Málo materiálu k dosažení matu (pouze 2 králové a 1 kůň). ";
                return true;
            }
            case 3 -> {
                //pokud jsou dvojice indexu radku a sloupce obe sude nebo obe liche pak se jedna o strelce na bilem poli
                return strelciNaStejneBarevnemPoli(poleStrelcu);
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Pomocna metoda. Pomaha identifikovat stavy pro remizu.
     * @param pocetKoni - Cislo urcujici pocet koni na sachovnici.
     * @param pocetStrelcu - Cislo urucujici pocet strelcu na sachovnici.
     * @param pocetKralu - Cislo urcujici pocet kralu na sachovnici.
     * @throws IllegalStateException Pokud pocet kralu neodpovida pravidlum sachu.
     * @return 0 -> pokud 2 kralove, 1 -> 2 kralove 1 strelec, 2 -> 2 kralove 1 strelec, 3 -> 2 kralove 2 strelci,
     * jinak -1.
     */
    private int stavFigurek(int pocetKralu, int pocetStrelcu, int pocetKoni) {
        if (pocetKralu != 2) throw new IllegalStateException("Pocet kralu neodpovida pravidlum sachu.");

        String kombinace = pocetStrelcu + "-" + pocetKoni;
        return switch (kombinace) {
            //2 kralove
            case "0-0" -> 0;
            //2 kralove 1 strelec
            case "1-0" -> 1;
            //2 kralove 1 kun
            case "0-1" -> 2;
            //2 kralove a 2 strelci
            case "2-0" -> 3;
            //vsechny ostatni kombinace
            default -> -1;
        };
    }

    /**
     * Pomocna metoda. Metoda zjisti jestli jsou strelci v poli na stejnem barevnem poli.
     * @param poleStrelcu - Pole obsahujici 2 predane strelce.
     * @return True pokud ano, jinak false.
     */
    private boolean strelciNaStejneBarevnemPoli(Strelec[] poleStrelcu) {
        if (poleStrelcu[0].jeNaBilemPoli() && poleStrelcu[1].jeNaBilemPoli()) {
            System.out.println("Málo materiálu k dosažení matu (pouze 2 králové a 2 na bílém poli).");
            VYKRESLI_KONEC[1] = "Málo materiálu k dosažení matu (pouze 2 králové a 2 na bílém poli). ";
            return  true;
        }

        if (poleStrelcu[0].jeNaCernemPoli() && poleStrelcu[1].jeNaCernemPoli()) {
            System.out.println("Málo materiálu k dosažení matu (pouze 2 králové a 2 střelci na černém poli).");
            VYKRESLI_KONEC[1] = "Málo materiálu k dosažení matu (pouze 2 králové a 2 střelci na černém poli). ";
            return  true;
        }

        return false;
    }

    /**
     * Metoda zjisti jestli nastala remiza. Tzn zkontroluje pat, stav hry nemozneho matu,
     * 50 tahu bez tahnutim pescem nebo brani figurky a vyskytnuti 3. identickeho stavu na sachovnici.
     * @param figurka - Figurka se kterou se tahne.
     * @param figurky - Pole obsahujici instance figurek na sachovnici.
     * @return True pokud nastala remiza, jinak false.
     */
    private boolean jeRemiza(AFigurka figurka, AFigurka[] figurky) {
        Kral k = figurka.najdiNepratelskehoKrale(figurky);
        //pokud nema nepratelsky kral sach, nemuze se hybat a nemuze ho zachranit jina figurka
        if (jePat(k) || jeSTAV_HRYNemoznehoMatu(figurky) || je50TahuBezBraniNeboPesce()) {

            return true;
        }

        if (!this.getFEN_ALG().nastal3StejnyStavSachovnice().isEmpty()) {
            System.out.println("3. stejný stav na šachovnici: " + this.getFEN_ALG().nastal3StejnyStavSachovnice());
            this.VYKRESLI_KONEC[1] = "3. stejný stav na šachovnici: " + this.getFEN_ALG().nastal3StejnyStavSachovnice() + ". ";

            return true;
        }

        return false;
    }

    /**
     * Pomocna metoda. Zjisti jestli nastal pat
     * @param k - Predany nepratelsky kral hrace ktery tahnul.
     * @return True pokud ano, jinak false.
     */
    private boolean jePat(Kral k) {
        if (!k.getSach() && k.nemuzeSeKralHybat() && k.nemuzouSeHybatFigurkyKrale()) {
            this.VYKRESLI_KONEC[1] = "Pat. ";
            return  true;
        }

        return false;
    }

    /**
     * Metoda na zaklade prislusneho atributu otestuje jestli nebylo tazeno 50 krat bez tahnuti pesce nebo
     * sebrani figurky.
     * @return Tru pokud byla hranice prekrocena, jinak false.
     */
    private boolean je50TahuBezBraniNeboPesce() {
        final int HRANICE = 50;
        if (this.getPocetTahuBezBraniAPesce() >= HRANICE) {
            System.out.println("50 tahů bez sebrání figurky nebo táhnutí pěšcem.");
            this.VYKRESLI_KONEC[1] = "50 tahů bez sebrání figurky nebo táhnutí pěšcem. ";
            return true;
        } else {
            return false;
        }
    }

    /**
     * Metoda vypise do konzole prislousny duvod konce a nastavi klicovy atribut.
     * @param figurka - Predana figurka se kterou se tahne.
     * @param remiza - Boolean urcujici jestli nastala remiza nebo ne.
     */
    private void ukonciHru(AFigurka figurka, boolean remiza) {
        if (remiza) {
            System.out.println("Nastala Remíza.");
            VYKRESLI_KONEC[1] = VYKRESLI_KONEC[1] + "Nastala Remíza !!!!!";
        } else {
            System.out.println("ŠACH MAT: VYHRÁL " + figurka.getPopis() + "." );
            VYKRESLI_KONEC[1] = VYKRESLI_KONEC[1] + "ŠACH MAT: VYHRÁL " + figurka.getPopis() + ".";
        }

        this.VYKRESLI_KONEC[0] = true;
        this.setKonecHry(true);
    }

    /**
     * Metoda zkontroluje jestli jsou indexy validni, jestli se pod prvnimi dvema indexami nachazi figurka
     * na sachovnici, jestli je pole pod cilovymi idnexy mezi validnimi tahy.
     * @param i - Index urcujici radek zacatecniho pole na sachovnici.
     * @param j - Index urcujici sloupec zacatecniho pole na sachovnici.
     * @param ip - Index urcujici radek ciloveho pole na sachovnici.
     * @param jp - Index urcujici sloupec ciloveho pole na sachovnici.
     * @throws IllegalArgumentException Pokud kontrola podminek pro presun neprosla.
     */
    private void zkontrolujParametryExistenciPresouvaneFValidituTahu(int i, int j, int ip, int jp) {
        if (nejsouIndexyVSachovnici(i, j) || nejsouIndexyVSachovnici(ip, jp)) {
            throw new IllegalArgumentException("Indexy provadeneho tahu jsou neplatne.");
        }
        AFigurka figurka;
        figurka = this.STAV_HRY[i][j];
        if (figurka == null) { //presouvana figurka v poli neni nastala chyba
            throw new IllegalArgumentException("Presouvana figurka v poli(i,j) neni.");
        }

        if (!jePoleMeziValidnimiTahy(ip, jp)) {
            throw new IllegalArgumentException("Pole (ip, jp) na ktere se figurka presouva neni mezi validnimi tahy.");
        }
    }

    /**
     * Pomocna metoda. Zjisti jestli je pole (ip,jp) validnim tahem.
     * @param ip - Index radku na ktery se bude figurka presouvat.
     * @param jp - Index sloupc na ktery se bude figurka presouvat.
     * @return True pokud je, jinak false.
     * @throws IllegalStateException Pokud validni tahy nebyly nastaveny.
     */
    private boolean jePoleMeziValidnimiTahy(int ip, int jp) {
        //zkontrolujeme jestli cilove pole je mezi validnimi tahy
        if (jsouValidniTahyInicializovane()) {
            for (int k = 0; k < this.getValidniTahy().length; k++) { //projdeme vsechna pole v moznych
                if (jePoleValidnimiTahem(ip, jp, k)) { //pokud je tah na pole validni
                    return true;
                }
            }
        } else {
            throw new IllegalStateException("Validni tahy figurky nebyli nastaveny.");
        }

        return false;
    }

    /**
     * Pomocna metoda. Zjisti jestli jsou pole(ip, jp) a pole z validnich tahu na indexu k stejne.
     * @param ip - Index radku na ktery se bude figurka presouvat.
     * @param jp - Index sloupc na ktery se bude figurka presouvat.
     * @param k - Index urcujici pole ve validnich tazich.
     * @return True pokud ano, jinak false.
     */
    private boolean jePoleValidnimiTahem(int ip, int jp, int k) {
        return this.getPole(ip, jp) != null && this.getValidniTahy()[k] != null &&
               this.getPole(ip, jp).equals(this.getValidniTahy()[k]);
    }

    /**
     * Pomocna metoda. Zjisti jestli jsou nastavene validni pole pro tah.
     * @return True pokud ano jinak false.
     */
    private boolean jsouValidniTahyInicializovane() {
        return this.getValidniTahy() != null && this.getValidniTahy().length != 0;
    }

    /**
     * Metoda zkontroluje jestli se na poli nachazi figurka, pokud ano a je jineho tymu, tak figurku vyhodi a
     * aktualizje prislusny atribut brani figurek.
     * @param figurka - Instance figurky se kterou se tahne.
     * @param ip - Index urcujici radek ciloveho pole na sachovnici.
     * @param jp - Index urcujici sloupec ciloveho pole na sachovnici.
     * @throws IllegalArgumentException Pokud presouvame na misto, kde je figurka stejneho tymu.
     * @throws IllegalStateException Pokud se nepodarilo vyhodit nepratelskou figurku.
     */
    private void zkontrolujCilovePole(AFigurka figurka, int ip, int jp) {
        if (this.STAV_HRY[ip][jp] != null) { // pokud se na cilovem miste nachazi figurka
            if (this.STAV_HRY[ip][jp].getTym().equals(figurka.getTym())) { //figurka stejneho tymu
                throw new IllegalArgumentException("Presouvame na misto, kde je figurka stejenho tymu.");
            } else { //figurka protejsiho tymu
                //figurku vyhodime
                if (vyhodFigurku(ip, jp, this.STAV_HRY[ip][jp])) {
                    throw new IllegalStateException("Figurku se nepodarilo vyhodit.");
                } else {
                    this.setByloBrano(true);
                    return;
                }
            }
        }
        this.setByloBrano(false);
    }

    /**
     * Metoda aktualizuje atributy pro krale nebo veze (pokud je figurka vezi nebo kralem),
     * ktere se pouzivaji pro rosady.
     * @param figurka - Predana figurka.
     */
    private void aktualizujAtributyVezNeboKral(AFigurka figurka) {
        if (figurka instanceof Vez v && !v.getPohnulSe()) {
            //zaznamename stav pohnuti krale -> true pokud se jeste nepohl, jinak false.
            v.setStavKrale(!v.getKral(this.getFIGURKY()).getPohnulSe());
            v.setPohnulSe(true);
        }
        //pokud jsme tahli s kralem
        if (figurka instanceof Kral k && !k.getPohnulSe() ) {
            //ulozime atributy vezi o pohnuti se kterou muze udelat rosadu
            boolean[] pohnutiVezi = new boolean[] {false, false};
            if (k.ziskejVezeKraleProRosadu()[0] != null) { //vez pro kralovskou rosadu se nepohla
                pohnutiVezi[0] = true;
            }
            if (k.ziskejVezeKraleProRosadu()[1] != null) {//vez pro damskou rosadu se nepohla
                pohnutiVezi[1] = true;
            }
            k.setStavVezi(pohnutiVezi);
            k.setPohnulSe(true);
        }
    }

    /**
     * Metoda zkontroluje jestli figurka je kralem a jestli tahne na pole, ktere slouzi pro rosadu.
     * @param figurka - Figurka, ktera tahne.
     * @param ip - Index urcujici radek ciloveho pole na sachovnici.
     * @param jp - Index urcujici sloupec ciloveho pole na sachovnici.
     */
    private void zkontrolujRosadu(AFigurka figurka, int ip, int jp) {
        final int POCET_ROSAD = 2;
        //zkontrolujeme  jestli tah byl rosada a pripadne tahnem i vezi
        for (int l = 0; l < POCET_ROSAD; l++) {
            if (nastalaRosada(figurka, ip, jp, l)) { //delame rosadu
                Kral k = (Kral)figurka;
                k.setUdelalRosadu(true);
                //zjisitme cilovy index pro tah ciloveho pole rosady veze -> proadi rosady (l) + pripocet (2) pro
                //ziskani indexu cil pole veze
                int iCilovehoPoleVezeRosady = l + 2;
                //index startovni pozice veze pro rosadu = proadi rosady (l) * velikost 2D vektoru (2)
                int iRadkuStartPoleVezeRosady = l * 2;
                int iSloupStartPoleVezeRosady = l * 2 + 1;
                int iRCil = this.getIndexyPole(k.getRosada()[iCilovehoPoleVezeRosady].getX(),
                                               k.getRosada()[iCilovehoPoleVezeRosady].getY())[0];
                int iSCil = this.getIndexyPole(k.getRosada()[iCilovehoPoleVezeRosady].getX(),
                                               k.getRosada()[iCilovehoPoleVezeRosady].getY())[1];
                // ziskame indexy veze kterou presouvame
                int iRStart = k.getIndexyVeziRosady()[iRadkuStartPoleVezeRosady];
                int iSStart = k.getIndexyVeziRosady()[iSloupStartPoleVezeRosady];
                //jelikoz jde o rosadu tak ta se pocita jako jeden tah tzn
                this.setPocetTahuBezBraniAPesce(this.getPocetTahuBezBraniAPesce() - 1);
                //udelame tah
                tahni(iRStart, iSStart, iRCil, iSCil, this.STAV_HRY[iRStart][iSStart]);
                aktualizujTahyFigurkam();
            }
        }
    }

    /**
     * Pomocna metoda. Metoda zjisti jestli se kralem tahne na misto rosady a jestli podminky jsou splneny.
     * @param figurka - Figurka, ktera tahne.
     * @param ip - Index urcujici radek ciloveho pole na sachovnici.
     * @param jp - Index urcujici sloupec ciloveho pole na sachovnici.
     * @param l - Index urcujici pole rosady daneho krale.
     * @return True pokud nastala, jinak false.
     */
    private boolean nastalaRosada(AFigurka figurka, int ip, int jp, int l) {
       return figurka instanceof Kral k && !k.getPohnulSe() && this.hraciPole[ip][jp].equals(k.getRosada()[l]);
    }

    /**
     * Metoda zajisti provedeni techniky brani mimo pokud cilove pole umoznuje jeji provedeni.
     * Nasledne Aktualizuje pescum atributy pro brani mimochodem.
     * @param figurka - Figurka se kterou se tahne.
     * @param i - Index urcujici radek zacatecniho pole na sachovnici.
     * @param ip - Index urcujici radek ciloveho pole na sachovnici.
     * @param jp - Index urcujici sloupec ciloveho pole na sachovnici.
     * @throws IllegalStateException Pokud se nepodarilo vyhodit figurku pri brani mimo.
     */
    private void zkontrolujBraniMimo(AFigurka figurka, int i, int ip, int jp) {
        //pokud jsme inicializovali pohybem na pole techniku brani mimo
        if (berePesecMimo(figurka, ip, jp)) {
            Pesec p = (Pesec)figurka;
            int indexVyhozPR = p.getIndexyOdstranovanehoPesce()[0];
            int indexVyhozPS = p.getIndexyOdstranovanehoPesce()[1];
            //pesce vyhodime
            if (vyhodFigurku(indexVyhozPR, indexVyhozPS, this.STAV_HRY[indexVyhozPR][indexVyhozPS])) {
                throw new IllegalStateException("Nepodarilo se pri brani mimo vyhodit figurku.");
            }
        }
        //musime projit vsechny pesce a zkontrolovat jestli uz nevyprsel pocet tahu brani Mimochodem
        for (AFigurka f : FIGURKY) {
            if (f instanceof Pesec && ((Pesec) f).getBraniMimochodem()) {
                ((Pesec) f).setBraniMimochodem(false);
            }
        }
        //zkontrolujeme brani mimochodem a aktualizujeme atributy
        if (umozniPesecPohybemBraniMimo(figurka, ip, i)) {
            Pesec p = (Pesec)(figurka);
            p.setBraniMimochodem(true);
        }
    }

    /**
     * Pomocna metoda. Zjisti jestli pesec spustil techniku brani mimo.
     * @param figurka - Figurka se kterou se tahne.
     * @param ip - Index urcujici radek ciloveho pole na sachovnici.
     * @param jp - Index urcujici sloupec ciloveho pole na sachovnici.
     * @return True pokud pese tahnutim na cilove pole vyhazuje pesce technikou brani mimo, jinak false.
     */
    private boolean berePesecMimo(AFigurka figurka, int ip, int jp) {
        return figurka instanceof Pesec p && this.hraciPole[ip][jp].equals(p.getPoleBraniMimochodem());
    }

    /**
     * Pomocna metoda. Zjisti jestli se pesec hybe z pocatecni pozice o 2 vpred cimz umoznuje se sebrat technikou mimo.
     * @param figurka - Figurka se kterou se tahne.
     * @param i - Index urcujici radek zacatecniho pole na sachovnici.
     * @param ip - Index urcujici radek ciloveho pole na sachovnici.
     * @return True pokud ano, jinak false.
     */
    private boolean umozniPesecPohybemBraniMimo(AFigurka figurka, int ip, int i) {
        if (figurka instanceof Pesec p) {
            return p.tahnePesecZeZacatecniPozice(i, ip);
        }

        return false;
    }

    /**
     * Metoda presune figurku z pole danymi indexy (i,j) na pole s danymi indexy (ip,jp).
     * Aktualizuje indexy pole figurky ve kterem se nachazi, vytvori telo. Zkontroluje jestli
     * cilove pole neni pole rosady (pokud ano presune i vez), aktualizuje pozici, kontroluje promenu pesce,
     * aktualizuje sach, a aktualizuje atributy pro remizu.
     * @param i - Index urcujici radek zacatecniho pole na sachovnici.
     * @param j - Index urcujici sloupec zacatecniho pole na sachovnici.
     * @param ip - Index urcujici radek ciloveho pole na sachovnici.
     * @param jp - Index urcujici sloupec ciloveho pole na sachovnici.
     * @param figurka - Presouvana figurka.
     */
    private void tahni(int i, int  j, int  ip, int  jp, AFigurka figurka) {
        //zkontrolujeme rosadu
        zkontrolujRosadu(figurka, ip, jp);
        //presuneme figurku a aktualizujeme pozicni atributy
        udelejVnitrniTah(i, j, ip, jp, figurka);
        //aktualizace atributu pro rosady -> pohnuti vezi a kralu
        aktualizujAtributyVezNeboKral(figurka);
        //aktualizujeme atribtury pro brani mimo popripade vyhodime figurku ktera byl vzata technikou brani mimo
        zkontrolujBraniMimo(figurka, i, ip, jp);
        //jedna se o promenu
        if (figurka instanceof Pesec p && p.jdeOPromenu(ip))  {
            obsluzPromenu(figurka, ip, jp);
        }
        //aktualizujeme atribut pro remizu
        if (figurka instanceof Pesec || this.getByloBrano()) {
            this.setPocetTahuBezBraniAPesce(0);
        } else {
            this.setPocetTahuBezBraniAPesce(this.getPocetTahuBezBraniAPesce() + 1);
        }
        //vytvorime fen retezec
        this.getFEN_ALG().fenAlgoritmus(this);
        //aktualizujeme tahy figurkam
        aktualizujTahyFigurkam();
        //aktualizujeme sach
        zkontrolujSach(figurka);
    }

    /**
     * Metoda zajisti promenu pesce. Nabidne uzivateli vstup a nasledne ho zpracuje a vytvori pozadovanou figurku.
     * @param figurka - Pesec, ktery tahne a dostal se na misto promeny.
     * @param ip - Index radku ciloveho pole promeny.
     * @param jp - Index sloupce ciloveho pole promeny.
     */
    private void obsluzPromenu(AFigurka figurka, int ip, int jp) {
        AFigurka novaF = null;
        boolean nactiej = true;

        while (nactiej) {
            String vyber = JOptionPane.showInputDialog(null,
                    "Pěšec může být proměněn!\nZadejte: dama, vez, kun nebo strelec:",
                    "Proměna pěšce", JOptionPane.QUESTION_MESSAGE);

            if (vyber == null) {
                vyber = "dama";
            }

            vyber = vyber.toLowerCase();
            switch (vyber) {
                case "strelec":
                    novaF = new Strelec(ip, jp, this.getHraciPole()[ip][jp].getHeight(), figurka.getTym(), this);
                    nactiej = false;
                    break;
                case "kun":
                    novaF = new Kun(ip, jp, this.getHraciPole()[ip][jp].getHeight(), figurka.getTym(), this);
                    nactiej = false;
                    break;
                case "vez":
                    novaF = new Vez(ip, jp, this.getHraciPole()[ip][jp].getHeight(), figurka.getTym(), this);
                    nactiej = false;
                    ((Vez)novaF).setPohnulSe(true);
                    break;
                case "dama":
                    novaF = new Dama(ip, jp, this.getHraciPole()[ip][jp].getHeight(), figurka.getTym(), this);
                    nactiej = false;
                    break;
                default:
                    JOptionPane.showMessageDialog(null,
                            "Neplatný výběr! Zadejte: dama, vez, kun nebo strelec.",
                            "Chyba", JOptionPane.ERROR_MESSAGE);
            }
        }

        nahradFigurku(figurka, novaF);
    }

    /**
     * Metoda nahradi starou figurku za novou. Tzn. aktualizuje atributy STAV_HRY, figurky, nastavi pozice figurce a
     * vytvori telo.
     * @param figurka - Predana stara figurka ktera se nahrazuje.
     * @param novaF - Nova figurka ktera se vklada na sachovnici.
     */
    private void nahradFigurku(AFigurka figurka, AFigurka novaF) {
        for (int i = 0; i < this.getFIGURKY().length; i++) {
            if (this.getFIGURKY()[i] != null && this.getFIGURKY()[i].equals(figurka)) {
                //nahradime figurku
                this.getSTAV_HRY()[figurka.getIndexRadkuPoleF()][figurka.getIndexSloupcePoleF()] = novaF;
                novaF.setPoziceX(this.getHraciPole()[figurka.getIndexRadkuPoleF()][figurka.getIndexSloupcePoleF()].getX());
                novaF.setPoziceY(this.getHraciPole()[figurka.getIndexRadkuPoleF()][figurka.getIndexSloupcePoleF()].getY());
                novaF.vytvorT();
                this.getFIGURKY()[i] = novaF;
            }
        }
    }

    /**
     * Metoda zkontroluje jestli figurka dostal svymi moznymi tahy nepratelskeho krale do sachu a dle toho nastavi sach.
     * @param hrajiciFigurka - Instance figurky ktera hraje.
     */
    private void zkontrolujSach(AFigurka hrajiciFigurka) {
        //vytvorime tahy figurce
        hrajiciFigurka.vytvorMozneTahy();
        //zjistime jestli dostala krale do sachu a pokud ano tak mu nastavime prislusny atribut
        Kral k = hrajiciFigurka.dostalaFigurkaKraleDoSachu(hrajiciFigurka.getMozneTahy());
        if (k != null) {
            k.setSach(true);
            System.out.println("SACH !!!!!!!!!!!!!!!!!!");
            this.VYKRESLI_SACH[0] = true;
            this.VYKRESLI_SACH[1] = k;

        } else {
            //pokud figurka nedala krali sach pak muisme zkontrolovat jestli ostatni figurky stejneho tymu nedavaji
            //nepratelskemu krali sach
            k = hrajiciFigurka.najdiNepratelskehoKrale(this.getFIGURKY());
            k.setSach(ohrozujiFigurkyKrale(k));
        }
    }


    /**
     * Metoda zjisti jestli je kral predany v parametru metody v sachu.
     * @param kral - Predany Kral.
     * @return True pokud je v sachu, jinak false.
     */
    private boolean ohrozujiFigurkyKrale(Kral kral) {
        for (int k = 0; k < this.getFIGURKY().length; k++) {
            if (kral.jeFigurkaAJeNepratelska(this.getFIGURKY()[k])) {
                this.getFIGURKY()[k].vytvorMozneTahy();
                if (this.getFIGURKY()[k].dostalaFigurkaKraleDoSachu(this.getFIGURKY()[k].getMozneTahy()) != null) {
                    System.out.println("SACH !!!!!!!!!!!!!!!!!!");
                    this.VYKRESLI_SACH[0] = true;
                    this.VYKRESLI_SACH[1] = kral;
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Metoda presune figurku z pole danymi indexy (i,j) na pole s danymi indexy (ip,ij).
     * Aktualizuje pozici, indexy pole figurky ve kterem se nachazi, vytvori telo.
     * @param i - Index urcujici radek zacatecniho pole na sachovnici.
     * @param j - Index urcujici sloupec zacatecniho pole na sachovnici.
     * @param ip - Index urcujici radek ciloveho pole na sachovnici.
     * @param jp - Index urcujici sloupec ciloveho pole na sachovnici.
     * @param figurka - Presouvana figurka.
     */
    public void udelejVnitrniTah(int i, int  j, int  ip, int  jp, AFigurka figurka) {
        //udelame presun
        this.STAV_HRY[i][j] = null; //odstranime figurku z puvodniho mista
        this.STAV_HRY[ip][jp] = figurka; //umistime figurku na pole
        // nastavime figurce pozici
        figurka.setPoziceX(this.hraciPole[ip][jp].getX());
        figurka.setPoziceY(this.hraciPole[ip][jp].getY());
        // aktualizujeme ji indexy herniho pole kteremu nalezi
        figurka.setIndexRadkuPoleF(ip);
        figurka.setIndexSloupcePoleF(jp);
        // aktualizujeme posuvy tela
        figurka.vytvorT();
    }

    /**
     * Metoda na zaklade indexu i a j najde pole a to vrati.
     * @param i - Urcuje radek ve kterem se danne pole nachazi.
     * @param j - Urcuje o kolikate pole se v danne radce jedna.
     * @return Hledane pole na indexech i a j nebo null pokud
     * jsou predane indexy mimo rozsah sachovnice.
     */
    private Rectangle2D getPole(int i, int j) {
        return this.hraciPole[i][j];
    }

    /**
     * Seter k atributum sirka, vyska, nejmensi a nejvetsi.
     * Slouzi k resetovani potrebnych atributu, pokud se zmeni rozmery okna.
     * @param s - Nastavi atribut sirka.
     * @param v - Nastavi atribut vyska.
     * @param nejm - Nastavi atribut nejmensi a delkaCtv.
     * @param nejv - Nastavi atribut nejvetsi.
     * @param obrazovka - Predana instance Dimension pro definovani rozmeru zarizeni.
     * @throws IllegalArgumentException Pokud je sirka, vyska, nejmensi nebo nejvetsiz rozmeru okna nevalidni.
     */
    public void setSachovnice(double s, double v, double nejm, double nejv, Dimension obrazovka) {
        overPlatnostRozmeruOkna((int) s, (int) v, obrazovka);

        if (nejm != Math.min(s, v)) {
            throw new IllegalArgumentException("Nevalidni mensi z rozmeru okna.");
        }

        if (nejv != Math.max(s, v)) {
            throw new IllegalArgumentException("Nevalidni vetsi z rozmeru okna.");
        }

        final double RADKY_D = 8.0;
        this.sirka = s;
        this.vyska = v;
        this.nejvetsi = nejv;
        this.nejmensi = nejm;
        delkaCtv = nejm/RADKY_D;
    }

    /**
     * Pomocna metoda pro overeni validity sirky a vysky vuci rozmeru obrazovky.
     * @param sirka - Sirka okna.
     * @param vyska - Vyska okna.
     * @param obrazovka - Predana instance Dimension pro definovani rozmeru zarizeni.
     * @throws IllegalArgumentException Pokud sirka nebo vyska nejsou validni vzhledem k obrazovce.
     */
    private void overPlatnostRozmeruOkna(int sirka, int vyska, Dimension obrazovka) {
        if (jeSirkaNevalidni(sirka, obrazovka) || jeVyskaNevalidni(vyska, obrazovka)) {
            throw new IllegalArgumentException("Nevalidni sirka nebo vyska okna.");
        }
    }

    /**
     * Metoda vytvori jednotliva pole sachovnice, ulozi je do dvourozmerneho pole,
     * nastavi atribut hraciPole.
     * Princip - V metode se nejdrive urci rozdil, ktery se vypocita z rozdilu nejvetstiho a nejmensiho.
     * Pote se rozdil vydeli dvemi, a dle toho, jestli sirka je vetsi, nebo mensi, se pricte rozdil k X, nebo k Y
     * (zajistuje metoda pocatecniPoziceSachovnice()).
     * Dale se definuje pole pomoci dvou for cyklu a atributu delkaCtv.
     */
    public void vytvorSachovnici() {
        double[] rozmery = pocatecniPoziceSachovnice();
        double zacatekCx = rozmery[0];
        double zacatekCy = rozmery[1];
        // Pro ulozeni hracih poli do pole
        Rectangle2D[][] hraciPole = new Rectangle2D[RADKY][SLOUPCE];
        for (int i = 0;  i < RADKY; i++) {
            for (int j = 0; j < SLOUPCE; j++) {
                //ctverec sachovnice
                hraciPole[i][j] = new Rectangle2D.Double(zacatekCx, zacatekCy, delkaCtv, delkaCtv);
                zacatekCx += delkaCtv;
            }
            zacatekCy += delkaCtv;
            zacatekCx  = rozmery[0];
        }
        this.hraciPole = hraciPole;
    }

    /**
     * Metoda zjisti pocatecni pozici scachovnice (bod X,Y) a delku jednotlivych poli,
     * tak aby sachovnice byla vycentrovana a zabirala co nejvetsi plochu.
     * @return double[] - Pole urcujici pocatecni bod X,Y sachovnice.
     */
    private double[] pocatecniPoziceSachovnice(){
        final double RADKY_D = 8.0;
        double[] rozdili= new double[2];
        double rozdil = nejvetsi - nejmensi;
        rozdili[0] = rozdil;
        rozdili[1] = rozdil;

        int stav = zjistiStavOkna();
        switch (stav) {
            case 0 -> {
                rozdili[0] = 0;
                rozdili[1] = 0;
            }
            case 1 -> {
                rozdili[0] = 0;
                rozdili[1] = rozdili[1]/2.0;
                delkaCtv = this.sirka/RADKY_D;
            }
            case 2 -> {
                rozdili[0] = rozdili[0]/2.0;
                rozdili[1] = 0;
                delkaCtv = this.vyska/RADKY_D;
            }
        }

        return rozdili;
    }

    /**
     * Pomocna metoda. Metoda zjisti stav okna a dle toho vrati oznaceni stavu.
     * @return int -> 1 pokud je sirka mensi nez vyska, 2 pokud je vetsi a 0 pokud jsou rozmery stejne.
     */
    private int zjistiStavOkna() {
        if(this.sirka < this.vyska) {
            return 1;
        }else if(this.sirka > this.vyska){
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * Metoda vykresli ctvercovou sachovnici, ktera bude na stredu okna.
     * a bude zabirat maximalni mozny prostor tohoto okna a v ni figurky.
     * @param c1 - Prvni barva poli
     * @param c2 - Druha barva poli
     * @param g2 - Instance tridy Graphics2D pro kresleni na platno.
     * @param vybranaF - Definuje figurku, ktere se maji vykreslit tahy (typicky u presouvane figurky).
     * @param dr -
     */
    public void vykresliSachovnici(Color c1, Color c2, Graphics2D g2, AFigurka vybranaF, DrawingPanel dr) {
        zkontrolujParametryVykresleni(c1, c2, g2);

        vykresliHerniPole(c1, c2, g2);

        if (this.getZnazorneniTahu() != null ) {
            g2.setColor(Color.BLUE);
            g2.fill(this.getHraciPole()[this.getZnazorneniTahu()[0]][this.getZnazorneniTahu()[1]]);
            g2.fill(this.getHraciPole()[this.getZnazorneniTahu()[2]][this.getZnazorneniTahu()[3]]);
        }

        if (vybranaF != null) {
            this.vykresliValidniTahy(g2);
        }

        vykresliVsechnyFigurky(g2);

        if (vybranaF != null) {
            vybranaF.vykresliFigurku(g2);
        }

        upozorniSachNeboKonec(dr);
    }

    /**
     * Metoda zobrazi upozornovaci panel s popiskem pokud nastane sach poprpade hlasku pro vysledek hry.
     * @param dr - Hlavni komponenta (panel na ktery se vykresluje.
     */
    private void upozorniSachNeboKonec(DrawingPanel dr) {
        if ((boolean)this.VYKRESLI_SACH[0] && !((boolean)VYKRESLI_KONEC[0])) {
            zobrazDialog(dr, ((AFigurka)VYKRESLI_SACH[1]).getPopis() + " dostal šach !!!!!!!!!!", 2000);
            this.VYKRESLI_SACH[0] = false;
            this.VYKRESLI_SACH[1] = null;
        }

        if ((boolean)this.VYKRESLI_KONEC[0]) {
            zobrazDialog(dr, (String)VYKRESLI_KONEC[1], 5000);
            this.VYKRESLI_KONEC[0] = false;
            this.VYKRESLI_KONEC[1] = "";
            this.VYKRESLI_SACH[0] = false;
            this.VYKRESLI_SACH[1] = null;
        }
    }
    
    /**
     * Metoda se pouziva pro zobrazeni dialogu a v ni predane zpravy vycentrovane horizontalne na stred s posuvnikem,
     * pokud dojde k preteceni dialogu. Dialog zobrazi na pozadovany pocet milisekund uprosted sachovnice,pak ho zavre.
     * @param hlavniKomponenta - Hlavni komponenta na ktere se vykresluje.
     * @param zprava - Predana zprava.
     * @param mls - Cas v milisekundach pro dobu zobrazeni dialogu
     */
    private void zobrazDialog(Component hlavniKomponenta, String zprava, int mls) {
        //vytvorime JDialog s upozornenim ktery bude modalni (nezastavi hru)
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(hlavniKomponenta), "Upozornění",
                                                                        false);
        //urcime rozmery dialogu a odsazeni
        final int ODSAZENI = (int)(this.nejmensi * 0.1);
        final int SIRKA_DIALOGU = (int)(this.nejmensi);
        final int VYSKA_DIALOGU = (SIRKA_DIALOGU / 2);
        //vytvoreni textoveho pole pro zobrazeni zpravy vycentrovane na stred horizontalne
        JTextPane textPol = new JTextPane();
        //nastaveni zobrazovaneho textutext
        textPol.setText(zprava);
        //zakaz editace textu (jen pro cteni)
        textPol.setEditable(false);
        //zakaz kurzoru a zamerovani (neinteraktivni)
        textPol.setFocusable(false);
        //nastaveni pisma (Arial, tucne, velikost podle sirky dialogu)
        textPol.setFont(new Font("Arial", Font.BOLD, SIRKA_DIALOGU / 20));
        //nastaveni pozadi stejne jako ma dialog
        textPol.setBackground(dialog.getBackground());
        //vnitrni okraje (odsazeni textu od okraju)
        textPol.setBorder(BorderFactory.createEmptyBorder(ODSAZENI, ODSAZENI, ODSAZENI, ODSAZENI));
        //nastaveni velikosti textoveho pole
        textPol.setPreferredSize(new Dimension(SIRKA_DIALOGU, VYSKA_DIALOGU));
        //prizpusobeni aby textove pole bylo pruhledne (neprepisuje pozadi)
        textPol.setOpaque(false);
        //ziskani obsahu z textoveho pole
        StyledDocument doc = textPol.getStyledDocument();
        //vytvoreni sady atributu pro zarovnani
        SimpleAttributeSet center = new SimpleAttributeSet();
        //nastaveni zarovnani na stred (horizontalne)
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        //aplikovani zarovnani na cely text (od pozice 0 po konec)
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        //scroll pokud by tex pretekl areu
        JScrollPane posuny = new JScrollPane(textPol);
        //horizontalne nepotrebujem protze zalomujeme
        posuny.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //vertikalne zalomujeme
        posuny.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        //nebudeme mit ramecek
        posuny.setBorder(null);
        //pridame areu na dialog
        dialog.getContentPane().add(posuny);
        //nastavime velikost dialogu
        dialog.setSize(SIRKA_DIALOGU, VYSKA_DIALOGU);
        //vycentrovani popisku do stredu sachovnice (neboli okna protoze sachovnice se vykresluje vzdy doprostred okna)
        int x = ziskejSouradniceProDialog(hlavniKomponenta, dialog)[0];
        int y = ziskejSouradniceProDialog(hlavniKomponenta, dialog)[1];
        dialog.setLocation(x, y);
        //zobrazime dialog
        dialog.setVisible(true);
        //casovac na 2 sekundy a pak se zavre okno
        Timer zavirac = new Timer(mls, e -> dialog.dispose());
        //jenom jednou se spusti
        zavirac.setRepeats(false);
        //spustime casovac
        zavirac.start();
    }

    /**
     * Pomocna metoda. Ziska souradnice pro vykresleni dialogu uprosted sachovnice, ktera je veporsted hlavni
     * komponenty.
     * @param hlavniKomponenta - Hlavni komponenta na ktere se vykresluje.
     * @param dialog - Pradny dialog.
     * @return int[] -> kde prvni prvek odpovida x-ove souradnici a druhy y-ove.
     */
    private int[] ziskejSouradniceProDialog(Component hlavniKomponenta, JDialog dialog) {
        Point lokace = hlavniKomponenta.getLocationOnScreen();
        int x = lokace.x + (int)((this.getSirka() - dialog.getWidth()) / 2);
        int y = lokace.y + (int)((this.getVyska() - dialog.getHeight()) / 2);

        return new int[] {x, y};
    }

    /**
     * Pomocna metoda pro vykresleni vsech figurek na sachovnici.
     * @param g2 - Instance tridy Graphics2D pro kresleni na platno.
     */
    private void vykresliVsechnyFigurky(Graphics2D g2) {
        for (AFigurka figurka : FIGURKY) {
            if (figurka != null) {
                figurka.vykresliFigurku(g2);
            }
        }
    }

    /**
     * Pomocna metoda pro kontrolu parametru metody pro vykresleni sachovice.
     * @param c1 - Prvni barva poli
     * @param c2 - Druha barva poli
     * @param g2 - Instance tridy Graphics2D pro kresleni na platno.
     * @throws IllegalArgumentException Pokud nektera z barev neni validni popripade instance g2.
     */
    private void zkontrolujParametryVykresleni(Color c1, Color c2, Graphics2D g2) {
        if (c1.equals(c2)) {
            throw new IllegalArgumentException("Predane barvy poli sachovnice jsou stejne.");
        }
        zkontrolujBarvuPole(c1);
        zkontrolujBarvuPole(c2);

        if (g2 == null) {
            throw new IllegalArgumentException("Pradana instance Graphics2D je null");
        }
    }

    /**
     * Pomocna metoda. Zkontroluje jestli barva neodpovida nektere barve z figurek patrici hracum.
     * @param c - Instance tridy Color.
     * @throws IllegalArgumentException - Pokud barva uz patri nekomu z hracu nebo je barva null.
     */
    private void zkontrolujBarvuPole(Color c) {
        if (c == null) {
            throw new IllegalArgumentException("Predana barva pole sachovnice je null.");
        }

        if (c.equals(this.getTym1()) || c.equals(this.getTym2())) {
            throw new IllegalArgumentException(("Predana barva pro pole odpovida barve figurek jednoho z hracu."));
        }
    }

    /**
     * Pomocna metoda. Vykresli herni pole sachovnice.
     * @param c1 - Prvni barva poli
     * @param c2 - Druha barva poli
     * @param g2 - Instance tridy Graphics2D pro kresleni na platno.
     */
    private void vykresliHerniPole( Color c1, Color c2, Graphics2D g2) {
        Color barva;
        for (int i = 0;  i < hraciPole.length; i++) {
            barva = (i % 2 == 1) ? c2 : c1;
            for (int j = 0; j < hraciPole.length; j++) {
                g2.setColor(barva);
                g2.fill(hraciPole[i][j]);
                barva = (barva.equals(c1)) ? c2 : c1;
            }
        }
    }

    /**
     * Metoda prida figurku do pole figurek.
     * @param f - Instance figurky.
     * @throws IllegalArgumentException Pokud je predana figurka null.
     */
    public void pridejF(AFigurka f) {
        if (f == null) {
            throw new IllegalArgumentException("Predana figurka je null");
        }
        FIGURKY[pocetF] = f;
        pocetF++;
    }

    /**
     * Geter k atributu figurky.
     * @return figurky.
     */
    public AFigurka[] getFIGURKY() {
        return this.FIGURKY;
    }

    /**
     * Metoda ziska indexy pole ve kterem se nachazi body (x, y).
     * @param x - X-ova souradnice.
     * @param y - Y-ova souradnice.
     * @return int[] indexu pole nebo null.
     */
    public int[] getIndexyPole(double x, double y) {
        for (int i = 0; i < RADKY; i++) {
            for (int j = 0; j < SLOUPCE; j++) {
                if (this.hraciPole[i][j].contains(x, y)) {//najdeme pole
                    return new int[]{i, j};
                }
            }
        }

        return null; //nenasli jsme pole
    }

    /**
     * Metoda vyhodi figurky tzn. odstrani odkazy na instanci v stavuHry a v atributu figurky.
     * @param i - Index radku ve kterem se figurka nachazi.
     * @param j - Index sloupce ve kterem se figurka nachazi.
     * @param f - Instance figurky.
     * @return False pokud uspesne, jinak true.
     */
    private boolean vyhodFigurku(int i, int j, AFigurka f) {
        this.STAV_HRY[i][j] = null;
        for (int k = 0; k < this.FIGURKY.length; k++) {
            if (f.equals(this.FIGURKY[k])) {
                this.FIGURKY[k] = null;
                return false;
            }
        }
        return true;
    }

    /**
     * Metoda zjisti jestli jsou figurky stejneho tymu.
     * @param f1 - Instance prvni porovnavane figurky.
     * @param f2 - Instance druhe porovnavane figurky.
     * @throws IllegalArgumentException Pokud nektera z figurek je null.
     * @return True pokud jsou, jinak false.
     */
    public boolean jsouFigStejnehoTymu(AFigurka f1, AFigurka f2) {
        if (f1 == null || f2 == null) {
            throw new IllegalArgumentException("Nektera z predanych figurek je null.");
        }

        return f1.getTym().equals(f2.getTym());
    }

    /**
     * Metoda zjisti jestli se na poli urcenymi indexy nachazi figurka.
     * @param i - Index radku pole sachovnice.
     * @param j - Index sloupce pole sachovnice.
     * @throws IllegalArgumentException Pokud predane indexy nejsou v sachovnici.
     * @return true pokud ano jinak false.
     */
    public boolean jeNaPoliFigurka(int i, int j) {
        if (nejsouIndexyVSachovnici(i, j)) {
            throw new IllegalArgumentException("Predane indexy nesjou v sachovnici.");
        }

        return this.getSTAV_HRY()[i][j] != null;
    }

    /**
     * Metoda zjisti jestli je index v hranici sachovnice.
     * @param iSloupce Index sloupce v sachovnici.
     * @return True pokud index je v v hranici sachovnice, jinak false.
     */
    public boolean jeISloupVSachovnici(int iSloupce) {
        return (iSloupce >= HORNI_A_LEVA_HRANICE_SACH && iSloupce <= DOLNI_A_PRAVA_HRANICE_SACH);
    }

    /**
     * Metoda zjisti jestli je index v hranici sachovnice.
     * @param iRadku Index radku v sachovnici.
     * @return True pokud index je v v hranici sachovnice, jinak false.
     */
    public boolean jeIRadkuVSachovnici(int iRadku) {
        return (iRadku >= HORNI_A_LEVA_HRANICE_SACH && iRadku <= DOLNI_A_PRAVA_HRANICE_SACH);
    }

    /**
     * Geter k atributu HORNI_A_LEVA_HRANICE_SACH.
     * @return HORNI_A_LEVA_HRANICE_SACH.
     */
    public int getHORNI_A_LEVA_HRANICE_SACH() {
        return this.HORNI_A_LEVA_HRANICE_SACH;
    }

    /**
     * Geter k atributu DOLNI_A_PRAVA_HRANICE_SACH.
     * @return DOLNI_A_PRAVA_HRANICE_SACH.
     */
    public int getDOLNI_A_PRAVA_HRANICE_SACH() {
        return this.DOLNI_A_PRAVA_HRANICE_SACH;
    }

    /**
     * Geter k atributu radky.
     * @return radky.
     */
    public int getRadky() {
        return RADKY;
    }

    /**
     * Geter k atributu tym1.
     * @return tym1.
     */
    public Color getTym1() {
        return this.tym1;
    }

    /**
     * Geter k atributu tym2
     * @return tym2.
     */
    public Color getTym2() {
        return this.tym2;
    }

    /**
     * Geter k atributu naTahu.
     * @return naTahu.
     */
    public Color getNaTahu() {
        return this.naTahu;
    }

    /**
     * Nastavi kdo je dalsi na tahu.
     * @param tymAktualneNaTahu Color.
     * @throws IllegalArgumentException Pokud predana barva je null.
     */
    public void setNaTahu(Color tymAktualneNaTahu) {
        if (tymAktualneNaTahu == null) {
            throw new IllegalArgumentException("Predana barva hrace na tahu je null.");
        }

        if (tymAktualneNaTahu.equals(tym1)) {
            this.naTahu = tym2;
        } else {
            this.naTahu = tym1;
        }
    }

    /**
     * Metoda aktualizuje mozne tahy vsem figurkam.
     */
    private void aktualizujTahyFigurkam() {
        for (AFigurka figurka : this.FIGURKY) {
            if (figurka != null) {
                figurka.vytvorMozneTahy();
            }
        }
    }

    /**
     * Geter k atributu validniTahy.
     * @return validniTahy.
     */
    public Rectangle2D[] getValidniTahy() {
        return validniTahy;
    }

    /**
     * Nastavi validni atribut validniTahy.
     * @param validniTahy Rectangle2D[].
     */
    public void setValidniTahy(Rectangle2D[] validniTahy) {
        this.validniTahy = validniTahy;
    }

    /**
     * Metoda vyfiltruje pole moznych tahu figurky o pole ktera by dostala jejiho krale do sachu.
     * Princip spociva v tom, ze se vnitrne provede tah s figurkou na mozny tah. Pote si vytvori vsechny nepratelske
     * figurky tahy a pokud nektery z techto tahu bude obsahovat pole na kterem je kral testovane figurky pak se jedna
     * o sach a pole se neprida do validnich tahu.
     * @param mozneTahy - Mozne tahy figurky.
     * @param vybranaF - Tahnouci figurka.
     */
    public void filtrujSachNaMoznychTazich(Rectangle2D[] mozneTahy, AFigurka vybranaF) {
        if (mozneTahy == null ) {
            throw new IllegalArgumentException("Predane mozne tahy jsou null");
        }

        if (vybranaF == null) {
            throw new IllegalArgumentException("Predana figurka je null");
        }

        Rectangle2D[] validniTahy = new Rectangle2D[mozneTahy.length];
        //provedeme vnitrne krok tzn ulozime pred provedenim potrebne veci pro navrat
        StavFigurky stavF = ulozStavFigurky(this.getFIGURKY(), vybranaF);
        //udelame filtr na vybrane tahy
        for (int i = 0; i < vybranaF.getPocitadlo(); i++) { //projdeme kazdy mozny tah figurky
            validniTahy[i] = provedSimulaciTahuNaMoznePole(mozneTahy, i, vybranaF, stavF);
        }
        //nastavime validni tahy
        this.setValidniTahy(validniTahy);
    }

    /**
     * Metoda slouzi k ulozeni klicovych atributu pro figurku pred vykonanim vnitrniho tahu pro zaverecnou filtraci
     * moznych tahu.
     * @param figurky - Pole figurek na sachovnici.
     * @param vybranaF - Vybrana figurka.
     * @return StavFigurky objekt udrzujici klicove informace.
     */
    private StavFigurky ulozStavFigurky(AFigurka[] figurky , AFigurka vybranaF) {
        return new StavFigurky(vybranaF, figurky);
    }

    /**
     * Metoda provede simulacni tah na pole z moznych tahu danym indexem i. Zkontroluje jestli by tahem na toto pole
     * dostala sveho krale do sachu (nastavi atribut kralFigurkyByDostalSach) a dle toho pida pole do validnich tahu.
     * Nakonec vrati STAV_HRY do puvodniho stavu.
     * @param mozneTahy - Pole Rectangle2D[] reprezentujici mozne tahy figurky.
     * @param i - Index urcujici pole ktere se bude testovat.
     * @param vybranaF - Tahnouci figurka.
     * @param stavF - Objekt obsahujici ulozeny stav figurky.
     * @throws IllegalArgumentException Pokud bylo predano pole tahu, ktere obsahuje tah s nesmyslnimi souradnicemi.
     * @return Pole Rectangle2D obsahujci validni tah nebo null pokud tah neni validni (kral figurky by dostal sach).
     */
    private Rectangle2D provedSimulaciTahuNaMoznePole(Rectangle2D[] mozneTahy, int i, AFigurka vybranaF,
                                                        StavFigurky stavF) {
        AFigurka ulozVyhozovanouFigurku;
        boolean vyhazjeme = false;
        Rectangle2D validniTah = null;

        int[] indexyPole = this.getIndexyPole(mozneTahy[i].getX(), mozneTahy[i].getY());
        if (indexyPole == null) {
            throw new IllegalArgumentException("Ziskane indexy pole z predaneho tahu se nepodarilo ziskat");
        }
        //ulozime si stav ciloveho mista
        ulozVyhozovanouFigurku = this.STAV_HRY[indexyPole[0]][indexyPole[1]];
        //pokud budeme vyhazovat
        if (this.STAV_HRY[indexyPole[0]][indexyPole[1]] != null) {
            vyhazjeme = true;
        }
        //provedem tah a posoudime sach z pohledu nepratelskych figurek
        this.provedSimulacniTahAPosudSach(vybranaF.getIndexRadkuPoleF(), vybranaF.getIndexSloupcePoleF(), indexyPole[0],
                                          indexyPole[1]);
        //pokud by kral nedostal sach
        if (!this.kralFigurkyByDostalSach) {
            validniTah = mozneTahy[i];
        }
        //vratime stav hry do puvodniho stavu
        vratSTAV_HRY(indexyPole, ulozVyhozovanouFigurku, vybranaF, vyhazjeme, stavF);
        //vratime validni tahy
        return validniTah;
    }

    /**
     * Metoda vrati stav hry do puvodniho stavu.
     * @param indexyPole - Indexy urcujici pole na sachovnici kde se aktualne nachazi figurka.
     * @param ulozVyhozovanouFigurku - Stav mista pole na sachovnici pred tim nez jsme tam poslali figurku.
     * @param vybranaF - Vybrana figurka.
     * @param vyhazjeme - Boolean urcujici jestli jsme pri presunu figurky vyhodili nepratelskou figurku.
     * @param stavF - Obejkt obsahujici puvodni stav figurky.
     */
    private void vratSTAV_HRY(int[] indexyPole, AFigurka ulozVyhozovanouFigurku, AFigurka vybranaF, boolean vyhazjeme,
                            StavFigurky stavF) {
        //musime vratit tah, figurku a potrebne atributy
        this.provedSimulacniTahAPosudSach(indexyPole[0], indexyPole[1], stavF.getIndexRPuvodnihoPole(),
                                          stavF.getIndexSPuvodnihoPole());
        //vratime stav ciloveho pole
        this.STAV_HRY[indexyPole[0]][indexyPole[1]] = ulozVyhozovanouFigurku;
        //obnovime figurce klicove atributy
        stavF.obnovStavFigurky(vybranaF, this.getFIGURKY());
        //pokud jsme vyhodili pri testu figurku tak ji vratime zpet
        if (vyhazjeme) {
            //vratime figurku do pole
            for (int k = 0; k < this.getFIGURKY().length; k++) {
                if (this.getFIGURKY()[k] == null) {
                    this.getFIGURKY()[k] = ulozVyhozovanouFigurku;
                    break;
                }
            }
        }
    }

    /**
     * Metoda vykresli validni tahy na sachovnici.
     * @param g2 - Instance tridy Graphics2D.
     */
    private void vykresliValidniTahy(Graphics2D g2) {
        for (Rectangle2D validniTah : this.validniTahy) {
            if (validniTah != null) {
                g2.setColor(Color.RED);
                g2.fill(validniTah);
                g2.setColor(Color.GREEN);
                g2.draw(validniTah);
            }
        }
    }

    /**
     * Metoda provede tah a zkontroluje jestli nedostala sveho krale tahem do sachu
     * (nastavi atribut kralFigurkyByDostalSach). Provadi temer to same co metoda proved tah akorat neaktulizuje
     * atributy pro rosady, brani mimochodem a sach, jelikoz chceme jenom posoudit stav na sachovnici po tahu a
     * pak ho zase vratit.
     * @param i - Index radku na kterem se figurka nachazi.
     * @param j - Index sloupce na kterem se figurka nachazi.
     * @param ip - Index radku na ktery se bude figurka presouvat.
     * @param jp - Index sloupc na ktery se bude figurka presouvat.
     */
    private void provedSimulacniTahAPosudSach(int i, int j, int ip, int jp) {
        AFigurka figurka;
        figurka = this.STAV_HRY[i][j];
        //zkontroluje jak je na tom cilove pole a pripadne vyhodime figurku
        zkontrolujCilovePole(figurka, ip, jp);
        //udelame tah
        udelejVnitrniTah(i, j, ip, jp, figurka);
        //nastavime atribut upozornujici na sach
        this.kralFigurkyByDostalSach = figurka.dostalaTahemKraleDoSachu();
    }

    /**
     * Metoda zjisti jestli je pole volne (pokud je figurka ciziho tymu
     * v poli pak se bere pole jako volne).
     * @param x - X-ova souradnice presunu.
     * @param y - Y-ova souradnice presunu.
     * @param vybranaF - Tahnouci figurka.
     * @throws IllegalArgumentException Pokud predana tahnouci figurka je null.
     * @return True pokud je pole volne nebo tam je figurka protejsiho tymu, jinak false.
     */
    public boolean jePoleVolne(double x, double y, AFigurka vybranaF) {
        int[] indexyPole;

        indexyPole = this.getIndexyPole(x, y);
        if (indexyPole == null) {
            return false;
        }

        if (vybranaF == null) {
            throw new IllegalArgumentException("Predana tahnouci figurka je null.");
        }

        return jePoleVolneNaIndexech(indexyPole[0], indexyPole[1], vybranaF);
    }

    /**
     * Pomocna metoda zjisti, zda je pole na dane pozici volne nebo obsazene figurkou protihrace.
     * @param i - Index radku.
     * @param j - Index sloupce.
     * @param vybranaF - Tahnouci figurka.
     * @return True pokud pole neobsahuje zadnou figurku nebo obsahuje figurku soupere.
     */
    private boolean jePoleVolneNaIndexech(int i, int j, AFigurka vybranaF) {
        if (!this.jeNaPoliFigurka(i, j)) {
            return true;
        }

        return !jsouFigStejnehoTymu(this.getSTAV_HRY()[i][j], vybranaF);
    }

    /**
     * Metoda vrati posledni uskutecneny tah a k tomu i aktualizuje potrebne atributy figurkam. Vyuziva fen retezcu.
     */
    public void vratTah() {
        if (jeKonecHry()) {
            this.setKonecHry(false);
        }
        int iRAktualniF = this.getZnazorneniTahu()[2];
        int iSAktualniF = this.getZnazorneniTahu()[3];
        int iRPuvodniPoziceF = this.getZnazorneniTahu()[0];
        int iSPuvodniPoziceF = this.getZnazorneniTahu()[1];
        AFigurka puvdoneVyhozenaF;
        //pokud vracime prvni tah tak muzeme figurku natvrdo vrati bez kontrol
        if (this.getFEN_ALG().getPRUBEH_HRY().size() <= 2) {
            vratPrvniTah(iRAktualniF, iSAktualniF, iRPuvodniPoziceF, iSPuvodniPoziceF);
            return;
        }
        //pokud se jedna o tah vezi nebo kralem zajistime zpetnou aktualizaci atributu pohnuti pro rosady
        this.getFEN_ALG().aktualizujPohnutiVezAKral(this);
        //zkontrolujeme jestli se nevraci promena pesce a pripadne mu nastavime indexy pozice pred promenou,
        //jinak pouze vratime figurku.
        vratPesunutouF(iRAktualniF, iSAktualniF, iRPuvodniPoziceF, iSPuvodniPoziceF);
        //aktualizujeme pescum atribut pro brani mimo (jestli mohou byt technikou vyhozeni).
        this.getFEN_ALG().aktualizujBraniMimoUPescu(this);
        //pokud jsme predchozim tahem vyhodili figurku musime ji vratit
        puvdoneVyhozenaF = this.getFEN_ALG().zjistiVyhozenouF(this);
        if (puvdoneVyhozenaF != null) { //pokud jsme vyhazovali
            vratFigurkuDoHry(puvdoneVyhozenaF.getIndexRadkuPoleF(), puvdoneVyhozenaF.getIndexSloupcePoleF(), puvdoneVyhozenaF);
        }
        //odstranime posledni z fen retezcu a aktualizujeme je
        this.getFEN_ALG().odstranPosledniTah();
        //aktualizujeme atribut pro remizu 50 tahu
        this.setPocetTahuBezBraniAPesce(this.getFEN_ALG().ziskejPocetZPoslednihoFen());
        //ziskame indexy posledniho tahu a nimi nastavime prislusny atribut sachovnice pro znazorneni predchoziho tahu
        aktualizujZnazorneniTahu();
        //aktualizujeme sach neboli ziskame krale hrace ktery bude hrat najdeme jeho krale a zkontroluje
        //jestli jeho figurky davaji sach a pripadne nastavime
        Kral k = ziskejKrale(this.getNaTahu());
        zkontrolujSach(k);
    }

    /**
     * Pomocna Metoda. Metoda vrati prvni tah hry. Specialni pripad kdy nemusime kontrolovat mimochodem a veze a krale
     * @param iRAktualniF - Index urcujici radek zacatecniho pole na sachovnici.
     * @param iSAktualniF - Index urcujici sloupec zacatecniho pole na sachovnici.
     * @param iRPuvodniPoziceF - Index urcujici radek ciloveho pole na sachovnici.
     * @param iSPuvodniPoziceF - Index urcujici sloupec ciloveho pole na sachovnici.
     */
    private void vratPrvniTah(int iRAktualniF, int iSAktualniF, int iRPuvodniPoziceF, int iSPuvodniPoziceF) {
        udelejVnitrniTah(iRAktualniF, iSAktualniF, iRPuvodniPoziceF, iSPuvodniPoziceF, this.getSTAV_HRY()[iRAktualniF][iSAktualniF]);
        if (this.getFEN_ALG().getPRUBEH_HRY().size() == 2) {
            //odstranime posledni z fen retezcu a aktualizujeme je
            this.getFEN_ALG().odstranPosledniTah();
            //aktualizujeme pocet pro remizu
            this.setPocetTahuBezBraniAPesce(this.getFEN_ALG().ziskejPocetZPoslednihoFen());
        }

        this.setZnazorneniTahu(new int[] {iRPuvodniPoziceF, iSPuvodniPoziceF, iRPuvodniPoziceF, iSPuvodniPoziceF});
    }

    /**
     * Pomocna metoda. Vrati figurku, ktera tahla v poslednim tahu na puvodni misto. Pocita i s promenou pescu.
     * @param iRAktualniF - Index urcujici radek pole cile posledniho tahu na sachovnici.
     * @param iSAktualniF - Index urcujici sloupec pole cile posledniho tahu na sachovnici.
     * @param iRPuvodniPoziceF - Index urcujici radek pole zacatku posledniho tahu na sachovnici.
     * @param iSPuvodniPoziceF - Index urcujici sloupec pole zacatku posledniho tahu na sachovnici.
     * @throws IllegalStateException Pokud se nepodarilo vyhodit promenenou figurku.
     */
    private void vratPesunutouF(int iRAktualniF, int  iSAktualniF, int iRPuvodniPoziceF, int iSPuvodniPoziceF) {
        AFigurka pesec = this.getFEN_ALG().nastalaPromena(iRAktualniF, iSAktualniF, iRPuvodniPoziceF, iSPuvodniPoziceF, this);
        if (pesec != null) { //pokud doslo k promene, tak musime vyhodit figurku a vratit pesce
            if (vyhodFigurku(iRAktualniF, iSAktualniF, this.getSTAV_HRY()[iRAktualniF][iSAktualniF])) {
                throw new IllegalStateException("Promenenou figurku se nepodarilo vyhodit");
            }
            vratFigurkuDoHry(pesec.getIndexRadkuPoleF(), pesec.getIndexSloupcePoleF(), pesec);
        } else {
            //vratime figurku na tvrdo bez kontrol sachu atd.
            udelejVnitrniTah(iRAktualniF, iSAktualniF, iRPuvodniPoziceF, iSPuvodniPoziceF, this.getSTAV_HRY()[iRAktualniF][iSAktualniF]);
        }
    }

    /**
     * Pomocna metoda. Vrati puvodne vyhozenou figurku do hry.
     * @param iRAktualniF - Index urcujici radek pole cile posledniho tahu na sachovnici.
     * @param iSAktualniF - Index urcujici sloupec pole cile posledniho tahu na sachovnici.
     * @param puvdoneVyhozenaF - Nova instance figurky, ktera byla vyhozena.
     */
    private void vratFigurkuDoHry(int iRAktualniF, int  iSAktualniF, AFigurka puvdoneVyhozenaF) {
        //figurku vratime
        puvdoneVyhozenaF.setPoziceX(this.getHraciPole()[iRAktualniF][iSAktualniF].getX());
        puvdoneVyhozenaF.setPoziceY(this.getHraciPole()[iRAktualniF][iSAktualniF].getY());
        puvdoneVyhozenaF.vytvorT();
        this.getSTAV_HRY()[iRAktualniF][iSAktualniF] = puvdoneVyhozenaF;
        for (int i = 0; i < this.getFIGURKY().length; i++) {
            if (this.getFIGURKY()[i] == null) {
                this.getFIGURKY()[i] = puvdoneVyhozenaF;
                break;
            }
        }
        //aktualizace atributu pro urcite figurky
        if (puvdoneVyhozenaF instanceof Vez v) {
            v.setPohnulSe(this.getFEN_ALG().pohlaSeVez(this, v));
        }

        if (puvdoneVyhozenaF instanceof Pesec p) {
            p.setBraniMimochodem(this.getFEN_ALG().muzeBytBranMimo(this, p));
        }
    }

    /**
     * Pomocna metoda. Zjisti idnexy posledniho tahu z fen retezcu a nastavi jimi prislusny atribut sachovnice.
     * @throws IllegalStateException Pokud nastala chyba pri zjisteni indexu posledniho tahu.
     */
    private void aktualizujZnazorneniTahu() {
        int[] indexy = this.getFEN_ALG().zjistiIndexyPoslednihoTahu(this);
        if (indexy[0] != -1) {
            this.setZnazorneniTahu(indexy);
        } else {
            throw new IllegalStateException("Pri zjistovani indexu posledniho tahu nastala chyba.");
        }
    }

    /**
     * Zjisti jestli nastal konec hry.
     * @return (boolean) -> konecHry.
     */
    public boolean jeKonecHry() {
        return konecHry;
    }

    /**
     * Nastavi konec hry.
     */
    private void setKonecHry(boolean stav) {
        this.konecHry = stav;
    }

    /**
     * Geter k atributu znazorneniTahu.
     * @return (int[]) -> znazorneniTahu
     */
    public int[] getZnazorneniTahu() {
        return znazorneniTahu;
    }

    /**
     * Seter k atributu znazorneniTahu.
     * @param znazorneniTahu - int[]
     */
    private void setZnazorneniTahu(int[] znazorneniTahu) {
        this.znazorneniTahu = znazorneniTahu;
    }

    /**
     * Geter k atributu pocetTahuBezBraniAPesce.
     * @return (int) -> pocetTahuBezBraniAPesce
     */
    public int getPocetTahuBezBraniAPesce() {
        return pocetTahuBezBraniAPesce;
    }

    /**
     * Seter k atributu pocetTahuBezBraniAPesce.
     * @param pocetTahuBezBraniAPesce - int
     */
    private void setPocetTahuBezBraniAPesce(int pocetTahuBezBraniAPesce) {
        this.pocetTahuBezBraniAPesce = pocetTahuBezBraniAPesce;
    }

    /**
     * Geter k atributu byloBrano.
     * @return (boolean) -> byloBrano
     */
    private boolean getByloBrano() {
        return byloBrano;
    }

    /**
     * Seter k atributu byloBrano.
     * @param byloBrano - boolean
     */
    private void setByloBrano(boolean byloBrano) {
        this.byloBrano = byloBrano;
    }

    /**
     * Geter k atributu FEN_ALG.
     * @return (ForsythEdwardsNotation) -> FEN_ALG
     */
    public ForsythEdwardsNotation getFEN_ALG() {
        return FEN_ALG;
    }

    /**
     * Metoda zjisti jestli jsou indexy radku a sloupce v sachovnici.
     * @param i - Index radku.
     * @param j - Index sloupce.
     * @return True pokud nejsou, jinak fasle.
     */
    public boolean nejsouIndexyVSachovnici(int i, int j) {
        return !this.jeISloupVSachovnici(j) || !this.jeIRadkuVSachovnici(i);
    }

    /**
     * Metoda zjisti jestli je konkretni instance sachovnice validni tzn ma inicializovane klicvoe atributy.
     * @return True pokud neni validni, jinak false.
     */
    public boolean neniSachovniceValidni() {
        return this.getTym1() == null || this.getFIGURKY() == null || this.getSTAV_HRY() == null;
    }

    /**
     * Metoda zjisti jestli index urcujici posledni tah jsou validni a jestli davaji smysl, tzn. na miste odkud se
     * tahlo je volno a na misto kam se tahlo neco je.
     * @param iRAktualniF - Index urcujici radek ciloveho pole tahu.
     * @param iSAktualniF - Index urcujici sloupec ciloveho pole tahu.
     * @param iRPuvodniPoziceF - Index urcujici radek zacatecniho pole tahu.
     * @param iSPuvodniPoziceF - Index urcujici sloupec zacatecniho pole tahu.
     * @throws IllegalArgumentException Pokud stavy poli pri vraceni tahu jsou nesmyslne.
     */
    public void zkontrolujIndexyPoslTahu(int iRAktualniF, int iSAktualniF, int iRPuvodniPoziceF, int iSPuvodniPoziceF) {
        overIndexyVSachovnici(iRAktualniF, iSAktualniF, iRPuvodniPoziceF, iSPuvodniPoziceF);

        if (this.getSTAV_HRY()[iRAktualniF][iSAktualniF] == null) {
            throw new IllegalArgumentException("Spatne indexy urcujici cilovy tah, kde se nachazi posledni tazena" +
                                               " figurka.");
        }

        if (this.getSTAV_HRY()[iRPuvodniPoziceF][iSPuvodniPoziceF] != null) {
            throw new IllegalArgumentException("Spatne indexy urcujici pole zacatku tahu, neboli pole, ktere figurka " +
                                               "opustila pri tahu.");
        }
    }

    /**
     * Pomocna metoda pro overeni, zda jsou vsechny indexy v ramci sachovnice.
     * @param iRAktualniF - Index urcujici radek ciloveho pole tahu.
     * @param iSAktualniF - Index urcujici sloupec ciloveho pole tahu.
     * @param iRPuvodniPoziceF - Index urcujici radek zacatecniho pole tahu.
     * @param iSPuvodniPoziceF - Index urcujici sloupec zacatecniho pole tahu.
     * @throws IllegalArgumentException Pokud nektery z indexu nelezi v sachovnici.
     */
    private void overIndexyVSachovnici(int iRAktualniF, int iSAktualniF, int iRPuvodniPoziceF, int iSPuvodniPoziceF) {
        if (nejsouIndexyVSachovnici(iRAktualniF, iSAktualniF) || nejsouIndexyVSachovnici(iRPuvodniPoziceF, iSPuvodniPoziceF)) {
            throw new IllegalArgumentException("Predane indexy nejsou v sachovnici.");
        }
    }

    /**
     * Pomocna metoda. Ziska krale predaneho tymu.
     * @param tym - instance tridy Color.
     * @return instance tridy Kral daneho tymu, jinak null.
     */
    private Kral ziskejKrale(Color tym) {
        for (AFigurka f : this.getFIGURKY()) {
            if (f instanceof Kral k && k.getTym().equals(tym)) {
                return k;
            }
        }

        throw new IllegalStateException("V poli figurek se nenachazi kral hrace.");
    }
}
