import org.nasdanika.capability.CapabilityFactory;
import org.nasdanika.models.uaf.util.SqlEPackageResourceSetCapabilityFactory;

module org.nasdanika.models.uav {
	
//	exports org.nasdanika.models.uaf;
//	exports org.nasdanika.models.uaf.impl;
	exports org.nasdanika.models.uaf.util;
	
	opens org.nasdanika.models.uaf.util to org.nasdanika.common; // For reflection to generate Ecore
	
	requires transitive org.eclipse.emf.ecore;
	requires transitive org.eclipse.emf.common;
	requires org.nasdanika.capability;
	requires org.eclipse.emf.ecore.xmi;
	
//	provides CapabilityFactory with UafEPackageResourceSetCapabilityFactory;
	
}