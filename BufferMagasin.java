package phenix_challenge_cattaneo_v2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

    public BufferMagasin(UUID magasin){
        this.magasin = magasin;
        this.nbEntres = 0;
        this.buffer = new HashMap<Integer, Integer>();
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
 
        Map<Integer, Integer> ventesMagasin = null;
        
        int debut = 0;
        boolean premier = true;
        
        // On crée un ensemble des références déjà écrites
        Set<Integer> estEcrit = new HashSet<Integer>();
        
        // Tant que on a pas fini de lire le fichier et que tous les éléments du 
        // buffer ne sont pas écrits on continue
        while(debut != -1 && estEcrit.size() < nbEntres){
            
            // On lit le fichier à partir de début
            Magasin mag = EntreesSortie.obtenirMagasin(this.magasin, debut);
            debut = mag.fin;
            ventesMagasin = mag.tableauMagasin;
            
            if(ventesMagasin != null){
                
                // Pour chaque produit du buffer non déjà traité on vérifie s'il 
                // est dans cette partie du fichier
                for(Entry<Integer, Integer> produitCourant: buffer.entrySet()){
                    
                    int referenceCourante = produitCourant.getKey();
                    
                    // Si le produit n'est pas déjà écrit
                    if(!estEcrit.contains(referenceCourante)){

                        // Si la référence existe déjà on ajoute la quantité
                        if(ventesMagasin.containsKey(referenceCourante)){

                            int quantiteCourante = ventesMagasin.get(referenceCourante) + produitCourant.getValue();
                            ventesMagasin.remove(referenceCourante);
                            ventesMagasin.put(referenceCourante, quantiteCourante);
                            estEcrit.add(referenceCourante);
                            
                        // Sinon si on est à la fin du fichier on crée la référence
                        }else if(debut == -1){
                            ventesMagasin.put(referenceCourante, produitCourant.getValue());
                            estEcrit.add(referenceCourante);
                        }
                    }
                }
            // On écrit la partie du fichier mis à jour
            EntreesSortie.ecrireMagasin(ventesMagasin, magasin, !premier);
            premier = false;
                
            // Si le fichier et vide on écrit le buffer
            }else{
                EntreesSortie.ecrireMagasin(buffer, magasin, false);
            }
        }
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

            buffer = new HashMap<Integer, Integer>();
            this.nbEntres = 0;

            this.ajoutEntree(ref, qte);

        }
    }
}
