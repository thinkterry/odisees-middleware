package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import odisees.core.ComparisonCase;
import odisees.core.ParameterCase;
import odisees.core.VariableCase;
import odisees.utils.App;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.Suite;

import com.hp.hpl.jena.rdf.model.ModelFactory;

@RunWith(Suite.class)
@SuiteClasses({ ParameterCase.class, VariableCase.class, ComparisonCase.class})
public class TestSuite {
	
	@BeforeClass
	public static void setup() throws FileNotFoundException {
		String local= "src/test/resources/test.ttl";
		FileInputStream fis= new FileInputStream(new File(local));
		App.localService= ModelFactory.createDefaultModel();
		App.localService.read(fis, null, "TTL"); }}
