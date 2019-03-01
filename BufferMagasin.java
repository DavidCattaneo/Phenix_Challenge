package phenix_challenge_cattaneo_v2;

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
    public Map<Integer, Integer> majMagasin(){

        Map<Integer, Integer> ventesMagasin = EntreesSortie.obtenirMagasin(this.magasin);

            if(ventesMagasin != null){

                for(Entry<Integer, Integer> produitCourant: buffer.entrySet()){
                    
                    int referenceCourante = produitCourant.getKey();
                    
                    // Si la référence existe déjà on ajoute la quantité
                    if(ventesMagasin.containsKey(referenceCourante)){
                        
                        int quantiteCourante = ventesMagasin.get(referenceCourante) + produitCourant.getValue();
                        ventesMagasin.remove(referenceCourante);
                        ventesMagasin.put(referenceCourante, quantiteCourante);
                    // Sinon on crée la référence
                    }else{
                        ventesMagasin.put(referenceCourante, produitCourant.getValue());
                    }
                }
            }else{
                ventesMagasin = buffer;
            }

            return ventesMagasin;

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

            Map<Integer, Integer> ventesMagasin = this.majMagasin();

            EntreesSortie.ecrireMagasin(ventesMagasin, magasin);

            buffer = new HashMap<Integer, Integer>();
            this.nbEntres = 0;

            this.ajoutEntree(ref, qte);

        }
    }
}
