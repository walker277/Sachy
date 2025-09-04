import java.awt.geom.Rectangle2D;

/**
 * Trida slouzi pro vytvareni instanci stavu figurky. Vyuziva se pro ulozeni klicovych atributu pro urcite figurky
 * jako jsou veze, pesci, kralove. Vyuziva se hlavne pri vnitrni simulaci tahu pro testovani sachu.
 * @author Filip Valtr
 */
public class StavFigurky {
    //== Atributy instanci
    /** Slouzi pro uchovani atributu braniMimochodem u pesce. */
    public boolean braniMimochodem;
    /** Pro uchovani atributu poleBraniMimochodem u pesce. */
    public Rectangle2D poleBraniMimochodem;
    /** Pro uchovani atributu pohnulSe u veze nebo krale. */
    public boolean pohnulSe;
    /** Pro uchovani atributu rosada u krale. */
    public Rectangle2D[] rosada;
    /** Pro uchovani atributu indexyVeziRosady u krale. */
    public int[] indexyVeziRosady;
    /** Pro uchovani atributu indexyOdstranovanehoPesce u pesce. */
    public int[] indexyOdstranovanehoPesce;
    /** Pro uchovani atributu stavSachu krale predane figurky. */
    public boolean stavSachu;
    /** Pro uchovani atributu indexRPuvodnihoPole predane figurky. */
    public int indexRPuvodnihoPole;
    /** Pro uchovani atributu indexSPuvodnihoPole predane figurky. */
    public int indexSPuvodnihoPole;
    /** Pro uchovani atributu udelalRosadu predane figurky. */
    public boolean udelalRosadu;
    //==Konstruktory
    /**
     * Konstruktor nastavi atributy pro ulozeni stavu figurky.
     * @param vybranaF - Instance figurky jejichz atributy chceme uchovat.
     * @param figurky - Pole instanic figurek na sachovnici.
     * @throws IllegalArgumentException Pokud predana figurka je null.
     */
    public StavFigurky(AFigurka vybranaF, AFigurka[] figurky) {
        if (vybranaF == null) {
            throw new IllegalArgumentException("Predana figurka je null");
        }

        if (figurky == null) {
            throw new IllegalArgumentException("Predane pole figurek je null");
        }

        this.stavSachu = vybranaF.getKral(figurky).getSach();
        this.indexRPuvodnihoPole = vybranaF.getIndexRadkuPoleF();
        this.indexSPuvodnihoPole = vybranaF.getIndexSloupcePoleF();

        switch (vybranaF) {
            case Pesec p -> {
                this.braniMimochodem = p.getBraniMimochodem();
                this.poleBraniMimochodem = p.getPoleBraniMimochodem();
                this.indexyOdstranovanehoPesce = p.getIndexyOdstranovanehoPesce();
            }
            case Kral k -> {
                this.pohnulSe = k.getPohnulSe();
                this.rosada = k.getRosada();
                this.indexyVeziRosady = k.getIndexyVeziRosady();
                this.udelalRosadu = k.getUdelalRosadu();
            }
            case Vez v -> this.pohnulSe = v.getPohnulSe();
            default -> {
            }
        }
    }

    /**
     * Geter k atributu indexRPuvodnihoPole.
     * @return indexRPuvodnihoPole.
     */
    public int getIndexRPuvodnihoPole() {
        return indexRPuvodnihoPole;
    }

    /**
     * Geter k atributu indexSPuvodnihoPole.
     * @return indexSPuvodnihoPole.
     */
    public int getIndexSPuvodnihoPole() {
        return indexSPuvodnihoPole;
    }

    /**
     * Metoda obnovi klicove atributy prislusnym figurkam.
     * @param vybranaF - Instance figurky jejichz atributy chceme obnovit.
     * @param figurky - Pole instanic figurek na sachovnici.
     */
    public void obnovStavFigurky(AFigurka vybranaF, AFigurka[] figurky) {
        if (vybranaF == null) {
            throw new IllegalArgumentException("Predana figurka je null.");
        }

        if (figurky == null) {
            throw new IllegalArgumentException(("Predane pole figurek je null"));
        }

        vybranaF.getKral(figurky).setSach(stavSachu);

        switch (vybranaF) {
            case Pesec p -> {
                p.setBraniMimochodem(braniMimochodem);
                p.setPoleBraniMimochodem(poleBraniMimochodem);
                p.setIndexyOdstranovanehoPesce(indexyOdstranovanehoPesce);
            }
            case Kral k -> {
                k.setPohnulSe(pohnulSe);
                k.setRosada(rosada);
                k.setIndexyVeziRosady(indexyVeziRosady);
                k.setUdelalRosadu(udelalRosadu);
            }
            case Vez v -> v.setPohnulSe(pohnulSe);
            default -> {
            }
        }
    }
}

