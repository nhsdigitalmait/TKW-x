<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2005/xpath-functions" xmlns:s="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:wsa="http://www.w3.org/2005/08/addressing">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:variable name="MessageID" select="/*[local-name()='Envelope']/*[local-name()='Header']/*[local-name()='MessageID']"/>
		<xsl:variable name="TimestampID" select="/*[local-name()='Envelope']/*[local-name()='Header']/*[local-name()='Security']/*[local-name()='Timestamp']/@*[local-name()='Id']"/>
		<xsl:variable name="TrackingID" select="//*[local-name()='DistributionEnvelope']/*[local-name()='header']/@trackingid"/>
		<xsl:variable name="ManifestitemID" select="//*[local-name()='DistributionEnvelope']/*[local-name()='header']/*[local-name()='manifest']/*[local-name()='manifestitem'][1]/@id"/>
		<xsl:variable name="PayloadID" select="//*[local-name()='DistributionEnvelope']/*[local-name()='payloads']/*[local-name()='payload'][1]/@id"/>
		<!--Handle if no Message ID returned-->
		<xsl:if test="string-length($MessageID)!=0">
			<xsl:choose>
				<xsl:when test="$MessageID=$TimestampID">
					<xsl:text>ERROR: </xsl:text>
					<xsl:text>MessageID and TimestampID are identical: </xsl:text>
					<xsl:value-of select="$MessageID"/>
					<br/>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$MessageID=$TrackingID">
					<xsl:text>ERROR: </xsl:text>
					<xsl:text>MessageID and TrackingID are identical: </xsl:text>
					<xsl:value-of select="$MessageID"/>
					<br/>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$MessageID=$ManifestitemID">
					<xsl:text>ERROR: </xsl:text>
					<xsl:text>MessageID and ManifestitemID are identical: </xsl:text>
					<xsl:value-of select="$MessageID"/>
					<br/>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$MessageID=$PayloadID">
					<xsl:text>ERROR: </xsl:text>
					<xsl:text>MessageID and PayloadID are identical: </xsl:text>
					<xsl:value-of select="$MessageID"/>
					<br/>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
		<!--Handle if no TimestampID returned-->
		<xsl:if test="string-length($TimestampID)!=0">
			<xsl:choose>
				<xsl:when test="$TimestampID=$TrackingID">
					<xsl:text>ERROR: </xsl:text>
					<xsl:text>TimestampID and TrackingID are identical: </xsl:text>
					<xsl:value-of select="$TimestampID"/>
					<br/>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$TimestampID=$ManifestitemID">
					<xsl:text>ERROR: </xsl:text>
					<xsl:text>TimestampID and ManifestitemID are identical: </xsl:text>
					<xsl:value-of select="$TimestampID"/>
					<br/>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$TimestampID=$PayloadID">
					<xsl:text>ERROR: </xsl:text>
					<xsl:text>TimestampID and PayloadID are identical: </xsl:text>
					<xsl:value-of select="$TimestampID"/>
					<br/>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
		<!--Handle if no TrackingID returned-->
		<xsl:if test="string-length($TrackingID)!=0">
			<xsl:choose>
				<xsl:when test="$TrackingID=$ManifestitemID">
					<xsl:text>ERROR: </xsl:text>
					<xsl:text>TrackingID and ManifestitemID are identical: </xsl:text>
					<xsl:value-of select="$TrackingID"/>
					<br/>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$TrackingID=$PayloadID">
					<xsl:text>ERROR: </xsl:text>
					<xsl:text>TrackingID and PayloadID are identical: </xsl:text>
					<xsl:value-of select="$TrackingID"/>
					<br/>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
		<!--Handle if no ManifestID returned-->
		<xsl:if test="string-length($ManifestitemID)!=0">
			<xsl:choose>
				<xsl:when test="$ManifestitemID!=$PayloadID">
					<xsl:text>ERROR: </xsl:text>
					<xsl:text>ManifestitemID and PayloadID are NOT identical: </xsl:text>
					<br/>
					<xsl:text>ManifestitemID: </xsl:text>					
					<xsl:value-of select="$ManifestitemID"/>
					<br/>
					<xsl:text>PayloadID:  </xsl:text>					
					<xsl:value-of select="$PayloadID"/>
					<br/>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
