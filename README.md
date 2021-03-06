# Phenix_Challenge
## 1) Fichiers
   
   L'application est consituée de 8 classes Java dont 2 exécutables: GénérationTest qui permet de générer les tests et Phenix_challenge_Cattaneo_v3 qui est la classe exécutable de l'application. J'ai implémenté l'application avec l'IDE netBeans. La branche de base est Optimisation.

## 2) Compilation

   L'application est écrite en JAVA SE 7 est n'utilise que les bibliotèques standards java.util (pour les UUID et les Map et Set) java.io (pour la gestion des entrées/sorties) et java.text (pour la gestion de la date).
   Pour compiler après avoir téléchargé renommer le répertoire en Phenix_Challenge_Optimisation (de base Phenix_Challenge-Optimisation mais le "-" pose problème), se mettre dans le répertoire parent et faire "javac Phenix_Challenge_Optimisation\GenerationTest.java" et "javac Phenix_Challenge_Optimisation\Phenix_challenge_Cattaneo_v3.java".

## 3) Exécution

   Il faut définir les paramètres dans la classe Parametres et lancer GénérationTest qui génèrera les transactions les fichiers de références des magasins créés de façon aléatoire.
   Pour l'exécution avoir le chemin de java dans le path et toujours dans le répertoire parent faire respectivement "java Phenix_Challenge_Optimisation\GenerationTest.class" et "java Phenix_Challenge_Optimisation\Phenix_challenge_Cattaneo_v3.class" avec les bons arguments pour exécuter.
   
## 4) Arguments

   - GénérationTest:
   
      Sans arguments: génère le fichier transaction pour la journée actuelle.
   
      "-C" pour générer les fichiers références.
      
      "-D" <AAAAMMJJ> pour sélectionner une date.
   
      "-S" pour générer toute une semaine (précédent la date actuelle ou celle sélectionnée).
   
   - Phenix_challenge_Cattaneo_v2:
   
      Sans arguments: calcule le top des ventes au global pour la journée actuelle.
      
      "-C" pour calculer les tops ca au lieu des top ventes.
      
      "-D" <AAAAMMJJ> pour sélectionner une date.
   
      "-M" <UUID> pour sélectionner un magasin par son UUID.
   
      "-S" pour calculer toute une semaine (prédcédent la date actuelle ou celle sélectionnée).
   
## 5) Paramètres

   - tailleBuffer: La taille maximale d'un buffer (maximum 600: voir Gestion de la mémoire).

   - nbReferences: Le nombre de références par magasin.
    
   - nombreTop: le nombre de produits dans le top à créer.
    
   - nbTransactions: le nombre de transactions journalières.
    
   - prixMax: le prix maximum d'un produit. 
    
   - quantiteMax: la quantité maximale vendu d'une référence lors d'une unique transaction.
    
   - nbMagasin: le nombre de magasins.
   
   - joursSemaine: le nombre de jours d'une semaine (permet de faire de plus longues périodes).

## 6) Gestion de la mémoire

   La mémoire disponible étant de 512 mo et le nombre de références produits de plusieurs millions par magasin sur pluss de 1000 magasins. la gestion de la mémoire est la partie critique:
   
   -  Pour lire les transactions j'ai utiliser un BufferedReader qui met pas l'inégralité du fichier en mémoire pour pouvoir lire de grands fichiers sans dépasser la mémoire.
   
   -  Pour les top100 ventes et ca pour un seul magasin la mise en mémoire dans un tableaux des ventes est possible (entier 4 x ref 15 000 000 = 60 mo) et donc ne posent pas de difficultés.
   
   -  Pour les top100 vente global et top100 ca global il devient impossible de stocker chaque référence. On peut répéter pour chaque magasin la solution d'un magasin mais le temps d'exécution est alors énorme. J'ai choisit d'opter pour un buffer où l'on va écrire une partie des références et quand celui-ci est plein on l'écrire dans un fichier temporaire et le vider. On peut donc avoir un bon nombre de transactions avant d'effectuer des lectures/écritures couteuses.
   
   - On utilise des HashMap pour le buffer permet de d'avoir un cout unitaire de test d'appartenance d'une référence produit lors de la lecture d'une transaction. La taille maximale est de 666 (600 pour plus de suretée) pour 1500 magasins: taille table = 256o x taille au max or pour utiliser moins de la moitié de la mémoire soit 256mo avec 1500 magasins on a obtient taille max = 256mo/(1500 * 256) = 666. 
   
   - On utilise un simple tableau pour la lecture/écriture des fichiers temporaires et fusion avec le buffer. En effet il n'est pas requis d'effectuer des recherches à cette occasion est en prenant 15 million d'entrées avec 4o pour les références et 8o pour les prix, on a donc 180mo donc correct par rapport à la condition 512mo.
   
   - Le calcul d'une semaine se faisant en calculant les ventes jour par jour pour un/chaque magasin seul le temps d'exécution varie, l'espace mémoire utilisé reste identique.
   
## 7) Tests

   - J'ai effectué les tests fonctionnels sur de petits jeux de données pour toutes les combinaisons de types de top puis vérifié à partir des fichiers de transactions.
   
   - Pour les tests de performence j'ai configurer la JVM pour avoir 512mo de mémoire. Sur les grands tests de le top100ventes d'une journée avec 150 000 000 de transactions 1500 magasins et 15 000 000 de références une taille buffer de 600 lignes. Le temps d'exécution sur ma machine était de 7heures soit moins que la limite ojective de 24h qui permet de ne pas predre de retard.

## 8) A Faire

   1/ Factoriser le code en utilisant du polymorphisme pour le calcul de vente et du ca en ulisant une interface sur les produitsTop. Le code sera plus clair et plus facilement réutilisable.

   2/ Gérer le fait que les différents magasins n'ont pas le même nombre de références: une table idMagasin - nbRef.

   3/ Gérer les fichiers non-valides ex: la ref d'un magasin n'est pas UUID ou juste un fichier de transactions ne respectant pas le format.

   4/ Lire les paramètres dans un fichier annexe et pas dans le code.
   
   5/ Faire des scripts pour automatiser les tests fonctionnels et de performance.
      
   6/ MultiThread: en effet la consigne donne 2 cpu et le calcul est très séparable: pour le global il faut uniquement pensé à l'accès concurent des fichiers temporaires (peu fréquent sur 1500 magasins).
