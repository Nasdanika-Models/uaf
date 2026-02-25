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

@EClassifierNodeProcessorFactory(classifierID = SqlPackage.TABLE)
public class TableProcessorsFactory {
	private Context context;
	
	public TableProcessorsFactory(Context context) {
		this.context = context;
	}	
		
	@EClassifierNodeProcessorFactory(
			icon = "http://sql.models.nasdanika.org/images/table.svg", 
			documentation = 
                    """					
					A database table is the core structure used to store data in a relational database. 
					Think of it as a grid or spreadsheet where each row represents a record and each column represents a field or attribute of that record.

					## Anatomy of a Table

					* Rows: Each row (also called a record or tuple) holds a single instance of data.
					* Columns: Each column defines a specific attribute, with a name and data type.
					* Data Types: Columns are typed—e.g., ``INTEGER``, ``VARCHAR``, ``DATE`` — to control what kind of data they hold.
					* Constraints: Tables can enforce rules like ``PRIMARY KEY``, ``FOREIGN KEY``, ``NOT NULL``, ``UNIQUE``, etc.					
                    """
	)
	public EClassNodeProcessor createTableProcessor(
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
