# ING5-APP-SI01-BigData_SAVERY_SIGIC_MAATOUK

## Introduction
Notre projet consiste à lire une base de données de gestion de tickets d'une entreprise. Cette dernière est stockée dans **HBase** et est lue à partir d'un code **Java configuré avec Maven**.


## Architecture de la base de données
Utilisation des tables imbriquées.

![image](https://user-images.githubusercontent.com/47555601/147259664-3771dc47-0a7f-4bb0-a7d8-444cc5f2dc2e.png)

Une table principale :
- Ticket

2 sous-tables :
- Equipe
- Service

Préfixer le nom des colonnes :
- t + ticket_id
- e + equipe_id
- a + application_id

Toutes ces données sont réunies dans un seul fichier ***csv*** stocké dans le **HDFS** :
```console
/education/ece_2021_fall_app_1/d.sigic-ece/projet/analyse_causale.csv
```

## Configuration Java
On a configuré Java en utilisant des projets Maven (parent et enfant). Nous avons ajouté les dependences nécessaires dans le projet enfant, soit :
- hbase-common
- hbase-client

Nous voulons ensuite établir une connexion entre la table HBase et le projet Maven (vous retrouverez le code dans le dossier "code" du git). 
Cependant, nous n'avons pas réussi à faire fonctionner les démons Hadoop (NameNode, SecondaryNameNode et ResourceManager) et Hbase (HRegionServer, HMaster et Zookeeper) en lançant les commandes :
```console
./start-hadoop.sh
start-hbase.sh
```

En effet lorsque nous exécutons la commande ***jps*** nous obtenons :
```console
2925578 Jps
```

Finalement, nous avons continué le projet sans réaliser les requêtes dans le code Java mais dans le **Hbase shell** directement.

## Configuration HBase
La row key principale est celle des identifiants des tickets afin de pouvoir faire des requêtes comme suit :
- Récupérer la sévérité du ticket 342

	t_342, cf : severite, 4
	
- Récupérer le nom du ticket 342 

	t_342, cf : name, « ticket : regler pb »
	
- Récupérer la criticité de l’app associée au ticket 342

	t_342, cf : a_criticite, « silver »

Nous avons différentes ***colonnes***, qui se trouvent toutes dans la ***column family "cf"***.
Si nécessaire, nous pourrons également créer des duplications de colonnes afin d'avoir d'autres row key pour créer d'autre types de requêtes comme :
- Récupérer la sévérite du ticket 342 associée à l’appli 28

	a_28, cf : t_severite_t342, 4
 
- Récupérer le nom du ticket ayant une sévérité 4 associé à l’appli 28

	a_28, cf : t_name_s4, « ticket : regler pb » 
	
Pour cela, nous avons créé une table HBase puis importé les données de notre fichier ***csv*** grâce à la commande suivante :
```console
hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.separator="," -Dimporttsv.columns=HBASE_ROW_KEY,cf:severite_ticket,cf:identifiant_application,cf:criticite_application,cf:statut_application,cf:date_ouverture_application,cf:semaine_ouverture_application,cf:identifiant_equipe,cf:equipe_traitement ece_2021_fall_app_1:analyse_causale /education/ece_2021_fall_app_1/d.sigic-ece/projet/analyse_causale.csv

```

## Exemple de requêtes avec notre modèle

- Récupérer les tickets traiter par l’équipe SUPERVISION

	t | e_nom=‘SUPERVISION’

    	scan 'ece_2021_fall_app_1:analyse_causale', { COLUMNS => 'cf:equipe_traitement', FILTER => "ValueFilter( =, 'binaryprefix:SUPERVISION')" }
    
    ou : 
    
    	scan 'ece_2021_fall_app_1:analyse_causale', { COLUMNS => 'cf:identifiant_equipe', FILTER => "ValueFilter( =, 'binaryprefix:e1')" } 


- Récupérer les tickets ayant une criticité élevée (gold)

	t | a_criticite=‘Gold’
	
		scan 'ece_2021_fall_app_1:analyse_causale', { COLUMNS => 'cf:criticite_application', FILTER => "ValueFilter( =, 'binaryprefix:Gold')" } 


- Récupérer les tickets à traiter en urgence

	t_severite=1

		scan 'ece_2021_fall_app_1:analyse_causale', { COLUMNS => 'cf:severite_ticket', FILTER => "ValueFilter( =, 'binaryprefix:1')" } 

	

- Récupérer les tickets ouverts pour l’app a0599_00

	t | a0599_00

		scan 'ece_2021_fall_app_1:analyse_causale', { COLUMNS => 'cf:identifiant_application', FILTER => "ValueFilter( =, 'binaryprefix:a0599_00')" } 

