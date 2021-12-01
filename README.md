# ING5-APP-SI01-BigData

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

3. Il faut créer le hbase dans le edge (importer les données, les ajouter dans hbase shell) :
...
