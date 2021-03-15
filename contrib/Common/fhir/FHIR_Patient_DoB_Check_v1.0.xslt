<?xml version="1.0" encoding="UTF-8"?>
<!-- Check MessageHeader data reference has correct format. -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fhir="http://hl7.org/fhir">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<table border="0">
			<xsl:for-each select="//fhir:Patient">
				<xsl:variable name="patient_DoB" select="./fhir:birthDate/@value"/>
				<xsl:choose>
					<xsl:when test="matches($patient_DoB,'^(19|20)\d\d[-](0[1-9]|1[012])[-](0[1-9]|[12][0-9]|3[01])$' )">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS: The Pateint DoB '</xsl:text>
								<xsl:value-of select="$patient_DoB"/>
								<xsl:text>' is in an acceptible YYYY-MM-DD format </xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:when test="matches($patient_DoB,'^(19|20)\d\d[-](0[1-9]|1[012])$' )">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS: The Patient DoB '</xsl:text>
								<xsl:value-of select="$patient_DoB"/>
								<xsl:text>' is in an acceptible YYYY-MM format </xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:when test="matches($patient_DoB,'^(19|20)\d\d$' )">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS: The Patient DoB '</xsl:text>
								<xsl:value-of select="$patient_DoB"/>
								<xsl:text>' is in an acceptible YYYY format </xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:otherwise>
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: The Patient DoB '</xsl:text>
								<xsl:value-of select="$patient_DoB"/>
								<xsl:text>' is not in an acceptible format (YYYY or YYYY-MM or YYYY-MM-DD)</xsl:text>
							</td>
						</tr>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
