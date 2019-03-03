package phenix_challenge_cattaneo_v3;

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
    public static int tailleBuffer = 10000;
    
    // Le nombre de références par magasin
    public static int nbReferences = 1000;
    
    // Le nombre de produits dans le top
    public static int nombreTop = 5;
    
    // Le nombre de transactions
    public static int nbTransactions = 1000000;
    
    // Le prix maximum d'un produit
    public static int prixMax = 100;
    
    // La quantité maximum sur une vente
    public static int quantiteMax = 10;
    
    // Le nombre de magasins;
    public static int nbMagasin = 10;
    
    // Le nombre de jours que l'on veut étudier
    public static int joursSemaine = 7;
}
