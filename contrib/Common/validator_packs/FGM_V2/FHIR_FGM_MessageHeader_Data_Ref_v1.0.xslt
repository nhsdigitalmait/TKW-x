<?xml version="1.0" encoding="UTF-8"?>
<!-- Check MessageHeader data reference has correct format. -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:fhir="http://hl7.org/fhir">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<table border="0">
			<xsl:for-each select="//fhir:MessageHeader">
				<xsl:variable name="event_code" select="substring(./fhir:event/fhir:code/@value,39)"/>
				<xsl:choose>
					<xsl:when test="$event_code =  'FGMQuery_2_0'">
					
					<xsl:choose>
					
						<xsl:when test="substring-before(./fhir:data/fhir:reference/@value,'/') !='Parameters' ">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR: The @value </xsl:text>
									<xsl:value-of select="substring-before(./fhir:data/fhir:reference/@value,'/')"/>
									<xsl:text> in xpath  /Bundle/entry/resource/MessageHeader/data/reference/@value</xsl:text>
									<xsl:text> is not the expected resource here for this message type.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:otherwise>
						<tr>
								<td style="color:#008000">
									<xsl:text>PASS: The @value </xsl:text>
									<xsl:value-of select="substring-before(./fhir:data/fhir:reference/@value,'/')"/>
									<xsl:text> in xpath  /Bundle/entry/resource/MessageHeader/data/reference/@value</xsl:text>
									<xsl:text> is the expected resource here for this message type.</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
						
						</xsl:choose>
						
					</xsl:when>
					<xsl:when test="$event_code =  'FGMCreate_1_0'">
						<xsl:choose>
					
						<xsl:when test="substring-before(./fhir:data/fhir:reference/@value,'/') !='Flag' ">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR: The @value </xsl:text>
									<xsl:value-of select="substring-before(./fhir:data/fhir:reference/@value,'/')"/>
									<xsl:text> in xpath  /Bundle/entry/resource/MessageHeader/data/reference/@value</xsl:text>
									<xsl:text> is not the expected resource here for this message type.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:otherwise>
						<tr>
								<td style="color:#008000">
									<xsl:text>PASS: The @value </xsl:text>
									<xsl:value-of select="substring-before(./fhir:data/fhir:reference/@value,'/')"/>
									<xsl:text> in xpath  /Bundle/entry/resource/MessageHeader/data/reference/@value</xsl:text>
									<xsl:text> is the expected resource here for this message type.</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
						
						</xsl:choose>
					</xsl:when>
					<xsl:when test="$event_code =  'FGMDelete_1_0'">
						<xsl:choose>
					
						<xsl:when test="substring-before(./fhir:data/fhir:reference/@value,'/') !='Parameters' ">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR: The @value </xsl:text>
									<xsl:value-of select="substring-before(./fhir:data/fhir:reference/@value,'/')"/>
									<xsl:text> in xpath  /Bundle/entry/resource/MessageHeader/data/reference/@value</xsl:text>
									<xsl:text> is not the expected resource here for this message type.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:otherwise>
						<tr>
								<td style="color:#008000">
									<xsl:text>PASS: The @value </xsl:text>
									<xsl:value-of select="substring-before(./fhir:data/fhir:reference/@value,'/')"/>
									<xsl:text> in xpath  /Bundle/entry/resource/MessageHeader/data/reference/@value</xsl:text>
									<xsl:text> is the expected resource here for this message type.</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
						
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<!-- Do Nothing. -->
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</table>
	</xsl:template>
	
</xsl:stylesheet>
