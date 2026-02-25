package org.nasdanika.models.uaf.ecore.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;
import org.nasdanika.common.Context;
import org.nasdanika.common.Diagnostic;
import org.nasdanika.common.MutableContext;
import org.nasdanika.common.NullProgressMonitor;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.gen.AppSiteGenerator;
import org.nasdanika.models.ecore.graph.processors.EcoreHtmlAppGenerator;
import org.nasdanika.models.ecore.graph.processors.EcoreNodeProcessorFactory;
import org.nasdanika.models.uaf.ecore.EcoreGenSysMLProcessorsFactory;
import org.nasdanika.models.uaf.ecore.EcoreGenUafProcessorsFactory;


/**
 * Tests Ecore -> Graph -> Processor -> actions generation
 * @author Pavel
 *
 */
public class TestUafModelDocGen {
	
	private static EPackage loadSysMLPackage() throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet
			.getResourceFactoryRegistry()
			.getExtensionToFactoryMap()
			.put(
				Resource.Factory.Registry.DEFAULT_EXTENSION, 
				new XMIResourceFactoryImpl());
		
		
		URI resourceURI = URI.createFileURI(new File("sysml.ecore").getCanonicalPath());
		Resource resource = resourceSet.getResource(resourceURI, true);
		return (EPackage) resource.getContents().get(0);
	}
	
	private static EPackage loadUafPackage() throws IOException {
		URI resourceURI = URI.createFileURI(new File("uaf.ecore").getCanonicalPath());
		Resource resource = loadSysMLPackage().eResource().getResourceSet().getResource(resourceURI, true);
		return (EPackage) resource.getContents().get(0);
	}
	
	@Test
	public void testLoadEPackages() throws IOException {
		EPackage uafPackage = loadUafPackage();
		System.out.println("Loaded UAF package: " + uafPackage.getName());
	}
	
	@Test
	public void testGenerateUafModelDoc() throws IOException, DiagnosticException {
		generateSysMLModelDoc();
		generateUafModelDoc();	
	}
	
	
protected void generateSysMLModelDoc() throws IOException, DiagnosticException {	
	ProgressMonitor progressMonitor = new NullProgressMonitor(); // new PrintStreamProgressMonitor();
	MutableContext context = Context.EMPTY_CONTEXT.fork();
	Consumer<Diagnostic> diagnosticConsumer = d -> d.dump(System.out, 0);
	List<Function<URI,Action>> actionProviders = new ArrayList<>();		
	EcoreGenSysMLProcessorsFactory ecoreGenSysMLProcessorFactory = new EcoreGenSysMLProcessorsFactory(context);		
	EcoreNodeProcessorFactory ecoreNodeProcessorFactory = new EcoreNodeProcessorFactory(
			context, 
			(uri, pm) -> {
				for (Function<URI, Action> ap: actionProviders) {
					Action prototype = ap.apply(uri);
					if (prototype != null) {
						return prototype;
					}
				}
				return null;
			},
			diagnosticConsumer,
			ecoreGenSysMLProcessorFactory);
	
	File actionModelsDir = new File("target\\action-models\\");
	actionModelsDir.mkdirs();
	File output = new File(actionModelsDir, "sysml.xmi");
	
	
	Map<EPackage, URI> packageURIMap = Map.ofEntries(
			Map.entry(EcorePackage.eINSTANCE, URI.createURI("https://ecore.models.nasdanika.org/"))	
		);
		
	EcoreHtmlAppGenerator eCoreHtmlAppGenerator = new EcoreHtmlAppGenerator(
			loadSysMLPackage(), 
			packageURIMap, 
			ecoreNodeProcessorFactory);
	
	eCoreHtmlAppGenerator.generateHtmlAppModel(diagnosticConsumer, output, progressMonitor);
			
	String rootActionResource = "sysml-actions.yml";
	URI rootActionURI = URI.createFileURI(new File(rootActionResource).getAbsolutePath());//.appendFragment("/");
	URI pageTeplateURI = URI.createFileURI(new File("page-template.yml").getAbsolutePath());//.appendFragment("/");
	String siteMapDomain = "https://uaf.models.nasdanika.org/sysml";		
	AppSiteGenerator actionSiteGenerator = new AppSiteGenerator() {
		
		protected boolean isDeleteOutputPath(String path) {
			return !"CNAME".equals(path) && !path.startsWith("images/") && !path.startsWith("libraries/") && !path.startsWith("demos/");			
		};
		
	};		
	
	Map<String, Collection<String>> errors = actionSiteGenerator.generate(
			rootActionURI, 
			pageTeplateURI, // Theme.Cerulean.pageTemplateCdnURI, 
			siteMapDomain, 
			new File("../docs/sysml"), 
			new File("target/doc-site-work-dir"), 
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
}
	
		
	protected void generateUafModelDoc() throws IOException, DiagnosticException {	
		ProgressMonitor progressMonitor = new NullProgressMonitor(); // new PrintStreamProgressMonitor();
		MutableContext context = Context.EMPTY_CONTEXT.fork();
		Consumer<Diagnostic> diagnosticConsumer = d -> d.dump(System.out, 0);
		List<Function<URI,Action>> actionProviders = new ArrayList<>();		
		EcoreGenUafProcessorsFactory ecoreGenFamilyProcessorFactory = new EcoreGenUafProcessorsFactory(context);		
		EcoreNodeProcessorFactory ecoreNodeProcessorFactory = new EcoreNodeProcessorFactory(
				context, 
				(uri, pm) -> {
					for (Function<URI, Action> ap: actionProviders) {
						Action prototype = ap.apply(uri);
						if (prototype != null) {
							return prototype;
						}
					}
					return null;
				},
				diagnosticConsumer,
				ecoreGenFamilyProcessorFactory);
		
		File actionModelsDir = new File("target\\action-models\\");
		actionModelsDir.mkdirs();
		File output = new File(actionModelsDir, "uaf.xmi");
		
		
		Map<EPackage, URI> packageURIMap = Map.ofEntries(
				Map.entry(EcorePackage.eINSTANCE, URI.createURI("https://ecore.models.nasdanika.org/")),	
				Map.entry(loadSysMLPackage(), URI.createURI("https://uaf.models.nasdanika.org/sysml/"))	
			);
			
		EcoreHtmlAppGenerator eCoreHtmlAppGenerator = new EcoreHtmlAppGenerator(
				loadUafPackage(), 
				packageURIMap, 
				ecoreNodeProcessorFactory);
		
		eCoreHtmlAppGenerator.generateHtmlAppModel(diagnosticConsumer, output, progressMonitor);
				
		String rootActionResource = "uaf-actions.yml";
		URI rootActionURI = URI.createFileURI(new File(rootActionResource).getAbsolutePath());//.appendFragment("/");
		URI pageTeplateURI = URI.createFileURI(new File("page-template.yml").getAbsolutePath());//.appendFragment("/");
		String siteMapDomain = "https://uaf.models.nasdanika.org/uaf";		
		AppSiteGenerator actionSiteGenerator = new AppSiteGenerator() {
			
			protected boolean isDeleteOutputPath(String path) {
				return !"CNAME".equals(path) && !path.startsWith("images/") && !path.startsWith("libraries/") && !path.startsWith("demos/");			
			};
			
		};		
		
		Map<String, Collection<String>> errors = actionSiteGenerator.generate(
				rootActionURI, 
				pageTeplateURI, // Theme.Cerulean.pageTemplateCdnURI, 
				siteMapDomain, 
				new File("../docs/uaf"), 
				new File("target/doc-site-work-dir"), 
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
	}
				
}
