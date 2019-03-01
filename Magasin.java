package phenix_challenge_cattaneo_v2;

import java.util.Map;

/**
 *
 * @author Moloch
 * 
 * Encapsule le résultat de obtenirMagasin
 */

public class Magasin{

    public int fin;

    public Map<Integer, Integer> tableauMagasin;

    public Magasin(int fin, Map<Integer, Integer> tableauMagasin) {
        this.fin = fin;
        this.tableauMagasin = tableauMagasin;
    }    
}
