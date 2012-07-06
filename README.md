ethos-bl
========

A simple client for mass download theses digitised by the BL EThOS project (http://ethos.bl.uk/)

The client is written in Groovy scripting language. Download Groovy environment from this [link|http://groovy.codehaus.org/Download]. The script was developed with Groovy 1.x but it should also run on version 2.x.

Installation
============

1. EthosDownloadClient.groovy utilise EThOS WSDL web service  available at http://ethosdownload.bl.uk/EthosDownload/EthosDownloadService?wsdl

2. Download and unzip a copy of reference implementation of Java API for XML Web Services (JAX-WS) available at http://jax-ws.java.net/. 

3. Add a path to the bin subdirectory so wsimport command can be used
{code}
  $ cd ~/WORKSPACE/ethos-bl
  $ export PATH=$PATH:~/WORKSPACE/jaxws-ri/bin/
{code}

4. Generate WS stub classes 
{code}
  $ wsimport http://ethosdownload.bl.uk/EthosDownload/EthosDownloadService?wsdl
  parsing WSDL...
  generating code...
  compiling code...
{code}

5. Add your user and password for accessing http://ethosdownload.bl.uk at config.groovy

6. Create subdirectory for storing full text files
{code}
  mkdir EthosDwonloadStore
{code}

Usage
=====

1. Execute script: check example thesis metadata
{code}
  groovy EthosDownloadClient.groovy -i 238830
{code}

2. Execute script: download example full text thesis. The output file would be saved in a subdirectory EthosDownloadStore under file name <ID>.zip. 
{code}
  groovy EthosDownloadClient.groovy -i 238830 -m download
{code}

Additional information about how to consume this service can be found in the BL’s document “Thesis export customer summary.doc” acquired by EThOS customer support.
