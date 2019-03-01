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
    
    // La taille des buffers (à varier pour trouver l'optimum de performence)
    public static int tailleBuffer = 10;
    
    // Le nombre de références par magasin
    public static int nbReferences = 100;
    
    // Le nombre de produits dans le top
    public static int nombreTop = 5;
    
}
