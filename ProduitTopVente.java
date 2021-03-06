package Phenix_Challenge_Optimisation;

import java.util.UUID;

/**
 *
 * @author David Cattanéo
 * 
 * Cette classe sert à encapsuler les produits dans les top des ventes.
 * 
 */

public class ProduitTopVente{
        
        private UUID magasin;
        
        private int reference;
        
        private int quantite;

        public UUID getMagasin() {
            return magasin;
        }

        public int getQuantite() {
            return quantite;
        }

        public int getReference() {
            return reference;
        }

        public ProduitTopVente(UUID magasin, int reference, int quantite) {
            this.magasin = magasin;
            this.reference = reference;
            this.quantite = quantite;
        }
    }