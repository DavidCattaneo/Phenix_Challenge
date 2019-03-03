package phenix_challenge_cattaneo_v3;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 *
 * @author Moloch
 * 
 * 1) La classe BufferMagasin sert à stocker de manière temporaire une partie des 
 * références et quantités dans le cadre des ventes et ca globaux car on ne peut pas pour 
 * chaque magasin avoir les ventes pour chaque référence.
 * 
 * 2) On va donc créer une instance de buffer pour chaque magasin identifié par
 * son UUID. Le nombre d'entrée correspond a un instant aux nombre de références
 * contenues dans le buffer.
 * 
 * 3) Lors de l'ajout d'un élément d'une vente on va donc commencer par vérifier
 * si la référence existe déjà auquel cas on ajoute la quantité et si on crée une
 * nouvelle référence et que le buffer et rempli on devra "le vider" c.à.d. le mettre
 * dans un fichier temporaire.
 * 
 */
public class BufferMagasin {
    
    private UUID magasin;

    private Map<Integer, Integer> buffer;

    private int nbEntres;
    
    private boolean vente;
    
    public BufferMagasin(UUID magasin, String date, boolean vente){
        this.magasin = magasin;
        this.nbEntres = 0;
        this.buffer = new HashMap<Integer, Integer>();
        this.vente = vente;
    }

    public boolean getVente() {
        return vente;
    }

    public Map<Integer, Integer> getBuffer() {
        return buffer;
    }

    public UUID getMagasin() {
        return magasin;
    }

    public int getNbEntres() {
        return nbEntres;
    }

    /* Cas où l'on vide un buffer:
     * 1) On récupère le fichier temporaire si il existe
     * 2) On fusionne les ventes récupérées avec les nouvelles
     */
    public void majMagasin(String dateActuelle, String dateSuivante){
 
        int[] ventesMagasin;
        
        // On récupère la table des ventes à partir du fichier temporaire
         ventesMagasin = EntreesSortie.obtenirMagasin(magasin, dateActuelle);
        
        // Cas du premier appel: le fichier temporaire n'existe pas
        if(ventesMagasin == null){
            
            // Dans le cas des ventes
            // On récupère le fichier du jour suivant si il y en a un           
            if(this.vente && dateSuivante != null){
                ventesMagasin = EntreesSortie.obtenirMagasin(magasin, dateSuivante);
            }
            
            // Si on en a pas on crée un nouveau tableau
            if(ventesMagasin == null){
                ventesMagasin = new int[Parametres.nbReferences];
            }
            
        }
        
        // Pour chaque entrée du buffer on ajoute sa quantité (valide en cas de
        // du tableau car les entrée sont à 0 à la création en Java
        for(Entry<Integer, Integer> produitCourant: buffer.entrySet()){
            
                int referenceCourante = produitCourant.getKey();
                int quantiteCourante = produitCourant.getValue();
                ventesMagasin[referenceCourante - 1] += quantiteCourante;
        }
        
        // On écrit la table dans le fichier temporaire
        EntreesSortie.ecrireMagasin(ventesMagasin, magasin, dateActuelle);
        
        // On réinitialise le buffer
        buffer = new HashMap<Integer, Integer>();
        this.nbEntres = 0;
        
    }

    // Ajoute une entrée référence-quantité à un magasin
    public void ajoutEntree(int ref, int qte, String dateActuelle, String dateSuivante){

        if(this.nbEntres < Parametres.tailleBuffer){
            
            // Si la référence existe déjà on ajoute la quantité
            if(buffer.containsKey(ref)){
                
                int quantiteCourante = buffer.get(ref) + qte;
                buffer.remove(ref);
                buffer.put(ref, quantiteCourante);
                
            // Sinon on ajoute la référence
            }else{
                buffer.put(ref, qte);
                this.nbEntres ++;
            }

        }else{

            this.majMagasin(dateActuelle, dateSuivante);

            this.ajoutEntree(ref, qte, dateActuelle, dateSuivante);

        }
    }
}
