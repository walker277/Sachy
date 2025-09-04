import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Trida reprezentuje graficky panel pro zobrazeni a ovladani sachovnice. Panel pracuje s instancí Sachovnice,
 * uchovává aktuálně vybranou figurku a dynamicky reaguje na změnu velikosti okna. Dědi od JPanel.
 * Kresli na platno metodou paint.
 * @author Filip Valtr
 */
public class DrawingPanel extends JPanel {
    //== Konstanty
    /** Barva prvniho hrace. */
    private final Color TYM_1 = Color.LIGHT_GRAY;
    /** Barva druheho hrace. */
    private final Color TYM_2 = Color.DARK_GRAY;
    /** Uchovava defaultni sirku okna. */
    private final int SIRKA;
    /** Uchovava defaultni vysku okna. */
    private final int VYSKA;
    /** Obrazovka */
    private final Dimension OBRAZOVKA = Toolkit.getDefaultToolkit().getScreenSize();
    //== Atributy
    /** Uchovava instanci Sachovnice. */
    private Sachovnice s;
    /** Uchova aktualne vybranou (tahnouci) figurku. */
    private AFigurka vybranaF;
    //==Konstruktor
    /**
     * Konstruktor, nastavi klicove atributy jako jsou s a vybranaF. Vystavi sachovnici, vytvori a vystavi figurky na
     * sachovnici. Prida Mouse a Motion listenery pro ovladani figurek hracem.
     * @param sirka - Udava inicializacni sirku okna.
     * @param vyska - Udava iniciallzacni vysku okna.
     */
    public DrawingPanel(int sirka, int vyska) {
        this.SIRKA = sirka;
        this.VYSKA = vyska;
        //nastaveni sachovnice a figurek
        pripravHru(sirka, vyska);
        //nastaveni okna
        this.setPreferredSize(new Dimension(sirka, vyska));
        //mouse listenery pro ovladani figurek.
        pridejListenerProKlikani();
        pridejListenerProTahnutiMysi();
        pridejListenerProKlavesu();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;
        //zkontroluje resize okna
        if(this.getWidth()!= s.getSirka() || this.getHeight() != s.getVyska()) {
            double sirka = this.getWidth();
            double vyska = this.getHeight();
            //prenastavime sachovnici k parametrum okna
            try {
                prenastavSachovnici(sirka, vyska);
            } catch (Exception e) {
                //nepodarilo prenastavit sachovnici, vypiseme vyjimku a okno vratime do puvodniho stavu
                System.err.println("Vyjimka: " + e.getMessage());
                this.setPreferredSize(new Dimension((int)s.getSirka(), (int)s.getVyska()));
                this.revalidate();
            }
        }
        //vykreslime sachovnici
        try {
            s.vykresliSachovnici(Color.WHITE, Color.BLACK, g2, vybranaF, this);
        } catch (Exception e) {
            //nepodarilo se vykreslit sachovnici proto program ukoncime
            System.err.println("Vyjimka: " + e.getMessage());
            //Oznaceni pro chybovi stav kdy se nepodarilo vykreslit sachovnici
            final int SPATNE_VYKRESLENI_S = 5;
            System.exit(SPATNE_VYKRESLENI_S);
        }
    }

    /**
     * Metoda vytvori instanci sachovnice, na kterou odkaze refernci s.
     * @param sirka - Sirka okna.
     * @param vyska - Vyska okna.
     * @param nejmensi - Mensi z sirky nebo vysky.
     * @param nejvetsi - Vetsi z sirky nebo vysky.
     */
    private void vystavSachovnici(double sirka, double vyska, double nejmensi, double nejvetsi) {
        s = new Sachovnice(sirka, vyska, nejmensi, nejvetsi, TYM_1, TYM_2, this.OBRAZOVKA);
        s.vytvorSachovnici();
    }

    /**
     * Prenastavi rozmery sachovnice a upravi pozice figurek na sachovnici.
     * (Skaluje celou sachovnici).
     * @param sirka - Sirka okna.
     * @param vyska - Vyska okna.
     */
    private void prenastavSachovnici(double sirka, double vyska){
        s.setSachovnice(sirka, vyska, Math.min(sirka, vyska), Math.max(sirka, vyska), this.OBRAZOVKA);
        s.vytvorSachovnici();

        for (int i = 0; i < s.getRadky(); i++) {
            for (int j = 0; j < s.getRadky(); j++) {
                if (s.getSTAV_HRY()[i][j] != null) {
                    s.getSTAV_HRY()[i][j].setPoziceX(s.getHraciPole()[i][j].getX());
                    s.getSTAV_HRY()[i][j].setPoziceY(s.getHraciPole()[i][j].getY());
                    s.getSTAV_HRY()[i][j].setVelikostCtv(s.getHraciPole()[i][j].getHeight());
                    s.getSTAV_HRY()[i][j].skalujFigurku();
                    s.getSTAV_HRY()[i][j].vytvorT();
                }
            }
        }
    }

    /**
     * Metoda vlozi figurku na sachovnici.
     * @param f - Predana figurka.
     * @param s - Sachovnice na kterou se figurka umistuje.
     * @param radek - Index radku pole na sachovnici kam se figurka umistuje.
     * @param sloupec - Index sloupce pole na sachovnici kam se figurka umistuje.
     */
    private void vlozFigurku(AFigurka f, Sachovnice s, int radek, int sloupec) {
        f.setPoziceX(s.getHraciPole()[radek][sloupec].getX());
        f.setPoziceY(s.getHraciPole()[radek][sloupec].getY());
        f.vytvorT();
        s.getSTAV_HRY()[radek][sloupec] = f;
    }

    /**
     * Metoda vytvori instance jednotlivych figurek, prida figurky sachovnici do atritbutu uchovavajici figurky
     * a vlozi je na sachovnici.
     * @param s - Instance tridy Sachovnice.
     */
    private void vytvorAVystavFigurky(Sachovnice s) {
        //pesci
        for (int i = 0; i < s.getRadky(); i++) {
            Pesec pesec = new Pesec(1, i, s.getHraciPole()[1][i].getHeight(), TYM_2, s);
            vytvorAVystavFigurku(s, 1, i, pesec);
            pesec = new Pesec(6, i, s.getHraciPole()[6][i].getHeight(), TYM_1, s);
            vytvorAVystavFigurku(s, 6, i, pesec);
        }
        //prvni hrac
        Kral kral1 = new Kral(0, 4, s.getHraciPole()[0][4].getHeight(), TYM_2, s);
        vytvorAVystavFigurku(s, 0, 4, kral1);

        Dama dama1 = new Dama(0, 3, s.getHraciPole()[0][3].getHeight(), TYM_2, s);
        vytvorAVystavFigurku(s,0, 3, dama1);

        Strelec strelec1 = new Strelec(0, 2, s.getHraciPole()[0][2].getHeight(), TYM_2, s);
        vytvorAVystavFigurku(s, 0, 2, strelec1);
        Strelec strelec2 = new Strelec(0, 5, s.getHraciPole()[0][5].getHeight(), TYM_2, s);
        vytvorAVystavFigurku(s, 0, 5, strelec2);

        Kun kun1 = new Kun(0, 1, s.getHraciPole()[0][1].getHeight(), TYM_2, s);
        vytvorAVystavFigurku(s, 0, 1, kun1);
        Kun kun2 = new Kun(0, 6, s.getHraciPole()[0][6].getHeight(), TYM_2, s);
        vytvorAVystavFigurku(s, 0,6, kun2);

        Vez vez1 = new Vez(0, 0, s.getHraciPole()[0][0].getHeight(), TYM_2, s);
        vytvorAVystavFigurku(s, 0, 0, vez1);
        Vez vez2 = new Vez(0, 7, s.getHraciPole()[0][7].getHeight(), TYM_2, s);
        vytvorAVystavFigurku(s, 0, 7, vez2);
        //druhy hrac
        Kral kral2 = new Kral(7, 4, s.getHraciPole()[7][4].getHeight(), TYM_1, s);
        vytvorAVystavFigurku(s, 7, 4, kral2);

        Dama dama2 = new Dama(7, 3, s.getHraciPole()[7][3].getHeight(), TYM_1, s);
        vytvorAVystavFigurku(s, 7, 3, dama2);

        Strelec strelec3 = new Strelec(7, 2, s.getHraciPole()[7][2].getHeight(), TYM_1, s);
        vytvorAVystavFigurku(s, 7, 2, strelec3);
        Strelec strelec4 = new Strelec(7, 5, s.getHraciPole()[7][5].getHeight(), TYM_1, s);
        vytvorAVystavFigurku(s, 7, 5, strelec4);

        Kun kun3 = new Kun(7, 1, s.getHraciPole()[7][1].getHeight(), TYM_1, s);
        vytvorAVystavFigurku(s, 7, 1, kun3);
        Kun kun4 = new Kun(7, 6, s.getHraciPole()[7][6].getHeight(), TYM_1, s);
        vytvorAVystavFigurku(s, 7, 6, kun4);

        Vez vez3 = new Vez(7, 0, s.getHraciPole()[7][0].getHeight(), TYM_1, s);
        vytvorAVystavFigurku(s, 7, 0, vez3);
        Vez vez4 = new Vez(7, 7, s.getHraciPole()[7][7].getHeight(), TYM_1, s);
        vytvorAVystavFigurku(s, 7, 7, vez4);

        s.getFEN_ALG().fenAlgoritmus(s);
    }

    /**
     * Metoda prida vytvorenou figurku do pole figurek a vlozi ji na sachovnici
     * @param s - Instance tridy Sachovnice.
     * @param iRadkuPoleF - Index radku pole figruky.
     * @param iSloupcePoleF - Index sloupce pole figurky.
     * @param figurka - Instance predane figurky.
     */
    private void vytvorAVystavFigurku(Sachovnice s, int iRadkuPoleF, int iSloupcePoleF, AFigurka figurka) {
        s.pridejF(figurka);
        vlozFigurku(figurka, s, iRadkuPoleF, iSloupcePoleF);
    }

    /**
     * Metoda zjisti pole na zaklade souradnic a presune figurku.
     * @param x - X-ova souradnice presunu.
     * @param y - Y-ova souradnice presunu.
     * @return True pokud presun probehl v poradku, jinak false.
     */
    private boolean presunF(double x, double y) {
        int[] indexyPole = s.getIndexyPole(x, y);
        if (indexyPole == null) {
            return false; //pole se nepodarilo ziskat
        }
        try {
            s.provedTah(vybranaF.getIndexRadkuPoleF(), vybranaF.getIndexSloupcePoleF(), indexyPole[0], indexyPole[1]);
        } catch (Exception e) {
            System.err.println("Vyjimka: " + e.getMessage());
            if (e instanceof IllegalStateException) {
                System.exit(4);
                //nastal neplatny stav v interni strukture
                final int NEPLATNY_STAV_V_S = 4;
                System.exit(NEPLATNY_STAV_V_S);
            }
            return false;
        }
        //nastavime kdo bude tahnout pristi kolo
        s.setNaTahu(vybranaF.getTym());//tahne souper

        return true;
    }

    /**
     * Metoda prida mouse listenery pro vybery a pokladani figurek na sachovnici.
     */
    private void pridejListenerProKlikani() {
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    obsluzUdalostStisknuti(e);
                } catch (Exception ex) {
                    System.err.println("Vyjimka: " + ex.getMessage());
                    //figurku vratime do puvodniho stavu
                    vratDoPuvodnihoStavu();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    obsluzUdalostUvolneni(e);
                } catch (Exception ex) {
                    System.err.println("Vyjimka: " + ex.getMessage());
                    //figurku vratime do puvodniho stavu
                    vratDoPuvodnihoStavu();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    /**
     * Metoda pridava mouse listenery pro tahnuti mysi.
     */
    private void pridejListenerProTahnutiMysi() {
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                try {
                    if (vybranaF != null) { //figurku budeme opticky presouvat po platne
                        vybranaF.setPoziceX(e.getX());
                        vybranaF.setPoziceY(e.getY());
                        repaint();
                    }
                } catch (Exception ex) {
                    System.err.println("Vyjimka: " + ex.getMessage());
                    //figurku vratime do puvodniho stavu
                    vratDoPuvodnihoStavu();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
    }

    /**
     * Metoda obslouzi udalost tak, ze nastavi atribut vybranaF pro dalsi manipulaci.
     * Pokud vybere figurku nastavi vybranaF a vytvori ji validniTahy.
     * @param e - Udalost.
     */
    private void obsluzUdalostStisknuti(MouseEvent e) {
        for( int i = 0; i < s.getFIGURKY().length; i++) {
            if (jeMozneVybratF(e, i)) {
                vybranaF = s.getFIGURKY()[i]; //vybereme figurku
                vybranaF.vytvorMozneTahy();
                s.filtrujSachNaMoznychTazich(vybranaF.getMozneTahy(), vybranaF);
                break;
            }
        }
    }

    /**
     * Metoda obslouzi udalost uvolneni mysi tak, ze pokud pred tim byla vybrana figurka,
     * tak prenastavi jeji pozice X a Y (Kontroluje i jestli je pole k tomu uzposobene).
     * Na uplnem konci nastavi atribut na vychozi hodnotu null a prekresli okno.
     * @param e - Udalost.
     */
    private void obsluzUdalostUvolneni(MouseEvent e) {
        if (vybranaF != null && s.jePoleVolne(e.getX(), e.getY(), vybranaF)) {
            if (!presunF(e.getX(), e.getY())) { //presuneme figurku
               vybranaF.vraFDoPuvodnihoStavu(); //presun se nezdaril aproto figurku vratime.
            }
        } else { // pokud pole neni volne tzn. je tam figurka stejneho tymu tak se figurka vrati na puvodni misto
            if (vybranaF != null) {
                vybranaF.vraFDoPuvodnihoStavu();
            }
        }
        vybranaF = null;
        repaint();
    }

    /**
     * Metoda obslouzi udalost stisknuti klavesy. Pro klavesu n ukonci hru a spusti novou. Pro klavesu z vrati tah.
     */
    private void pridejListenerProKlavesu() {
        //nastavime focus klavesnice na jpanel
        this.setFocusable(true);
        this.requestFocusInWindow();

        this.addKeyListener(new KeyListener() {
           @Override
           public void keyTyped(KeyEvent e) {
               if (e.getKeyChar() == 'z') {
                   obsluzVraceni();
                   repaint();
               }
               if (e.getKeyChar() == 'n') {
                   //zahodime objekt
                   s = null;
                   //nastaveni sachovnice a figurek
                   pripravHru(SIRKA, VYSKA);
                   repaint();
               }
           }

           @Override
           public void keyPressed(KeyEvent e) {

           }

           @Override
           public void keyReleased(KeyEvent e) {

           }
       });
    }

    /**
     * Metoda zjisti jestli dana figurka z pole figurek byla vybrana uzivatelem ktery je na tahu.
     * @param e - Udalost obsahujici souradnice bodu kam uzivatel klikl.
     * @param i - Index urcujici prvek v poli figurek.
     * @return True pokud je, jinak false.
     */
    private boolean jeMozneVybratF(MouseEvent e, int i) {
        if (!s.jeKonecHry() && s.getFIGURKY()[i] != null && s.getFIGURKY()[i].jeFVybrana(e.getX(), e.getY())) {
            //pokud figurka nalezi hraci ktery je na tahu)
            return s.getFIGURKY()[i].getTym().equals(s.getNaTahu());
        }

        return false;
    }

    /**
     * Metoda vrati posledni tah a aktualizuje atribut pro urceni hrace ktery je na tahu.
     */
    private void obsluzVraceni() {
        try {
            if (s.getZnazorneniTahu() == null) { //pokud neni co vracet
                s.setNaTahu(s.getTym2()); //bude hrat hrac 1
                return;
            }
            s.vratTah();
            Color barvaPoslednihoTazeneho = s.getSTAV_HRY()[s.getZnazorneniTahu()[2]][s.getZnazorneniTahu()[3]].getTym();
            if (s.getFEN_ALG().getPRUBEH_HRY().size() == 1) {
                s.setNaTahu(s.getTym2());
            } else {
                s.setNaTahu(barvaPoslednihoTazeneho);
            }
        } catch (Exception e) {
            System.err.println("Vyjimka: " + e.getMessage());
            //nepodarilo se vratit tah a tim padem program ukoncime
            final int CHYBNE_VRACENI_TAHU = 3;
            System.exit(CHYBNE_VRACENI_TAHU);
        }
    }

    /**
     * Metoda pripravi hru, tak že vytvori instanci sachovnice a vytvori a vystavi na ni figurky v zakldanich pozicich.
     * @param sirka - Udava inicializacni sirku okna.
     * @param vyska - Udava iniciallzacni vysku okna.
     */
    private void pripravHru(int sirka, int vyska) {
        try {
            vystavSachovnici(sirka, vyska, Math.min(sirka, vyska), Math.max(sirka, vyska));
            vytvorAVystavFigurky(s);
        } catch (Exception e) {
            System.err.println("Vyjimka: " + e.getMessage());
            //nepodarilo se vytvorit sachovnici nebo vystavit figurky program ukoncime
            final int CHYBNA_PRIPRAVA_HRY = 1;
            System.exit(CHYBNA_PRIPRAVA_HRY);
        }
    }

    /**
     * Pomocna Metoda. Pouziva se pro vraceni figurek pokud selze neco v listenerech pro tahnuti, volbu figurky a
     * volbu mista.
     */
    private void vratDoPuvodnihoStavu() {
        if (vybranaF != null) {
            try {
                vybranaF.vraFDoPuvodnihoStavu();
            } catch (Exception e2) {
                System.err.println("Vyjimka: " + e2.getMessage());
                //nepodarilo se vrati figurku do puvodniho stavu
                final int CHYBNE_VRACENI_F = 2;
                System.exit(CHYBNE_VRACENI_F);
            }
        }
    }
}
