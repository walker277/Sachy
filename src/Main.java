import javax.swing.JFrame;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

/**
 * Hlavni trida ve ktere se nachazi main metoda.
 * @author Filip Valtr
 */
public class Main {
    /**
     * Main metoda. Vytvori okno a do nej umisti komponentu drawing panel a tim spusti hru.
     * @param args - Predane argumenty.
     */
    public static void main(String[] args) {
        try {
            //Definuje Sriku okna.
            final int SIRKA = 800;
            //Definuje Vysku okna.
            final int VYSKA = 600;

            JFrame okno = new JFrame();
            okno.setTitle("Semetralni Prace: Filip Valtr,  A22B0107P");
            okno.setSize(SIRKA, VYSKA);

            okno.add(new DrawingPanel(SIRKA, VYSKA)); //prida komponentu
            okno.pack(); //udela resize okna dle komponent

            okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            okno.setLocationRelativeTo(null); //vycentrovat na obrazovce
            okno.setVisible(true);
        } catch (Exception e) {
            //nastal neocekavany stav programu tak ukoncime
            System.err.println("Vyjimka: " + e.getMessage());
            System.exit(6);
        }
    }
}