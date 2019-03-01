package phenix_challenge_cattaneo_v2;

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

    private int[][] buffer;

    private int nbEntres;

    public BufferMagasin(UUID magasin){
        this.magasin = magasin;
        this.nbEntres = 0;
        this.buffer = new int[Parametres.tailleBuffer][2];
    }

    public int[][] getBuffer() {
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
    public int[][] majMagasin(){

        int[][] ventesMagasin = EntreesSortie.obtenirMagasin(this.magasin);

            if(ventesMagasin != null){

                for(int i = 0; i < Parametres.tailleBuffer && this.buffer[i][0] != 0; i++){

                    int produitCourant = this.buffer[i][0];

                    int j = 0;
                    while (j < Parametres.nbReferences && ventesMagasin[j][0] != 0 && produitCourant != ventesMagasin[j][0]) j++;

                    // Cas erreur
                    if(j == Parametres.nbReferences){
                        System.err.println("Erreur: le produit courant:" + produitCourant + " n'est pas référencé");
                        System.exit(-1);
                    }
                    // Cas où la référence nexiste pas encore
                    else if(ventesMagasin[j][0] == 0){
                        ventesMagasin[j][0] = produitCourant;
                        ventesMagasin[j][1] = this.buffer[i][1];
                    }
                    // Cas où le produit existe déja
                    else if(produitCourant == ventesMagasin[j][0]){
                        ventesMagasin[j][1] += this.buffer[i][1];
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

            int i = 0;
            while(i < this.nbEntres && (this.buffer[i][0] != ref)) i++;

            if(this.buffer[i][0] == ref){
                this.buffer[i][1] += qte;
            }
            else{
                this.buffer[i][0] = ref;
                this.buffer[i][1] = qte;
                this.nbEntres ++;
            }

        }else{

            int[][] ventesMagasin = this.majMagasin();

            EntreesSortie.ecrireMagasin(ventesMagasin, magasin);

            this.buffer = new int[Parametres.tailleBuffer][2];
            this.nbEntres = 0;

            this.ajoutEntree(ref, qte);

        }
    }
}
