<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xpath-default-namespace="http://hl7.org/fhir">
	<xsl:output method="html" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	<xsl:include href="reporting_templates.xslt"/>
	<xsl:include href="referencing_templates.xslt"/>
	<xsl:template match="/">
		<table border="0">
			<!-- Load reference file-->
			<xsl:variable name="supportedevents" select="document('./SupportedEvents.xml')"/>
			<!-- Get full criteria string-->
			<xsl:variable name="criteria" select="//criteria/@value"/>
			<!-- Set start character and delimiter-->
			<xsl:variable name="startchar" select="'/'"/>
			<xsl:variable name="delim" select="'&amp;'"/>
			<!-- Declare criteria components-->
			<xsl:variable name="bundleType" select="'Bundle?type='"/>
			<xsl:variable name="serviceType" select="'serviceType='"/>
			<xsl:variable name="patient" select="'Patient.identifier='"/>
			<xsl:variable name="event" select="'MessageHeader.event='"/>
			<xsl:variable name="age" select="'Patient.age='"/>
			<!-- Count the number of occurence of each component in the full criteria string-->
			<xsl:variable name="count_bundleType" select="count(tokenize($criteria, replace($bundleType, '\?', '\\?')))-1"/>
			<xsl:variable name="count_serviceType" select="count(tokenize($criteria,$serviceType))-1"/>
			<xsl:variable name="count_patient" select="count(tokenize($criteria,$patient))-1"/>
			<xsl:variable name="count_event" select="count(tokenize($criteria,$event))-1"/>
			<xsl:variable name="count_age" select="count(tokenize($criteria,$age))-1"/>
			<!--Check the count of each component against the cardinality values in the reference file-->
			<xsl:call-template name="cardinality_check">
				<xsl:with-param name="count" select="$count_bundleType"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//criterion"/>
				<xsl:with-param name="ref_attribute" select="'bundleType'"/>
			</xsl:call-template>
			<xsl:call-template name="cardinality_check">
				<xsl:with-param name="count" select="$count_serviceType"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//criterion"/>
				<xsl:with-param name="ref_attribute" select="'serviceType'"/>
			</xsl:call-template>
			<xsl:call-template name="cardinality_check">
				<xsl:with-param name="count" select="$count_patient"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//criterion"/>
				<xsl:with-param name="ref_attribute" select="'patient'"/>
			</xsl:call-template>
			<xsl:call-template name="cardinality_check">
				<xsl:with-param name="count" select="$count_event"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//criterion"/>
				<xsl:with-param name="ref_attribute" select="'event'"/>
			</xsl:call-template>
			<xsl:call-template name="cardinality_check">
				<xsl:with-param name="count" select="$count_age"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//criterion"/>
				<xsl:with-param name="ref_attribute" select="'age'"/>
			</xsl:call-template>
		</table>
	</xsl:template>
</xsl:stylesheet>
