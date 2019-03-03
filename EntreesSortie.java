/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phenix_challenge_cattaneo_v3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author Moloch
 * 
 * 1) Cette classe gère toutes les méthodes de lecture et d'écriture dans les fichiers.
 * 
 * 2) Cette classe n'a donc pas d'attributs et uniquement des méthodes statiques.
 * 
 */
public class EntreesSortie {
    
    
    /*
     * Cette méthode lit le fichier temporaire des ventes d'un magasin et renvoie
     * un tableau contenant pour chaque référence la quantité vendu à partir de début. 
     * Si on dépasse la taille d'une table alors on renvoie la fin sinon 
     * 
     */
    public static int[] obtenirMagasin(UUID magasin, String date){   
        
        int[] tableauMagasin = null;
        
        BufferedReader reader = null;
        
        try {
            
            File file = new File("ventes-" + magasin.toString() + "-" + date + ".tmp");
            
            if(file.isFile()){
                
                tableauMagasin = new int[Parametres.nbReferences];
                
                reader = new BufferedReader(new FileReader(file));

                String ligne;
                
                while ((ligne = reader.readLine()) != null) {

                    String[] ligneEclate = ligne.split("\\|");
                    int produitCourant = Integer.parseInt(ligneEclate[0]);
                    int quantiteCourante = Integer.parseInt(ligneEclate[1]);
                    
                    if(produitCourant != 0){
                        tableauMagasin[produitCourant - 1] = quantiteCourante;
                    }
                }
                
            // Si le fichier n'existe pas on n'a pas de table et on a fini de lire 
            }else{
                        tableauMagasin = null;
            }
            

        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                if(reader != null)
                        reader.close();
                
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        
        return tableauMagasin;
    }
    
    
    // Cette méthode écrit le fichier temporaire d'un magasin à parir de la table
    // référence-quantité vendu.
    public static void ecrireMagasin(int[] ventesMagasin, UUID magasin, String date){
                
        BufferedWriter bw = null;
        FileWriter fw = null;
        String nom = "ventes-" + magasin.toString() + "-" + date + ".tmp";
        
        try {

                fw = new FileWriter(nom);
                bw = new BufferedWriter(fw);

                for(int i = 0; i < Parametres.nbReferences; i++)
                {
                    if(ventesMagasin[i] > 0){
                        bw.write( (i+1) + "|" + ventesMagasin[i] + System.getProperty("line.separator"));
                    }
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
    
    // Ecrit le résultat du calcult du top vente donné en paramètre.
    public static void ecritureTopVente(String date, ProduitTopVente[] top, UUID magasin){
                
        BufferedWriter bw = null;
        FileWriter fw = null;
        
        String nom;
        
        if(magasin == null){

            nom = "top_" + Parametres.nombreTop + "_ventes_" + date + ".data";

        }else{

            nom = "top_" + Parametres.nombreTop + "_ventes_" + magasin + "_" + date + ".data";

        }
        

        try {

                fw = new FileWriter(nom);
                bw = new BufferedWriter(fw);

                for(int i = 0; i < Parametres.nombreTop; i++)
                {
                    bw.write(top[i].getMagasin() + "|" + top[i].getReference() + "|" + top[i].getQuantite() + System.getProperty("line.separator"));
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
    
    // Ecrit le résultat du calcul du top ca donné en paramètre.
    public static void ecritureTopCa(String date, ProduitTopCa[] top, UUID magasin){
                
        BufferedWriter bw = null;
        FileWriter fw = null;
        
        String nom;
        if(magasin == null){

            nom = "top_" + Parametres.nombreTop + "_ca_" + date + ".data";

        }else{

            nom = "top_" + Parametres.nombreTop + "_ca_" + magasin + "_" + date + ".data";

        }

        try {

                fw = new FileWriter(nom);
                bw = new BufferedWriter(fw);

                for(int i = 0; i < Parametres.nombreTop; i++)
                {
                    bw.write(top[i].getMagasin() + "|" + top[i].getReference() + "|" + top[i].getCa() + System.getProperty("line.separator"));
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
    
    // Lit le fichier de références d'un magasins et le renvoie sous forme d'un 
    // tableau de float où l'indice i est égale à la référence du produit moins 1
    public static float[] miseEnMemoireReference(UUID magasin, String date) {
        
        BufferedReader reader = null;
        float[] tableReference = new float[Parametres.nbReferences];
        
        try {
            
            File file = new File("reference_prod-" + magasin.toString() + "_" + date + ".data");
            reader = new BufferedReader(new FileReader(file));

            String ligne;
            int produitCourant = 0;
            while ((ligne = reader.readLine()) != null) {
                
                String[] ligneEclate = ligne.split("\\|");

                tableReference[produitCourant] = Float.parseFloat(ligneEclate[1]);
                produitCourant++;
                
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
        
        return tableReference;
        
    }
    
    static void supprimerFichierTmp(UUID magasin, String date){
        
        File file = new File("ventes-" + magasin.toString() + "-" + date + ".tmp");
        
        if (file.exists() && file.canWrite()) {
            
            file.delete();
        }        
    }
}
