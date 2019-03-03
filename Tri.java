/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phenix_challenge_cattaneo_v3;

/**
 *
 * @author Moloch
 */
public class Tri {
    
    public static void triFusionVente(ProduitTopVente[] tableau){
        
        int longueur = tableau.length;
        
        if(longueur > 0){
            
            triFusionVente(tableau, 0, longueur-1);
            
        }
    }

    private static void triFusionVente(ProduitTopVente[] tableau, int debut,int fin){
        
        if(debut != fin){
            
            // On sépare le tableau en 2
            int milieu = (fin + debut) / 2;
            
            // On trie chaque partie
            triFusionVente(tableau, debut, milieu);
            triFusionVente(tableau, milieu+1, fin);
            
            // On fusionne les 2 parties
            fusionVente(tableau, debut, milieu, fin);
        }
    }

    private static void fusionVente(ProduitTopVente[] tableau, int debut, int milieu, int fin){
        
        int debutFin = milieu + 1;

        //on copie les éléments du début du tableau
        ProduitTopVente[] tableDebut = new ProduitTopVente[milieu - debut + 1];
        for(int i = debut; i <= milieu; i++){
            
            tableDebut[i - debut] = tableau[i];
        }
        
        int comptDebut = debut;
        int comptFin = debutFin;
        
        for(int i = debut; i <= fin; i++){      
            
            // Si on a inséré tous les éléments du débuts on arrète (ceux de la fin sont déjà ordonnés)
            if(comptDebut == debutFin){
                
                break;
            
            // Sinon si on a inséré tous les éléments de la fin on insère ceux du début
            }else if(comptFin == (fin + 1)){
                
                tableau[i] = tableDebut[comptDebut - debut];
                comptDebut++;
            
            // Sinon on regarde si on insère un élément du début ou de la fin
            }else if(tableDebut[comptDebut - debut].getQuantite() < tableau[comptFin].getQuantite()){
                
                tableau[i] = tableDebut[comptDebut - debut];
                comptDebut++;
                
            }else{
                
                tableau[i] = tableau[comptFin];
                comptFin++;
            }
        }
    }
    
    public static void triFusionCa(ProduitTopCa[] tableau){
        
        int longueur = tableau.length;
        
        if(longueur > 0){
            
            triFusionCa(tableau, 0, longueur-1);
            
        }
    }

    private static void triFusionCa(ProduitTopCa[] tableau, int debut,int fin){
        
        if(debut != fin){
            
            // On sépare le tableau en 2
            int milieu = (fin + debut) / 2;
            
            // On trie chaque partie
            triFusionCa(tableau, debut, milieu);
            triFusionCa(tableau, milieu+1, fin);
            
            // On fusionne les 2 parties
            fusionCa(tableau, debut, milieu, fin);
        }
    }

    private static void fusionCa(ProduitTopCa[] tableau, int debut, int milieu, int fin){
        
        int debutFin = milieu + 1;

        //on copie les éléments du début du tableau
        ProduitTopCa[] tableDebut = new ProduitTopCa[milieu - debut + 1];
        for(int i = debut; i <= milieu; i++){
            
            tableDebut[i - debut] = tableau[i];
        }
        
        int comptDebut = debut;
        int comptFin = debutFin;
        
        for(int i = debut; i <= fin; i++){      
            
            // Si on a inséré tous les éléments du débuts on arrète (ceux de la fin sont déjà ordonnés)
            if(comptDebut == debutFin){
                
                break;
            
            // Sinon si on a inséré tous les éléments de la fin on insère ceux du début
            }else if(comptFin == (fin + 1)){
                
                tableau[i] = tableDebut[comptDebut - debut];
                comptDebut++;
            
            // Sinon on regarde si on insère un élément du début ou de la fin
            }else if(tableDebut[comptDebut - debut].getCa() < tableau[comptFin].getCa()){
                
                tableau[i] = tableDebut[comptDebut - debut];
                comptDebut++;
                
            }else{
                
                tableau[i] = tableau[comptFin];
                comptFin++;
            }
        }
    }
}
