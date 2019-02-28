
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;


/**
 *
 * @author Moloch
 * 
 * Uniquement pour l'exercice 4
 * 
 * 1) Ici on va appliquer le même algorithme que pour l'exercice 2
 *    pour trouver le top ca on va simplement charger le référenciel de chaque magasin 
 *    lors de la lecture des fichiers de ventes.
 */
public class Exercice4 {

    
    public static int tailleBuffer = 1000;
    
    public static int nbReferences = 1000000;
    
    public static Map<UUID, BufferMagasin> tableBuffer;
    
    public static int nombreTop = 100;
    
    public static produitTop[] top;
    
    // Objet qui encapsule une référence vers un produit du top
    public static class produitTop {
        
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

        public produitTop(UUID magasin, int reference, float ca) {
            this.magasin = magasin;
            this.reference = reference;
            this.ca = ca;
        }
    }
    

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
        
        public int[][] majMagasin(){
            
            int[][] ventesMagasin = obtenirMagasin(this.magasin);
            
                /*System.out.println("Magasin obtenu:");
                if(ventesMagasin != null){
                    for(int i = 0; i < nbReferences; i++){
                        System.out.println(ventesMagasin[i][0] +" | " + ventesMagasin[i][1]);
                    }
                }else{
                    System.out.println("Magasin vide");
                }*/
                
                if(ventesMagasin != null){
                
                    for(int i = 0; i < tailleBuffer && this.buffer[i][0] != 0; i++){
                        
                        int produitCourant = this.buffer[i][0];
                        
                        int j = 0;
                        while (j < nbReferences && ventesMagasin[j][0] != 0 && produitCourant != ventesMagasin[j][0]) j++;
                        
                        // Cas erreur
                        if(j == nbReferences){
                            // System.out.println("i = " + i);
                            // System.out.println("Erreur: le produit courant:" + produitCourant + " n'est pas référencé");
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
        
        public void ajoutEntree(int ref, int qte){
            
            if(this.nbEntres < tailleBuffer){
                
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
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(reader != null)
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
        
        //System.out.println(" Création: " +nom );

        try {

                fw = new FileWriter(nom);
                bw = new BufferedWriter(fw);

                for(int i = 0; i < ventesMagasin.length; i++)
                {
                    // System.out.println(ventesMagasin[i][0] + " | " + ventesMagasin[i][1]);
                    bw.write(ventesMagasin[i][0] + "|" + ventesMagasin[i][1] + System.getProperty("line.separator"));
                }


                //System.out.println(nom + " Créé");

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

                //System.out.println("ajoutEntree: " + produitCourant + " " + quantiteCourante + " Magasin: " + bufferMag.magasin);
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
    
    public static void viderBuffers(){
        
        for(UUID magasinCourant: tableBuffer.keySet()){
            
            int[][] ventesMagasin = tableBuffer.get(magasinCourant).majMagasin();
            
            ecrireMagasin(ventesMagasin, magasinCourant);

        }
    }
    
    public static int insererProduitTop(UUID magasin, int reference, float ca, int emplacement){
        
        top[emplacement] = new produitTop(magasin, reference, ca);
        
        int refMin = 0;
        float min = top[0].getCa();
        
        for(int i = 1; i < nombreTop; i++){
            if(top[i] == null){
                min = 0;
                refMin = i;
            }else if(top[i].getCa() < min){
                min = top[i].getCa();
                refMin = i;
            }
            
        }
        
        return refMin;
    }
    
    public static void ecritureTop(String date){
        
        //System.out.println("Ecriture Top");
        
        BufferedWriter bw = null;
        FileWriter fw = null;
        String nom = "top_100_ventes_" + date + ".data";

        try {

                fw = new FileWriter(nom);
                bw = new BufferedWriter(fw);

                for(int i = 0; i < nombreTop; i++)
                {
                    //System.out.println("i = " + i);
                    //System.out.println(top[i].getMagasin() + "|" + top[i].getReference() + "|" + top[i].getQuantite());
                    bw.write(top[i].getMagasin() + "|" + top[i].getReference() + "|" + top[i].getCa() + System.getProperty("line.separator"));
                }


                //System.out.println(nom + "Créé");

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
    
    public static float[] miseEnMemoireReference(UUID magasin, String date) {
        
        BufferedReader reader = null;
        float[] tableReference = new float[nbReferences];
        
        try {
            
            File file = new File("reference_prod-" + magasin.toString() + "_" + date + ".data");
            reader = new BufferedReader(new FileReader(file));

            String ligne;
            int produitCourant = 0;
            while ((ligne = reader.readLine()) != null) {
                
                String[] ligneEclate = ligne.split("\\|");

                tableReference[produitCourant] = Float.parseFloat(ligneEclate[1]);
                produitCourant++;
                
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
        
        return tableReference;
        
    }
    
    public static void main(String[] args) {
        
        tableBuffer = new HashMap();
        top = new produitTop[nombreTop];
        
        // Création de la date au format yyyyMMdd
        
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        df.setTimeZone(tz);
        String dateFormatee = df.format(new Date());
        
        // Mise en mémoire de toutes les transactions
        miseEnMemoireTransactions(dateFormatee);
        viderBuffers();
        
        
        // Construction du top
        float min = 0;
        int refMin = 0;
        
        //System.out.println("Nombre magasins: " + tableBuffer.size());
        
        for(UUID magasinCourant: tableBuffer.keySet()){
            
            int[][] ventesMagasinCourant = obtenirMagasin(magasinCourant);
            float[] tableReference = miseEnMemoireReference(magasinCourant, dateFormatee);
            
            for(int i = 0; i < nbReferences; i++){
                float ca = ventesMagasinCourant[i][1] *  tableReference[ventesMagasinCourant[i][0] - 1];
                if(ca > min){
                    //System.out.println("ajout à l'emplacement: " + refMin);
                    //System.out.println(magasinCourant + " | " + ventesMagasinCourant[i][0] + " | " + ventesMagasinCourant[i][1]);
                    
                    refMin = insererProduitTop(magasinCourant, ventesMagasinCourant[i][0], ca, refMin);
                    if(top[refMin] != null){
                        min = top[refMin].getCa();
                    }else{
                        min = 0;
                    }
                    
                    //System.out.println("Nouveau min au: " + refMin + "quantite: " + min);
                }
            }
        }
        
        // ecriture du Top
        
        ecritureTop(dateFormatee);
               
    }
}
