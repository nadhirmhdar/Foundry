package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BottleneckDomain
import com.example.data.model.SeverityLevel
import com.example.ui.theme.*

@Composable
fun DomainBadge(
    domain: BottleneckDomain,
    modifier: Modifier = Modifier
) {
    val color = when (domain) {
        BottleneckDomain.ERP_LOGIC -> SophisticatedLavender
        BottleneckDomain.BPA_FRICTION -> SophisticatedSecondary
        BottleneckDomain.HUMAN_QC_LIMIT -> SophisticatedSoftAmber
        BottleneckDomain.CROSS_INDUSTRY -> SophisticatedSuccessGreen
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
            Text(
                text = domain.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = color
                )
            )
        }
    }
}

@Composable
fun SeverityBadge(
    severity: SeverityLevel,
    modifier: Modifier = Modifier
) {
    val color = when (severity) {
        SeverityLevel.CRITICAL -> SophisticatedCritical
        SeverityLevel.HIGH -> SophisticatedSoftAmber
        SeverityLevel.MEDIUM -> SophisticatedLavender
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        color = color.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Text(
            text = severity.label.uppercase(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
                color = color
            )
        )
    }
}
