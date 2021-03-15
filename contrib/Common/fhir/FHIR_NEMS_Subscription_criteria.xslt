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
			<!--Parse the criteria string into constituent component lists (allows all to be 0..* at this point)-->
			<xsl:variable name="bundleType_list" select="concat($startchar, $bundleType, substring-before(substring-after($criteria,$bundleType),concat($delim,serviceType)))"/>
			<xsl:variable name="serviceType_list" select="concat($delim,$serviceType, substring-before(substring-after($criteria,$serviceType),concat($delim,$patient)))"/>
			<xsl:variable name="patient_list" select="concat($delim,$patient, substring-before(substring-after($criteria,$patient),concat($delim,$event)))"/>
			<xsl:variable name="event_list" select="concat($delim,$event, substring-before(substring-after($criteria,$event),concat($delim,$age)))"/>
			<xsl:variable name="age_list" select="concat($delim,$age, substring-after($criteria,$age))"/>
			<!--Check each value in the parsed component lists against the allowable values in the reference file-->
			<xsl:call-template name="nems_ref_check">
				<xsl:with-param name="delim" select="'/'"/>
				<xsl:with-param name="name" select="replace($bundleType, '\?', '\\?')"/>
				<xsl:with-param name="list_name" select="$bundleType_list"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//bundleType"/>
				<xsl:with-param name="ref_attribute" select="'name'"/>
			</xsl:call-template>
			<xsl:call-template name="nems_ref_check">
				<xsl:with-param name="delim" select="$delim"/>
				<xsl:with-param name="name" select="$serviceType"/>
				<xsl:with-param name="list_name" select="$serviceType_list"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//serviceType"/>
				<xsl:with-param name="ref_attribute" select="'code'"/>
			</xsl:call-template>
			<xsl:call-template name="nems_ref_check">
				<xsl:with-param name="delim" select="$delim"/>
				<xsl:with-param name="name" select="$event"/>
				<xsl:with-param name="list_name" select="$event_list"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//event[@subscription='T']"/>
				<xsl:with-param name="ref_attribute" select="'code'"/>
			</xsl:call-template>
			<!--
			<xsl:call-template name="nems_ref_check">
				<xsl:with-param name="delim" select="$delim"/>
				<xsl:with-param name="name" select="$age"/>
				<xsl:with-param name="list_name" select="$age_list"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//parameter"/>
				<xsl:with-param name="ref_attribute" select="'name'"/>
			</xsl:call-template>
			-->
		</table>
	</xsl:template>
</xsl:stylesheet>
