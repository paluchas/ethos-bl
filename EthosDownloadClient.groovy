#!/usr/bin/env groovy

import java.net.URL;
import javax.xml.namespace.QName;
//XML parsing
import groovy.xml.StreamingMarkupBuilder
//stub generated  classes
import uk.bl.bspa.webservice.ethos.ejb.sessions.ws.*;

def cli = new CliBuilder(
	usage: 'EthosDownloadClient.groovy -i <ID>')
import org.apache.commons.cli.Option  
cli.with {
	h(longOpt: 'help', 'Show usage information', required: false)
	i(args: 1, argName:'ID', 'Specify an ID (e.g. 534130) from Ethos')
	m(args: 1, argName:'download', 'Download <ID>.zip with full text')
	v(longOpt: 'verbose', 'Show raw uketdXML')
}
def opt = cli.parse(args)

if(opt.h) {
	cli.usage()
	System.exit(0)
}

if(!opt.i){
	cli.usage()
	System.exit(0)
}

def wsdlLocation= new URL("http://ethosdownload.bl.uk/EthosDownload/EthosDownloadService?wsdl")
def serviceQName = new QName("http://ws.sessions.ejb.ethos.webservice.bspa.bl.uk/","EthosDownload")
def service = new EthosDownload(wsdlLocation,serviceQName)

def request = new Request()

//-i <ID> where ID is just a number form EtHOS persistend ID
request.eprintId = opt.i
request.provideUKETD = true
request.provideAdditionalContent = true

def config = new ConfigSlurper("auth").parse(new File('config.groovy').toURL())
//user and password for http://ethosdownload.bl.uk
request.username = config.username
request.password = config.password
println "======================= request: " + request.eprintId +" ==============================="

println "Retriwing port form following service: " + service.toString()
def port = service.getEthosDownloadServicePort()

println "Invokin operation on the port"
def response = port.getHarvestEprintDetails(request)
println "Retrive message " + response.message + " with code " + response.code	


/** Ethosdownload aviable fields: 
    "content", - arrays with EPrints objects
    "embargoed",
    "embargoedDate",
    "embargoedReason",
    "eprintId",
    "ethosURL",
    "institutionId",
    "institutionName",
    "institutionReference",
    "keywords",
    "loadDate",
    "sponsors",
    "thesisName",
    "thesisType",
    "title",
    "uketdXML",
    "year"
*/

def eprint = response.eprints[0]
println "Eprint author: " + eprint.author 
println "Eprint year: " + eprint.year 
println "Eprint title: " + eprint.title 
println "Eprint thesis nanme: " + eprint.thesisName 
println "Eprint thesis type: " + eprint.thesisType 
println "Eprint sponsors: " + eprint.sponsors 
println "Eprint keywords: " + eprint.keywords 
println "Eprint url: " + eprint.ethosURL
println "Eprint loadDate: " + new Date(eprint.loadDate)
println "Eprint institutionId: " + eprint.institutionId
println "Eprint institutionName: " + eprint.institutionName
println "Eprint institutionReference: " + eprint.institutionReference

def uketd = new XmlSlurper().parseText(eprint.uketdXML)
//print if verbose
if (opt.v) {
	println " ==== uketd XML ==== "
	println "Eprint uketdXML " + eprint.uketdXML

	//parse ukedt XML content
	def issued = uketd.issued.text()
	println "Thesees " + issued

	  if (eprint.abstractString != null) {
                abstra = "yes"
	        } else  {
                abstra = "no"
	        }
	println "Abstract: " + abstra
}

//download content with clouser
download = { address ->
	def file = new FileOutputStream("./EthosDownloadStore/" + eprint.eprintId + ".zip")
	def out = new BufferedOutputStream(file)
	out << new URL(address).openStream()
	out.close()
        //setProgressListener - can I do this
}

if (opt.m == "download"){
	//process all eprints
	println "==== Process Eprint content ==== "  
	eprint.content.each {
		println "Eprint size: " + it.size
		println "Eprint name: " + it.name
		println "Eprint download url " + it.downloadUrl
		download(it.downloadUrl)
	}
}
