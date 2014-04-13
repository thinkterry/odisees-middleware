package test;

import odisees.core.ParameterCase;
import odisees.core.VariableCase;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@SuiteClasses({ ParameterCase.class, VariableCase.class })
public class TestSuite {

}
