/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phenix_challenge_cattaneo_v2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.UUID;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 *
 * @author Moloch
 */
public class Phenix_challenge_Cattaneo_v2 {
    
    
    /*
     * Mise en mémoire des transactions pour les exercices avec un top global (2 et 4).
     * 
     * 1) On lit ligne par ligne les transactions
     * 2) Si le magasin n'existe pas on l'ajoute à la liste
     * 3) On ajoute l'entrée au buffer correspondant
     * 
     */
    public static void miseEnMemoireTransactionsBuffer(String date, Map<UUID, BufferMagasin> tableBuffer){

        BufferedReader reader = null;

        try {

            File file = new File("transactions_" + date + ".data");
            reader = new BufferedReader(new FileReader(file));

            String ligne;
            while ((ligne = reader.readLine()) != null) {

                String[] ligneEclate = ligne.split("\\|");

                UUID magasinCourant = UUID.fromString(ligneEclate[2]);
                int produitCourant = Integer.parseInt(ligneEclate[3]);
                int quantiteCourante = Integer.parseInt(ligneEclate[4]);
                BufferMagasin bufferMag;

                if(tableBuffer.containsKey(magasinCourant)){

                    bufferMag = tableBuffer.get(magasinCourant);

                }else{

                    bufferMag = new BufferMagasin(magasinCourant);
                    tableBuffer.put(magasinCourant, bufferMag);

                }

                bufferMag.ajoutEntree(produitCourant, quantiteCourante);

            }


        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
    
    // A la fin de la mise en mémoire on vide les derniers buffers pour avoir les
    // fichiers de ventes complets.
    public static void viderBuffers(Map<UUID, BufferMagasin> tableBuffer){
        
        for(UUID magasinCourant: tableBuffer.keySet()){
            
            int[][] ventesMagasin = tableBuffer.get(magasinCourant).majMagasin();
            
            EntreesSortie.ecrireMagasin(ventesMagasin, magasinCourant);

        }
    }
    
    // On insère un produit dans le top du ca à l'emplacement du dernier plus petit
    // et on met à jour l'emplacement du plus petit
    public static int insererProduitTopCa(UUID magasin, int reference, float ca, int emplacement, ProduitTopCa[] top){
        
        top[emplacement] = new ProduitTopCa(magasin, reference, ca);
        
        int refMin = 0;
        float min = top[0].getCa();
        
        for(int i = 1; i < Parametres.nombreTop; i++){
            if(top[i] == null){
                min = 0;
                refMin = i;
            }else if(top[i].getCa() < min){
                min = top[i].getCa();
                refMin = i;
            }
            
        }
        
        return refMin;
    }

    public static void main(String[] args) {
              
        // Création de la date au format yyyyMMdd
        
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        df.setTimeZone(tz);
        String dateFormatee = df.format(new Date());
        
        // Création de la table des magasins
        Map<UUID, BufferMagasin> tableBuffer = new HashMap();
        
        // Création du top ca
        ProduitTopCa[] topCa = new ProduitTopCa[Parametres.nombreTop];
        
        // Mise en mémoire de toutes les transactions
        miseEnMemoireTransactionsBuffer(dateFormatee, tableBuffer);
        viderBuffers(tableBuffer);
        
        
        // Construction du top
        float min = 0;
        int refMin = 0;
        
        for(UUID magasinCourant: tableBuffer.keySet()){
            
            int[][] ventesMagasinCourant = EntreesSortie.obtenirMagasin(magasinCourant);
            float[] tableReference = EntreesSortie.miseEnMemoireReference(magasinCourant, dateFormatee);
            
            for(int i = 0; i < Parametres.nbReferences; i++){
                if(ventesMagasinCourant[i][0] != 0){
                    float ca = ventesMagasinCourant[i][1] *  tableReference[ventesMagasinCourant[i][0] - 1];
                    
                    if(ca > min){

                        refMin = insererProduitTopCa(magasinCourant, ventesMagasinCourant[i][0], ca, refMin, topCa);
                        
                        if(topCa[refMin] != null){
                            min = topCa[refMin].getCa();
                        }else{
                            min = 0;
                        }
                    }
                }
            }
        }
        
        // Ecriture du Top
        
        EntreesSortie.ecritureTopCa(dateFormatee, topCa);
        
    }
}
