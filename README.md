# Phenix_Challenge
1) Fichiers
   
   L'application est consituée de 8 classes Java dont 2 exécutables: GénérationTest qui permet de générer les tests et Phenix_challenge_Cattaneo_v2 qui est la classe exécutable de l'application. J'ai implémenté l'application avec l'IDE netBeans.

2) Compilation

   L'application est écrite en JAVA SE 7 est n'utilise que les bibliotèques standards java.util (pour les UUID et les Map et Set) java.io (pour la gestion des entrées/sorties) et java.text (pour la gestion de la date).

3) Exécution
   Il faut définir les paramètres dans la classe Parametres et lancer GénérationTest qui génèrera les transactions pour la date actuelle et les fichiers de références des magasins créés de façon aléatoire. Si on ne veut pas les fichiers de référence on utilise l'argument "V". Ensuite on lance Phenix_challenge_Cattaneo_v2 avec comme premier paramètre "V" pour vente et "C" pour ca et comme deuxième "G" pour global et "M" pour magasin qui dans ce cas doit être suivi de l'UUID de celui-ci.

4) Gestion de la mémoire

   La mémoire disponible étant de 512 mo et le nombre de références produits de plusieurs millions par magasin sur pluss de 1000 magasins. la gestion de la mémoire est la partie critique:
   
   -  Pour lire les transactions j'ai utiliser un BufferedReader qui met pas l'inégralité du fichier en mémoire. 
   
   -  Pour les exercices 1 et 3 où il n'est demandé que de faire le top pour un seul magasin la mise en mémoire dans un tableaux des ventes est possible (entier 4 x ref 15 000 000 = 60 mo) et donc ne posent pas de difficultés. 
   
   -  Pour les exercices 2 & 4 où l'on demande un top global il devient impossible de stocker chaque référence. On peut répéter pour chaque magasin les solutions précédentes mais le temps d'exécution est alors énorme. J'ai choisit d'opter pour un buffer où l'on va écrire une partie des références et quand celui-ci est plein on l'écrire dans un fichier temporaire et le vider. On peut donc avoir un bon nombre de transactions avant d'effectuer des lectures/écritures longues.
   
   -  Dans un premier temps j'ai adopté comme structure de donnée des tableaux car plus économes en mémoire puis je me suis tourné vers des tables beaucoup plus rapides (exécutions 1 million de transactions de 7min à 5sec). Celà a entrainé la nécessité de fractionner les tables complêtes temporaires car trop grandes (256 * 150 millions) en plusieurs plus petites d'un facteur 10. On conserve donc une plus grande rapidité même sur les très grandes entrées.
   
5) Tests

   - J'ai effectué les tests fonctionnels sur de petits jeux de donnée et vérifié "à la main".
   
   - Pour les tests de performence j'ai configurer la JVM pour avoir 

6) A Faire:

      1/ Permettre de changer la date et implémenter les exercices 4 à 8. La structure de l'application ne change pas il suffit de lire les uns à la suite des autres les fichiers de transaction. Le coût en mémoire et le coût en temps est exactement le même que d'utiliser un fichier de transactions 7x plus grand.

      2/ Gérer le fait que les différents magasins n'ont pas le même nombre de références: une table idMagasin - nbRef.

      3/ Gérer les fichiers non-valides ex: la ref d'un magasin n'est pas UUID ou juste un fichier de transactions ne respectant pas le format.

      4/ Faire des scripts pour automatiser les tests fonctionnels et de performance.

      5/ MultiThread: en effet la consigne donne 2 cpu et le calcul est très séparable: pour le global il faut uniquement pensé à l'accès
   concurent des fichiers temporaires (peu fréquent sur 1500 magasins).
   
      6/ Trier le fichier de top de ventes: plus de lisibilité et plus facile pour les tests automatiques.
      
      7/ Lire les paramètres dans un fichier annexe et pas dans le code.
