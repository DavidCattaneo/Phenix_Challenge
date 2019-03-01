# Phenix_Challenge




A Faire:

1) Gérer le fait que les différents magasins n'ont pas le même nombre de références: une table idMagasin - nbRef.

2) Gérer les fichiers non-valides ex: la ref d'un magasin n'est pas UUID ou juste un fichier de transactions ne respectant pas le format.

3) Faire des scripts pour automatiser les tests fonctionnels et de performance.

4) MultiThread: en effet la consigne donne 2 cpu et le calcul est très séparable: pour le global il faut uniquement pensé à l'accès
   concurent des fichiers temporaires (peu fréquent sur 1500 magasins).
