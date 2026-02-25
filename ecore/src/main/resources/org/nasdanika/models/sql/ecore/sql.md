```drawio-resource
sql.drawio
```

Nasdanika SQL provides several modules aiming to help to work with SQL/JDBC data sources.

[TOC levels=6]

## Core

This module provides classes for loading data from result sets to EObjects using annotations and generating code.

* [Sources](https://github.com/Nasdanika-Models/sql/tree/main/core)
* [Maven Central](https://central.sonatype.com/artifact/org.nasdanika.models.sql/core)
* [Javadoc](https://javadoc.io/doc/org.nasdanika.models.sql/core)

### Key classes

#### Connector

Builds SQL statements and other objects for Ecore <-> JDBC interactions using model annotations.

In the below code snippet ``DataType`` EClass was generated from JDBC metadata with column name annotations for attributes.
``dataTypeConnector`` uses these annotations to populate ``dType`` objects with data from the ``dataTypes`` ResultSet.

```java
ResultSet dataTypes = databaseMetaData.getTypeInfo();
Connector<DataType> dataTypeConnector = new Connector<>(SqlPackage.Literals.DATA_TYPE);
while (dataTypes.next()) {
    DataType dType = dataTypeConnector.create(dataTypes);
    dType.setName(dataTypes.getString("TYPE_NAME"));            
    getDataTypes().add(dType);
}       
```

#### ConnectionFunction

A function which takes ``Connection`` and ``ProgressMonitor`` arguments.
It is used by the SQL command (below) to execute logic provided by subcommands.

#### Generator

Provides methods for generating Ecore models from metadata. 
The below code was used to generate the SQL model.

```java
String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
ePackage.setName("Sql");
try (Connection conn = DriverManager.getConnection(url, "sa", "")) {
    DatabaseMetaData metadata = conn.getMetaData();
    Generator generator = new Generator();
    try (ResultSet resultSet = metadata.getTypeInfo()) {
        EClass eClass = generator.createEClass(resultSet.getMetaData());
        eClass.setName("DataType");
        ePackage.getEClassifiers().add(eClass);
    }
    try (ResultSet resultSet = metadata.getTableTypes()) {
        EClass eClass = generator.createEClass(resultSet.getMetaData());
        eClass.setName("TableType");
        ePackage.getEClassifiers().add(eClass);
    }           
    try (ResultSet catalogsResultSet = metadata.getCatalogs()) {
        EClass catalogsEClass = generator.createEClass(catalogsResultSet.getMetaData());
        catalogsEClass.setName("Catalog");
        ePackage.getEClassifiers().add(catalogsEClass);
    }
    try (ResultSet schemaResultSet = metadata.getSchemas()) {
        EClass schemaEClass = generator.createEClass(schemaResultSet.getMetaData());
        schemaEClass.setName("Schema");
        ePackage.getEClassifiers().add(schemaEClass);
    }
    try (ResultSet resultSet = metadata.getTables(null, null, null, null)) {
        EClass eClass = generator.createEClass(resultSet.getMetaData());
        eClass.setName("Table");
        ePackage.getEClassifiers().add(eClass);
    }           
    try (ResultSet resultSet = metadata.getColumns(null, null, null, null)) {
        EClass eClass = generator.createEClass(resultSet.getMetaData());
        eClass.setName("Column");
        ePackage.getEClassifiers().add(eClass);
    }           
    try (ResultSet resultSet = metadata.getPrimaryKeys(null, null, "MY_TABLE")) {
        EClass eClass = generator.createEClass(resultSet.getMetaData());
        eClass.setName("PrimaryKey");
        ePackage.getEClassifiers().add(eClass);
    }           
    try (ResultSet resultSet = metadata.getImportedKeys(null, null, "MY_TABLE")) {
        EClass eClass = generator.createEClass(resultSet.getMetaData());
        eClass.setName("ImportedKey");
        ePackage.getEClassifiers().add(eClass);
    }           
}       

EEnum typesEnum = EcoreFactory.eINSTANCE.createEEnum();
typesEnum.setName("Types");
ePackage.getEClassifiers().add(typesEnum);

Stream.of(java.sql.Types.class.getDeclaredFields())
    .filter(f -> Modifier.isStatic(f.getModifiers()))
    .sorted((a,b) -> a.getName().compareTo(b.getName()))
    .map(f -> {
        EEnumLiteral literal = EcoreFactory.eINSTANCE.createEEnumLiteral();
        literal.setName(f.getName());
        literal.setLiteral(f.getName());
        try {
            literal.setValue(f.getInt(null));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return literal;
    })
    .forEach(typesEnum.getELiterals()::add);
                    
CapabilityLoader capabilityLoader = new CapabilityLoader();
ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);       
ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);

Resource resource = resourceSet.createResource(URI.createFileURI("target/sql.ecore"));
resource.getContents().add(ePackage);
resource.save(null);        
```

## Model

Ecore model of JDBC metatadata. It can be loaded from a ``DatabaseMetadata`` object and then be used to generate documentation, Ecore models, code, and diagrams.

* [Sources](https://github.com/Nasdanika-Models/sql/tree/main/model)
* [Maven Central](https://central.sonatype.com/artifact/org.nasdanika.models.sql/model)
* [Javadoc](https://javadoc.io/doc/org.nasdanika.models.sql/model)


### Loading medatata and saving to XMI

```java
try (Connection conn = ...) {
    Database database = Database.create(conn.getMetaData(), null, null, null, null);
    
    CapabilityLoader capabilityLoader = new CapabilityLoader();
    ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
    Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);       
    ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
    
    Resource resource = resourceSet.createResource(URI.createFileURI("target/metadata.xml"));
    resource.getContents().add(database);
    resource.save(null);                    
}       
```

### Generating a diagram

```java

try (Connection conn = ...) {
    Database database = Database.create(conn.getMetaData(), null, null, null, null);
    
    DiagramGenerator diagramGenerator = new DiagramGenerator();
    for (Catalog catalog: database.getCatalogs()) {
        CatalogGenerationResult cr = diagramGenerator.generateCatalog(catalog, null, null, 1920, 1080);
        Files.writeString(new File("target/" + catalog.getName() + ".drawio").toPath(), cr.document().save(null));              
    }            
}       
```

### Generating an Ecore model

```java
try (Connection conn = ...) {
    Database database = Database.create(conn.getMetaData(), null, null, null, null);
    Collection<EObject> sources = new ArrayList<>();
    sources.add(database);
    for (Catalog catalog: database.getCatalogs()) {
        sources.add(catalog);
        for (Schema schema: catalog.getSchemas()) {
            sources.add(schema);
            for (Table table: schema.getTables()) {
                sources.add(table);
                for (ForeignKey ik: table.getImportedKeys()) {
                    sources.add(ik);
                }
                for (Column col: table.getColumns()) {
                    sources.add(col);
                }                       
            }
        }
    }
    
    EcoreGenerator ecoreGenerator = new EcoreGenerator();
    Transformer<EObject,EModelElement> transformer = new Transformer<>(ecoreGenerator);
    ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
    Map<EObject, EModelElement> result = transformer.transform(sources, false, progressMonitor);

    CapabilityLoader capabilityLoader = new CapabilityLoader();
    Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);       
    ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
    
    Resource resource = resourceSet.createResource(URI.createFileURI("target/metadata.ecore"));
    resource.getContents().add(result.get(database));
    resource.save(null);        
}       
```

## CLI

This module provides several commands which can be organized in chains to perform database operations from the command line.

* [Sources](https://github.com/Nasdanika-Models/sql/tree/main/cli)
* [Maven Central](https://central.sonatype.com/artifact/org.nasdanika.models.sql/cli)
* [Javadoc](https://javadoc.io/doc/org.nasdanika.models.sql/cli)

### SQL

[sql](https://docs.nasdanika.org/nsd-cli/nsd/sql/index.html) establishes a database connection and is used by subcommands to execute connection functions with built-in telemetry.

### Metadata

[metadata](https://docs.nasdanika.org/nsd-cli/nsd/sql/metadata/index.html) command loads database metadata into a model and makes it available to subcommands.

### Catalog diagram

[catalog-diagram](https://docs.nasdanika.org/nsd-cli/nsd/sql/metadata/catalog-diagram/index.html) generates a [Draw.io](https://docs.nasdanika.org/core/drawio/index.html) diagram for a database catalog with a diagram page per schema.
There is an option to add documentation properties to diagram elements and then generate a documentation site from the diagram.

### Ecore

[ecore](https://docs.nasdanika.org/nsd-cli/nsd/sql/metadata/ecore/index.html) generates an Ecore model from database metadata. 
The model can then be used to generate Java model classes and use them to load data from the database using the ``Connector`` class without having to write SQL.
The generated model can be modified by removing attributes which are not needed, introducing type hierarchy based on some database information. 
For example, Man/Woman classes extending Person class and loaded from PEOPLE table. 
Or a Manager class extending Employee class - Manager class would have ``reports`` reference and would only be created if there are people reporting to a given employee.

## Ecore

This module provides several processor classes for generating this Ecore model documentation.

* [Sources](https://github.com/Nasdanika-Models/sql/tree/main/ecore)
* [Maven Central](https://central.sonatype.com/artifact/org.nasdanika.models.sql/ecore)
* [Javadoc](https://javadoc.io/doc/org.nasdanika.models.sql/ecore)

## Doc

This module provides several processor classes for generating HTML documentation from the metadata models.

* [Sources](https://github.com/Nasdanika-Models/sql/tree/main/doc)
* [Maven Central](https://central.sonatype.com/artifact/org.nasdanika.models.sql/doc)
* [Javadoc](https://javadoc.io/doc/org.nasdanika.models.sql/doc)
* [Demo](demos/sample-database-docs/index.html)


The below snippet demonstrates how to generate database metamodel documentation.

```java
CapabilityLoader capabilityLoader = new CapabilityLoader();
ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();

Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);       
ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
Resource resource = resourceSet.getResource(URI.createFileURI("test-metadata.xml"), true);
Database database = (Database) resource.getContents().get(0);
MutableContext context = Context.EMPTY_CONTEXT.fork();

Consumer<Diagnostic> diagnosticConsumer = d -> d.dump(System.out, 0);       
EcoreHtmlAppGenerator htmlAppGenerator = EcoreHtmlAppGenerator.loadEcoreHtmlAppGenerator(
        Collections.singleton(database), 
        context,
        null, // java.util.function.BiFunction<URI, ProgressMonitor, Action> prototypeProvider,         
        null, // Predicate<Object> factoryPredicate,
        null, // Predicate<EPackage> ePackagePredicate,
        diagnosticConsumer,
        progressMonitor);

File actionModelsDir = new File("target\\action-models\\");
actionModelsDir.mkdirs();
File output = new File(actionModelsDir, "sql-actions.xmi");
htmlAppGenerator.generateHtmlAppModel(
        diagnosticConsumer, 
        output,
        progressMonitor);
        
// Generating a web site
String rootActionResource = "sql-actions.yml";
URI rootActionURI = URI.createFileURI(new File(rootActionResource).getAbsolutePath());//.appendFragment("/");

AppSiteGenerator actionSiteGenerator = new AppSiteGenerator() {
    
    @Override
    protected Context createContext(ProgressMonitor progressMonitor) {
        return context;
    }
    
};      

String siteMapDomain = "https://something.org";     
Map<String, Collection<String>> errors = actionSiteGenerator.generate(
        rootActionURI, 
        Theme.Cerulean.pageTemplateCdnURI, 
        siteMapDomain, 
        new File("target/sample-database-docs"),  
        new File("target/sample-database-doc-site-work-dir"), 
        true);
        
int errorCount = 0;
for (Entry<String, Collection<String>> ee: errors.entrySet()) {
    System.err.println(ee.getKey());
    for (String error: ee.getValue()) {
        System.err.println("\t" + error);
        ++errorCount;
    }
}

System.out.println("There are " + errorCount + " site errors");
```

### sql-actions.yml

```yaml
Action:
    icon: https://docs.nasdanika.org/images/nasdanika-logo.png
    text: Nasdanika SQL Model
    location: https://crew-ai.models.nasdanika.org/
    children:
     - ActionReference: "target/action-models/sql-actions.xmi#/"
    navigation:
      - Action:
          text: Source
          icon: fab fa-github
          location: https://github.com/Nasdanika-Models/sql
```          

