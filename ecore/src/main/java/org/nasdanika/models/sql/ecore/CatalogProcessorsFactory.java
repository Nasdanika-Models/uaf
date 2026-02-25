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
import org.nasdanika.models.ecore.graph.processors.EReferenceNodeProcessor;
import org.nasdanika.models.ecore.graph.processors.EStructuralFeatureNodeProcessorFactory;
import org.nasdanika.models.sql.SqlPackage;

@EClassifierNodeProcessorFactory(classifierID = SqlPackage.CATALOG)
public class CatalogProcessorsFactory {
	private Context context;
	
	public CatalogProcessorsFactory(Context context) {
		this.context = context;
	}	
		
	@EClassifierNodeProcessorFactory(
			icon = "http://sql.models.nasdanika.org/images/catalog.svg", 
			description = "Top-level organizational structure containing schemas",
			documentation = """
					* A catalog is typically the top-level container for database objects like [schemas](../schema/index.html), [tables](../table/index.html), and views.
					* In many databases, a catalog corresponds to a database instance itself.
					* Not all databases support catalogs. Some use schemas as the highest level, while others (like MySQL) treat each database as a catalog.										
                    """
	)
	public EClassNodeProcessor createCatalogProcessor(
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
	
	@EStructuralFeatureNodeProcessorFactory(
			nsURI = SqlPackage.eNS_URI,
			classID = SqlPackage.CATALOG,
			featureID = SqlPackage.CATALOG__SCHEMAS,
			description = "Catalog schemas",
			documentation = 
					"""
					Catalog schemas. 					  					  				
					
					"""
	)
	public EReferenceNodeProcessor createSchemasProcessor(
			NodeProcessorConfig<WidgetFactory, WidgetFactory, Object> config, 
			java.util.function.BiFunction<EObject, ProgressMonitor, Action> prototypeProvider,
			BiConsumer<Label, ProgressMonitor> labelConfigurator,
			ProgressMonitor progressMonitor) {		
		return new EReferenceNodeProcessor(config, context, prototypeProvider) {
			
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
