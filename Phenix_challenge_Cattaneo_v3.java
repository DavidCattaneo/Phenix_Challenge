/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phenix_challenge_cattaneo_v3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.UUID;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * @author Moloch
 */
public class Phenix_challenge_Cattaneo_v3 {
    
    
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

                    bufferMag = new BufferMagasin(magasinCourant, date);
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
            
            BufferMagasin buffer = tableBuffer.get(magasinCourant);
            
            buffer.majMagasin();

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
    public static void constructionTopGlobalCa(String dateFormatee, Map<UUID, BufferMagasin> tableBuffer, ProduitTopCa[] topCa, String date){
        
        float min = 0;
        int refMin = 0;
        
        // Pour chaque magasin on parcour ses ventes
        for(UUID magasinCourant: tableBuffer.keySet()){
            
            int[] ventesMagasinCourant = EntreesSortie.obtenirMagasin(magasinCourant, date);
            float[] tableReference = EntreesSortie.miseEnMemoireReference(magasinCourant, dateFormatee);

            for(int i = 0; i < Parametres.nbReferences; i++){
                
                int referenceCourante = i + 1;
                float caCourant = ventesMagasinCourant[i] * tableReference[i];
                
                if(caCourant > min){

                    refMin = insererProduitTopCa(magasinCourant, referenceCourante, caCourant, refMin, topCa);
                    if(topCa[refMin] != null){
                        min = topCa[refMin].getCa();
                    }else{
                        min = 0;
                    }
                }
            }
        }
    }
    
    // Construit le top ventes à partir des fichiers temporaires de ventes
    public static void constructionTopGlobalVente(Map<UUID, BufferMagasin> tableBuffer, ProduitTopVente[] topVente, String date){
        
        int min = 0;
        int refMin = 0;
        
        // Pour chaque magasin on parcour ses ventes
        for(UUID magasinCourant: tableBuffer.keySet()){
            
            int[] ventesMagasinCourant = EntreesSortie.obtenirMagasin(magasinCourant, date);
            for(int i = 0; i < Parametres.nbReferences; i++){
                
                int referenceCourante = i + 1;
                int quantiteCourante = ventesMagasinCourant[i];
                
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
    
    // Calcule le jour précédent le jour donné en paramètre
    public static String jourPrecedent(String date){
        
        String jourPrecedent = null;
        
        int annee = Integer.parseInt(date.substring(0, 4));
        int mois = Integer.parseInt(date.substring(4, 6)) - 1;
        int jour = Integer.parseInt(date.substring(6, 8));
        
        TimeZone tz = TimeZone.getTimeZone("UTC");
        Calendar calendrier = Calendar.getInstance(tz);
        calendrier.set(annee, mois, jour);
        
        calendrier.add(Calendar.DATE, -1);
        
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        jourPrecedent = df.format(calendrier.getTime());
        
        return jourPrecedent;
    }
    

    /*
     * Arguments:
     *      -C pour avoir le ca aulieu des ventes
     *      -M <UUID> pour avoir le top du magasin identifié par l'UUID
     *      -D <AAAAMMJJ> pour selectionner une date au format AAAAMMJJ
     */
    
    public static void main(String[] args) {
        
        // Récupération des arguments et paramétrage
        int nombreJours = 1;
        boolean journalier = true;
        boolean vente = true;
        boolean global = true;
        UUID magasinCible = null;
        String dateFormatee = null;

        
        for(int i = 0; i < args.length; i++){
            
            if(args[i].equals("-M")){
                global = false;
                try{
                    magasinCible = UUID.fromString(args[i+1]);
                }
                catch(Exception e){
                    System.out.println("Erreur -M doit être suivi d'un UUID valide");
                    System.err.println(e);
                    System.exit(-1);
                }
            }
            else if(args[i].equals("-D")){
                journalier = false;
                try{
                    dateFormatee = args[i+1];
                }
                catch(Exception e){
                    System.out.println("Erreur -D doit être suivi d'une date au format AAAAMMJJ");
                    System.err.println(e);
                    System.exit(-1);
                }
            }
            else if(args[i].equals("-C")){
                vente = false;
            }
            else if(args[i].equals("-S")){
                nombreJours = 7;
            }
        } 
        
        // Création de la date au format yyyyMMdd
        if(journalier){
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            df.setTimeZone(tz);
            dateFormatee = df.format(new Date());
        }
        
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
                constructionTopGlobalVente(tableBuffer, topVente, dateFormatee);
            }else{
                constructionTopGlobalCa(dateFormatee, tableBuffer, topCa, dateFormatee);
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
        
        // Suppression des fichiers temporaires
        for(UUID magasin: tableBuffer.keySet()){
            EntreesSortie.supprimerFichierTmp(magasin,dateFormatee);
        }
    }
}
