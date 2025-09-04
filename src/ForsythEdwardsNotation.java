import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Trida slouzici pro vytvoreni fen algoritmu obsahujic metody pro vytvareni a uchovani fen retezcu.
 * Vyuziva se pro urceni remizi, kdy na sachovnici nastanou 3 stejne stavy a pro vraceni tahu.
 * @author Filip Valtr
 */
public class ForsythEdwardsNotation {
    //== Konstantni tridny atributy
    /** Symbol pro prazdne pole ve FEN retezci */
    private static final char PRAZDNE_POLE = '.';
    /** Symbol pro cerneho pesce ve FEN retezci */
    private static final char PESEC_CERNY = 'p';
    /** Symbol pro cernou damu ve FEN retezci */
    private static final char DAMA_CERNA = 'q';
    /** Symbol pro cerneho krale ve FEN retezci */
    private static final char KRAL_CERNY = 'k';
    /** Symbol pro cernou vez ve FEN retezci */
    private static final char VEZ_CERNA = 'r';
    /** Symbol pro cerneho kone ve FEN retezci */
    private static final char KUN_CERNY = 'n';
    /** Symbol pro cerneho strelce ve FEN retezci */
    private static final char STRELEC_CERNY = 'b';
    /** Symbol pro bileho pesce ve FEN retezci */
    private static final char PESEC_BILI = 'P';
    /** Symbol pro bilou damu ve FEN retezci */
    private static final char DAMA_BILA = 'Q';
    /** Symbol pro bileho krale ve FEN retezci */
    private static final char KRAL_BILI = 'K';
    /** Symbol pro bilou vez ve FEN retezci */
    private static final char VEZ_BILA = 'R';
    /** Symbol pro bileho kone ve FEN retezci */
    private static final char KUN_BILI = 'N';
    /** Symbol pro bileho strelce ve FEN retezci */
    private static final char STRELEC_BILI = 'B';
    /** Popis druheho hrace ve FEN retezci */
    private static final String POPIS_HRACE_2 = "h2";
    /** Popis prvniho hrace ve FEN retezci */
    private static final String POPIS_HRACE_1 = "h1";
    /** Symbol pro oddelovac radku ve FEN retezci */
    private static final String POPIS_ODDELOVACE_RADKU = "/";
    /** Symbol pro nemoznou rosadu ve FEN retezci */
    private static final char NEMOZNA_ROSADA = '-';
    /** Oddelovac posledniho radku ve FEN retezci */
    private static final String ODDELOVVAC_POSLEDNIHO_RADKU = " ";
    /** Cislo vyuzivajici se k ziskani predposedniho tahu. */
    private static final int I_PREDPOSLEDNIHO_TAHU = 2;
    //== Konstanty instanci
    /** Atribut uchovavajici fen retezce hry v listu. */
    private final ArrayList<String> PRUBEH_HRY = new ArrayList<>();
    /** Atribut uchovavajici pocet vyskytu daneho fen retezce ve hre. */
    private final ArrayList<Integer> POCET_VYSKYTU = new ArrayList<>();
    //== Konstruktory
    /**
     * Vytvori novou instanci tridy ForsythEdwardsNotation.
     */
    public ForsythEdwardsNotation() {
    }

    /**
     * Metoda projde list poctu vyskytu a pokud u nejakeho retezce najde vyskyt >= 3 tak ho vrati.
     * @return Fen retezec pokud se vyskytl vice jak 2, jinak "".
     */
    public String nastal3StejnyStavSachovnice() {
        //urcuje pocet stanoveny pro nastani remizy
        final int REMIZOVY_STAV = 3;
        for (int i = 0; i < this.POCET_VYSKYTU.size(); i++) {
            if (this.POCET_VYSKYTU.get(i) >= REMIZOVY_STAV) {
                return PRUBEH_HRY.get(i);
            }
        }

        return "";
    }

    /**
     * Metoda na zaklade predaneho stavu hry vytvori retezec podle algoritmu Forsyth Edward Notation a ten prida
     * do listu zaznamenavajici prubeh hry.
     * @param s - Instance tridy Sachovnice.
     */
    public void fenAlgoritmus(Sachovnice s) {
        zkontrolujParametrS(s);
        String radka;
        //zakladni popis sachovnice
        radka = vratPopisSachovnice(s.getSTAV_HRY(), s);
        //kdo je na tahu
        radka = radka + ODDELOVVAC_POSLEDNIHO_RADKU;
        radka = radka + vratPopisHrace(s);
        //moznost rosady pro h1
        radka = radka + ODDELOVVAC_POSLEDNIHO_RADKU;
        radka = radka + vratPopisRosadyH(s, 1);
        //moznost rosady pro h2
        radka = radka + ODDELOVVAC_POSLEDNIHO_RADKU;
        radka = radka + vratPopisRosadyH(s, 2);
        //pocet tahu bez brani pesce nebo vezmuti figurky
        radka = radka + ODDELOVVAC_POSLEDNIHO_RADKU;
        radka = radka + s.getPocetTahuBezBraniAPesce();
        System.out.println(radka);
        //aktualizujeme pocet vyskytu radku
        aktualizujPocetVyskytu(radka);
    }

    /**
     * Pomocna metoda. Zkontroluje jestli predany parametr s je validni i stav Hry na sachovnici jestli je validni.
     * @param s - Instance tridy Sachovnice.
     */
    private void zkontrolujParametrS(Sachovnice s) {
        if (s == null) {
            throw new IllegalArgumentException("Sachovnice je null.");
        }
        if (s.neniSachovniceValidni()) {
            throw new IllegalArgumentException("Doslo k vnitrnim nesrovnalostem v atributu sachovnice.");
        }
    }

    /**
     * Metoda z predaneho stavu hry vrati retezec reprezentujici rozestaveni figurek na sachovnici pro fen retezec.
     * @param stavHry - Pole obsahujici instance figurek.
     * @param s - Instance tridy sachovnice.
     * @return Retezec reprezentujici rozmisteni figurek.
     */
    private String vratPopisSachovnice(AFigurka[][] stavHry, Sachovnice s) {
        StringBuilder radka = new StringBuilder();
        for (int i = 0; i < s.getRadky(); i++) {
            //nacteme radek sachovnice
            radka.append(nactiRadekSachovnice(i, stavHry, s));
        }

        return radka.toString();
    }

    /**
     * Metoda aktualizuje kolikrat se predany string nachazi v listu zaznamenavajici prubeh hry. String nasledne prida
     * do listu.
     * @param radka - Predany retezec.
     */
    private void aktualizujPocetVyskytu(String radka) {
        int polsedniOddelovacR = radka.lastIndexOf(" ");
        int posledniOddelovac;
        String radkaBezPoctuR = radka.substring(0, polsedniOddelovacR);
        String radkaBezPoctu;

        for (int i = 0; i < this.PRUBEH_HRY.size(); i++) {
            posledniOddelovac = this.PRUBEH_HRY.get(i).lastIndexOf(" ");
            radkaBezPoctu =  this.PRUBEH_HRY.get(i).substring(0, posledniOddelovac);
            if (radkaBezPoctu.equals(radkaBezPoctuR)) { //pokud se retezce rovnaji zvysime vyskyt
                this.POCET_VYSKYTU.set(i, this.POCET_VYSKYTU.get(i) + 1); //zvysime pocet vyskytu
            }
        }
        //pridame radek do listu
        this.POCET_VYSKYTU.add(1);
        //pridame radek do listu prubehu hry
        this.PRUBEH_HRY.add(radka);
    }

    /**
     * Metoda vrati retezec reprezentujici stav rosady hrace urcehneho parametrem hrac.
     * @param s - Instance tridy Sachovnice.
     * @param hrac - Cislo konkretizujici hrace.
     * @return Retezec reprezentujici stav rosady konkretniho hrace.
     */
    private String vratPopisRosadyH(Sachovnice s, int hrac) {
        String rosada = "";
        Color barva = rozpoznejHrace(s, hrac);

        for (int i = 0; i < s.getFIGURKY().length; i++) {
            if (jeFKralHrace(s, i, barva)) { //pokud je kral figurka prislusneho hrace
                rosada = vratPopisRosadyKrale((Kral)(s.getFIGURKY()[i]));
            }
        }

        if (hrac == 1) {
            rosada = rosada.toUpperCase();
        }

        return rosada;
    }

    /**
     * Metoda zjisti stav rosady krale ktery byl predan parametrem metody.
     * @param k - Instance tridy Kral definujici krale u ktereho zjistujeme rosadu.
     * @return Retezec reprezentujici stav rosady krale.
     */
    private String vratPopisRosadyKrale(Kral k) {
        String rosada;
        if (!k.getUdelalRosadu()) {// rosadu kral jeste neudelal
            if (k.getPohnulSe()) { // pokud se pohnul kral
                rosada = "" + NEMOZNA_ROSADA + NEMOZNA_ROSADA;
            } else { //pokud se kral nepohl tak vratime popis dle pohnuti vezi
                rosada = vratPopisRosady(k);
            }
        } else { // udelal rosadu
            rosada = "" + NEMOZNA_ROSADA + NEMOZNA_ROSADA;
        }

        return rosada;
    }

    /**
     * Metoda na zaklade predaneho cisla zjisti barvu hrace.
     * @param s - Instance tridy Sachovnice.
     * @param hrac - Cislo konkretizujici hrace.
     * @return Instance tridy Color reprezentujici hrace.
     */
    private Color rozpoznejHrace(Sachovnice s, int hrac) {
        Color barva;
        if (hrac == 1) {
            barva = s.getTym1();
        } else {
            barva = s.getTym2();
        }

        return barva;
    }

    /**
     * Metoda dle predane instance figurky vrati popisek teto figurky pro fen algoritmus.
     * @param f - Instance predane figurky.
     * @param s - Instance tridy Sachovnice.
     * @return - Retezec reprezentujici popis figurky.
     */
    private String vratPismenoFigurky(AFigurka f, Sachovnice s) {
        char pismeno = switch (f) {
            case Pesec ignored -> f.getTym().equals(s.getTym1()) ? PESEC_BILI : PESEC_CERNY;
            case Kral ignored -> f.getTym().equals(s.getTym1()) ? KRAL_BILI : KRAL_CERNY;
            case Dama ignored -> f.getTym().equals(s.getTym1()) ? DAMA_BILA : DAMA_CERNA;
            case Vez ignored -> f.getTym().equals(s.getTym1()) ? VEZ_BILA : VEZ_CERNA;
            case Kun ignored -> f.getTym().equals(s.getTym1()) ? KUN_BILI : KUN_CERNY;
            case Strelec ignored -> f.getTym().equals(s.getTym1()) ? STRELEC_BILI : STRELEC_CERNY;
            case null, default -> throw new IllegalArgumentException("Neznama figurka predana jako parametr.");
        };

        return Character.toString(pismeno);
    }

    /**
     * Metoda zjisti jestli byla behem posledniho tahu vyhozena figurka a pokud ano, tak ji vrati.
     * @param s - Instance tridy Sachovnice
     * @return (AFigurka) - Novou instanci tridy figurky ktera byla vyhozen nebo null pokud nebyla.
     */
    public AFigurka zjistiVyhozenouF(Sachovnice s) {
        zkontrolujParametrS(s);
        String posledniTah = this.getPRUBEH_HRY().getLast();
        String predposledniTah = this.getPRUBEH_HRY().get(this.getPRUBEH_HRY().size() - I_PREDPOSLEDNIHO_TAHU);
        String[] castiPosledniho = posledniTah.split(POPIS_ODDELOVACE_RADKU);
        String[] castiPredPosledniho = predposledniTah.split(POPIS_ODDELOVACE_RADKU);
        String radekPredPos;
        String radekPos;

        for (int i = 0; i < s.getRadky(); i++) {
            radekPos = vytvorPlnyRadek(castiPosledniho[i]);
            radekPredPos = vytvorPlnyRadek(castiPredPosledniho[i]);
            if (!radekPos.equals(radekPredPos)) { //pokud se lisi vime ze probehla zmena
                AFigurka vyhozenaF = projdiZnakyVradceAZjistiVyhozenou(radekPos, radekPredPos, s, i, castiPosledniho,
                                                                       castiPredPosledniho);
                if (vyhozenaF != null) {
                    return vyhozenaF;
                }
            }
        }
        //pole kam tahla figurka bylo volne takze vratime null
        return null;
    }

    /**
     * Pomocna metoda. Metoda projde vsechny znaky na stejnem indexu v i-te casti fen retezcich a dle jejich porovnani
     * urci vyhozenou figurku.
     * @param radekPos - String reprezentujici i-ty radek sachovnice po poslednim tahu.
     * @param radekPredPos - String reprezentujici i-ty radek sachovnice po predposlednim tahu.
     * @param s - Instance tridy Sachovnice.
     * @param i - Index urcujici radek sachovnice.
     * @param castiPosledniho - String[] reprezentujici rozdelenny posledni fen retezec podle /.
     * @param castiPredPosledniho - String[] reprezentujici rozdelenny predposledni fen retezec podle /.
     * @return (AFigurka) - Novou instanci tridy figurky ktera byla vyhozen nebo null pokud nebyla.
     */
    private AFigurka projdiZnakyVradceAZjistiVyhozenou(String radekPos, String radekPredPos, Sachovnice s, int i,
                                                       String[] castiPosledniho, String[] castiPredPosledniho) {
        for (int k = 0; k < s.getRadky(); k++)  {
            char znakPredPos = radekPredPos.charAt(k);
            char znakPos = radekPos.charAt(k);
            AFigurka vyhozenaF = bylaVyhozenaFigurka(znakPos, znakPredPos, i, k, s, castiPosledniho,
                                                     castiPredPosledniho);
            if (vyhozenaF != null) {
                return vyhozenaF;
            }
        }

        return null;
    }

    /**
     * Pomoocna metoda. Metoda postupne otestuje v poli (i,j) zakladni brani a pak brani mimochodem.
     * @param znakPos - K-ty znak fen retezce reprezentujici stav pole po poslednim tahu.
     * @param znakPredPos - K-ty znak fen retezce reprezentujici stav pole po predposlednim tahu.
     * @param i - Index urcujici radek sachovnice.
     * @param k - Index urcujici sloupec sachovnice.
     * @param s - Instance tridy Sachovnice.
     * @param castiPosledniho - String[] reprezentujici rozdelenny posledni fen retezec podle /.
     * @param castiPredPosledniho - String[] reprezentujici rozdelenny predposledni fen retezec podle /.
     * @return (AFigurka) - Novou instanci tridy figurky ktera byla vyhozen nebo null pokud nebyla.
     */
    private AFigurka bylaVyhozenaFigurka(char znakPos, char znakPredPos, int i, int k, Sachovnice s,
                                         String[] castiPosledniho, String[] castiPredPosledniho) {
        if (bylaVyhozenaFZakladniTechnikou(znakPredPos, znakPos)) {//jedna se misto kde figurka vyhodila jinou
            int hrac = ziskejHraceKteryPriselOF(castiPosledniho); //pokud mel dalsi tah hrat 2 hrac pak prisel o figurku
            return vratFigurkuNaZakladePismena(znakPredPos, i, k, s, hrac);
        }
        //kontrola brani mimo
        return bylaVyhozenaFBranimMimo(znakPos, znakPredPos, i, k, s, castiPosledniho, castiPredPosledniho);
    }

    /**
     * Pomocna metoda. Metoda porovna pole (i,j) po poslednim a predposlednim tahu, a urci, jestli doslo k zakladnimu
     * vyhozeni figurky.
     * @param znakPredPos - K-ty znak fen retezce reprezentujici stav pole po predposlednim tahu.
     * @param znakPos - K-ty znak fen retezce reprezentujici stav pole po poslednim tahu.
     * @return True pokud doslo k zakladimu vyhozeni, jinak false.
     */
    private boolean bylaVyhozenaFZakladniTechnikou(char znakPredPos, char znakPos) {
        return znakPredPos != PRAZDNE_POLE && znakPos != PRAZDNE_POLE && jsouNepratelske(znakPos, znakPredPos);
    }

    /**
     * Pomoocna metoda. Metoda otestuje jestli stavy (posledni a predposledni tah) pole (i,j) mohly zpusobit techniku
     * brani mimochodem. Pokud ano zjisti jestli byl vyhozeny pesec v poli za a pripadne ho vrati.
     * @param znakPos - K-ty znak fen retezce reprezentujici stav pole po poslednim tahu.
     * @param znakPredPos - K-ty znak fen retezce reprezentujici stav pole po predposlednim tahu.
     * @param i - Index urcujici radek sachovnice.
     * @param k - Index urcujici sloupec sachovnice.
     * @param s - Instance tridy Sachovnice.
     * @param castiPosledniho - String[] reprezentujici rozdelenny posledni fen retezec podle /.
     * @param castiPredPosledniho - String[] reprezentujici rozdelenny predposledni fen retezec podle /.
     * @return (AFigurka) - Novou instanci tridy Pesec, ktery byl vyhozen technikou brani mimo nebo null pokud nebyl.
     */
    private AFigurka bylaVyhozenaFBranimMimo(char znakPos, char znakPredPos, int i, int k, Sachovnice s,
                                             String[] castiPosledniho, String[] castiPredPosledniho) {
        //otestujeme jestli pesec tahnul na pole kde mohl spustit brani mimo
        if (mohlTahZpusobitBraniMimoPesecem(znakPos, znakPredPos, i)) {
            //musime zkontrolovat jestli se za touto pozici nenachazel nepratelsky pesec.
            Object[] smerANepritel = nastavSmerANepriteleDleZnaku(znakPos);
            int smer = (int)smerANepritel[0];
            char nepratelskyPes = (char)smerANepritel[1];
            int indexRadkuZa = i;
            indexRadkuZa = indexRadkuZa - smer;
            if (s.jeIRadkuVSachovnici(indexRadkuZa)) {
                String popisRadkuZaPescemPred = vytvorPlnyRadek(castiPredPosledniho[indexRadkuZa]);
                String popisRadkuZaPescemPos = vytvorPlnyRadek(castiPosledniho[indexRadkuZa]);
                char znakZaPescemPred = popisRadkuZaPescemPred.charAt(k);
                char znakZaPescemPos = popisRadkuZaPescemPos.charAt(k);
                //pokud byl nepratelsky pesec vyhozen v poli za pescem kterym se tahlo
                if (bylVyhozenPesec(znakZaPescemPred, nepratelskyPes, znakZaPescemPos)) {
                    //pokud mel dalsi tah hrat 2 hrac pak prisel o figurku
                    int hrac = ziskejHraceKteryPriselOF(castiPosledniho);
                    return vratFigurkuNaZakladePismena(znakZaPescemPred, indexRadkuZa, k, s, hrac);
                }
            }
        }

        return null;
    }

    /**
     * Pomocna metoda. Zjisti jestli stavy pole (i,j) (po poslednim a predposlednim tahu) umoznily vyhodit pesce
     * technikou brani mimo. Jedna se pouze o test zakladni podminky ciloveho pole pesce.
     * @param znakPos - K-ty znak fen retezce reprezentujici stav pole po poslednim tahu.
     * @param znakPredPos - K-ty znak fen retezce reprezentujici stav pole po predposlednim tahu.
     * @param i - Index urcujici radek sachovnice.
     * @return True pokud pesec svym tahem na cilove pole mohl spustit techniku brani mimo.
     */
    private boolean mohlTahZpusobitBraniMimoPesecem(char znakPos, char znakPredPos, int i) {
        if (mohlSpustitPesecBraniMimo(znakPos, i)) {
            //pokud pred tim v sloupci bylo volno
            return znakPredPos == PRAZDNE_POLE;
        }

        return false;
    }

    /**
     * Pomocna metoda. Zjisti jestli stavy pole (i,j) (po poslednim) obsahuje pesce ktery by mohl spustit brani mimo.
     * @param znakPos - K-ty znak fen retezce reprezentujici stav pole po poslednim tahu.
     * @param i - Index urcujici radek sachovnice.
     * @return True pokud ano, jinak false.
     */
    private boolean mohlSpustitPesecBraniMimo(char znakPos, int i) {
        //Konstanty urcujici radek sachovnice (pro pesece) na ktery kdyz pesci tahnou muzou spusti branim mimo.
        final int I_RADKU_BRANI_MIMO_CER_PES = 5;
        final int I_RADKU_BRANI_MIMO_BIL_PES = 2;
        return (znakPos == PESEC_CERNY && i == I_RADKU_BRANI_MIMO_CER_PES) ||
               (znakPos == PESEC_BILI && i == I_RADKU_BRANI_MIMO_BIL_PES);
    }

    /**
     * Pomocna metoda. Metoda zjisti smer pohybu daneho pesce na sachovnici a znak nepratelskeho pesce.
     * @param znakPos - K-ty znak fen retezce reprezentujici stav pole po poslednim tahu.
     * @return Object[] - pole kde prvni prvek predstavuje smer pesce a druhy oznaceni nepratelskeho pesce ve fen
     * retezcich.
     */
    private Object[] nastavSmerANepriteleDleZnaku(char znakPos) {
        int smer = -1;
        char nepratelskyPes = PESEC_CERNY;
        if (znakPos == PESEC_CERNY) {
            smer = 1;
            nepratelskyPes = PESEC_BILI;
        }

        return new Object[] {smer, nepratelskyPes};
    }

    /**
     * Pomocna metoda. Zjisti jestli byl vyhozen pesec na poli. Vyuziva se pro zjisteni vyhozene figurky v brani mimo.
     * @param znakZaPescemPred - K-ty znak fen retezce reprezentujici stav pole v radku za pasecem ktery tahl po
     *                         predposlednim tahu.
     * @param nepratelskyPes - Znak nepratelskeho pesce k pesci ktery tahl.
     * @param znakZaPescemPos - K-ty znak fen retezce reprezentujici stav pole v radku za pasecem ktery tahl po
     *                        poslednim tahu.
     * @return True pokud nepratelsky pesec za pescem ktery tahl byl vyhozen, jinak false.
     */
    private boolean bylVyhozenPesec(char znakZaPescemPred, char nepratelskyPes, char znakZaPescemPos) {
        return znakZaPescemPred == nepratelskyPes && znakZaPescemPos == PRAZDNE_POLE;
    }

    /**
     * Pomocna metoda. Na zaklade predane cast fen retezce ziska hrace ktery hraje pristi tah, a tim padem se jedna o
     * hrace ktery straci figury.
     * @param castiPosledniho - String[] reprezentujici rozdelenny posledni fen retezec podle /.
     * @return (int) - urcujici hrace ktery bude tahnout v pristim tahu.
     */
    private int ziskejHraceKteryPriselOF(String[] castiPosledniho) {
        String[] posledniCasti = castiPosledniho[castiPosledniho.length - 1].split(ODDELOVVAC_POSLEDNIHO_RADKU);
        if (posledniCasti[1].equals(POPIS_HRACE_2)) { //pokud mel dalsi tah hrat 2 hrac pak prisel o figurku
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * Pomocna metoda. Metoda vytvori instanci figurky dle predaneho znaku a tu vrati.
     * @param znak - Znak definujici fgiruku, ktera se ma vytvorit
     * @param i - Index defingujici radek sachovnice kam se figurka vlozi.
     * @param j - Index defingujici sloupec sachovnice kam se figurka vlozi.
     * @param s - Instance tridy Sachovnice.
     * @param hrac - (int) definujici hrace ktermu bude figurka pridelena
     * @return Instanci pozadovane figurky, jinak null.
     */
    private AFigurka vratFigurkuNaZakladePismena(char znak, int i, int j, Sachovnice s, int hrac) {
        Color barva = rozpoznejHrace(s, hrac);
        znak = Character.toLowerCase(znak);

        return switch (znak) {
            case PESEC_CERNY -> new Pesec(i, j, s.getHraciPole()[i][j].getHeight(), barva, s);
            case DAMA_CERNA -> new Dama(i, j, s.getHraciPole()[i][j].getHeight(), barva, s);
            case VEZ_CERNA-> new Vez(i, j, s.getHraciPole()[i][j].getHeight(), barva, s);
            case KUN_CERNY -> new Kun(i, j, s.getHraciPole()[i][j].getHeight(), barva, s);
            case STRELEC_CERNY -> new Strelec(i, j, s.getHraciPole()[i][j].getHeight(), barva, s);
            default -> throw new IllegalArgumentException("Neznamy znak figurky.");
        };
    }

    /**
     * Metoda zjisti indexy posledniho tahu.
     * @param s Instance tridy Sachovnice.
     * @return Pole obsahujic indexy posledniho tahu, kde prvni 2 prvky urcuji okdud figurka tahla a dalsi 2 kam tahla.
     */
    public int[] zjistiIndexyPoslednihoTahu(Sachovnice s) {
        zkontrolujParametrS(s);
        String posledniTah = this.getPRUBEH_HRY().getLast();
        String predposledniTah = this.getPRUBEH_HRY().get(this.getPRUBEH_HRY().size() - I_PREDPOSLEDNIHO_TAHU);
        String[] castiPosledniho = posledniTah.split(POPIS_ODDELOVACE_RADKU);
        String[] castiPredPosledniho = predposledniTah.split(POPIS_ODDELOVACE_RADKU);
        String radekPredPos;
        String radekPos;
        int[] indexyPoslednihoTahu = new int[] {-1, -1, -1, -1};

        for (int i = 0; i < s.getRadky(); i++) {
            radekPos = vytvorPlnyRadek(castiPosledniho[i]);
            radekPredPos = vytvorPlnyRadek(castiPredPosledniho[i]);
            if (!radekPos.equals(radekPredPos)) { //pokud se lisi vime ze probehla zmena
                int[] zjisteneIndexy = projdiRadekAUrciIndexy(s, radekPredPos, radekPos, i, castiPosledniho,
                                                              castiPredPosledniho, indexyPoslednihoTahu);
                if (urcilSePlnyTah(zjisteneIndexy)) {
                    return zjisteneIndexy;
                }
                indexyPoslednihoTahu = pridejZjistenIndexy(zjisteneIndexy, indexyPoslednihoTahu);
            }
        }

        return indexyPoslednihoTahu;
    }

    /**
     * Pomocna metoda. Procje radek sachovnice znak po znaku a zjisti index posledniho tahu.
     * @param s - Instance tridy Sachovnice.
     * @param radekPredPos - String predstavuje i-ty radek sachovnice v podobe predposledniho fen retezce.
     * @param radekPos - String predstavuje i-ty radek sachovnice v podobe posledniho fen retezce.
     * @param i - Index definujici radek sachovnice.
     * @param castiPosledniho - String[] reprezentujici rozdelenny posledni fen retezec podle /.
     * @param castiPredPosledniho - String[] reprezentujici rozdelenny predposledni fen retezec podle /.
     * @param indexyPoslednihoTahu -  int[] obsahujici aktualne zjistene indexyPoslednihoTahu.
     * @return - int[] -> Zjistene indexy tahu.
     */
    private int[] projdiRadekAUrciIndexy(Sachovnice s, String radekPredPos, String radekPos, int i,
                                         String[] castiPosledniho, String[] castiPredPosledniho,
                                         int[] indexyPoslednihoTahu) {
        for (int k = 0; k < s.getRadky(); k++)  {
            int[] zjisteneIndexy = zeStavuUrciIndexyTahu(s, radekPredPos, radekPos, i, castiPosledniho,
                                                         castiPredPosledniho, k);
            if (urcilSePlnyTah(zjisteneIndexy)) { //nastane pri brani mimo
                return zjisteneIndexy;
            }
            indexyPoslednihoTahu = pridejZjistenIndexy(zjisteneIndexy, indexyPoslednihoTahu);
        }

        return indexyPoslednihoTahu;
    }

    /**
     * Pomocna metoda. Zjisti jestli se nalezly indexy celeho tahu (nastane napriklad pri brani mimochodem).
     * @param zjisteneIndexy - Prednae zjistene indexy.
     * @return True pokud je nalezen cely tah, jinak false.
     */
    private boolean urcilSePlnyTah(int[] zjisteneIndexy) {
        for (int index : zjisteneIndexy) {
            if (index == -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Pomocna metoda. Do doposud nalezenych indexu tahu prida dalsi zjistene.
     * @param zjisteneIndexy - Nove nalezene indexy tahu.
     * @param indexyPoslednihoTahu - Vsechny doposud zjistene indexy tahu.
     * @return - int[] -> Vsechny doposud nalezene indexy doplney o zjistene.
     */
    private int[] pridejZjistenIndexy(int[] zjisteneIndexy, int[] indexyPoslednihoTahu) {
        int[] indexy = Arrays.copyOf(indexyPoslednihoTahu, indexyPoslednihoTahu.length);
        for (int i = 0; i < zjisteneIndexy.length; i++) {
            if (zjisteneIndexy[i] != -1) {
                indexy[i] = zjisteneIndexy[i];
            }
        }

        return indexy;
    }

    /**
     * Pomocna metoda. Metoa zjisti indexy tahu na zaklade predaneho stavu pole ve fen retezci poslednim a
     * predposlednim.
     * @param s - Instance tridy Sachovnice.
     * @param radekPredPos - String predstavuje i-ty radek sachovnice v podobe predposledniho fen retezce.
     * @param radekPos - String predstavuje i-ty radek sachovnice v podobe posledniho fen retezce.
     * @param i - Index definujici radek sachovnice.
     * @param castiPosledniho - String[] reprezentujici rozdelenny posledni fen retezec podle /.
     * @param castiPredPosledniho - String[] reprezentujici rozdelenny predposledni fen retezec podle /.
     * @param k - Index urucujici sloupec sachovnice.
     * @return - int[] -> Zjistene indexy tahu.
     */
    private int[] zeStavuUrciIndexyTahu(Sachovnice s, String radekPredPos, String radekPos, int i,
                                        String[] castiPosledniho, String[] castiPredPosledniho, int k) {
        int iRadkuStart = -1;
        int iSloupceStart = -1;
        int iRadkuCil = -1;
        int iSloupceCil = -1;
        char znakPredPos;
        char znakPos;

        znakPredPos = radekPredPos.charAt(k);
        znakPos = radekPos.charAt(k);
        if (tahlaJsemFigurka(znakPredPos, znakPos)) {//Jedna se o misto kam tahla figurka
            iRadkuCil = i;
            iSloupceCil = k;
            AFigurka pesec = bylaVyhozenaFBranimMimo(znakPos, znakPredPos, i, k, s, castiPosledniho,
                    castiPredPosledniho);
            if (pesec != null) {
                iRadkuStart = pesec.getIndexRadkuPoleF();
                iSloupceStart = ziskejiSloupcePohybu(pesec.getIndexRadkuPoleF(),
                        pesec.getIndexSloupcePoleF(), castiPosledniho,
                        castiPredPosledniho, s);
                return new int[] {iRadkuStart, iSloupceStart, iRadkuCil, iSloupceCil};
            }
        } else if (opustilaFigurkaPole(znakPredPos, znakPos)) {
            //jedna se o misto ktere opustila figurka
            iRadkuStart = i;
            iSloupceStart = k;
            //jedna se misto kde figurka vyhodila jinou
        } else if (bylaVyhozenaFZakladniTechnikou(znakPredPos, znakPos)) {
            iRadkuCil = i;
            iSloupceCil = k;
        }

        return new int[] {iRadkuStart, iSloupceStart, iRadkuCil, iSloupceCil};
    }


    /**
     * Pomocna metoda. Ziska index sloupce pesce ktery sebral pesce na poli (i,j) branim mimochodem.
     * @param i - Index radku vyhozeneho pesce branim mimo.
     * @param j - Index sloupce vyhozeneho pesce branim mimo.
     * @param castiPosledniho - String[] reprezentujici rozdelenny posledni fen retezec podle /.
     * @param castiPredPosledniho - String[] reprezentujici rozdelenny predposledni fen retezec podle /.
     * @param s - Instance tridy Sachovnice.
     * @return Index sloupce pesce ktery bral mimochodem, jinak -1.
     */
    private int ziskejiSloupcePohybu(int i, int j, String[] castiPosledniho, String[] castiPredPosledniho,
                                     Sachovnice s) {
        String popisRadkuVyhozenehoPes = vytvorPlnyRadek(castiPredPosledniho[i]);
        String popisRadkuVyhozenehoPesPos = vytvorPlnyRadek(castiPosledniho[i]);
        char znakVyhozenehoPes = popisRadkuVyhozenehoPes.charAt(j);
        char tahnouciPes = (char)nastavSmerANepriteleDleZnaku(znakVyhozenehoPes)[1];

        int indexSloup;
        int indexSloupL = j - 1;
        int indexSloupP = j + 1;
        indexSloup = otestujPolePesDleISindexSloupP(popisRadkuVyhozenehoPes, popisRadkuVyhozenehoPesPos, tahnouciPes,
                                                    s, indexSloupL);
        if (indexSloup != -1) {
            return indexSloup;
        }

        indexSloup = otestujPolePesDleISindexSloupP(popisRadkuVyhozenehoPes, popisRadkuVyhozenehoPesPos, tahnouciPes,
                                                    s, indexSloupP);
        return indexSloup;
    }

    /**
     * Pomocna metoda. Metoda zjsiti jestli tahnouci pesec opustil pole na predanem indexu sloupce.
     * @param popisRadkuVyhozenehoPes - String casti predposledniho fen retezce, kde byl vyhozen pesec branim mimo.
     * @param popisRadkuVyhozenehoPesPos - String casti posledniho fen retezce, kde byl vyhozen pesec branim mimo.
     * @param tahnouciPes - Znak urcujici tahnouciho pesce.
     * @param s - Instance tridy Sachovnice.
     * @param indexSloup - Index urucjici sloupec sachovnice.
     * @return Index sloupce pesce ktery opustil pozici, jinak -1.
     */
    private int otestujPolePesDleISindexSloupP(String popisRadkuVyhozenehoPes, String  popisRadkuVyhozenehoPesPos,
                                               char tahnouciPes, Sachovnice s, int indexSloup) {
        if (s.jeISloupVSachovnici(indexSloup)) {
            char znakPolePVedleVyhozPescePredPos = popisRadkuVyhozenehoPes.charAt(indexSloup);
            char znakPolePVedleVyhozPescePos  =  popisRadkuVyhozenehoPesPos.charAt(indexSloup);
            if (opustilPesecPole(znakPolePVedleVyhozPescePos, znakPolePVedleVyhozPescePredPos, tahnouciPes)) {
                return  indexSloup;
            }
        }

        return  -1;
    }

    /**
     * Pomocna metoda. Zjisti jestli figurka tahla na pole (i,k) jehoz stav je definovan parametry metody.
     * @param znakPos - K-ty znak fen retezce reprezentujici stav pole po poslednim tahu.
     * @param znakPredPos - K-ty znak fen retezce reprezentujici stav pole po predposlednim tahu.
     * @return True pokud figurka tahla na prazdne pole, jinak false.
     */
    private boolean tahlaJsemFigurka(char znakPredPos, char znakPos) {
        return znakPredPos == PRAZDNE_POLE && znakPos != PRAZDNE_POLE;
    }

    /**
     * Pomocna metoda. Zjisti jestli figurka opusitla pole (i,k) jehoz stav je definovan parametry metody.
     * @param znakPos - K-ty znak fen retezce reprezentujici stav pole po poslednim tahu.
     * @param znakPredPos - K-ty znak fen retezce reprezentujici stav pole po predposlednim tahu.
     * @return True pokud figurka tahla na prazdne pole, jinak false.
     */
    private boolean opustilaFigurkaPole(char znakPredPos, char znakPos) {
        return znakPredPos != PRAZDNE_POLE && znakPos == PRAZDNE_POLE;
    }

    /**
     * Pomocna metoda. Metoda prevede predany radek sachovnice fen retezce na popis, kde volna pole jsou reprezentovana
     * '.'.
     * @param radek - String popisujice radek sachovnice -> cast fen retezce.
     * @return String popisujici radek sachovnice kde volna mista jsou reprezentovana '.'.
     */
    private String vytvorPlnyRadek(String radek) {
        StringBuilder novyRadek = new StringBuilder();
        for (int i =  0; i < radek.length(); i++) {
            if (Character.isDigit(radek.charAt(i))) { //pokud je cislo
                //tak doplnime tecky jako volna pole
                novyRadek.append(Character.toString(PRAZDNE_POLE).repeat(Math.max(0, (radek.charAt(i) - '0'))));
            } else {
                novyRadek.append(radek.charAt(i));
            }
        }

        return novyRadek.toString();
    }

    /**
     * Metoda snizi pocet vyskytu stavu (zaznamu po poslednim tahu -> fen retezec) dle predaneho stavu.
     * @param vyhozovanyStav - Stav u ktereho se bude snizovat pocet vyskytu.
     */
    private void snizPocetVyskytu(String vyhozovanyStav) {
        for (int i = 0; i < this.getPRUBEH_HRY().size(); i++) {
            if (this.getPRUBEH_HRY().get(i).equals(vyhozovanyStav)) {
                this.getPOCET_VYSKYTU().set(i, this.getPOCET_VYSKYTU().get(i) - 1);
            }
        }
    }

    /**
     * Geter k atributu PRUBEH_HRY.
     * @return PRUBEH_HRY.
     */
    public ArrayList<String> getPRUBEH_HRY() {
        return this.PRUBEH_HRY;
    }

    /**
     * Geter k atributu POCET_VYSKYTU.
     * @return POCET_VYSKYTU.
     */
    public ArrayList<Integer> getPOCET_VYSKYTU() {
        return this.POCET_VYSKYTU;
    }

    /**
     * Metoda pred vracenim aktualizuje klicove atributy pohnuti pro veze a krale a take se postara o vraceni rosady,
     * kdy vnitrne vrati i vez.
     * @param s - Instance tridy Sachovnice
     */
    public void aktualizujPohnutiVezAKral(Sachovnice s) {
        zkontrolujParametrS(s);
        String posledniTah = this.getPRUBEH_HRY().getLast();
        String predposledniTah = this.getPRUBEH_HRY().get(this.getPRUBEH_HRY().size() - I_PREDPOSLEDNIHO_TAHU);
        int[] indexyPoslednihoTahu = zjistiIndexyPoslednihoTahu(s);
        AFigurka vracenaF = s.getSTAV_HRY()[indexyPoslednihoTahu[2]][indexyPoslednihoTahu[3]];
        int indexTextuRosady;
        int radekVeze;
        String[] castiPosledniho = posledniTah.split(POPIS_ODDELOVACE_RADKU);
        String[] castiPredPosledniho = predposledniTah.split(POPIS_ODDELOVACE_RADKU);
        String[] posledniCasti = castiPosledniho[castiPosledniho.length - 1].split(ODDELOVVAC_POSLEDNIHO_RADKU);
        String[] predPosledniCasti = castiPredPosledniho[castiPredPosledniho.length - 1].split(ODDELOVVAC_POSLEDNIHO_RADKU);

        //pokud probehla rosada -> vnitrne jakoby tahl dvakrat za sebou jeden hrac, coz nastane pouze pri rosade.
        if (predPosledniCasti[1].equals(posledniCasti[1])) {
            obsluzVraceniRosady(vracenaF, s);
        } else if (vracenaF instanceof  Vez v) { //pokud je vracena figurka vez
            //musime zknotrolovat aktualizaci atributu veze jestli nezamezila rosadu svym pohybem
            int[] iPopisuARadkuVeziRosady = vratITextuRosAIRadkuRosVeze(v, s);
            indexTextuRosady = iPopisuARadkuVeziRosady[0];
            radekVeze = iPopisuARadkuVeziRosady[1];
            aktualizujPohnutiVeze(v, indexTextuRosady, radekVeze, indexyPoslednihoTahu, posledniCasti,
                                  predPosledniCasti, s);
        } else if (vracenaF instanceof Kral k) { //pokud je vracne figurka kral
            //musime zknotrolovat aktualizaci atributu krale jestli nezamezil rosade svym pohybem
            int[] iPopisuARadkuVeziRosady = vratITextuRosAIRadkuRosVeze(k, s);
            indexTextuRosady = iPopisuARadkuVeziRosady[0];
            aktualizujPohnutiKrale(k, indexTextuRosady, posledniCasti, predPosledniCasti);
        }
    }

    /**
     * Pomocna metoda. Nejdrive snizi pocest vysktytu u posledniho stavu, ktery se vraci (zahazuje). Nasledne stav
     * posledniho tahu odstrani z listu.
     */
    public void odstranPosledniTah() {
        if (getPRUBEH_HRY().size() <= 1) {
            throw new IllegalStateException("Neni co vracet");
        }
        this.snizPocetVyskytu(this.getPRUBEH_HRY().getLast());
        this.getPRUBEH_HRY().removeLast();
        this.getPOCET_VYSKYTU().removeLast();
    }

    /**
     * Pomocna metoda. Metoda nastavi klicove atributy (jako pohnulSe a udelalRosadu) vezi a krali, kteri delali rosadu
     * na vychozi hodnoty pred rosadou. Nasledne odstarni z fen retezcu posledni tah (tah krale), aby nasledne mohl
     * vratit vez. Odstrani posledni tah a vytvori novy fen retezec, ktery vypada jako kdyby tahl jenom kral. Ten se
     * nasledne vrati.
     * @param vracenaF - Instance tridy Vez, ktera se vnitrne vraci.
     * @param s - Instance tridy Sachovnicie.
     */
    private void obsluzVraceniRosady(AFigurka vracenaF, Sachovnice s) {
        //natavime krali pohnuti na false
        ((Kral)vracenaF).setPohnulSe(false);
        ((Kral)vracenaF).setUdelalRosadu(false);
        ((Kral)vracenaF).nastavVezimPohnuti();
        //musime vratit vez
        //odstranime tah veze
        //1. tah krale
        odstranPosledniTah();
        //ziskame indexy tahu veze
        int[] indexyPredPoslednihoTahu = zjistiIndexyPoslednihoTahu(s);
        AFigurka vez = s.getSTAV_HRY()[indexyPredPoslednihoTahu[2]][indexyPredPoslednihoTahu[3]];
        ((Vez)vez).setPohnulSe(false);
        //figurku na tvrdo vratime bez kontrol sachu atd.
        s.udelejVnitrniTah(indexyPredPoslednihoTahu[2], indexyPredPoslednihoTahu[3], indexyPredPoslednihoTahu[0],
                indexyPredPoslednihoTahu[1], vez);
        //odstranime tah veze z fen retezcu pridame fen retezec popisujici aktualni stav (jakoby tahl jenom kral)
        //2.tah veze
        odstranPosledniTah();
        //pridame tah uz vracene veze jakoby tahl jenom kral
        this.fenAlgoritmus(s);
    }

    /**
     * Pomocna metoda. Vradi int[], kde prvni prvek predstavuje index popisu rosady mezi vezemi a kralem stejneho tymu
     * figurky ve fen retezci. Druhy prvek predstavuje index radku sachovnice, kde se veze nachazi ve startovni pozici
     * pravidel sachu.
     * @param f - Predana instance figurky
     * @param s - Instance tridy Sachovnice.
     * @return int[]
     */
    private int[] vratITextuRosAIRadkuRosVeze(AFigurka f, Sachovnice s) {
        int indexTextuRosady;
        int radekVeze;
        if (f.getTym() == s.getTym1()) {
            indexTextuRosady = 2;
            radekVeze = s.getDOLNI_A_PRAVA_HRANICE_SACH();
        } else {
            indexTextuRosady = 3;
            radekVeze = s.getHORNI_A_LEVA_HRANICE_SACH();
        }

        return new int[] {indexTextuRosady, radekVeze};
    }

    /**
     * Pomocna metoda. Metoda aktualizuje atribut pohnuti veze na false pokud se jedna o pripad,
     * kdy se vez poprve pohnula z pocatecni pozice, pri provedeni obecneho pohybu. Take nastavi atribut pohnuti krale
     * na stav, ve kterem byl, kdyz se vez poprve pohla.
     * @param v - Instance tridy Vez u ktere se aktualiuje klicovy atribut pohnuti.
     * @param indexTextuRosady - Index urcujici popis textu rosady tymu ke kteremu vez patri.
     * @param radekVeze - Index radku sachovnice startovni pozice veze.
     * @param indexyPoslednihoTahu - (int[]) Pole urucujici indexy posledniho tahu.
     * @param posledniCasti - Posledni cast posledniho fen retezce
     * @param predPosledniCasti - Posledni cast predposledniho fen retezce
     * @param s - Instance tridy Sachovnice.
     */
    private void aktualizujPohnutiVeze(Vez v, int indexTextuRosady, int radekVeze, int[] indexyPoslednihoTahu,
                                       String[] posledniCasti, String[] predPosledniCasti, Sachovnice s) {
        char damskaRosPosTah;
        char damskaRosPredPosTah;
        char rosPosTah;
        char rosrPredPosTah;
        //pokud se popisy o rosade nerovnaji neboli mohla se pohnout vez
        if (!posledniCasti[indexTextuRosady].equals(predPosledniCasti[indexTextuRosady])) {
            rosPosTah = posledniCasti[indexTextuRosady].charAt(0);
            damskaRosPosTah = posledniCasti[indexTextuRosady].charAt(1);
            rosrPredPosTah = predPosledniCasti[indexTextuRosady].charAt(0);
            damskaRosPredPosTah = predPosledniCasti[indexTextuRosady].charAt(1);
            if (pohlaSeVezZeZacatecniPozice(rosPosTah, rosrPredPosTah, indexyPoslednihoTahu, radekVeze,
                                            s.getDOLNI_A_PRAVA_HRANICE_SACH())) {
                v.nastavKraliPohnuti();
                v.setPohnulSe(false);
            }
            if (pohlaSeVezZeZacatecniPozice(damskaRosPosTah, damskaRosPredPosTah, indexyPoslednihoTahu, radekVeze,
                                            s.getHORNI_A_LEVA_HRANICE_SACH())) {
                v.nastavKraliPohnuti();
                v.setPohnulSe(false);
            }
        }
    }

    /**
     * Pomocna metoda. Porovna znaky urcujici stav rosady z 2 poslednich fen retezcu a pokud se lisi a zaroven pole,
     * ktere opustila tahnuta figurka je vychozi pole veze, tak se hybla vez a pri vraceni tahu je potreba
     * aktualizovat klicovy atribut.
     * @param rosPosTah - Znak stavu rosady na jedne strane v poslednim fen retezci.
     * @param rosrPredPosTah - Znak stavu rosady na jedne strane v predposlednim fen retezci.
     * @param indexyPoslednihoTahu - (int[]) Pole obsahujici indexy posledniho tahu.
     * @param pocatecniRadkaVeze - Index udavajici radku vychozi pozice veze.
     * @param pocatecniPoziceVeze - Index udavajici sloupec vychozi pozice veze.
     * @return True pokud zamezeni rosady zpusobila vez svym pohybem, jinak false.
     */
    private boolean pohlaSeVezZeZacatecniPozice(char rosPosTah, char rosrPredPosTah, int[] indexyPoslednihoTahu,
                                                int pocatecniRadkaVeze, int pocatecniPoziceVeze) {
        //pokud se lisi znaky rosady 2 poslednich fen retezcu (doslo k rosade) a odchozi pozice veze pri tahnuti
        //odpovida vychozi pozici
        return rosPosTah == NEMOZNA_ROSADA && rosrPredPosTah != NEMOZNA_ROSADA &&
               indexyPoslednihoTahu[0] == pocatecniRadkaVeze && indexyPoslednihoTahu[1] == pocatecniPoziceVeze;
    }

    /**
     * Pomocna metoda. Metoda zjisti jestli se pohnul kral. Pokud ano tak mu obnovi klicove atributy. Prenastavi i
     * atributy pohybu vezi na stavy pri jeho prvnim pohnuti. Take nastavi inicializacne atribut pohnuti a rosady na
     * false pokud jsou jeho veze nedostupne.
     * @param k - Instance tridy Kral u ktereho se aktualiuji klicove atributy.
     * @param indexTextuRosady - Index urcujici popis textu rosady tymu ke kteremu vez patri.
     * @param posledniCasti - Posledni cast posledniho fen retezce
     * @param predPosledniCasti - Posledni cast predposledniho fen retezce
     */
    private void aktualizujPohnutiKrale(Kral k, int indexTextuRosady, String[] posledniCasti,
                                        String[] predPosledniCasti) {
        //pokud se popisy o rosade nerovnaji
        if (!posledniCasti[indexTextuRosady].equals(predPosledniCasti[indexTextuRosady])) {
            if (zmenilSeStavRosNaNemozny(posledniCasti[indexTextuRosady], predPosledniCasti[indexTextuRosady])) {
                //vratime stav prislusnym vezim pred tim nez se pohnul kral
                k.nastavVezimPohnuti();
                k.setPohnulSe(false);
                k.setUdelalRosadu(false);
            }
        }
        //Pro pripad ze jsou vyhozney obe dveze nebo se pohli tak je vlastne jedno jestli se kral pohl nebo ne takze se
        // nastavi inicializacne na fasle
        if (k.ziskejVezeKraleProRosadu()[0] == null && k.ziskejVezeKraleProRosadu()[1] == null) {
            k.setPohnulSe(false);
            k.setUdelalRosadu(false);
        }
    }

    /**
     * Pomocna metoda. Metoda zjisti jestli se v aktualnim tahu kral hybnul poprve.
     * @param rosPosTah - Retezec stavu rosady v poslednim fen retezci.
     * @param rosrPredPosTah - Retezec stavu rosady v predposlednim fen retezci.
     * @return True pokud se zmenil stav rosady na nemozny, jinak false.
     */
    private boolean zmenilSeStavRosNaNemozny(String rosPosTah, String rosrPredPosTah) {
        return !rosrPredPosTah.equals("" + NEMOZNA_ROSADA + NEMOZNA_ROSADA) &&
                rosPosTah.equals("" + NEMOZNA_ROSADA + NEMOZNA_ROSADA);
    }

    /**
     * Metoda zjisti jestli se vez pohla ze zacatecni pozice Pred tim nez byla vyhozena. Vyuziva se pri nastaveni
     * klicoveho atributu pro vez kdyz se vraci vyhozena figurka.
     * @param s - Instance tridy Sachovnice.
     * @param v - Instance tridy Vez.
     * @throws IllegalStateException Pokud byla predana null vez.
     * @return True pokud se vez pohla ze zacatecniho stavu, jinak false.
     */
    public boolean pohlaSeVez(Sachovnice s, Vez v) {
        zkontrolujParametrS(s);
        if (v == null) {
            throw new IllegalStateException("Byla predana null vez");
        }
        String posledniTah = this.getPRUBEH_HRY().get(this.getPRUBEH_HRY().size() - I_PREDPOSLEDNIHO_TAHU);
        String[] castiPosledniho = posledniTah.split(POPIS_ODDELOVACE_RADKU);
        String[] posledniCasti = castiPosledniho[castiPosledniho.length - 1].split(ODDELOVVAC_POSLEDNIHO_RADKU);
        char kralovskaRosPosTah;
        char rosPosTah;
        int[] iPopisuARadkuVeziRosady = vratITextuRosAIRadkuRosVeze(v, s);
        int indexRosady = iPopisuARadkuVeziRosady[0] ;

        rosPosTah = posledniCasti[indexRosady].charAt(0);
        kralovskaRosPosTah = posledniCasti[indexRosady].charAt(1);

        if (v.jeVezMimoVychoziPoziciRadku()) {
            return true;
        } else {
            return pohlaSevezZVychoziPoziceDleSloupce(rosPosTah, kralovskaRosPosTah, v);
        }
    }

    /**
     * Metoda vrati true pokud se vez pohla ze zacatecni pozice pred tim nez byla vyhozena, jinak false.
     * @param rosPosTah - Znak stavu rosady na strane kralovske v predposlednim tahu.
     * @param kralovskaRosPosTah - Znak stavu rosady na damske v predposlednim tahu.
     * @param v - Predana instance Vez.
     * @return True pokud se vez pohla ze zacatecniho stavu, jinak false.
     */
    private boolean pohlaSevezZVychoziPoziceDleSloupce(char rosPosTah, char kralovskaRosPosTah, Vez v) {
        if (v.jeVezKralovska()) {
            return rosPosTah == NEMOZNA_ROSADA;
        } else if (v.jeVezDamska()) {
            return kralovskaRosPosTah == NEMOZNA_ROSADA;
        }

        return false;
    }

    /**
     * Metoda zjisti jestli predany pesec umoznil v predchozim tahu nechat se sebrat branim mimo.
     * @param s - Instance tridy Sachovnice.
     * @param p - Instance tridy Pesec.
     * @return True pokud pesec umoznil brani mimo na sebe, jinak false.
     */
    public boolean muzeBytBranMimo(Sachovnice s, Pesec p) {
        zkontrolujParametrS(s);
        if (p == null) {
            throw new IllegalStateException("Byl predany null pesec");
        }
        String predposledniTah = this.getPRUBEH_HRY().get(this.getPRUBEH_HRY().size() - I_PREDPOSLEDNIHO_TAHU);

        int[] vychoziRadkyPes = p.urciVychoziRadkyProPescePriBraniMimo();
        int iRadkuVychozPozice = vychoziRadkyPes[0];
        int indexRadkuBraniMimo = vychoziRadkyPes[1];

        if (p.jePesecVPozicProBraniMimo()) {
            String predPredposledniTah = this.getPRUBEH_HRY().get(this.getPRUBEH_HRY().size() - 3);
            return umoznilPesecVpredChozimTahuTechniku(iRadkuVychozPozice, p.getIndexSloupcePoleF(),
                                                       indexRadkuBraniMimo, predposledniTah, predPredposledniTah,
                                                       p.getTym(), s.getTym1());
        }

        return false;
    }

    /**
     * Pomocna metoda. Zjisti jestli pesec umoznil svym tahem v predchozim tahu se nechat sebrat branim mimo.
     * @param iRadkuVychozPozice - Index radku vychozi pozice pescu.
     * @param indexSloupcePesce - Index sloupce pole pesce.
     * @param indexRadkuBraniMimo - Index radku kde pesci mohou inicializovat brani mimo.
     * @param tah - Fen retezec tahu.
     * @param predchazejiciTah - Fen retezec predchazejici tahu.
     * @param barvaF - Barva pesce.
     * @param barvaH1 - Barva 1. hrace.
     * @return True pokud pesec v predchozim tahu, nez byl vyhozen, umoznil svym pohybem brani mimo, jiank false.
     */
    private boolean umoznilPesecVpredChozimTahuTechniku(int iRadkuVychozPozice, int indexSloupcePesce,
                                                        int indexRadkuBraniMimo, String tah, String predchazejiciTah,
                                                        Color barvaF, Color barvaH1) {
        String[] castiPosledniho = tah.split(POPIS_ODDELOVACE_RADKU);
        String[] castiPredchoziho = predchazejiciTah.split(POPIS_ODDELOVACE_RADKU);

        String plnyRadekPoslednihoCile = vytvorPlnyRadek(castiPosledniho[indexRadkuBraniMimo]);
        String plnyRadekPoslednihoVychozi = vytvorPlnyRadek(castiPosledniho[iRadkuVychozPozice]);
        String plnyRadekPredchozihoCile = vytvorPlnyRadek(castiPredchoziho[indexRadkuBraniMimo]);
        String plnyRadekPredchozihoVychozi = vytvorPlnyRadek(castiPredchoziho[iRadkuVychozPozice]);

        char stavCilovePozicePosl = plnyRadekPoslednihoCile.charAt(indexSloupcePesce);
        char stavVychoziPozicePosl = plnyRadekPoslednihoVychozi.charAt(indexSloupcePesce);
        char stavVychoziPozicePred = plnyRadekPredchozihoVychozi.charAt(indexSloupcePesce);
        char stavCilovePozicePred = plnyRadekPredchozihoCile.charAt(indexSloupcePesce);

        //pokud mohl byt vyhozeny pesec bran mimochodem pak musel tah pred tim jit z vychozi pozice do pozice z ktere
        //byl nasledujicim tahem vyhozen
        char znakPesce = urciZnakPesce(barvaF, barvaH1);

        return stavVychoziPozicePred == znakPesce && stavCilovePozicePred == PRAZDNE_POLE &&
               stavVychoziPozicePosl == PRAZDNE_POLE && stavCilovePozicePosl == znakPesce;
    }

    /**
     * Vrati znak figurky (pesce) dle barvy.
     */
    private char urciZnakPesce(Color barvaF, Color barvaH1) {
        return barvaF.equals(barvaH1) ? PESEC_BILI : PESEC_CERNY;
    }

    /**
     * Metoda aktualizuje atribut brani mimo vsem pescum.
     * @param s - Instance tridy Sachovnice.
     */
    public void aktualizujBraniMimoUPescu(Sachovnice s) {
        zkontrolujParametrS(s);
        for (int i = 0; i < s.getFIGURKY().length; i++) {
            if (s.getFIGURKY()[i] != null && s.getFIGURKY()[i] instanceof Pesec p) {
                p.setBraniMimochodem(muzeBytBranMimo(s, p));
            }
        }
    }

    /**
     * Metoda zjisti jestli provedeny tah zpusobil promenu a pokud ano tak vrati pesce ktery tuto promenu zpusobil.
     * @param iRAktualniF - Index radku, kde se nachazi figurka ktera tahla.
     * @param iSAktualniF - Index sloupce, kde se nachazi figurka ktera tahla.
     * @param iRPuvodniPoziceF - Index radku odkud figurka tahla.
     * @param iSPuvodniPoziceF - Index sloupce odkud figurka tahla.
     * @param s - Instance tridy Sachovnice.
     * @return Pesce hrace ktery zpusobil promenu, jinak null.
     */
    public AFigurka nastalaPromena(int iRAktualniF, int iSAktualniF, int iRPuvodniPoziceF, int iSPuvodniPoziceF,
                                   Sachovnice s) {
        zkontrolujParametrS(s);
        s.zkontrolujIndexyPoslTahu(iRAktualniF, iSAktualniF, iRPuvodniPoziceF, iSPuvodniPoziceF);
        String posledniTah = this.getPRUBEH_HRY().getLast();
        String predPosledniTah = this.getPRUBEH_HRY().get(this.getPRUBEH_HRY().size() - I_PREDPOSLEDNIHO_TAHU);
        String[] castiPosledniho = posledniTah.split(POPIS_ODDELOVACE_RADKU);
        String[] castiPredPosledniho = predPosledniTah.split(POPIS_ODDELOVACE_RADKU);

        char znakPesce = PESEC_CERNY;
        Color barvaF = s.getSTAV_HRY()[iRAktualniF][iSAktualniF].getTym();
        if (barvaF.equals(s.getTym1())) {
            znakPesce = PESEC_BILI;
        }

        if (mohloVTahuJitOPromenu(iRAktualniF, iRPuvodniPoziceF, s)) {
            if (!castiPosledniho[iRPuvodniPoziceF].equals(castiPredPosledniho[iRPuvodniPoziceF])) {
                return zjistiPromennuPesce(castiPosledniho, castiPredPosledniho, iRPuvodniPoziceF, iSPuvodniPoziceF,
                                           znakPesce, iRAktualniF, iSAktualniF, s);
            }
        }

        return null;
    }

    /**
     * Metoda zjisti jestli v tahu mohlo jit o promenou na zaklade pozice radku.
     * @param iRAktualniF - Index radku kam tahla figurka.
     * @param iRPuvodniPoziceF - Index radku odkud tahla figurka.
     * @param s - Instance tridy Sachovnice.
     * @return True pokud mohlo, jinak false.
     */
    private boolean mohloVTahuJitOPromenu(int iRAktualniF, int iRPuvodniPoziceF, Sachovnice s) {
        return jeBilyTahNaPromenovaciRadek(iRAktualniF, iRPuvodniPoziceF, s) ||
                jeCernyTahNaPromenovaciRadek(iRAktualniF, iRPuvodniPoziceF, s);
    }

    /**
     * Pomocna metoda zjisti, zda bily pesec dosahl horniho radku sachovnice.
     *
     * @param aktualniRadek - Cilovy radek.
     * @param puvodniRadek - Vychozi radek pred tahem.
     * @param s - Sachovnice.
     * @return True pokud bily pesec dosahl promenovaciho radku.
     */
    private boolean jeBilyTahNaPromenovaciRadek(int aktualniRadek, int puvodniRadek, Sachovnice s) {
        int horniHranice = s.getHORNI_A_LEVA_HRANICE_SACH();
        return aktualniRadek == horniHranice && puvodniRadek == horniHranice + 1;
    }

    /**
     * Pomocna metoda zjisti, zda cerny pesec dosahl dolniho radku sachovnice.
     * @param aktualniRadek - Cilovy radek.
     * @param puvodniRadek - Vychozi radek pred tahem.
     * @param s - Sachovnice.
     * @return True pokud cerny pesec dosahl promenovaciho radku.
     */
    private boolean jeCernyTahNaPromenovaciRadek(int aktualniRadek, int puvodniRadek, Sachovnice s) {
        int dolniHranice = s.getDOLNI_A_PRAVA_HRANICE_SACH();
        return aktualniRadek == dolniHranice && puvodniRadek == dolniHranice - 1;
    }

    /**
     * Pomocna metoda. Metoda zjisti jestli figurka, ktera opustila pozici, byla pesec.
     * @param znakPos - Znak urcujici stav pole v posledni fen retezci.
     * @param znakPredPos - Znak urcujici stav pole v predposlednim fen retezci.
     * @param znakPesce - Znak pesce ktery mel tahnout pri promene.
     * @return True pokud pokud ano, jinak false.
     */
    private boolean opustilPesecPole (char znakPos, char znakPredPos, char znakPesce) {
        return znakPos == PRAZDNE_POLE && znakPredPos == znakPesce;
    }

    /**
     * Pomocna metoda. Zjisti jestli figurka ktera opustial pole byl pesec a jestli se promenil. Pokud ano tak vrati
     * pesce, jiank null.
     * @param castiPosledniho - String[] reprezentujici rozdelenny posledni fen retezec podle /.
     * @param castiPredPosledniho - String[] reprezentujici rozdelenny predposledni fen retezec podle /.
     * @param iRPuvodniPoziceF - Index radku sachovnice odkud se stal tah.
     * @param iSPuvodniPoziceF - Index sloupce sachovnice odkud se stal tah.
     * @param znakPesce - Znak pesce ktery mel tahnout pri promene.
     * @param iRAktualniF - Index radku sachovnice kam tahla figurka.
     * @param iSAktualniF - Index sloupce sachovnice kam tahla figurka.
     * @param s - Instance tridy Sachovnice
     * @return Pesce hrace ktery zpusobil promenu, jinak null.
     */
    private AFigurka zjistiPromennuPesce(String[] castiPosledniho, String[] castiPredPosledniho, int iRPuvodniPoziceF,
                                         int iSPuvodniPoziceF, char znakPesce, int iRAktualniF, int iSAktualniF,
                                         Sachovnice s) {
        String radekPos;
        String radekPredPos;
        char znakPos;
        char znakPredPos;

        radekPos = vytvorPlnyRadek(castiPosledniho[iRPuvodniPoziceF]);
        radekPredPos = vytvorPlnyRadek(castiPredPosledniho[iRPuvodniPoziceF]);
        znakPos = radekPos.charAt(iSPuvodniPoziceF);
        znakPredPos = radekPredPos.charAt(iSPuvodniPoziceF);
        if (opustilPesecPole(znakPos, znakPredPos, znakPesce)) { //vime ze pesec se pohnul z pozice mozne promeny
            radekPos = vytvorPlnyRadek(castiPosledniho[iRAktualniF]);
            radekPredPos = vytvorPlnyRadek((castiPredPosledniho[iRAktualniF]));
            znakPos = radekPos.charAt(iSAktualniF);
            znakPredPos = radekPredPos.charAt(iSAktualniF);
            //pokud se neco pohlo na misto a pak tam byla figurka odpovidajici promene tak nastala promena
            if (znakPredPos != znakPesce && rovnaSeZnakNekomuZPromeny(znakPos)) {
                String[] posledniCasti = castiPosledniho[castiPosledniho.length - 1].split(ODDELOVVAC_POSLEDNIHO_RADKU);
                int hrac = 2;
                if (posledniCasti[1].equals(POPIS_HRACE_2)) {
                    hrac = 1;
                }
                return vratFigurkuNaZakladePismena(znakPesce, iRPuvodniPoziceF, iSPuvodniPoziceF, s, hrac);
            }
        }

        return null;
    }

    /**
     * Pomocna metoda. Zjisti jestli pismeno odpovida nekteremu pismenu z promeny.
     * @param pismeno - Znak urcujici stav pole, kde mohla nastat promena.
     * @return True pokud odpovida, jinak false.
     */
    private boolean rovnaSeZnakNekomuZPromeny(char pismeno) {
        char[] znakPromeny;
        if (Character.isUpperCase(pismeno)) {
            znakPromeny = new char[] {DAMA_BILA, KUN_BILI, STRELEC_BILI, VEZ_BILA};
        } else {
            znakPromeny = new char[] {DAMA_CERNA, KUN_CERNY, STRELEC_CERNY, VEZ_CERNA};
        }

        for (char znak: znakPromeny) {
            if (znak == pismeno) {
                return true;
            }
        }

        return false;
    }

    /**
     * Pomocna metoda. Metoda zjsti jestli jsou figurky v predanych stavech nepratelske.
     * @param znakPos - Znak urcujici stav pole v poslednim fen retezci.
     * @param znakPredPos - Znak urcujici stav pole v predposlednim fen retezci.
     * @return True pokud jsou, jinak false,
     */
    private boolean jsouNepratelske(char znakPos, char znakPredPos) {
        return znakPos != znakPredPos && Character.isUpperCase(znakPos) != Character.isUpperCase(znakPredPos);
    }

    /**
     * Metoda ziska pocet tahu uskutecnenych bez tahu pescem nebo sebrani figurky z posledniho fen retezce.
     * @return (int)
     */
    public int ziskejPocetZPoslednihoFen() {
        if (getPRUBEH_HRY().isEmpty()) {
            throw new IllegalStateException("Neni co ziskavat z prubehu hry.");
        }
        String posledniTah = this.getPRUBEH_HRY().getLast();
        String[] castiPosledniho = posledniTah.split(POPIS_ODDELOVACE_RADKU);
        String radekPos = castiPosledniho[castiPosledniho.length - 1];
        String cislo;
        String[] posledniCasti = radekPos.split(ODDELOVVAC_POSLEDNIHO_RADKU);
        cislo = posledniCasti[posledniCasti.length - 1];

        return Integer.parseInt(cislo);
    }

    /**
     *Metoda vrati popis hrance, ktery bude hrat v pristim tahu.
     * @param s - Instance tridy Sachovnice.
     * @return Popis hrace ktery je na tahu.
     */
    private String vratPopisHrace(Sachovnice s) {
        if (s.getNaTahu().equals(s.getTym1())) {
            return  POPIS_HRACE_2;
        } else {
            return  POPIS_HRACE_1;
        }
    }

    /**
     * Pomocna metoda. Nacte i-ty radek sachovnice v podobe vhodne pro fen retezec a ten vrati.
     * @param i - Index radku sachovnice ktery se bude nacitat.
     * @param stavHry - Pole definujici stav hry na sachovnici.
     * @param s - Instance tridy sachovnice.
     * @return Radek sachovnice v podobe pro fen retezec.
     */
    private String nactiRadekSachovnice(int i, AFigurka[][] stavHry, Sachovnice s) {
        int pocet = 0;
        StringBuilder radka = new StringBuilder();
        String pismenoF;
        Object[] retezecAPocet;
        for (int j = 0; j < s.getRadky(); j++) {
            if (stavHry[i][j] == null) {
                pocet++;
            } else {
                retezecAPocet = pridejPocet(pocet, radka);
                radka = (StringBuilder)retezecAPocet[0];
                pocet = (int)retezecAPocet[1];
                pismenoF = vratPismenoFigurky(stavHry[i][j], s);
                radka.append(pismenoF);
            }
        }
        retezecAPocet = pridejPocet(pocet, radka);
        radka = (StringBuilder)retezecAPocet[0];
        radka.append(POPIS_ODDELOVACE_RADKU);

        return radka.toString();
    }

    /**
     * Pomocna metoda. Prida do radku pocet pokud je vetsi nez nula.
     * @param pocet - Cislo urucjici pocet volnych poli.
     * @param radka - Retezec reprezentujici doposud nacteny radek sachovnice.
     * @return Object[], kde prvni prvek reprezentuje radku s pridanym poctem a druhy pocet.
     */
    private Object[] pridejPocet(int pocet, StringBuilder radka) {
        if (pocet > 0) {
            radka.append(pocet);
            pocet = 0;
        }

        return new Object[] {radka, pocet};
    }

    /**
     * Zjisti jestli je dana figurka v poli figurek na indexu i kral hrace prislusejici barvy.
     * @param s - Instance tridy Sachovnice.
     * @param i - Index urcujici figurku v poli figurek.
     * @param barva - Color definujici barvu hrace
     * @return True pokud ano, jinak false.
     */
    private boolean jeFKralHrace(Sachovnice s, int i, Color barva) {
        return s.getFIGURKY()[i] != null && s.getFIGURKY()[i] instanceof Kral k && k.getTym().equals(barva);
    }

    /**
     * Metoda vrati popis rosady na zaklade stavu vezi a krale.
     * @param k - Instance predaneho krale.
     * @return Popis rosady krale pro fen retezec.
     */
    private String vratPopisRosady(Kral k) {
        String rosada;
        if (k.ziskejVezeKraleProRosadu()[0] == null) {
            rosada = Character.toString(NEMOZNA_ROSADA);
        } else {
            rosada = Character.toString(KRAL_CERNY);
        }

        if (k.ziskejVezeKraleProRosadu()[1] == null) {
            rosada = rosada + NEMOZNA_ROSADA;
        } else {
            rosada = rosada + DAMA_CERNA;
        }

        return rosada;
    }
}
