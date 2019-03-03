package phenix_challenge_cattaneo_v3;


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
                

                System.out.println(nom + " Créé");

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
     *      -C pour générer les référenciels magasin
     *      -D <AAAAMMJJ> pour spécifier une date au format AAAAMMJJ
     */
    public static void main(String[] args) {
        
        boolean vente = true;
        boolean journalier = true;
        String dateFormatee = null;
        int nombreJours = 1;
        String datedebut = null;
        
        for(int i = 0; i < args.length; i++){
            
            if(args[i].equals("-C")){
                
                vente = false;
                
            }else if(args[i].equals("-D")){
                
                journalier = false;
                try{
                    dateFormatee = args[i+1];
                }
                catch(Exception e){
                    System.out.println("Erreur -D doit être suivi d'une date au format AAAAMMJJ");
                    System.err.println(e);
                    System.exit(-1);
                }
            }else{
                if(args[i].equals("-S")){
                    nombreJours = Parametres.joursSemaine;
                }
            }
        }
        
        // La liste des magasins
        List<UUID> listeMagasin = new ArrayList();
        
        // Création de la date au format yyyyMMdd
        if(journalier){
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            df.setTimeZone(tz);
            dateFormatee = df.format(new Date());
        }
        
        datedebut = dateFormatee;
        
        // Création des UUID des magasins et génération des références si besoin
        for(int i = 0; i < Parametres.nbMagasin; i++){
            UUID magasinCourant = UUID.randomUUID();
            listeMagasin.add(magasinCourant);
            
            if(!vente){
                for(int j = 0; j < nombreJours; j++){
                    genererReferencielProduit(dateFormatee,magasinCourant);
                    dateFormatee = Phenix_challenge_Cattaneo_v3.jourPrecedent(dateFormatee);
                }
            }       
        }
        
        if(!vente){
            System.out.println("Référenciels créés");
        }
        
        datedebut = dateFormatee;
        
        // Génération du fichier de transaction
        for(int j = 0; j < nombreJours; j++){
            genererTransactions(dateFormatee, listeMagasin);
            dateFormatee = Phenix_challenge_Cattaneo_v3.jourPrecedent(dateFormatee);
        }
                
        
    }
}
