<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xpath-default-namespace="http://hl7.org/fhir">
	<xsl:output method="html" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	<xsl:include href="reporting_templates.xslt"/>
	<xsl:include href="referencing_templates.xslt"/>
	<xsl:template match="/">
		<table border="0">
			<!-- Load reference file-->
			<xsl:variable name="supportedevents" select="document('./SupportedEvents2.xml')"/>
			<!-- Get the counts of elements with business rules -->
			<xsl:variable name="status_count" select="count(//*[name()='status'])"/>
			<xsl:variable name="contact_count" select="count(//*[name()='contact'])"/>
			<xsl:variable name="end_count" select="count(//*[name()='end'])"/>
			<xsl:variable name="reason_count" select="count(//*[name()='reason'])"/>
			<xsl:variable name="criteria_count" select="count(//*[name()='criteria'])"/>
			<xsl:variable name="channel_type_count" select="count(//*[name()='channel']/*[name()='type'])"/>
			<xsl:variable name="channel_endpoint_count" select="count(//*[name()='channel']/*[name()='endpoint'])"/>
			<!--Check the count of each component against the cardinality values in the reference file-->
			<xsl:call-template name="cardinality_check">
				<xsl:with-param name="count" select="$status_count"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//subscription/elements/element"/>
				<xsl:with-param name="ref_attribute" select="'status'"/>
			</xsl:call-template>
			<xsl:call-template name="cardinality_check">
				<xsl:with-param name="count" select="$contact_count"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//subscription/elements/element"/>
				<xsl:with-param name="ref_attribute" select="'contact'"/>
			</xsl:call-template>
			<xsl:call-template name="cardinality_check">
				<xsl:with-param name="count" select="$end_count"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//subscription/elements/element"/>
				<xsl:with-param name="ref_attribute" select="'end'"/>
			</xsl:call-template>
			<xsl:call-template name="cardinality_check">
				<xsl:with-param name="count" select="$reason_count"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//subscription/elements/element"/>
				<xsl:with-param name="ref_attribute" select="'reason
				'"/>
			</xsl:call-template>
			<xsl:call-template name="cardinality_check">
				<xsl:with-param name="count" select="$channel_type_count"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//subscription/elements/element"/>
				<xsl:with-param name="ref_attribute" select="'channel.type'"/>
			</xsl:call-template>
			<xsl:call-template name="cardinality_check">
				<xsl:with-param name="count" select="$channel_endpoint_count"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//subscription/elements/element"/>
				<xsl:with-param name="ref_attribute" select="'channel.endpoint'"/>
			</xsl:call-template>
		</table>
	</xsl:template>
</xsl:stylesheet>
