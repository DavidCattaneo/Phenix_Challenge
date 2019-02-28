
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;


/**
 *
 * @author Moloch
 * 
 * Uniquement pour l'exercice 2
 * 
 * 1) Ici on ne peut pas charger la quantite de chaque produit vendu dans chaque magasin
 * car on a plusieurs millions par magasin pour mille magasins soit plusieurs milliards
 * soit plus que la taille disponible.
 * 
 * 2) La solution consitant a répéter l'exercice 1 pour chaqun des magasins sera inefficace
 * car 1000 fois plus lente et lira chaque reference inutilement un grand nombre de fois
 * 
 * 3) La solution consistant à écrire dans un fichier chaque entrée pour chaque magasins
 * demanderait bien trop d'accès aux fichier
 * 
 * 4) La solution retenue consiste à allouer à chaque magasin un buffer d'une taille maximale
 * qui sera vidé une fois celle-ci atteinte limitant ainsi les accès aux fichier
 */
public class Exercice2 {

    
    public static int tailleBuffer = 10;
    
    public static int nbReferences = 100;
    
    public static Map<UUID, BufferMagasin> tableBuffer;

    // La classe BufferMagasin sert a stocker de manière temporaire les transactions
    // vu pour un magasin
    public static class BufferMagasin {
    
        private UUID magasin;
        
        private int[][] buffer;
        
        private int nbEntres;
        
        public BufferMagasin(UUID magasin){
            this.magasin = magasin;
            this.nbEntres = 0;
            this.buffer = new int[tailleBuffer][2];
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
        
        public void ajoutEntree(int ref, int qte){
            
            if(this.nbEntres < tailleBuffer){
                
                int i = 0;
                while(i < this.nbEntres && (this.buffer[i][0] != ref)) i++;
                
                if(this.buffer[i][0] == ref){
                    this.buffer[i][1] += qte;
                }
                else{
                    this.buffer[i][1] = qte;
                    this.nbEntres ++;
                }
                
            }else{
                
                int[][] ventesMagasin = obtenirMagasin(this.magasin);
                
                if(ventesMagasin != null){
                
                    for(int i = 0; i < tailleBuffer; i++){
                        
                        int produitCourant = this.buffer[i][0];
                        
                        int j = 0;
                        while (j < nbReferences && ventesMagasin[j][0] != 0 && produitCourant != ventesMagasin[j][0]) j++;
                        
                        // Cas erreur
                        if(j == nbReferences){
                            System.out.println("Erreur: le produit courant n'est pas référencé");
                            System.exit(-1);
                        }
                        // Cas où la référence nexiste pas encore
                        else if(ventesMagasin[j][0] == 0){
                            ventesMagasin[j][0] = produitCourant;
                            ventesMagasin[j][1] = this.buffer[i][1];
                        }
                        // Cas où le produit n'existe pas
                        else if(produitCourant == ventesMagasin[j][0]){
                            ventesMagasin[j][1] += this.buffer[i][1];
                        }
                    }
                }else{
                    ventesMagasin = buffer;
                }
                
                ecrireMagasin(ventesMagasin, magasin);
                
                this.buffer = new int[tailleBuffer][2];
                this.nbEntres = 0;
                
                this.ajoutEntree(ref, qte);
                
            }
        }
    }
    
    // Lit le fichier temporraire d'un magasin si il existe puis le supprime
    //  et renvoie les ventes et renvoie null sinon
    
    public static int[][] obtenirMagasin(UUID magasin){
        
        BufferedReader reader = null;
        int[][] tableauMagasin = null;
        
        try {
            
            File file = new File("ventes-" + magasin.toString() + ".tmp");
            
            if(file.isFile()){
                tableauMagasin = new int[nbReferences][2];
                reader = new BufferedReader(new FileReader(file));

                String ligne;
                int produitCourant = 0;
                while ((ligne = reader.readLine()) != null) {

                    String[] ligneEclate = ligne.split("\\|");

                    tableauMagasin[produitCourant][0] = Integer.parseInt(ligneEclate[0]);
                    tableauMagasin[produitCourant][1] = Integer.parseInt(ligneEclate[1]);
                    produitCourant++;

                }
                file.delete();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return tableauMagasin;
    }
    
    // Ecrit le fichier temporaire du magasin
    public static void ecrireMagasin(int[][] ventesMagasin, UUID magasin){
        
        BufferedWriter bw = null;
        FileWriter fw = null;
        String nom = "ventes-" + magasin.toString() + ".tmp";

        try {

                fw = new FileWriter(nom);
                bw = new BufferedWriter(fw);

                for(int i = 0; i < ventesMagasin.length; i++)
                {
                    bw.write(ventesMagasin[i][0] + "|" + ventesMagasin[i][1] + System.getProperty("line.separator"));
                }


                System.out.println(nom + " Créé");

        } catch (IOException e) {

                e.printStackTrace();

        } finally {

                try {

                        if (bw != null)
                                bw.close();

                        if (fw != null)
                                fw.close();

                } catch (IOException ex) {

                        ex.printStackTrace();

                }
        }
    }
            
    public static void miseEnMemoireTransactions(String date){

        BufferedReader reader = null;

        try {

            File file = new File("transactions_" + date + ".data");
            reader = new BufferedReader(new FileReader(file));

            String ligne;
            while ((ligne = reader.readLine()) != null) {

                String[] ligneEclate = ligne.split("\\|");

                UUID magasinCourant = UUID.fromString(ligneEclate[2]);
                int produitCourant = Integer.parseInt(ligneEclate[3]);
                int quantiteCourante = Integer.parseInt(ligneEclate[4]);
                BufferMagasin bufferMag;

                if(tableBuffer.containsKey(magasinCourant)){

                    bufferMag = tableBuffer.get(magasinCourant);

                }else{

                    bufferMag = new BufferMagasin(magasinCourant);
                    tableBuffer.put(magasinCourant, bufferMag);

                }

                bufferMag.ajoutEntree(produitCourant, quantiteCourante);

            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    public static void main(String[] args) {
        // Création de la date au format yyyyMMdd
        
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        df.setTimeZone(tz);
        String dateFormatee = df.format(new Date());
        
        miseEnMemoireTransactions(dateFormatee);
        
    }
}
