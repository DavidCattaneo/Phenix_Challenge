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
import java.util.Map.Entry;
import java.util.TimeZone;


/**
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
    
    /*
     * Mise en mémoire des transaction pour les exercices avec un top magasin.
     * 
     * 1)On lit chaque transaction
     * 2)Si on est sur le magasin concerné on ajoute la quantité à la référence
     */
    public static int[] miseEnMemoireTransactionsMagasin(String date, UUID magasin){
        
        BufferedReader reader = null;
        int[] produitsGeneraux = new int[Parametres.nbReferences];
        
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
            System.err.println(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        
        return produitsGeneraux;
    }
    
    // A la fin de la mise en mémoire on vide les derniers buffers pour avoir les
    // fichiers de ventes complets.
    public static void viderBuffers(Map<UUID, BufferMagasin> tableBuffer){
        
        for(UUID magasinCourant: tableBuffer.keySet()){
            
            Map<Integer, Integer> ventesMagasin = tableBuffer.get(magasinCourant).majMagasin();
            
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
    
    
    public static int insererProduitTopVente(UUID magasin, int reference, int quantite, int emplacement, ProduitTopVente[] top){
        
        top[emplacement] = new ProduitTopVente(magasin, reference, quantite);
        
        int refMin = 0;
        int min = top[0].getQuantite();
        
        for(int i = 1; i < Parametres.nombreTop; i++){
            if(top[i] == null){
                min = 0;
                refMin = i;
            }else if(top[i].getQuantite() < min){
                min = top[i].getQuantite();
                refMin = i;
            }
            
        }
        
        return refMin;
    }
    
    // Construit le top ca à partir des fichiers temporaires de ventes
    public static void constructionTopGlobalCa(String dateFormatee, Map<UUID, BufferMagasin> tableBuffer, ProduitTopCa[] topCa){
        
        float min = 0;
        int refMin = 0;
        
        for(UUID magasinCourant: tableBuffer.keySet()){
            
            Map<Integer, Integer> ventesMagasinCourant = EntreesSortie.obtenirMagasin(magasinCourant);
            float[] tableReference = EntreesSortie.miseEnMemoireReference(magasinCourant, dateFormatee);
            
            for(Entry<Integer, Integer> produitCourant: ventesMagasinCourant.entrySet()){
                
                int referenceCourante = produitCourant.getKey();
                int quantiteCourante = produitCourant.getValue();
                        
                if(referenceCourante != 0){
                    float ca = quantiteCourante *  tableReference[referenceCourante - 1];
                    
                    if(ca > min){

                        refMin = insererProduitTopCa(magasinCourant, referenceCourante, ca, refMin, topCa);
                        
                        if(topCa[refMin] != null){
                            min = topCa[refMin].getCa();
                        }else{
                            min = 0;
                        }
                    }
                }
            }
        }
    }
    
    // Construit le top ventes à partir des fichiers temporaires de ventes
    public static void constructionTopGlobalVente(Map<UUID, BufferMagasin> tableBuffer, ProduitTopVente[] topVente){
        
        int min = 0;
        int refMin = 0;
                
        for(UUID magasinCourant: tableBuffer.keySet()){
            
            Map<Integer, Integer> ventesMagasinCourant = EntreesSortie.obtenirMagasin(magasinCourant);
            
            for(Entry<Integer, Integer> produitCourant: ventesMagasinCourant.entrySet()){
                
                int referenceCourante = produitCourant.getKey();
                int quantiteCourante = produitCourant.getValue();
                
                if(quantiteCourante > min){
                    
                    refMin = insererProduitTopVente(magasinCourant, referenceCourante, quantiteCourante, refMin, topVente);
                    if(topVente[refMin] != null){
                        min = topVente[refMin].getQuantite();
                    }else{
                        min = 0;
                    }
                }
            }
        }
    }
    
    public static void constructionTopMagasinVente(UUID magasin, int[] produitsGeneraux, ProduitTopVente[] topVente){
        
        int min = 0;
        int refMin = 0;
        
        for(int i = 0; i < Parametres.nbReferences; i++){
            
            if(produitsGeneraux[i] > min){

                refMin = insererProduitTopVente(magasin, i + 1, produitsGeneraux[i], refMin, topVente);

                if(topVente[refMin] != null){
                    min = topVente[refMin].getQuantite();
                }else{
                    min = 0;
                }
            }
        }
    }
    
    public static void constructionTopMagasinCa(String dateFormatee, UUID magasin, int[] produitsGeneraux, ProduitTopCa[] topCa){
        
        float min = 0;
        int refMin = 0;
        
        float[] tableReference = EntreesSortie.miseEnMemoireReference(magasin, dateFormatee);
            
        for(int i = 0; i < Parametres.nbReferences; i++){
            if(produitsGeneraux[i] != 0){
                float ca = produitsGeneraux[i] *  tableReference[i];

                if(ca > min){

                    refMin = insererProduitTopCa(magasin, i + 1, ca, refMin, topCa);

                    if(topCa[refMin] != null){
                        min = topCa[refMin].getCa();
                    }else{
                        min = 0;
                    }
                }
            }
        }
    }

    /*
     * Arguments:
     *      1er) "V" pour ventes "C" pour ca
     *      2ème) "M" pour un magasin "G" pour global
     *      3ème) Si magasin alors l'UUID du magasin
     */
    
    public static void main(String[] args) {
        
        // Récupération des arguments et paramétrage
        boolean vente = args[0].equals("V");
        boolean global = args[1].equals("G");
        UUID magasinCible = null;
        
        if(!vente && !args[0].equals("C")){
            System.err.println("Erreur du 1er argument: V pour vente C pour ca");
            System.exit(-1);
        }
        if(!global && !args[1].equals("M")){
            System.err.println("Erreur du 2eme argument: G pour global C pour un magasin");
            System.exit(-1);
        }
        if(global && args.length != 2){
            System.err.println("Erreur: seulement 2 argument en global");
            System.exit(-1);
        }
        
        if(!global){
            if(args.length != 3){
                System.err.println("Erreur: 3 arguments pour un magasin");
                System.exit(-1);
            }
            try{
                magasinCible = UUID.fromString(args[2]);
            }
            catch(Exception e){
                System.err.println("Le 3ème argument n'est pas un UUID");
                System.exit(-1);
            }
        }
        
              
        // Création de la date au format yyyyMMdd
        
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        df.setTimeZone(tz);
        String dateFormatee = df.format(new Date());
        
        // Création de la table des magasins
        Map<UUID, BufferMagasin> tableBuffer = null;
        if(global){
            tableBuffer = new HashMap();
        }
        
        // Création de la table des ventes du magasin
        int[] tableVentes = null;
        
        // Création des top
        ProduitTopCa[] topCa = null;
        ProduitTopVente[] topVente = null;
        
        if(vente){
            topVente = new ProduitTopVente[Parametres.nombreTop];
        }else{
            topCa = new ProduitTopCa[Parametres.nombreTop];
        }
        
        // Mise en mémoire de toutes les transactions
        if(global){
            miseEnMemoireTransactionsBuffer(dateFormatee, tableBuffer);
            viderBuffers(tableBuffer);
        }else{
            tableVentes = miseEnMemoireTransactionsMagasin(dateFormatee, magasinCible);
        }
        
        
        // Construction du top
        if(global){
            if(vente){
                constructionTopGlobalVente(tableBuffer, topVente);
            }else{
                constructionTopGlobalCa(dateFormatee, tableBuffer, topCa);
            }
        }else{
            if(vente){
                constructionTopMagasinVente(magasinCible, tableVentes, topVente);
            }else{
                constructionTopMagasinCa(dateFormatee, magasinCible, tableVentes, topCa);
            }
        }
        
        // Ecriture du Top
        if(vente){
            if(global){
                EntreesSortie.ecritureTopVente(dateFormatee, topVente, null);
            }else{
                EntreesSortie.ecritureTopVente(dateFormatee, topVente, magasinCible);
            }
        }else{
            if(global){
                EntreesSortie.ecritureTopCa(dateFormatee, topCa, null);
            }
            else{
                EntreesSortie.ecritureTopCa(dateFormatee, topCa, magasinCible);
            }
        }
        
    }
}
