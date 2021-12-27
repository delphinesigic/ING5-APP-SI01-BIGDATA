# ING5-APP-SI01-BigData_SAVERY_SIGIC_MAATOUK

## Introduction
Notre projet consiste à lire une base de données de gestion de ticket d'une entreprise. Cette dernière est stockée dans **HBase** et est lu à partir d'un code **Java configurer avec Maven**.


## Architecture de la base de données
Utilisation des tables imbriquées.

![image](https://user-images.githubusercontent.com/47555601/147259664-3771dc47-0a7f-4bb0-a7d8-444cc5f2dc2e.png)

Une table principal :
- Ticket

2 sous-tables :
- Equipe
- Service

Préfixer le nom des colonnes :
- t + ticket_id
- e + equipe_id
- a + application_id

Toutes ces données sont réunit dans un seul fichier ***csv*** stocké dans le **HDFS**.

## Configuration Java
On a configurer Java en utilisant des projets Maven. Nous avons ajouté les dependencies nécessaire, soit :
- hbase-common
- hbase-client

Nous voulons ensuite établir une connexion entre la table HBase et le projet Maven. 
Cependant, nous n'avons pas réussi à faire fonctionner les démons Hadoop (NameNode, SecondaryNameNode et ResourceManager) et le démon Hbase (HRegionServer, HMaster et Zookeeper) en lançant les commandes :
```console
./start-hadoop.sh
start-hbase.sh
```

En effet lorsque nous exécutons la commande ***jps*** nous obtenons :
```console
2925578 Jps
```

Finalement, nous avons continuer le projet sans réaliser les requêtes dans le code Java mais dans le **Hbase shell** directement.

## Configuration HBase
La row key principal est celle des identifiants des tickets afin de pouvoir faire des requêtes comme suit :
- Récupérer la sévérité du ticket 342

	t_342, cf : severite, 5
	
- Récupérer le nom du ticket 342 

	t_342, cf : name, « ticket : regler pb »
	
- Récupérer la criticité de l’app associée au ticket 342

	t_342, cf : a_criticite, « silver »

Nous avons des ***column families*** différents pour les autres colonnes.
Si nécessaire, nous pouvons également créer des duplications de colonne et tables afin d'avoir d'autre row key pour créer d'autre types de requêtes comme :
- Récupérer la sévérite du ticket 342 associé à l’appli 28

	a_28, cf : t_severite_t342, 5
 
- Récupérer le nom du ticket ayant une sévérité 5 associé à l ’appli 28

	a_28, cf : t_name_s5, « ticket : regler pb » 

## Exemple de requêtes avec notre modèle
- Récupérer les tickets traiter par l’équipe SUPERVISION

	t | e_nom=‘SUPERVISION’

- Récupérer les tickets ayant une criticité élevée (gold)

	t | a_criticite=‘Gold’

- Récupérer les tickets à traiter en urgence

	t_severite=1

- Récupérer les tickets ouverts pour l’app a0599_00

	t | a0599_00


---------------------------------------------
# Brouillon

Etapes pour configurer Java :
1. Créer un projet et configurer un projet Maven parent
```console
  <modelVersion>4.0.0</modelVersion>
  <groupId>app_project</groupId>
  <artifactId>ticket</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>pom</packaging>
```

2. Créer un projet Maven enfant dans le parent:
```console
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>app_project</groupId>
    <artifactId>ticket</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </parent>
  <groupId>com</groupId>
  <artifactId>hbase</artifactId>
  <version>0.0.1-HBASE-SNAPSHOT</version>
  <dependencies>
	<dependency>
	    <groupId>org.apache.hbase</groupId>
	    <artifactId>hbase-client</artifactId>
	    <version>2.0.2</version>
	</dependency>
	<dependency>
	    <groupId>org.apache.hbase</groupId>
	    <artifactId>hbase-common</artifactId>
	    <version>2.0.2</version>
	</dependency>
  </dependencies>
```

3. Il faut créer le HBase dans le edge (importer les données, les ajouter dans hbase shell) : 
	- Ajout du fichier analyse_causale.csv dans le edge puis nous l'avons copié vers HDFS : /education/ece_2021_fall_app_1/m.maatouk-ece/projet .
	- Création de la table HBase : 'ece_2021_fall_app_1:analyse_causale' et des colonnes families (3 row keys : ticket, application et equipe).
	- Configuration et connexion HBase avec Java.
