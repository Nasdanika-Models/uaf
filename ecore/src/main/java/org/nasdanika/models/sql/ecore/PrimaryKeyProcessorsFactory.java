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

@EClassifierNodeProcessorFactory(classifierID = SqlPackage.PRIMARY_KEY)
public class PrimaryKeyProcessorsFactory {
	private Context context;
	
	public PrimaryKeyProcessorsFactory(Context context) {
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
			icon = "http://sql.models.nasdanika.org/images/key.svg", 
			documentation = 
                    """
					A primary key is a fundamental concept in relational databases — it uniquely identifies each record (row) in a [table](../Table/index.html).

					## Key Characteristics of a Primary Key
					
					* Uniqueness: No two rows can have the same primary key value.
					* Non-nullable: Every row must have a value for the primary key; NULL is not allowed.
					* Immutable (ideally): Primary key values should rarely change, as they anchor relationships across tables.					
                    """
	)
	public EClassNodeProcessor createPrimaryKeyProcessor(
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
