package phenix_challenge_cattaneo_v2;

/**
 *
 * @author Moloch
 * 
 * 1) Cette classe sert à gérer les paramètres globaux de l'application comme le
 * nomnre de références par magasin etc...
 * 
 * 2) Cette classe ne contiendra donc que des attributs static
 * 
 */
public class Parametres {
    
    /*
     * La taille max des buffers
     * MAXIMUM 600: taille d'un Buffer = 256 x nbEntrées
     * Or avec 1500 magasins et 512 mo si on utilise la moitié de la mémoire à
     * une taille de 666. Pour laisser un espace de travaille on prends 600 
     * (pour la table de référence par ex.).
     */
    public static int tailleBuffer = 600;
    
    /*
     * La taille max des tables
     * MAXIMUM: 500 000: taille d'une table = 256 x nbEntrées
     * Or avec 512 mo on utilise la moitié de la mémoire à 1 000 000. 
     * Pour laissé un espace de travail on prends 500 000 (pour la table de référence par ex.).
     */
    public static int tailleTable = 500000;
    
    // Le nombre de références par magasin
    public static int nbReferences = 15000000;
    
    // Le nombre de produits dans le top
    public static int nombreTop = 5;
    
    // Le nombre de transactions
    public static int nbTransactions = 15000000;
    
    // Le prix maximum d'un produit
    public static int prixMax = 100;
    
    // La quantité maximum sur une vente
    public static int quantiteMax = 10;
    
    // Le nombre de magasins
    public static int nbMagasin = 5;
    
}
