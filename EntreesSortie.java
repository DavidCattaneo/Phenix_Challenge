/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phenix_challenge_cattaneo_v2;

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
    
    
    // Cette méthode lit le fichier temporaire des ventes d'un magasin et renvoie
    // un tableau contenant pour chaque référence la quantité vendu.
    public static int[][] obtenirMagasin(UUID magasin){
        
        BufferedReader reader = null;
        int[][] tableauMagasin = null;
        
        try {
            
            File file = new File("ventes-" + magasin.toString() + ".tmp");
            
            if(file.isFile()){
                tableauMagasin = new int[Parametres.nbReferences][2];
                reader = new BufferedReader(new FileReader(file));

                String ligne;
                int produitCourant = 0;
                while ((ligne = reader.readLine()) != null) {

                    String[] ligneEclate = ligne.split("\\|");

                    tableauMagasin[produitCourant][0] = Integer.parseInt(ligneEclate[0]);
                    tableauMagasin[produitCourant][1] = Integer.parseInt(ligneEclate[1]);
                    produitCourant++;

                }
                
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
    
    
    // Cette méthode écrit le fichier temporaire d'in magasin à parir du tableau
    // référence-quantité vendu.
    public static void ecrireMagasin(int[][] ventesMagasin, UUID magasin){
        
        BufferedWriter bw = null;
        FileWriter fw = null;
        String nom = "ventes-" + magasin.toString() + ".tmp";
        
        try {

                fw = new FileWriter(nom);
                bw = new BufferedWriter(fw);

                for(int i = 0; i < ventesMagasin.length; i++)
                {
                    bw.write(ventesMagasin[i][0] + "|" + ventesMagasin[i][1] + System.getProperty("line.separator"));
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
    
    
    // Ecrit le résultat du calcult du top ca donné en paramètre.
    public static void ecritureTopCa(String date, ProduitTopCa[] top){
                
        BufferedWriter bw = null;
        FileWriter fw = null;
        String nom = "top_100_ventes_" + date + ".data";

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
    
}
