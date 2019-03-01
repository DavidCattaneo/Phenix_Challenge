package phenix_challenge_cattaneo_v2;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

/**
 *
 * @author David Cattaneo
 * 
 * Le but de cette classe est de générer les tests pour valider l'application
 * 
 * 1) Dans le jeu de données présentés on:
 *      0 < prix < 100
 *      0 < qte < 10
 *    Les tests générés respecteront ces conditions
 * 
 * 2) On va générer 1500 références magasins et leurs référenciels produits: 
 *      nombre de magasins actuel + une marge pour l'augmentation de plus de 10ans
 *    Ces référenciels produits contiendront 15 millions d'entrées:
 *      quelques millions et une augmentation sur 10 ans ne fait que peu varier
 *    Les fichiers de transactions auront 150 millions d'entrées:
 *      quelques millions qui augment significativement chaque mois
 * 
 * 3) L'heure des transactions n'étant pas nécessaire pour les exercices je vais la fixer
 *    De même le fait d'avoir plusieurs transactions de même id n'est ici pas traité.
 * 
 */
public class GenerationTest {
    
    // Génère le fichier de référence produit pour un magasin donné
    public static void genererReferencielProduit(String date, UUID idMagasin){
        
        BufferedWriter bw = null;
        FileWriter fw = null;
        String nom = "reference_prod-" + idMagasin.toString() + "_" + date + ".data";
        
        try {

                fw = new FileWriter(nom);
                bw = new BufferedWriter(fw);
                
                for(int i = 1; i < Parametres.nbReferences + 1; i++)
                {
                    bw.write(Integer.toString(i) + "|" + Double.toString(Math.round(Math.random()*Parametres.prixMax*100)/100.0) + System.getProperty("line.separator"));
                }
                

                System.out.println(nom + "Créé");

        } catch (IOException e) {

                System.err.println(e);

        } finally {

                try {

                        if (bw != null)
                                bw.close();

                        if (fw != null)
                                fw.close();

                } catch (IOException e) {

                        System.err.println(e);

                }

        }
    }
    
    
    // Générère la liste des transactions pour liste de magasins donnée
    public static void genererTransactions(String date, List<UUID> listeMagasin){
        
        
        BufferedWriter bw = null;
        FileWriter fw = null;
        String nom = "transactions_" + date + ".data";
        String temps = date + "T" + "000000" + "+" + "0000";
        
        try {

                fw = new FileWriter(nom);
                bw = new BufferedWriter(fw);
                
                for(int i = 1; i < Parametres.nbTransactions + 1; i++)
                {
                    UUID magasin = listeMagasin.get((int) (Math.random() * Parametres.nbMagasin));
                    int produit =  (int) (Math.random() * (Parametres.nbReferences)) + 1;
                    int quantite =  (int) (Math.random() * Parametres.quantiteMax);
                    
                    String ligne = Integer.toString(i) + "|" + temps + "|" + 
                            magasin.toString() + "|" + Integer.toString(produit) +
                            "|" + Integer.toString(quantite) +  System.getProperty("line.separator");
                    
                    bw.write(ligne);
                }
                

                System.out.println(nom + "Créé");

        } catch (IOException e) {

                System.err.println(e);

        } finally {

                try {

                        if (bw != null)
                                bw.close();

                        if (fw != null)
                                fw.close();

                } catch (IOException e) {

                        System.err.println(e);

                }

        }
    }

    /*
     * Cette classe exécutable génère les fichier de test pour l'application 
     * Phenix_challenge_Cattaneo_v2 à partir des paramètres de l'application
     * 
     * Arguments:
     *      1er) "V" (Vente) pour ne pas générer tous les référenciels
     */
    public static void main(String[] args) {
        
        // La liste des magasins
        List<UUID> listeMagasin = new ArrayList();
        
        // Création de la date au format yyyyMMdd
        
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        df.setTimeZone(tz);
        String dateFormatee = df.format(new Date());
        
        // Création des UUID des magasins et génération des références
        
        for(int i = 0; i < Parametres.nbMagasin; i++){
            UUID magasinCourant = UUID.randomUUID();
            listeMagasin.add(magasinCourant);
            
            if(args.length == 0 || !args[0].equals("V")){
                genererReferencielProduit(dateFormatee,magasinCourant);
            }
            
        }
        
        // Génération du fichier de transaction
        
        genererTransactions(dateFormatee, listeMagasin);
                
        
    }
}
