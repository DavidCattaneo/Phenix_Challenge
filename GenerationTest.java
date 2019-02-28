
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
 *    Dans un premier temps les tests générés respecteront ces conditions
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
    
    private static int nbMagasin = 5;
    private static int nbReferences = 100;
    private static int prixMax = 100;
    private static int quantiteMax = 10;
    private static int nbTransactions = 1000;
    
    // La conservation des UUID des magasins est nécessaire pour les transactions
    private static List<UUID> listeMagasin = new ArrayList();
    
    
    public static void genererReferencielProduit(String date, UUID idMagasin){
        
        BufferedWriter bw = null;
        FileWriter fw = null;
        String nom = "reference_prod-" + idMagasin.toString() + "_" + date + ".data";
        
        try {

                fw = new FileWriter(nom);
                bw = new BufferedWriter(fw);
                
                for(int i = 1; i < nbReferences + 1; i++)
                {
                    bw.write(Integer.toString(i) + "|" + Double.toString(Math.round(Math.random()*prixMax*100)/100.0) + System.getProperty("line.separator"));
                }
                

                System.out.println(nom + "Créé");

        } catch (IOException e) {

                e.printStackTrace();

        } finally {

                try {

                        if (bw != null)
                                bw.close();

                        if (fw != null)
                                fw.close();

                } catch (IOException ex) {

                        ex.printStackTrace();

                }

        }
    }
    
    public static void genererTransactions(String date){
        
        
        BufferedWriter bw = null;
        FileWriter fw = null;
        String nom = "transactions_" + date + ".data";
        String temps = date + "T" + "000000" + "+" + "0000";
        
        try {

                fw = new FileWriter(nom);
                bw = new BufferedWriter(fw);
                
                for(int i = 1; i < nbTransactions + 1; i++)
                {
                    UUID magasin = listeMagasin.get((int) (Math.random() * nbMagasin));
                    int produit =  (int) (Math.random() * (nbReferences - 1)) + 1;
                    int quantite =  (int) (Math.random() * quantiteMax);
                    
                    String ligne = Integer.toString(i) + "|" + temps + "|" + 
                            magasin.toString() + "|" + Integer.toString(produit) +
                            "|" + Integer.toString(quantite) +  System.getProperty("line.separator");
                    
                    bw.write(ligne);
                }
                

                System.out.println(nom + "Créé");

        } catch (IOException e) {

                e.printStackTrace();

        } finally {

                try {

                        if (bw != null)
                                bw.close();

                        if (fw != null)
                                fw.close();

                } catch (IOException ex) {

                        ex.printStackTrace();

                }

        }
    }

    public static void main(String[] args) {
        
        // Création de la date au format yyyyMMdd
        
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        df.setTimeZone(tz);
        String dateFormatee = df.format(new Date());
        
        // Création des UUID des magasins et génération des références
        
        for(int i = 0; i < nbMagasin; i++){
            UUID magasinCourant = UUID.randomUUID();
            listeMagasin.add(magasinCourant);
            //genererReferencielProduit(dateFormatee,magasinCourant);
        }
        
        // Génération du fichier de transaction
        //genererTransactions(dateFormatee);
        
        genererReferencielProduit(dateFormatee,UUID.fromString("6c0ec7c8-8b3b-4eed-9698-a2fb85dd6462"));
        
        
    }
}
