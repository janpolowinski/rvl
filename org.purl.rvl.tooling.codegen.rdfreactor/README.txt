TestReactor main class in src/test/java to avoid overwriting by generated code which goes to src/main/java/

log4j.properties copied from https://code.google.com/p/semweb4j/source/browse/trunk/org.semweb4j.rdfreactor.generator/src/test/resources/log4j.properties
try to run with -Dlog4.debug=true

pom.xml was extended by an additional entry for rdf2go.impl.sesame22 which could not be found otherwise