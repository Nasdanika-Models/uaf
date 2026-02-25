module org.nasdanika.models.uaf.ecore {
		
	requires transitive org.nasdanika.models.ecore.graph;
	requires org.eclipse.emf.common;
	requires org.eclipse.emf.ecore.xmi;
	
	exports org.nasdanika.models.uaf.ecore;
	opens org.nasdanika.models.uaf.ecore; // For loading resources
	
}
