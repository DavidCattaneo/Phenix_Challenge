
package phenix_challenge_cattaneo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;



/**
 *
 * @author Moloch
 * 
 * Version Exercice 1 et 3 uniquement
 * 
 * But: Générer un fichier contenant les 100 meilleurs ventes d'un magasin (id et nombre)
 * et les 100 meilleurs ca
 * 
 * 1) En mémoire 15 millions d'entrées (une pour chaque article) rentre en mémoire
 * 
 * 2) On ne veut pas forcément un tri sur tous les produits (n log n)
 * 
 * 3) Pour l'égalité rien n'est précisé. J'utilise l'ordre des références.
 * 
 * 4) Pour le ca la mise en mémoire du fichier de référence va aussi et on a juste a 
 *      faire la multiplication par le prix. -> possibilité de faire les 2 en même temps
 * 
 * Réalisation:
 * 
 * - Lire le fichier de transaction et pour celle du magasin selectionné ajouter les
 *      quantités en conservant les 100 plus grands à jour.
 * - Chercher les 100 plus vendus à la fin (on pourra trier ultérieurement sur le top)
 * 
 */
public class Phenix_challenge_Cattaneo {
    
    private static int nbReferences = 1000;
    private static int top = 5;

    private static int[] produitsGeneraux;
    private static float[] prixProduits;
    private static int[][] topProduitsQte;
    private static float[][] topProduitsCa;
    
    
    public static int majReferenceDernierQte(){
        
        int minQte = topProduitsQte[0][1];
        int minInd = 0;
        
        for(int i = 0; i < top; i++){
            if (topProduitsQte[i][1] < minQte){
                minQte = topProduitsQte[i][1];
                minInd = i;
            }
        }
        
        return minInd;
    }
    
    public static int majReferenceDernierCa(){
        
        float minCa = topProduitsCa[0][1];
        int minInd = 0;
        
        for(int i = 0; i < top; i++){
            if (topProduitsCa[i][1] < minCa){
                minCa = topProduitsCa[i][1];
                minInd = i;
            }
        }
        
        return minInd;
    }
    
    public static void miseEnMemoireTransactions(String date, UUID magasin){
        
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
                
                if(magasinCourant.equals(magasin)) {
                    produitsGeneraux[produitCourant-1] += quantiteCourante;
                }
                    
            }
            

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void ecritureTopQte(String date, UUID magasin){
        
        BufferedWriter bw = null;
        FileWriter fw = null;
        String nom = "top_100_ventes_" + magasin.toString() + "_" + date + ".data";

        try {

                fw = new FileWriter(nom);
                bw = new BufferedWriter(fw);

                for(int i = 0; i < top; i++)
                {
                    bw.write((topProduitsQte[i][0]+1) + "|" + topProduitsQte[i][1] + System.getProperty("line.separator"));
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
    
    public static void ecritureTopCa(String date, UUID magasin){
        
        BufferedWriter bw = null;
        FileWriter fw = null;
        String nom = "top_100_Ca_" + magasin.toString() + "_" + date + ".data";

        try {

                fw = new FileWriter(nom);
                bw = new BufferedWriter(fw);

                for(int i = 0; i < top; i++)
                {
                    bw.write((Math.round(topProduitsCa[i][0])+1) + "|" + produitsGeneraux[Math.round(topProduitsCa[i][0])] + "|" + topProduitsCa[i][1] + System.getProperty("line.separator"));
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
    
    public static void miseEnMemoireReference(UUID magasin, String date) {
        
        BufferedReader reader = null;
        
        try {
            
            File file = new File("reference_prod-" + magasin.toString() + "_" + date + ".data");
            reader = new BufferedReader(new FileReader(file));

            String ligne;
            int produitCourant = 0;
            while ((ligne = reader.readLine()) != null) {
                
                String[] ligneEclate = ligne.split("\\|");

                prixProduits[produitCourant] = Float.parseFloat(ligneEclate[1]);
                produitCourant++;
                
            }
            

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    public static void calculTopQte(){
        
        int quantiteTopieme = produitsGeneraux[0];
        int referenceTopieme = 0;
        
        int compteurTop=0;
        
        for(int i = 0; i < nbReferences; i++){
            if(compteurTop < top) {
                
                topProduitsQte[compteurTop][0] = i;
                topProduitsQte[compteurTop][1] = produitsGeneraux[i];
                
                if(produitsGeneraux[i] < quantiteTopieme){
                    referenceTopieme = compteurTop;
                    quantiteTopieme = topProduitsQte[referenceTopieme][1];
                }
                compteurTop = compteurTop + 1;
            }else{
                if(produitsGeneraux[i] > quantiteTopieme){
                    topProduitsQte[referenceTopieme][0] = i;
                    topProduitsQte[referenceTopieme][1] = produitsGeneraux[i];
                    referenceTopieme = majReferenceDernierQte();
                    quantiteTopieme = topProduitsQte[referenceTopieme][1];
                }
            }
        }
    }
    
    public static void calculTopCa(){
        
        float caTopieme = produitsGeneraux[0] * prixProduits[0];
        int referenceTopieme = 0;
        
        int compteurTop = 0;
        
        for(int i = 0; i < nbReferences; i++){
            if(compteurTop < top) {
                
                topProduitsCa[compteurTop][0] = i;
                topProduitsCa[compteurTop][1] = produitsGeneraux[i] * prixProduits[i];
                
                if(produitsGeneraux[i] * prixProduits[0] < caTopieme){
                    referenceTopieme = compteurTop;
                    caTopieme = topProduitsCa[referenceTopieme][1];
                }
                compteurTop = compteurTop + 1;
            }else{
                if(produitsGeneraux[i] * prixProduits[i] > caTopieme){
                    topProduitsCa[referenceTopieme][0] = i;
                    topProduitsCa[referenceTopieme][1] = produitsGeneraux[i] * prixProduits[i];
                    referenceTopieme = majReferenceDernierCa();
                    caTopieme = topProduitsCa[referenceTopieme][1];
                }
            }
        }
    }
    
    public static void main(String[] args) {
        
        int exercice = Integer.parseInt(args[0]);
        
        produitsGeneraux = new int[nbReferences];
        if(exercice == 3){
            prixProduits = new float[nbReferences];
            topProduitsCa = new float[top][2];
        }
        if(exercice == 1){
            topProduitsQte = new int[top][2];
        }
        
        // Récupération du magasin en argument
        
        UUID magasin = UUID.fromString(args[1]);

        // Création de la date au format yyyyMMdd
        
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        df.setTimeZone(tz);
        String dateFormatee = df.format(new Date());
        
        // Lecture du fichier de references et mise en mémoire du prix de chaque
        //  article
        
        if(exercice == 3){
            miseEnMemoireReference(magasin, dateFormatee);
        }
        
        // Lecture du fichier de transactions et mise en mémoire de la quantité 
        //  de chaque produit
        
        miseEnMemoireTransactions(dateFormatee, magasin);
        
        /*for(int i = 0; i < nbReferences; i++){
            System.out.println(i + " " + produitsGeneraux[i]);
        }*/
        
        // Calcul et mise en mémoire du top
        if(exercice == 1){
            calculTopQte();
        }
        if(exercice == 3){
            calculTopCa();
        }
        
        /*for(int i = 0; i < top; i++){
            System.out.println(topProduits[i][0] + " " + topProduits[i][1]);
        }*/
        
        // Ecriture du top
        
        if(exercice == 1){
            ecritureTopQte(dateFormatee, magasin);
        }
        if(exercice == 3){
            ecritureTopCa(dateFormatee, magasin);
        }
    }

}
