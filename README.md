# ING5-APP-SI01-BigData_SAVERY_SIGIC_MAATOUK

## Introduction
Notre projet consiste à lire une base de données de gestion de tickets d'une entreprise. Cette dernière est stockée dans **HBase** et est lue à partir d'un code **Java configuré avec Maven**.


## Architecture de la base de données
Utilisation des tables imbriquées.

![image](https://user-images.githubusercontent.com/47555601/147259664-3771dc47-0a7f-4bb0-a7d8-444cc5f2dc2e.png)

Une table principale :
- Ticket

2 sous-tables (imbriquées) :
- Equipe
- Application

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

Nous voulions ensuite établir une connexion entre la table HBase et le projet Maven (vous retrouverez le code dans le dossier "code" du git). 
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
- Récupérer la sévérité du ticket t20077449
	
- Récupérer l'identifiant du ticket t24240870 
	
- Récupérer la criticité de l’application associée au ticket t15674071

Nous avons différentes ***colonnes***, qui se trouvent toutes dans la ***column family "cf"***.
	
Pour réaliser nos requêtes, nous avons créé une table HBase puis importé les données de notre fichier ***csv*** grâce à la commande suivante :
```console
hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.separator="," -Dimporttsv.columns=HBASE_ROW_KEY,cf:severite_ticket,cf:identifiant_application,cf:criticite_application,cf:statut_application,cf:date_ouverture_application,cf:semaine_ouverture_application,cf:identifiant_equipe,cf:equipe_traitement ece_2021_fall_app_1:analyse_causale /education/ece_2021_fall_app_1/d.sigic-ece/projet/analyse_causale.csv

```

## Exemple de requêtes avec notre modèle

- Récupérer les tickets traités par l’équipe SUPERVISION

	t | equipe_traitement=‘SUPERVISION’

    	scan 'ece_2021_fall_app_1:analyse_causale', { COLUMNS => 'cf:equipe_traitement', FILTER => "ValueFilter( =, 'binaryprefix:SUPERVISION')" }
    
    ou : 
    
    	scan 'ece_2021_fall_app_1:analyse_causale', { COLUMNS => 'cf:identifiant_equipe', FILTER => "ValueFilter( =, 'binaryprefix:e1')" } 


- Récupérer les tickets ayant une criticité élevée (gold)

	t | criticite_application=‘Gold’
	
		scan 'ece_2021_fall_app_1:analyse_causale', { COLUMNS => 'cf:criticite_application', FILTER => "ValueFilter( =, 'binaryprefix:Gold')" } 


- Récupérer les tickets à traiter en urgence

	severite_ticket=1

		scan 'ece_2021_fall_app_1:analyse_causale', { COLUMNS => 'cf:severite_ticket', FILTER => "ValueFilter( =, 'binaryprefix:1')" } 

	

- Récupérer les tickets ouverts pour l’app a0599_00

	t | identifiant_application=a0599_00

		scan 'ece_2021_fall_app_1:analyse_causale', { COLUMNS => 'cf:identifiant_application', FILTER => "ValueFilter( =, 'binaryprefix:a0599_00')" } 

