package org.nasdanika.models.uaf.tests;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EMOFResourceFactoryImpl;
import org.junit.jupiter.api.Test;

public class UafTests {

	@Test
	public void generateEcoreFromEMOF() throws Exception {
		ResourceSet rs = new ResourceSetImpl();
		rs
			.getResourceFactoryRegistry()
			.getExtensionToFactoryMap()
		    .put("xmi", new EMOFResourceFactoryImpl());
		
		UMLPackage.eINSTANCE.eClass();

		URI uri = URI.createFileURI("model/uaf.xmi");
		Resource r = rs.getResource(uri, true);

		EcoreUtil.resolveAll(r);
		
		URI out = URI.createFileURI("model/uaf.ecore");
		r.setURI(out);
		r.save(null);
	}
	
}
