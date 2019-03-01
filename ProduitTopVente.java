package phenix_challenge_cattaneo_v2;

import java.util.UUID;

/**
 *
 * @author Moloch
 * 
 * Cette classe sert à encapsuler les produits dans les top des ventes.
 * 
 */

class ProduitTopVente {
        
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