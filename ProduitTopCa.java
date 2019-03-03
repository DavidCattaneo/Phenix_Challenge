package Phenix_Challenge_Optimisation;

import java.util.UUID;

/**
 *
 * @author David Cattanéo
 * 
 * Cette classe sert à encapsuler les produits dans les top des ca.
 * 
 */
public class ProduitTopCa{
    
    private UUID magasin;
        
    private int reference;

    private float ca;

    public UUID getMagasin() {
        return magasin;
    }

    public float getCa() {
        return ca;
    }

    public int getReference() {
        return reference;
    }

    public ProduitTopCa(UUID magasin, int reference, float ca) {
        this.magasin = magasin;
        this.reference = reference;
        this.ca = ca;
    }
    
}
