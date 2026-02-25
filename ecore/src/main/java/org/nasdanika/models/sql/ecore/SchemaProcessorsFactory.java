package org.nasdanika.models.sql.ecore;

import java.util.function.BiConsumer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.Context;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.graph.processor.NodeProcessorConfig;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.graph.WidgetFactory;
import org.nasdanika.models.ecore.graph.processors.EClassNodeProcessor;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;
import org.nasdanika.models.sql.SqlPackage;

@EClassifierNodeProcessorFactory(classifierID = SqlPackage.SCHEMA)
public class SchemaProcessorsFactory {
	private Context context;
	
	public SchemaProcessorsFactory(Context context) {
		this.context = context;
	}	
		
	/**
	 * Test of different ways to configure action prototype.
	 * @param config
	 * @param prototypeProvider
	 * @param progressMonitor
	 * @return
	 */
	@EClassifierNodeProcessorFactory(
			icon = "http://sql.models.nasdanika.org/images/scheme.svg", 
			documentation = 
					"""
					A database schema is the blueprint or architecture of a database. 
					It defines how data is organized, structured, and related within the system.

					## What Does a Schema Include?
					
					* Tables: Definitions of each table, including names and columns.
					* Columns: Names, data types, constraints (e.g., NOT NULL, UNIQUE).
					* Relationships: Foreign keys that link tables together.
					* Indexes: Structures that speed up data retrieval.	
					* Views: Virtual tables based on queries.
					* Stored Procedures & Functions: Reusable logic stored in the database.
					* Triggers: Automated actions based on data changes.

					## Schema vs. Database
					
					* A database is the container for all data.
					* A schema is the organizational structure within that container.
					* Some databases (like PostgreSQL or Oracle) support multiple schemas within a single database.
					* Others (like MySQL) treat each database as a single schema.					  					
                    """
	)
	public EClassNodeProcessor createSchemaProcessor(
			NodeProcessorConfig<WidgetFactory, WidgetFactory, Object> config, 
			java.util.function.BiFunction<EObject, ProgressMonitor, Action> prototypeProvider,
			BiConsumer<Label, ProgressMonitor> labelConfigurator,
			ProgressMonitor progressMonitor) {		
		return new EClassNodeProcessor(config, context, prototypeProvider) {
			
			@Override
			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
				super.configureLabel(source, label, progressMonitor);
				if (labelConfigurator != null) {
					labelConfigurator.accept(label, progressMonitor);
				}
			}
			
			@Override
			protected EList<? super Action> getMembersActionCollection(Action parent) {
				return parent.getChildren();
			}
			
			@Override
			protected EList<? super Action> getMembersCollection(Action membersAction) {
				return membersAction.getChildren();
			}
						
		};
	}

}
