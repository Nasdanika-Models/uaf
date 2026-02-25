package org.nasdanika.models.sql.ecore;

import java.util.function.BiConsumer;

import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.Context;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Reflector.Factory;
import org.nasdanika.graph.processor.NodeProcessorConfig;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.graph.WidgetFactory;
import org.nasdanika.models.ecore.graph.processors.EPackageNodeProcessor;
import org.nasdanika.models.ecore.graph.processors.EPackageNodeProcessorFactory;
import org.nasdanika.models.sql.SqlPackage;

@EPackageNodeProcessorFactory(nsURI = SqlPackage.eNS_URI)
public class EcoreGenSqlProcessorsFactory {

	private Context context;
	
	@Factory
	public final CatalogProcessorsFactory catalogProcessorsFactory;
	
	@Factory
	public final ColumnProcessorsFactory columnProcessorsFactory;
	
	@Factory
	public final DatabaseProcessorsFactory databaseProcessorsFactory;
	
	@Factory
	public final DataTypeProcessorsFactory dataTypeProcessorsFactory;
	
	@Factory
	public final ForeignKeyProcessorsFactory importedKeyProcessorsFactory;
	
	@Factory
	public final ForeignKeyProcessorsFactory importedKeyColumnProcessorsFactory;
	
	@Factory
	public final PrimaryKeyProcessorsFactory primaryKeyProcessorsFactory;
	
	@Factory
	public final SchemaProcessorsFactory schemaProcessorsFactory;
	
	@Factory
	public final TableProcessorsFactory tableProcessorsFactory;
	
	@Factory
	public final TableTypeProcessorsFactory tableTypeProcessorsFactory;
	
	public EcoreGenSqlProcessorsFactory(Context context) {
		this.context = context;
		
		catalogProcessorsFactory = new CatalogProcessorsFactory(context);
		columnProcessorsFactory = new ColumnProcessorsFactory(context);
		databaseProcessorsFactory = new DatabaseProcessorsFactory(context);
		dataTypeProcessorsFactory = new DataTypeProcessorsFactory(context);
		importedKeyProcessorsFactory = new ForeignKeyProcessorsFactory(context);
		importedKeyColumnProcessorsFactory = new ForeignKeyProcessorsFactory(context);
		primaryKeyProcessorsFactory = new PrimaryKeyProcessorsFactory(context);
		schemaProcessorsFactory = new SchemaProcessorsFactory(context);
		tableProcessorsFactory = new TableProcessorsFactory(context);
		tableTypeProcessorsFactory = new TableTypeProcessorsFactory(context);
	}
	
	@EPackageNodeProcessorFactory(
			label = "SQL Model",
			icon = "https://sql.models.nasdanika.org/images/sql-server.svg",
			description = "A model of SQL database metadata for generating code and documentation",
			actionPrototype = """
                    app-action:
                      content:
                        content-markdown:
                          source:
                            content-resource:
                              location: sql.md
					"""
	)
	public EPackageNodeProcessor createEPackageProcessor(
			NodeProcessorConfig<WidgetFactory, WidgetFactory, Object> config, 
			java.util.function.BiFunction<EObject, ProgressMonitor, Action> prototypeProvider,
			BiConsumer<Label, ProgressMonitor> labelConfigurator,
			ProgressMonitor progressMonitor) {		
		return new EPackageNodeProcessor(config, context, prototypeProvider) {
			
			@Override
			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
				super.configureLabel(source, label, progressMonitor);
				if (labelConfigurator != null) {
					labelConfigurator.accept(label, progressMonitor);
				}
			}
			
		};
	}	

}
