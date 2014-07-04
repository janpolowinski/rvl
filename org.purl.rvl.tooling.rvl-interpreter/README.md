# RVL JAVA Interpreter and D3.js Generator #

- **org.purl.rvl.tooling.rvl2avm**
  - An interpreter for the RDFS/OWL Visualization Language (RVL) in Java that takes RVL mappings and rdf data and creates an Abstract Visual Model (AVM)

Currently other tooling for the OGVIC process is contained in this project, which may be separated in future:

- **org.purl.rvl.java**
  - Wrapping the generated RVL/VISO code for easier access (still stateless) + a few new classes (e.g. calculated value mapping)
- **org.purl.rvl.tooling.process**
  - Running the visualization process from command line
- **org.purl.rvl.tooling.avm2d3**
  - Transforming the AVM to D3.js code
		

- See https://github.com/janpolowinski/rvl/wiki/RVL-Tooling for documentation on how to install and run the interpreter
- Remember to copy ogvic.properties.template to ogvic.properties (which is git-ignored) and set up your configuration there.
