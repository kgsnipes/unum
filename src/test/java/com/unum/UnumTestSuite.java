package com.unum;


import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Unum Test Suite")
@SelectPackages("com.unum")
@IncludeClassNamePatterns(".*Test")
public class UnumTestSuite {
}
