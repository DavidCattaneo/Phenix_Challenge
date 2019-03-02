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
    
    private String date;

    public BufferMagasin(UUID magasin, String date){
        this.magasin = magasin;
        this.nbEntres = 0;
        this.buffer = new HashMap<Integer, Integer>();
        this.date = date;
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
    public void majMagasin(){
 
        // On récupère la table des ventes à partir du fichier temporaire
        int[] ventesMagasin = EntreesSortie.obtenirMagasin(magasin, date);
        
        // Cas du premier appel: le fichier temporaire n'existe pas
        if(ventesMagasin == null){
            
            // On crée la table
            ventesMagasin = new int[Parametres.nbReferences];
            
            // On la remplie avec le buffer
            for(Entry<Integer, Integer> produitCourant: buffer.entrySet()){
                int referenceCourante = produitCourant.getKey();
                int quantiteCourante = produitCourant.getValue();
                ventesMagasin[referenceCourante -1] = quantiteCourante;
            }
            
        // Sinon on ajoute chaque entrée du buffer dans la table
        }else{
            for(Entry<Integer, Integer> produitCourant: buffer.entrySet()){
                int referenceCourante = produitCourant.getKey();
                int quantiteCourante = produitCourant.getValue();
                ventesMagasin[referenceCourante - 1] += quantiteCourante;
            }
        }
        
        // On écrit la table dans le fichier temporaire
        EntreesSortie.ecrireMagasin(ventesMagasin, magasin, date);
        
        // On réinitialise le buffer
        buffer = new HashMap<Integer, Integer>();
        this.nbEntres = 0;
        
    }

    // Ajoute une entrée référence-quantité à un magasin
    public void ajoutEntree(int ref, int qte){

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

            this.majMagasin();

            this.ajoutEntree(ref, qte);

        }
    }
}
