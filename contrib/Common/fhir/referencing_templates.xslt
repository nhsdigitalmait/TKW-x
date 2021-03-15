<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xpath-default-namespace="http://hl7.org/fhir">
	<xsl:output method="html" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	<!-- 
	TEMPLATE NAME: cardinality_check:

	Use:
	Checks cardinality against an xml structure in a reference file such as SupportedEvents.xml e.g.
	<criterion name="bundleType" min="1" max="1"/>
	
	Parameters:
	count = the total occurences of the item being checked in the file being validated e.g. 1 - though usually passed as a calculated variable
	ref_xpath = The xpath (including document name via variable) of the element in the reference document e.g. $supportedevents//criterion
	ref_attribute = the value of @name for the element defined in ref_xpath e.g. 'bundleType'

	Example Call:
	Note::The external file name must have been declared as a variable e.g.
	<xsl:variable name="supportedevents" select="document('./SupportedEvents.xml')"/>
	
			<xsl:call-template name="cardinality_check">
				<xsl:with-param name="count" select="$count_bundleType"/>
				<xsl:with-param name="ref_attribute" select="'bundleType'"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//criterion"/>
			</xsl:call-template>

	Example Output (html table):
	Cardinality Check: bundleType Found= 1 Min= 1 Max=1 PASS 
	Cardinality Check: patient Found= 2 Min= 1 Max=1 ERROR 
	Cardinality Check: event Found= 3 Min= 1 Max=* PASS 
	
-->
	<xsl:template name="cardinality_check">
		<xsl:param name="count"/>
		<xsl:param name="ref_xpath"/>
		<xsl:param name="ref_attribute"/>
		<xsl:variable name="min" select="$ref_xpath[@name=$ref_attribute]/@min"/>
		<xsl:variable name="max" select="$ref_xpath[@name=$ref_attribute]/@max"/>
		<xsl:variable name="resultString" select="concat('Cardinality Check: ', $ref_attribute, ' Found= ', $count, ' Min= ', $min, ' Max=', $max)"/>
		<xsl:choose>
			<xsl:when test="((number($count) &lt; number($min)) or (number($count) &gt; number($max)))">
				<xsl:call-template name="reportError">
					<xsl:with-param name="resultString" select="$resultString"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="reportPass">
					<xsl:with-param name="resultString" select="$resultString"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- 
	TEMPLATE NAME: nems_ref_check:

	Use:
	
	Parses a delimited string e.g. subscription criteria and passes each element for validation against an external file e.g. SupportedEvents.xml
	
	Parameters:

		delim= The delimiter in string being passed in list_name e.g &amp;
		name=The text following the $delim e.g. MessageHeader.event=
		list_name = The delimited string of values to be checked against the external file e.g. &amp;MessageHeader.event=pds-birth-notification-1&amp;MessageHeader.event=vaccinations-1&amp;MessageHeader.event=pds-change-of-address-1
		ref_xpath = The xpath (including document name via variable) of the element in the reference document e.g. $supportedevents//event[@subscription='T']
		ref_attribute = the value of @name for the element defined in ref_xpath e.g. 'code'

	Example Call:
	Note::The external file name must have been declared as a variable e.g.
	<xsl:variable name="supportedevents" select="document('./SupportedEvents.xml')"/>

			<xsl:call-template name="nems_ref_check">
				<xsl:with-param name="delim" select="$delim"/>
				<xsl:with-param name="name" select="$event"/>
				<xsl:with-param name="list_name" select="$event_list"/>
				<xsl:with-param name="ref_xpath" select="$supportedevents//event[@subscription='T']"/>
				<xsl:with-param name="ref_attribute" select="'code'"/>
			</xsl:call-template>

	Example Output (html table):
	&MessageHeader.event=pds-birth-notification-1 validated against allowable values. Entry for "pds-birth-notification-1" found. PASS 
	&MessageHeader.event=vaccinations-1 validated against allowable values. Entry for "vaccinations-1" NOT found. ERROR 
	&MessageHeader.event=pds-change-of-address-1 validated against allowable values. Entry for "pds-change-of-address-1" found. PASS 

-->
	<xsl:template name="nems_ref_check">
		<xsl:param name="delim"/>
		<xsl:param name="name"/>
		<xsl:param name="list_name"/>
		<xsl:param name="ref_xpath"/>
		<xsl:param name="ref_attribute"/>
		<xsl:for-each select="tokenize($list_name,concat($delim, $name))">
			<xsl:if test="string-length(.) > 0">
				<xsl:variable name="token" select="."/>
				<xsl:variable name="report_string" select=" concat($delim, replace($name, '\\', ''), $token ,'  validated against allowable values. Entry for ') "/>
				<xsl:variable name="result" select="boolean($ref_xpath/@*[name()=$ref_attribute]=$token)"/>
				<xsl:choose>
					<xsl:when test="$result">
						<xsl:call-template name="reportPass">
							<xsl:with-param name="resultString" select="concat($report_string, ' &quot;',$token, '&quot; found.')"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="not($result)">
						<xsl:call-template name="reportError">
							<xsl:with-param name="resultString" select="concat($report_string, ' &quot;',$token, '&quot; NOT found.')"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>

